import java.util.*;
import java.io.*;

public class Naloga9 {

    private static int stDvojic;
    private static int [][] prijateljstva;
    private static boolean [] jeZe;
    private static int [] zapolnjenost;
    private static int [] kjeJe;
    private static String nepotrebno = "";
    private static File vhod;
    private static PrintWriter izhod;

    private static Scanner sc;
    public static void main(String [] args) throws Exception{
        vhod = new File (args[0]);
        izhod = new PrintWriter (new FileWriter(args[1]));
        sc = new Scanner (vhod);

        stDvojic = sc.nextInt(); sc.nextLine();
        inic();
        String vrstica = "";
        for (int i = 0; i < stDvojic; i++) {
            vrstica = sc.nextLine();
            String [] t = vrstica.split(",");

            int a = Integer.parseInt(t[0]);
            int b = Integer.parseInt(t[1]);

            vstavi(a,b);
        }
        izhod.close();
    }

    private static void inic () {
        prijateljstva = new int [stDvojic] [stDvojic];
        jeZe = new boolean [stDvojic*2];
        kjeJe = new int [jeZe.length];
        zapolnjenost = new int [prijateljstva.length];
    }

    private static void vstavi (int a, int b) throws Exception{
        if (jeZe[a]) {
            if (jeZe[b]) {
                if (kjeJe[a] == kjeJe[b]) {
                    izhod.println(a + "," + b);
                }
                else if (kjeJe[a] < kjeJe[b]) {
                    //kopiraj iz vrstice kjer je b a vrstico kjer je a
                    kopiraj(kjeJe[b], kjeJe[a]);
                }else {
                    kopiraj(kjeJe[a], kjeJe[b]);
                }
                return;
            }else {
                prijateljstva[kjeJe[a]] [zapolnjenost[kjeJe[a]]] = b;
                zapolnjenost [kjeJe[a]]++;
                jeZe[b] = true;
                kjeJe[b] = kjeJe[a];
                return;
            }
        }
        else if (jeZe[b]) {
            if (jeZe[a]) {
                if (kjeJe[a] == kjeJe[b]) {
                    izhod.println(a + "," + b);
                }
                else if (kjeJe[a] < kjeJe[b]) {
                    //kopiraj iz vrstice kjer je b a vrstico kjer je a
                    kopiraj(kjeJe[b], kjeJe[a]);
                }else {
                    kopiraj(kjeJe[a], kjeJe[b]);
                }
                return;
            }else {
                prijateljstva[kjeJe[b]] [zapolnjenost[kjeJe[b]]] = a;
                zapolnjenost [kjeJe[b]]++;
                jeZe[a] = true;
                kjeJe[a] = kjeJe[b];
                return;
            }
        }
        
        for (int i = 0; i < prijateljstva.length; i++) {
            if (prijateljstva[i][0] == 0) {
                prijateljstva[i] [0] = a;
                jeZe [a] = true; kjeJe[a] = i; zapolnjenost[i]++;

                prijateljstva[i] [1] = b;
                jeZe [b] = true; kjeJe[b] = i; zapolnjenost[i]++;
                return;
            }
           /*for (int j = 0; j < prijateljstva[i].length; j++) {
                boolean [] vneseno = new boolean [2];
                if(vneseno[0] && vneseno[1]) {
                    nepotrebno += (a + "," + b);
                    return;
                }
                else if (prijateljstva[i][j] == a)
                    vneseno[0] = true;
                else if (prijateljstva [i][j] == b)
                    vneseno[1] = true;
                else if (prijateljstva[i][j] == 0) {
                    if (vneseno[0]) {

                        return;
                    }
                    else if (vneseno[1]) {

                        return;
                    }
                }
            }*/

        }
    }

    private static void kopiraj (int izVrstice, int vVrstico) {
        for (int i = 0; i < zapolnjenost[izVrstice]; i++) {
            prijateljstva[vVrstico] [zapolnjenost[vVrstico]] = prijateljstva [izVrstice][i];
            prijateljstva[izVrstice][i] = 0;
            kjeJe [ prijateljstva[vVrstico] [zapolnjenost[vVrstico]] ]  = vVrstico;
            zapolnjenost[vVrstico]++;
        }
        zapolnjenost[izVrstice] = 0;
    }

    private static void izpis() {
        for (int i = 0; i < prijateljstva.length; i++) {
            for (int j = 0; j < prijateljstva[i].length; j++)
                System.out.print(prijateljstva[i][j] + " ");
            System.out.println();
        }
    }
}