import java.io.*;
import java.util.*;


public class Naloga7 {
    
    private static Scanner sc;
    private static HashMap <Integer, Vozlisce> hashId = new HashMap<>();
    private static HashMap <Integer, Vozlisce> hashVrednost = new HashMap<>();
    private static int stVozlisc;
    private static Vozlisce [] vozlisca;
    private static Vrsta zadnji;

    private static File vhod;
    private static PrintWriter izhod;

    public static void main(String[] args) throws IOException{

            vhod = new File (args[0]);
            izhod = new PrintWriter (new FileWriter(args[1]));
            sc = new Scanner(vhod);

            stVozlisc = sc.nextInt();sc.nextLine();
            vozlisca = new Vozlisce [stVozlisc];

            String smeti; String [] parametri;

            for (int i = 0; i < stVozlisc; i++) {
               
                smeti = sc.nextLine();
                parametri = smeti.split(",");

                int id = Integer.parseInt(parametri[0]);
                int vrednost = Integer.parseInt(parametri[1]);
                int idLevi = Integer.parseInt(parametri[2]);
                int idDesni = Integer.parseInt(parametri[3]);

                vozlisca[i] = new Vozlisce (id, vrednost, idLevi, idDesni);
                hashId.put(id, vozlisca[i]);
                hashVrednost.put(id, vozlisca[i]);

            }

            Vozlisce koren = sortiraj();
            najdiKoordinate(koren, "koren");
            
            
            //izpisKonzola(koren);
            izpisDatoteka(koren);
            izhod.close();
    }

    private static Vozlisce sortiraj() {
        for (int i = 0; i < stVozlisc; i++) {
            Vozlisce vozlisce = hashVrednost.get(i+1);

            if (vozlisce == null)
                continue;
            else {
                if (vozlisce.idLevi != -1) {
                    Vozlisce leviSin = hashId.get(vozlisce.idLevi);

                    vozlisce.levi = leviSin;
                    leviSin.stars = vozlisce;
                    leviSin.idStars = vozlisce.id;

                } if (vozlisce.idDesni != -1) {
                    Vozlisce desniSin = hashId.get(vozlisce.idDesni);
                    
                    vozlisce.desni = desniSin;
                    desniSin.stars = vozlisce;
                    desniSin.idStars = vozlisce.id;
                }
            }
        }

        return koren();
    }

    private static Vozlisce koren() {
        for (int i = 0; i < stVozlisc; i++) {
            Vozlisce vozlisce = hashVrednost.get(i+1);
            if (vozlisce.stars == null)
                return vozlisce;
        }
        return null;
    }

    private static void najdiKoordinate (Vozlisce vozlisce, String tip) {
        if (vozlisce == null)
            return;
        Vozlisce stars;
        
        if (tip.equals("koren")) {
            vozlisce.yKoordinata = 0;
            vozlisce.xKoordinata = velikostDrevesa(vozlisce.levi);

            najdiKoordinate(vozlisce.levi, "leviSin");
            najdiKoordinate(vozlisce.desni, "desniSin");
    
        } else if (tip.equals("leviSin")) {

            stars = vozlisce.stars;
            vozlisce.yKoordinata = stars.yKoordinata +1;
            vozlisce.xKoordinata = stars.xKoordinata - velikostDrevesa(vozlisce.desni) -1;

            najdiKoordinate(vozlisce.levi, "leviSin");
            najdiKoordinate(vozlisce.desni, "desniSin");

        } else if (tip.equals("desniSin")) {

            stars = vozlisce.stars;
            vozlisce.yKoordinata = stars.yKoordinata +1;
            vozlisce.xKoordinata = stars.xKoordinata + velikostDrevesa(vozlisce.levi) +1;

            najdiKoordinate(vozlisce.levi, "leviSin");
            najdiKoordinate(vozlisce.desni, "desniSin");
        }
    }

    private static int velikostDrevesa(Vozlisce vozlisce) {
        if (vozlisce == null)
            return 0;
        else
            return velikostDrevesa(vozlisce.levi) + velikostDrevesa(vozlisce.desni) +1;
    }
    
    private static void vVrsto (Vozlisce vozlisce) {
        if (vozlisce == null)
            return;
        
        Vrsta vrsta = new Vrsta(vozlisce, null);
        zadnji.naslednja = vrsta;
        zadnji = vrsta;
    }

    private static void izpisKonzola(Vozlisce vozlisce) {
        Vrsta vrsta = new Vrsta (vozlisce, null);
        zadnji = vrsta;

        for (; vrsta != null; vrsta = vrsta.naslednja) {
            System.out.println(vrsta.vozlisce.vrednost + "," + vrsta.vozlisce.xKoordinata + "," + vrsta.vozlisce.yKoordinata);
            vVrsto(vrsta.vozlisce.levi);
            vVrsto(vrsta.vozlisce.desni);
        }
    }

    private static void izpisDatoteka (Vozlisce vozlisce) {
        Vrsta vrsta = new Vrsta (vozlisce, null);
        zadnji = vrsta;

        for (; vrsta != null; vrsta = vrsta.naslednja) {
            izhod.println(vrsta.vozlisce.vrednost + "," + vrsta.vozlisce.xKoordinata + "," + vrsta.vozlisce.yKoordinata);
            vVrsto(vrsta.vozlisce.levi);
            vVrsto(vrsta.vozlisce.desni);
        }
    }

}

class Vozlisce {
    public int id;
    public int vrednost;

    public int idLevi;
    public int idDesni;
    public int idStars;

    public Vozlisce levi;
    public Vozlisce desni;
    public Vozlisce stars;

    public int xKoordinata;
    public int yKoordinata;

    public Vozlisce (int id, int vrednost, int idLevi, int idDesni) {
        this.id = id;
        this.vrednost = vrednost;
        this.idLevi = idLevi;
        this.idDesni = idDesni;
    }
}

class Vrsta {
    public Vozlisce vozlisce;
    public Vrsta naslednja;

    public Vrsta (Vozlisce vozlisce, Vrsta naslednja) {
        this.vozlisce = vozlisce;
        this.naslednja = naslednja;
    }
}