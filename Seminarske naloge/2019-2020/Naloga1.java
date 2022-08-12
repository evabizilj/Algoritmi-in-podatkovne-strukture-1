import java.io.*;
import java.util.*;

public class Naloga1 {
    private static int najkrajsa = Integer.MAX_VALUE;
    private static String optimalna = "";
    private static int [][] zemljevid;

    private static int stPotnikov;
    private static int xZac;
    private static int yZac;
    private static int stStrank;

    private static long c1;
    private static long c2;


    public static void main  (String [] args) throws Exception {
        //c1 = System.nanoTime();
        Scanner sc = new Scanner (new File (args[0]));

        stPotnikov = sc.nextInt();
        String vrstica = sc.next();
        String [] smeti = vrstica.split(",");

        xZac = Integer.parseInt(smeti[0]);
        yZac = Integer.parseInt(smeti[1]);

        stStrank = sc.nextInt();

        zemljevid = new int [stStrank][4];

        int []stanje = new int[stStrank]; //0 - potnik ni pobran || 1 - potnik v taxiju || 2 - potnik na cilju
        
        for (int i = 0; i < stStrank; i++) {
            vrstica = sc.next();
            smeti = vrstica.split(",");
            for (int j = 0; j < 4; j++) {
                zemljevid[i][j] = Integer.parseInt(smeti[j+1]);
            }
        }

        optimalna(xZac, yZac, 0, stanje, 0, "");
        izpis(args[1]);
    }

    private static void optimalna (int x, int y, int dolzina, int[] stanje, int zasedenost, String trenutnaPot) {
        if (dolzina > najkrajsa)
            return;
        
        int naCilju = 0;

        for (int i = 0; i < stStrank; i++) {
            if (stanje[i] == 0 && zasedenost < stPotnikov) {
                    int dolzinaTemp = dolzina + Math.abs(x- zemljevid[i][0]) + Math.abs(y - zemljevid[i][1]);
                    stanje[i]++;
                    optimalna(zemljevid[i][0], zemljevid[i][1], dolzinaTemp, stanje, zasedenost+1, (trenutnaPot + (i+1) + ","));
                    stanje[i]--;
                } else if (stanje[i] == 1) {
                    int dolzinaTemp = dolzina + Math.abs(x - zemljevid[i][2]) + Math.abs(y - zemljevid[i][3]);
                    stanje [i]++;
                    optimalna(zemljevid[i][2], zemljevid[i][3], dolzinaTemp, stanje, zasedenost-1, (trenutnaPot + (i+1) + ","));
                    stanje[i]--;
                } else if(stanje[i] == 2)
                    naCilju++;
        }

        if (naCilju == stStrank) {
            najkrajsa = dolzina;
            optimalna = trenutnaPot;
        }
    }

    private static void izpis(String args) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(args));
        /*for (int i = 0; i < optimalna.length()-1; i++)
            pw.print(optimalna.charAt(i));*/
        pw.print(optimalna.substring(0, optimalna.length()-1));
        pw.close();
        //c2 = System.nanoTime();
        //System.out.println((c2-c1) / 1000000);
    }
}