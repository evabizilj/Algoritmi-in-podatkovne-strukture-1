// javac Naloga1.java && time java Naloga1 Naloga1_TestniPrimeri/I1_10.txt
// grep mgfkimbfxehkzt Naloga1_TestniPrimeri/O1_10.txt 
// cat Naloga1_TestniPrimeri/I1_1.txt  

import java.io.*;
import java.util.Scanner;

public class Naloga1
{
    static int rows = 0;
    static int cols = 0;
    // število besed, ki jih iščemo v mreži
    static int numWords = 0;
    // tabela znakov (elementi mreže)
    static char tab[][];
    // besede, ki jih moramo najti v mreži 
    static String words[];

    // java.io throw without any "handling" (ne potrebujemo catch-ov)
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
            rows = sc.nextInt();
            cols = sc.nextInt();
            tab = new char[rows][cols];
            for (int i = 0; i < rows; ++i)
                for (int j = 0; j < cols; ++j)
                // vrne znak v podanem indeksu v nizu ("branje znakov/char-ov")
                    tab[i][j] = sc.next().charAt(0);
            numWords = sc.nextInt();
            words = new String[numWords];
            for (int i = 0; i < numWords; ++i)
                words[i] = sc.next();
        } // konec branja podatkov

        in.close(); // zapremo vhod

        // sortiranje besed po dolžini, daljše najprej --> algoritem: insertion sort

        for (int i = 0; i < numWords; ++i)
        {
            for (int j = i + 1; j < numWords; ++j) 
            {
                if (words[j].length() > words[i].length())
                {
                    String tmp = words[i];
                    words[i] = words[j];
                    words[j] = tmp;
                }
            }
        }
        
        // izpis rešitve (začnemo s prvo besedo - wordIndex = 0, najdaljša beseda)
        for (WordPositionList pos = solve(0); pos != null && !pos.isEmpty(); pos = pos.next)
            // WordPositionList pos = solve(0); dyqo,3,1,0,4
            // WordPositionList pos = solve(1); mpy,1,4,3,2
            // izpišemo za vsako besedo word + "," + String.valueOf(fromRow) + "," + String.valueOf(fromCol) + "," + String.valueOf(toRow) + "," + String.valueOf(toCol);
            out.println(pos.value);
        
        // zapremo izhod
        out.close();
    }
    
    // razred, ki opiše pozicijo besede v mreži
    static class WordPosition
    {
        public String word;
        public int fromRow;
        public int fromCol;
        public int toRow;
        public int toCol;
        // v katero smer je besede "usmerjena" v mreži (8 smeri), pomembno saj beseda ne more biti "vijugasta"
        public int direction;

        // začetek
        public WordPosition(String w, int a, int b)
        {
            word = w;
            fromRow = a;
            fromCol = b;
            toRow = -1;
            toCol = -1;
            direction = -1;
        }

        public WordPosition(String w, int a, int b, int c, int d, int dir)
        {
            word = w;
            fromRow = a;
            fromCol = b;
            toRow = c;
            toCol = d;
            direction = dir;
        }

        // formatiran izpis (za izpis rešitve)
        public String toString()
        {
            return word + "," +
              //  String.valueOf(direction) + "," +
                String.valueOf(fromRow) + "," +
                String.valueOf(fromCol) + "," +
                String.valueOf(toRow) + "," +
                String.valueOf(toCol);
        }
    }

    // seznam pozicij besed
    static class WordPositionList
    {
        // "data", objekt (pozicija besede - x0, y0, x1, x2, smer)
        WordPosition value;
        // "reference", referenca na naslednjo pozicijo besedo (petorček)
        WordPositionList next;

        // prazen seznam
        public WordPositionList()
        {
            this.value = null;
            this.next = null;
        }

        // naredi seznam pozicije besede (pozicija besede, naslednja pozicija)
        public WordPositionList(WordPosition value, WordPositionList next)
        {
            this.value = value;
            this.next = next;   
        }

        // pripnemo element seznama (pozicija besede), pomožna metoda za prependList
        void prepend(WordPosition v)
        {
            // beseda ne obstaja
            if (v == null)
                return;
            
            // trenutni seznam besed je prazen
            if (isEmpty())
            {
                value = v;
                next = null;
                return;
            }

            // pripnemo nov element seznama v obstoječ seznam
            next = new WordPositionList(value, next);
            value = v;
        }

        // pripnemo sezname (dobimo vse pozicije besed) --> s tem najdemo seznam, ki ima pravilne pozicije besed, da pokrijemo celotno mrežo
        void prependList(WordPositionList list)
        {
            // vsak člen seznama (pozicija besed) pripnemo temu seznamu
            for (; list != null && !list.isEmpty(); list = list.next)
                prepend(list.value);
            // prepend WordPositionList: npr. dyqo,3,1,0,4 --> mpy,1,4,3,2
        }

        boolean isEmpty()
        {
            return value == null;
        }
    }

    static WordPositionList solve(int wordIndex)
    {
        // če nam zmanjka besed oz. smo preiskali vse besede, vrnemo prazen seznam (value = null, next = null; že narejen konstruktor)
        if (wordIndex >= numWords)
            return new WordPositionList();

        String word = words[wordIndex]; // beseda, ki jo iščemo v mreži  (solve (0) ... prva beseda, ...)
        
        // sprehodimo se po vseh preostalih pojavitvah besede v križanki 
        for (WordPositionList pos = findWord(word); pos != null && !pos.isEmpty(); pos = pos.next)
        {
            // zbriši besedo (v mreži označimo s pikicami), s tem definiramo obiskanost polj 
            // pos.value ... WordPosition
            deleteWord(pos.value);

            // iščemo pojavitve naslednje besede v preostanku križanke
            WordPositionList newPos = solve(wordIndex + 1);
            if (newPos != null)
            {
               // našli smo rešitev za to besedo in vse za njo --> izpišemo, newPos je zdaj next
               return new WordPositionList(pos.value, newPos);
            }
            
            // če ne najdemo rešitev, izbrisano besedo znova dodamo (namesto pikic, damo nazaj črke, ki tvorijo besedo)
            addWord(pos.value);
        }
        return null;
    }

    // iskanje vseh pojavitev besede v mreži
    static WordPositionList findWord(String word)
    {
        WordPositionList result = new WordPositionList();
        for (int i = 0; i < rows; ++i)
            for (int j = 0; j < cols; ++j)
            {
                // združujemo sezname vseh (možnih) pojavitev besede, začenši na položaju (i,j)
                result.prependList(findWordAt(new WordPosition(word, i, j), 0, i, j));
            }
        return result;
    }

    // iskanje vseh pojavitev besede (start) v mreži, začenši z znakom na mestu pos in položajem v mreži (row,col), v smeri start.direction
    static WordPositionList findWordAt(WordPosition start, int pos, int row, int col)
    {
        String word = start.word;
        int dir = start.direction;

        // robni pogoj 1: padli smo ven iz mreže, ali pa položaj ne ustreza iskani črki v besedi
        if (row >= rows || col >= cols || row < 0 || col < 0 || tab[row][col] != word.charAt(pos))
            return null;

        // robni pogoj 2: našli smo celo besedo, s koncem na trenutnem položaju
        if (pos == word.length() - 1)
        {
            start.toRow = row;
            start.toCol = col;
            return new WordPositionList(start, null);
        }

        // besedo iščemo v 8 smereh
        // dir == -1 pomeni, da še nimamo določene smeri (prva črka besede)
        // sicer nadaljujemo le v dani smeri
        // WordPosition (word, fromRow, fromCol, toRow = 0, toCol = 0, direction = x, nastavljeni samo prvi trije)
        WordPositionList result = new WordPositionList();
        if (dir == -1 || dir == 0) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 0), pos + 1, row, col - 1)); // left
        if (dir == -1 || dir == 1) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 1), pos + 1, row, col + 1)); // right
        if (dir == -1 || dir == 2) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 2), pos + 1, row - 1, col)); // up
        if (dir == -1 || dir == 3) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 3), pos + 1, row + 1, col)); // down
        if (dir == -1 || dir == 4) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 4), pos + 1, row - 1, col - 1)); // up-left
        if (dir == -1 || dir == 5) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 5), pos + 1, row - 1, col + 1)); // up-right
        if (dir == -1 || dir == 6) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 6), pos + 1, row + 1, col - 1)); // down-left
        if (dir == -1 || dir == 7) result.prependList(findWordAt(new WordPosition(word, start.fromRow, start.fromCol, 0, 0, 7), pos + 1, row + 1, col + 1)); // down-right

        return result;
    }

    // brisanje najdene besede iz mreže; črke nadomesti s pikami
    // zbriši besedo (v mreži označimo s pikicami), s tem definiramo obiskanost polj 
    static void deleteWord(WordPosition pos)
    {
        String word = pos.word;
        int row = pos.fromRow;
        int col = pos.fromCol;
        for (int i = 0; i < word.length(); ++i)
        {
            tab[row][col] = '.';

            if (pos.direction == 0) col -= 1; // left
            if (pos.direction == 1) col += 1; // right
            if (pos.direction == 2) row -= 1; // up
            if (pos.direction == 3) row += 1; // down
            if (pos.direction == 4) { row -= 1; col -= 1; } // up-left
            if (pos.direction == 5) { row -= 1; col += 1; } // up-right
            if (pos.direction == 6) { row += 1; col -= 1; } // down-left
            if (pos.direction == 7) { row += 1; col += 1; } // down-right
        }
    }
    
    // vračanje besede v mrežo; pike nadomesti s črkami besede
    // če ne najdemo rešitve, izbrisano besedo znova dodamo (namesto pikic, damo nazaj črke, ki tvorijo besedo)
    static void addWord(WordPosition pos)
    {
        String word = pos.word;
        int row = pos.fromRow;
        int col = pos.fromCol;
        for (int i = 0; i < word.length(); ++i)
        {
            tab[row][col] = word.charAt(i);

            if (pos.direction == 0) col -= 1; // left
            if (pos.direction == 1) col += 1; // right
            if (pos.direction == 2) row -= 1; // up
            if (pos.direction == 3) row += 1; // down
            if (pos.direction == 4) { row -= 1; col -= 1; } // up-left
            if (pos.direction == 5) { row -= 1; col += 1; } // up-right
            if (pos.direction == 6) { row += 1; col -= 1; } // down-left
            if (pos.direction == 7) { row += 1; col += 1; } // down-right
        }
    }
}   