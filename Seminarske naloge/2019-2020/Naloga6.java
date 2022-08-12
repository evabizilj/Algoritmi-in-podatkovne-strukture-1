import java.io.*;
import java.util.*;

public class Naloga6 {

    private static Scanner sc;
    private static int steviloCest;
    private static boolean[] idCest;
    private static int rezultat = 0;
    private static Mesto [] mesta;

    private static int idIzhodisca;
    private static int idCilja;
    private static int visinaTovornjak;

    private static File vhod;
    private static PrintWriter izhod;

    public static void main(String[] args) throws Exception{
        sc = new Scanner (new File(args[0]));

        steviloCest = Integer.parseInt(sc.nextLine());
        inicializacija();
        steviloPoti(idIzhodisca);

        izhod = new PrintWriter(new FileWriter(args[1]));
        izhod.println(rezultat);
        izhod.close();

    }

    private static void inicializacija() {
        mesta = new Mesto [steviloCest+1];
        idCest = new boolean [steviloCest+1];

        String smeti;
        for (int i = 0; i < steviloCest; i++) {
            smeti = sc.nextLine();
            dodajPovezavo(smeti.split(","));
        }

        smeti = sc.nextLine();
        String [] parametri = smeti.split(",");
        idIzhodisca = Integer.parseInt(parametri[0]);
        idCilja = Integer.parseInt(parametri[1]);
        visinaTovornjak = sc.nextInt();

        idCest[idIzhodisca] = true;

    }

    private static void dodajPovezavo (String[] parametri) {
        int visinaPredor = Integer.parseInt(parametri[2]);

        int idMesto = Integer.parseInt(parametri[0]);
        if (mesta[idMesto] == null)
            mesta[idMesto] = new Mesto(idMesto);

        Mesto mesto1 = mesta[idMesto];

        idMesto = Integer.parseInt(parametri[1]);
        if (mesta[idMesto] == null)
            mesta[idMesto] = new Mesto(idMesto);

        Mesto mesto2 = mesta[idMesto];

        Cesta povezava1 = new Cesta(mesto1, mesto2, visinaPredor, mesto1.izhodisce.naslednja);
        Cesta povezava2 = new Cesta(mesto1, mesto2, visinaPredor, mesto2.izhodisce.naslednja);

        mesto1.izhodisce.naslednja = povezava1;
        mesto2.izhodisce.naslednja = povezava2;

    }

    private static void steviloPoti (int idTrenutno) {
        if (idTrenutno == idCilja) {
            rezultat++;
            return;
        }

        Mesto trenutno = mesta[idTrenutno];

        for (Cesta cesta = trenutno.izhodisce.naslednja; cesta != null; cesta = cesta.naslednja) {
            if (cesta.visinaPredor < visinaTovornjak && cesta.visinaPredor != -1)
                continue;
            else {
                int idNaslednje;
                
                if (cesta.mesto1.idMesto == idTrenutno)
                    idNaslednje = cesta.mesto2.idMesto;
                else
                    idNaslednje = cesta.mesto1.idMesto;
                
                if(!idCest[idNaslednje]) {
                    idCest[idNaslednje] = true;
                        steviloPoti(idNaslednje);
                    idCest[idNaslednje] = false;
                }
            }
        }
    }
}

class Mesto {
    public int idMesto;
    public Cesta izhodisce;

    public Mesto (int idMesto) {
        this.idMesto = idMesto;
        this.izhodisce = new Cesta();
    }

    public Mesto (int idMesto, Cesta izhodisce) {
        this.idMesto = idMesto;
        this.izhodisce = izhodisce;
    }
}

class Cesta {
    public Mesto mesto1;
    public Mesto mesto2;
    public int visinaPredor = -1;
    public Cesta naslednja;

    public Cesta () {}

    public Cesta (Mesto mesto1, Mesto mesto2, int visinaPredor, Cesta naslednja) {
        this.mesto1 = mesto1;
        this.mesto2 = mesto2;
        this.visinaPredor = visinaPredor;
        this.naslednja = naslednja;
    }
}