import java.util.*;
import java.io.*;

public class Naloga4 {
    public static void main(String [] args) throws Exception {
        //File vhod = new File (args[0]);
        Struktura s = new Struktura();
        Scanner sc = new Scanner(new File (args[0]));

        int steviloUkazov = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < steviloUkazov; i++) {
            String vrstica = sc.nextLine();
            char ukaz = vrstica.charAt(0);
            int prviParameter, drugiParameter;

            switch(ukaz) {
                case 's':
                    prviParameter = prviParameter(vrstica);
                    s.init(prviParameter);
                    break;
                case 'i':
                    prviParameter = prviParameter(vrstica);
                    drugiParameter = drugiParameter(vrstica);
                    s.insert(prviParameter, drugiParameter);
                    break;
                case 'r':
                    prviParameter = prviParameter(vrstica);
                    s.remove(prviParameter);
                    break;
            }
            //s.izpisKorak();
        }
        s.izpis(args[1]);

        /*Struktura s = new Struktura();
        s.init(5);
        s.insert(0,0); s.izpis();
        s.insert(1,1); s.izpis();
        s.insert(2,2); s.izpis();
        s.insert(4,3); s.izpis();
        s.insert(3,3); s.izpis();
        s.insert(4,4); s.izpis();
        s.insert(5,5); s.izpis();
        s.insert(6,6); s.izpis();
        s.insert(7,7); s.izpis();
        s.insert(8,8); s.izpis();
        s.insert(9,9); s.izpis();
        s.insert(10,10); s.izpis();
        s.insert(11,11); s.izpis();
        s.insert(12,12); s.izpis();
        s.remove(0); s.izpis();
        s.remove(0); s.izpis();
        s.remove(0); s.izpis();*/
    }

    private static int prviParameter (String x) {
        String [] tabela = x.split(",");
        return Integer.parseInt(tabela[1]);
    }

    private static int drugiParameter (String x) {
        String [] tabela = x.split(",");
        return Integer.parseInt(tabela[2]);
    }
}

class Struktura {

    private Enota prva;
    private Enota zadnja;
    private int velikost;
    private int steviloClenov = 0;

    public void init (int N) {
        this.velikost = N;
        prva = new Enota (N);
        zadnja = prva;
    }

    public boolean insert (int v, int p) {
        int logicna = 0;
        int fizicna = 0;
        Enota e = prva;

        if (p > steviloClenov || p < 0)
            return false;

        while (p != logicna) {
            if (e.seznam[fizicna].valueOf() != null)
            logicna++;
            fizicna++;
            if (fizicna == velikost) {
                if (e.next == null) {
                    e = novaEnota(e);
                    insert(v, p);
                    return true;
                }
                else
                    e = e.next;
                    fizicna = 0;
            }
        }

        if (e.seznam[fizicna].valueOf() == null)
            e.insert(fizicna, v);
        else if (e.seznam[fizicna].valueOf() != null && !e.polna)
            e.zamakni(fizicna, v);
        else if (e.seznam[fizicna].valueOf() != null && e.polna) {
            e = novaEnota(e);
            insert(v,p);
        }
        

        steviloClenov++;
        return true;

    }


