import java.io.*;
import java.util.*;


public class Naloga5 {

    public static void main(String[] args) throws Exception{
        File vhod = new File (args[0]);
        Scanner sc = new Scanner(vhod);
        int ukazov = Integer.parseInt(sc.nextLine());
        Struktura s = new Struktura();
        
        for (int i = 0; i < ukazov; i++) {
            String vrstica = sc.nextLine();
            String[] parametri =  vrstica.split(",");
            char ukaz = vrstica.charAt(0);

            switch(ukaz){
            case 'i': 
                s.init(Integer.parseInt(parametri[1]), Integer.parseInt(parametri[2]));
                break;     
            case 'a':
                s.alloc(Integer.parseInt(parametri[2]), Integer.parseInt(parametri[1]));
                break;   
            case 'f':
                s.free(Integer.parseInt(parametri[1]));
                break;
            }
        }
        s.izpisDat(args[1]);

    }

}


class Struktura {
    private Enota prva;
    private int velikostPolja;
    private int stClenov;

    public void init (int m, int n) {
        this.stClenov = m;
        this.velikostPolja = n;
        for (int i = 0; i < stClenov; i++) {
            Enota e = new Enota(velikostPolja, prva);
            prva = e;
        }
    }

    public boolean alloc (int id, int size) {
        Enota e = prva;

        int maxZaseden = 0;
        int minOstane = Integer.MAX_VALUE;

        for (; e != null; e = e.next) {
            if(e.prazna || (velikostPolja - e.zaseden) == size) {
                e.alloc(id, size);
                return true;
            } else if ( (velikostPolja - e.zaseden) > size) {
                int x = velikostPolja - e.zaseden;
                if (x - size < minOstane) {
                    minOstane = x-size;
                    maxZaseden = e.zaseden;
                }
            }
            if (e.next.prazna)
                for (Enota kandidat = prva; kandidat != null; kandidat = kandidat.next)
                    if(kandidat.zaseden == maxZaseden) {
                        kandidat.alloc(id, size);
                        return true;
                    }
        }
        e.alloc(id, size);
        
        return true;
    }

    public int free (int id) {
        
        for (Enota e = prva; e != null; e = e.next) {
            for (int i = 0; i < e.velikostPolja; i++)
                if (e.polje[i] == id) {
                    return e.free(id);
                }
        }
        return -1;
    }

    public void izpisKonzola() {
            int[] izpis = new int[velikostPolja+1];

            for (Enota e = prva; e != null; e = e.next) {
                int x = velikostPolja - e.zaseden;
                izpis[x]++;
            }
            
            for(int i = izpis.length; i > 0 ;i--)
                System.out.println(izpis[i-1]);
    }

    public void izpisDat(String args) {
        try {

            FileWriter izhod = new FileWriter (args);
            PrintWriter pw = new PrintWriter (izhod);
            //System.out.println("tukaj");
            int[] izpis = new int [velikostPolja+1];
            for (Enota e = prva; e != null; e = e.next)
                izpis[velikostPolja - e.zaseden]++;
    
            for(int i = izpis.length-1; i >= 0; i--) {
                pw.println(izpis[i]);
            }
            //System.out.println("tukaj");
            pw.close();
        }catch(IOException e) {System.out.println("napaka");}
        
    }

}

class Enota {   
    
    public Enota next = null;
    public int velikostPolja;
    public int zaseden = 0;
    public boolean prazna = true;
    public int polje[];
    
    public Enota(){
    }
    
    public Enota(int velikostPolja, Enota naslednji){  
        this.velikostPolja = velikostPolja;
        this.next = naslednji;
    }
    
    public boolean alloc(int id, int size) {
        if(prazna) {
            polje = new int [velikostPolja];
            prazna = false;
        }

        zaseden += size;

        for (int i = 0, j = 0; i < velikostPolja && j < size; i++)
            if (polje[i] == 0) {
                polje[i] = id;
                j++;
            }

        return true;
    }
    
    public int free(int id){
        int odstranjeno = 0;

        int kopija[] = new int [velikostPolja];

        for (int i = 0; i < velikostPolja; i++) {
            if (polje[i] == id) {
                kopija[i] = 0;
                odstranjeno++;
            } else
                kopija[i] = polje[i];
        }

        zaseden -= odstranjeno;
        polje = new int [velikostPolja];
        for (int i = 0, j = 0; i < velikostPolja; i++)
            if(kopija[i] != 0) {
                polje[j] = kopija[i];
                j++;
            }

        return odstranjeno;
    }


}