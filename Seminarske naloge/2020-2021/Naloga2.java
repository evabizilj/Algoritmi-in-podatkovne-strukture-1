import java.io.*;
import java.util.Scanner;

public class Naloga2
{
    static int numCards = 0;
    static int numShuffles = 0;
    static String D;
    static String V;
    static int S;
    public static void main(String[] args) throws IOException
    {
        InputStream in = System.in;
        if (args.length > 0)
            in = new FileInputStream(args[0]);
        
        PrintStream out = System.out;
        if (args.length > 1)
            out = new PrintStream(new FileOutputStream(args[1]));

        CardList K1 = new CardList();
        CardList K2 = new CardList();

        // branje podatkov
        try (Scanner sc = new Scanner(in); )
        {
            // podatke mejita vejica ali 1+ presledkov (vejica ALI nova vrstica: \, 1 ali več znakov)
            sc.useDelimiter(",|\\s+");
            
            numCards = sc.nextInt();
            numShuffles = sc.nextInt();

            // vse karte damo v kup K1
            for (int i = 0; i < numCards; ++i)
                K1.append(sc.next());

            // navodila za mešanje kart
            for (int i = 0; i < numShuffles; ++i)
            {
                if (!K2.isEmpty())
                    System.out.println("Napaka, K2 bi moral biti ob koncu mešanja prazen, ampak ni: " + K2);
                D = sc.next(); // delitelj kupa
                V = sc.next(); // mesto vstavljanja
                S = sc.nextInt(); // število kart, ki se vstavijo v eni iteraciji postopka (v enem vstavljanju)
                // System.out.printf("---\nNew test: %s %s %s\n", D, V, S);
                // System.out.println("New K1: " + K1);
                // System.out.println("New K2: " + K2);

                // iskanje karte (D), ki razdeli kup
                Card c = K1.search(D);
                // System.out.printf("Iskanje karte D: %s v K1, to je %s.\n", D, c == null ? "ni najdena" : "najdena");
                
                // karte D ni v kupu --> vse karte se premaknejo na K2, K1 ostane prazen
                if (c == null)
                {
                    K2.head = K1.head;
                    K1.head = null;
                    K1.tail = null;
                }
                // karta D je v kupu --> D je zadnja karta v kupu K1
                else
                {
                    K1.tail = c;
                    K2.head = c.next;
                }

                // deljenje kupa je končano

                // rep K2 mora nekam kazati (da ne izgubimo spremenljivke), ni pa pomembno kam

                K2.tail = K1.tail;

                // System.out.println("New2 K1: " + K1);
                // System.out.println("New2 K2: " + K2);

                // če obstaja D, potem K2.tail ne bo null
                if (K2.tail != null)
                    K2.tail.next = null;
                if (K2.head != null)
                    K2.head.prev = null;
                
                Card temp; 
                Card d;

                // mešanje dokler so v K2 še karte
                while (!K2.isEmpty())
                {
                    // System.out.println("---");
                    // System.out.println("Začetek K1: " + K1);
                    // System.out.println("Začetek K2: " + K2);
                    // System.out.println("Iskanje: " + V);

                    // iskanje V karte - določitev mesta vstavljanja v K1
                    c = K1.search(V); 
                    // karte V ni v kupu K1 --> S kart se vstavi na začetek K1 
                    if (c == null)
                    {   
                        temp = K1.head;
                        K1.head = K2.head;
                        d = K2.head;
                        // premaknemo se v kupu K2, da najdemo S-to karto ali pa konec kupa
                        for (int j = 1; j < S && d != null && d.next != null; ++j)
                            d = d.next;
                        if (temp != null)
                        {
                            temp.prev = d; // zadnjo karto ali S-to karto kupa K2 vstavimo pred prvo karto K1
                            K1.tail = d; // K1.tail nekam nastavimo
                        }
                        K2.head = d.next; // ostale karte ostanejo v kupu K2
                        d.next = temp; // vezava: temp.prev = d
                    }
                    // karta V je v kupu K1 --> S kart se vstavi v K1 za karto V
                    else
                    {
                        // 1 2 [3] 4 ...
                        // 5 6 7, S = 2
                        // 1 2 3 5 6 4 ... (temp = 4, d = 6)
                        temp = c.next; // del K1 --- V -- vstavimo del S kart K2 (-- d ---) --- temp
                        c.next = K2.head;
                        K2.head.prev = c; // vezava: c.next = K2.head;
                       
                        d = K2.head;
                        // premaknemo se v kupu K2, da najdemo S-to karto ali pa konec kupa
                        for (int j = 1; j < S && d != null && d.next != null; ++j)
                            d = d.next;

                        K2.head = d.next; // ostale karte ostanejo v kupu K2
                        d.next = temp; 
                        if (temp != null) // v primeru, da je v kupu K1 samo 1 karta
                            temp.prev = d; // vezava: d.next = temp
                    }
                    // System.out.println("Konec K1: " + K1);
                    // System.out.println("Konec K2: " + K2); // ob koncu mora biti prazen
                }
            }
        } 

        in.close(); // zapremo vhod

        out.println(K1);
        out.close();
    }

    // razred, ki predstavlja karto
    static class Card
    {
        public String value;
        public Card next;
        public Card prev;

        public Card(String c)
        {
            value = c;
        }   
    }

    // razred, ki predstavlja kup kart
    static class CardList
    {
        public Card head;
        public Card tail;

        public CardList()
        {
            head = null;
            tail = null;
        }

        public boolean isEmpty()
        {
            return head == null;
        }

        // formatiran izpis za rešitev
        public String toString()
        {
            String str = "";
            boolean first = true;
            for (Card c = head; c != null; c = c.next)
            {
                if (!first)
                    str += ",";
                str += c.value;
                first = false;
            }
            return str;
        }

        // pripnemo karto kupu
        public void append(String c)
        {
            Card card = new Card(c);
            if (isEmpty())
                head = card;
            card.next = null;
            card.prev = tail;
            tail = card;
            if (card.prev != null)
                card.prev.next = card;
        }

        // poiščemo karto v kupu
        public Card search(String c) 
        {
            for (Card cur = head; cur != null; cur = cur.next)
                if (cur.value.equals(c))
                    return cur;
            return null;
       }
    }
}
