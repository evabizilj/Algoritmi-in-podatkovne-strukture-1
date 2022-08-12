import java.io.*;
import java.util.*;

public class Naloga2 {
    public static void main(String[] args) throws Exception {
        //long startTime = System.nanoTime();
        File vhod = new File (args[0]);
        Struktura s = new Struktura();
            Scanner sc = new Scanner(vhod);

            int steviloUkazov = sc.nextInt();
            sc.nextLine();

            for (int i = 0; i < steviloUkazov; i++) {
                String vrstica = sc.nextLine();
                char ukaz = vrstica.charAt(0);
                int velikost, id , korakov;

                switch(ukaz) {
                    case 'i':
                    velikost = prviParameter(vrstica);
                    s.init(velikost);
                        break;
                    case 'a':
                        velikost = prviParameter(vrstica);
                        id = drugiParameter(vrstica);
                        s.alloc(velikost, id);
                        break;
                    case 'f':
                        id = prviParameter(vrstica);
                        s.free(id);
                        break;
                    case 'd':
                        korakov = prviParameter(vrstica);
                        s.defrag(korakov);
                        break;
                }

            }
        s.koncniIzpis(args[1]);
        /*long endTime = System.nanoTime();
        double sekunde = (double)(endTime - startTime) / 1000000000.0;
        System.out.println(sekunde);*/
    }

    private static int prviParameter (String x) {
        String [] array = x.split(",");
        return Integer.parseInt(array[1]);
    }

    private static int drugiParameter (String x) {
        String [] array = x.split(",");
        return Integer.parseInt(array[2]);
    }
}

class Struktura {
    public int array[];

    public void init (int size) {
        array = new int [size];
    }

    public boolean alloc(int size, int id){
        int occupiedSpace = 0, startIndex = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0 && occupiedSpace == 0) {
                startIndex = i; occupiedSpace = 1;
            } else if (array[i] == 0) {
                occupiedSpace++;
            } else if (array[i] == id) {
                return false;
            } else if (array[i] != 0 && occupiedSpace < size) {
                occupiedSpace = 0;
            }
        }
        if (occupiedSpace < size)
            return false;
        for (int i = startIndex; i < startIndex+size; i++)
            array[i] = id;
        return true;
    }

    public int free(int id) {
        boolean smoDosegli = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == id)
                smoDosegli = true;
            if(array[i] != id && smoDosegli)
                return i+1;
            if(array[i] == id)
                array[i] = 0;
        }
        return 0;
    }

    public void defrag (int korakov) {
        int i = 0;
        while (i < array.length)   {
            if (array[i] != 0) {
                i++;
                continue;
            }
            else {
                int j = i;
                for (; j < array.length-1; j++)
                    if (array[j] != 0)
                        break;
                int id = array[j];

                for (; j < array.length; j++) {
                    if (array[j] != id)
                        break;
                    else{
                        array[i] = array[j];
                        array[j] = 0;
                        i++;
                    }
                }

                korakov --;
                if (korakov == 0)
                    return;
            }
        }
    }

    public void koncniIzpis (String args) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(args));

        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0)
                continue;
            int iKonec = stevec(i, array[i]);

            pw.println(array[i] + "," + i + "," + iKonec);
            i = iKonec;
        }
        pw.close();
    }
    
    private int stevec (int i, int id) {
        while (array[i] == id) {
            i++;
            if (i >= array.length)
                return i-1;
        }
        return i-1;
    }
}