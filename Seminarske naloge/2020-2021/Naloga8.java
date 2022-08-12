import java.io.*;
import java.util.Scanner;
import java.util.Queue;
import java.util.ArrayDeque;

/*
Imamo višinski zemljevid dimenzij A * A. Za podano višino vodne gladine želimo izračunati delež potopljenih
točk.

Ideja: QuadTree, BFS, vrsta ... (Queue, ArrayDeque ... posebna oblika growable tabele, ki dovoli, da dodajamo in brišemo elemente iz obeh strani)

Naloga vsebuje:
    - QuadTree
*/

public class Naloga8
{
    public static void main(String[] args) throws IOException
    {
        // priprava vhoda
        InputStream in = System.in;
        if (args.length > 0)
            in = new FileInputStream(args[0]);
        
        // priprava izhoda
        PrintStream out = System.out;
        if (args.length > 1)
            out = new PrintStream(new FileOutputStream(args[1]));
        
        // branje podatkov
        try (Scanner sc = new Scanner(in))
        {
            sc.useDelimiter(",|\\s+");
            
            int size = sc.nextInt(); // dimenzija zemljevida 
            int data[][] = new int[size][size]; // podatki o višinah vodne gladine
            for (int i = 0; i < size; ++i)
                for (int j = 0; j < size; ++j)
                    data[i][j] = sc.nextInt();

            QuadTree root = new QuadTree(data, size, 0, 0, size); 

            // BFS ... uporaba vrste
            Queue<QuadTree> q = new ArrayDeque<QuadTree>();

            int num = sc.nextInt(); // število vodnih gladin
            for (int i = 0; i < num; ++i)
            {
                int height = sc.nextInt(); // višina vodne gladine
                int visited = 0; // število pregledanih vozlišč
                int subs = 0; // število potopljenih točk
                
                // v vrsto dodamo koren
                q.add(root);

                while (!q.isEmpty())
                {
                    // vrne in izbriše vozlišče iz vrste
                    QuadTree tree = q.poll();

                    // povečamo število prebranih vozlišč/točk
                    visited += 1;

                    // vse točke so nad višino h (če je to koren --> vrsta sprazne --> 0, 1)
                    if (tree.min > height)
                        continue;
        
                    // delna potopljenost (višina vodne gladine se nahaja med min in max vrednostjo) --> pregledati moramo še njene sinove 
                    // (ima sinove: min != max)
                    // delno potopljeno točko ne štejemo kot potopljeno
            
                    if (tree.max <= height || tree.min == tree.max)
                        subs += tree.size * tree.size; // povečamo število potopljenih točk (size == windowSize), v našem primeru: sinovi size (4 / 2 = 2),  2 * 2 pot. točk za levega sina

                    // razbitje prostora na sinove, tako se poveča tudi prostor, ki ga moramo preiskati
                    // dodajanje vozlišč/točk na konec vrste
                    else
                    {
                        q.add(tree.northWest);
                        q.add(tree.northEast);
                        q.add(tree.southWest);
                        q.add(tree.southEast);
                    }
                }
                // za vsako podano višino vodne gladine izpišemo <število potopljenih točk> in <število obiskanih vozlišč>
                out.printf("%d,%d\n", subs, visited);
            }
    
        } // konec branja podatkov

        // zapremo vhod
        in.close();

        // zapremo izhod
        out.close();
    
    }

    /*
    Drevesna struktura v kateri vsako vozlišče predstavlja kvadraten segment znotraj zemljevida (2D prostor).
    Korensko vozlišče predstavlja celoten zemljevid.
    Vsa notranja vozlišča drevesa imajo natanko 4 sinove, ki predstavljajo razdelitev tega vozlišča na enako velike kvadrante (nw, ne, sw, se).
    V vsakem vozlišču imamo min in max nadmorsko višino točk, ki jih to vozlišče pokriva.
    */

    public static class QuadTree
    {
        // sinovi drevesa
        QuadTree northWest;
        QuadTree northEast;
        QuadTree southWest;
        QuadTree southEast;

        int min; // minimalna višina vodne gladine 
        int max; // maksimalna višina vodne gladine 
        int size; // velikost drevesa ...

        // offsetX ... oddaljenost od (0, 0) v x smeri
        // offsetY ... oddaljenost od (0, 0) v y smeri
        // windowSize ... kvadraten segment ... npr. 4 --> koren: 4 (16 točk), sin: 4/2 (4 točk), vnuk: 4/2/2 (1 točk)
        
        public QuadTree(int[][] data, int size, int offsetX, int offsetY, int windowSize)
        {
            if (size == 0 || windowSize == 0 || offsetX >= size || offsetY >= size)
                throw new RuntimeException("Napaka!");
            
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            // sprehod po tabeli višin, izračun min in max višin
            for (int i = 0; i < windowSize; ++i) 
            {
                for (int j = 0; j < windowSize; ++j)
                {
                    int cell = data[offsetY + i][offsetX + j];
                    min = Math.min(min, cell); 
                    max = Math.max(max, cell);
                }
            }
            if (min == max) // vozlišče nima sinov
            {
                this.northWest = null;
                this.northEast = null;
                this.southWest = null;
                this.southEast = null;
            }
            
            // ustvarjanje sinov drevesa
            else
            {
                int hw = windowSize / 2; // "kvadrat" se razdeli na polovico
                this.northWest = new QuadTree(data, size, offsetX, offsetY, hw);
                this.northEast = new QuadTree(data, size, offsetX + hw, offsetY, hw);
                this.southWest = new QuadTree(data, size, offsetX, offsetY + hw, hw);
                this.southEast = new QuadTree(data, size, offsetX + hw, offsetY + hw, hw);
                
                min = Math.min(Math.min(northWest.min, northEast.min), Math.min(southWest.min, southEast.min));
                max = Math.max(Math.max(northWest.max, northEast.max), Math.max(southWest.max, southEast.max));
            }

            this.size = windowSize;
            this.min = min;
            this.max = max;
        }
    }
}