    public boolean remove (int p) {
        int logicna = 0;
        int fizicna = 0;

        Enota e = prva;
        while(p != logicna) {
            if (fizicna == velikost || e.seznam[fizicna].valueOf() == null) {
                if (e.next == null)
                    return false;
                else
                    e = e.next;
                    fizicna = 0;
            }
            if (e.seznam[fizicna].valueOf() != null) 
                logicna++;
            fizicna++;
        }

        if (fizicna == velikost-1 || e.seznam[fizicna+1].valueOf() == null) {
            e.seznam[fizicna].vrednost(null);
            e.clenov(-1);
        }
        else {
            e = zamakni(e, fizicna);
            e.clenov(-1);
        }

        if(e.clenov <= (velikost/2) && e.next != null) {
            Enota f = e.next;
            int z = fizicna;
            while (e.clenov < (velikost/2)) {
                e.seznam[z].vrednost(f.seznam[0].valueOf());
                f = zamakni(f, 0);
                f.clenov(-1); e.clenov(+1);
                z++;
            }

            if (f.clenov < (velikost/2)) {
                while (f.clenov > 0) {
                    e.seznam[z].vrednost(f.seznam[0].valueOf());
                    f = zamakni(f, 0);
                    f.clenov(-1); e.clenov(+1); z++;
                }
                e.next = f.next;
            }
        }
        return true;
    }

private Enota zamakni (Enota e, int z) {
        while (z < velikost-1 && e.seznam[z+1].valueOf() != null) {
            e.seznam[z].vrednost(e.seznam[z+1].valueOf());
            e.seznam[z+1].vrednost(null);
            z++;
        }
        return e;
    }


    public Enota novaEnota (Enota e) {
        Enota nova = new Enota (velikost, e.next);
        int c1 = velikost/2;
        for (int i = 0; c1 < velikost; c1++, i++) {
            nova.insert(i, e.seznam[c1].valueOf());
            e.seznam[c1].vrednost(null);
        }
        e.polna = false;
        e.clenov = velikost/2;
        e.next = nova;
        return nova;
    }


    public void izpisKorak() {
        Enota e = prva;
        while (e != null) {
            for (int i = 0; i < velikost; i++) {
                System.out.print(e.seznam[i].valueOf() + " | ");
            }
            System.out.println();
            e = e.next;
        }
        System.out.println();
    }

    public void izpis(String args) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(args));
        int stevec = 0;
        for (Enota e = prva; e != null; e = e.next)
            stevec++;
        pw.println(stevec);
        for (Enota e = prva; e != null; e = e.next) {
            for (int i = 0; i < velikost; i++) {
                if (e.seznam[i].valueOf() == null)
                    pw.print("NULL");
                else
                    pw.print(e.seznam[i].valueOf());
                if (i != velikost-1)
                    pw.print(",");
            }
            pw.println();
        }
        pw.close();
    }
}


class Enota {
    Element [] seznam;
    Enota next;
    public int clenov;
    boolean polna;

    public Enota (int velikost) {
        seznam = new Element[velikost];
        for (int i = 0; i < velikost; i++)
            seznam[i] = new Element();
        next = null;
        clenov  = 0;
        polna = false;
    }

    public Enota (int velikost, Enota next) {
        seznam = new Element[velikost];
        for (int i = 0; i < velikost; i++)
            seznam[i] = new Element();
        this.next =  next;
        this.clenov = 0;
        polna = false;
    }

    public void insert (int pozicija, Object element) {
        seznam[pozicija].vrednost(element);
        clenov++;
        if (clenov == seznam.length)
            polna = true;
    }

    public void clenov(int x) {
        this.clenov = clenov + x;
        if (clenov == seznam.length)
            polna = true;
        else
            polna = false;
    }

    public void zamikVlevo () {
        int i = 0;
        while (i < clenov) {
            seznam[i].vrednost(seznam[i+1].valueOf());
            i++;
        }
        seznam[clenov].vrednost(null);
        clenov--;
    }

    public void zamakni (int pozicija, Object element) {
        int prviNull = 0;
        while (seznam[prviNull].valueOf() != null)
            prviNull++;
        for (; prviNull > pozicija; prviNull--) {
            seznam[prviNull].vrednost(seznam[prviNull-1].valueOf());
        }
        insert(pozicija, element);
    }

    public void spremeniVrednost (int pozicija, Object vrednost) {
        seznam[pozicija].vrednost(vrednost);
    }
}

class Element {
    Object element;

    public Element () {
     this.element = null;
    }
    public Element (int element){
        this.element = element;
    }
    public Object valueOf () {
        return element;
    }
    public void vrednost (Object vrednost) {
        element = vrednost;
    }
}