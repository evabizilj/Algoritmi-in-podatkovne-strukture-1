import java.io.*;
import java.util.Scanner;

public class Naloga4
{
    public static void main(String[] args) throws IOException
    {
        InputStream in = System.in;
        if (args.length > 0)
            in = new FileInputStream(args[0]);

        PrintStream out = System.out;
        if (args.length > 1)
            out = new PrintStream(new FileOutputStream(args[1]));

        // tabela, ki vsebuje sezname vreč
        Bag[] bags = new Bag[100000];
            
        try (Scanner sc = new Scanner(in))
        {
            // podatke mejijo vejica ali dvopičje ali 1+ presledkov (vejica ALI : ALI nova vrstica: \, 1 ali več znakov)
            sc.useDelimiter(",|:|\\s+");

            // število ukazov
            int numInstructions = sc.nextInt();

            for (int i = 0; i < numInstructions; ++i)
            {
                // System.out.println("Bags:");
                // for (int j = 0; j < bags.length; ++j)
                //     if (bags[j] != null && !bags[j].isEmpty())
                //         System.out.printf("%d : %s\n", j, bags[j]);
                
                char operation = sc.next().charAt(0);
                int bagName1 = 0;
                int bagName2 = 0;
                int constant = 0;

                switch (operation)
                {
                case 'U':
                    bagName1 = sc.nextInt();
                    // System.out.printf("Ustvari vrečo %d.\n", bagName1);
                    bags[bagName1] = new Bag();
                    bags[bagName1].read(sc);
                    break;
                case 'Z':
                    bagName1 = sc.nextInt();
                    bagName2 = sc.nextInt();
                    bags[bagName1].join(bags[bagName2]);
                    // System.out.printf("Vreči %d pridruži %d.\n", bagName1, bagName2);
                    break;
                case 'R':
                    bagName1 = sc.nextInt();
                    bagName2 = sc.nextInt();
                    // System.out.printf("Iz vreče %d odstrani %d.\n", bagName1, bagName2);
                    bags[bagName1].remove(bags[bagName2]);
                    break;
                case 'S':
                    bagName1 = sc.nextInt();
                    bagName2 = sc.nextInt();
                    // System.out.printf("V vreči %d obdržimo skupne elemente z %d.\n", bagName1, bagName2);
                    bags[bagName1].intersect(bags[bagName2]);
                    break;
                case 'P':
                    bagName1 = sc.nextInt();
                    constant = sc.nextInt();
                    // System.out.printf("V vreči %d porežemo elemente do %d.\n", bagName1, constant);
                    bags[bagName1].keepMax(constant);
                    break;
                case 'O':
                    bagName1 = sc.nextInt();
                    constant = sc.nextInt();
                    // System.out.printf("V vreči %d ohranimo le elemente nad %d.\n", bagName1, constant);
                    bags[bagName1].keepMin(constant);
                    break;
                case 'I':
                    bagName1 = sc.nextInt();
                    // System.out.printf("Izpiši vrečo %d.\n", bagName1);
                    out.println(bags[bagName1]);
                    break;
                default:
                    System.out.println("Napaka.");
                }
            }
        
        } 

        in.close(); 

        // for (Bag bag : bags)
        //     if (bag != null && !bag.isEmpty())
        //         System.out.println(bag);
        out.close();
    }

    // razred, ki opisuje element vreče
    public static class Number
    {
        public int E; // element
        public int N; // število ponovitev elementa
        public Number next;
        public Number prev;

        public Number(int e, int n)
        {
            E = e;
            N = n;
        }

        public Number(Number that)
        {
            this.E = that.E;
            this.N = that.N;
        }

        // formatiran izpis za posamezen element
        public String toString()
        {
            return Integer.toString(E) + ":" + Integer.toString(N);
        }
    }

    // razred, ki opisuje podatkovno strukturo vreča
    public static class Bag
    {
        public Number head;
        public Number tail;

        public Bag()
        {
            head = null;
            tail = null;
        }

        public String toString()
        {
            String s = "";
            for (Number n = head; n != null; n = n.next)
            {                   
                s += n;
                if (n.next != null)
                    s += ",";
            }
            return s;
        }

        public boolean isEmpty()
        {
            return head == null;
        }

        // vstavljanje na začetek seznama
        public void prepend(Number n)
        {
            if (isEmpty())
                tail = n;
            n.prev = null;
            n.next = head;
            head = n;
            if (n.next != null)
                n.next.prev = n;
        }

        // vstavljanje na konec seznama
        public void append(Number n)
        {
            if (isEmpty())
                head = n;
            n.next = null;
            n.prev = tail;
            tail = n;
            if (n.prev != null)
                n.prev.next = n;
        }
        // vstavljanje vseh elementov na konec seznama
        public void appendAll(Number n)
        {
            for (; n != null; n = n.next)
                append(new Number(n));
        }

        // branje pri operaciji ustvari
        public void read(Scanner sc)
        {
            while (sc.hasNextInt())
            {
                int E = sc.nextInt();
                int N = sc.nextInt();
                // ustvarimo element
                Number num = new Number(E, N);
                // elemente uredimo oz. dodajamo v seznam v naraščajočem vrstnem redu -- glede na E (min --> max)
                // case 1: seznam prazen ali nov element manjši od začetka seznama --> novoustvarjen element vstavimo na začetek
                if (isEmpty() || this.head.E > E)
                    prepend(num);
                // case 2: nov element je enak elementu začetku seznama --> povečamo število ponovitev začetnega elementa
                else if (this.head.E == E)
                    this.head.N = N;
                // case 3: nov element je enak zadnjemu elementu seznama --> povečamo število ponovitev zadnjega elementa
                else if (this.tail.E == E)
                    this.tail.N = N;
                // case 4: nov element je večji od zadnjega elementa --> novoustvarjen element dodamo na konec seznama
                else if (this.tail.E < E)
                    append(num);
                else
                {
                    Number n = this.head;
                    // n: 1 3 5 19
                    // E: 13
                    // sprehajamo se čez seznam, dokler ne najdemo večjega elementa od novega (E), 14 < 13 ali pridemo do konca seznama
                    for (; n != null && n.E < E; n = n.next) {}
                    // če je element enak novemu elementu --> povečamo število ponovitev elementa
                    if (n.E == E)
                        n.N = N;
                    // prevezovanje kazalcev: 1 3 5 <--> 13 <--> 19
                    else
                    {
                        num.next = n.prev.next;
                        num.prev = n.prev;
                        num.prev.next = num;
                        num.next.prev = num;
                    }
                }
            }
        }
        
        // Iz vreče this vrečo that.
        public void remove(Bag that)
        {
            Number a = this.head;
            Number b = that.head;

            while (a != null && b != null)
            {
                if (a.E == b.E)
                {
                    // zmanjšamo število pojavitev elementa
                    a.N -= b.N;
                    // če smo odstranili vse pojavitve elementa, nimamo več tega elementa v vreči
                    if (a.N <= 0)
                        remove(a);
                    a = a.next;
                    b = b.next;
                }
                else if (b.E < a.E)
                {
                    b = b.next;
                }
                else // b.E > a.E
                {
                    a = a.next;
                }
            }
        }

        // pomožna metoda za odstranitev elemetov iz vreče, odstranimo element n
        public void remove(Number n)
        {
            if (n == head && n == tail)
            {
                head = null;
                tail = null;
            }
            else if (n == head)
            {
                head = n.next;
                if (head != null)
                    head.prev = null;
            }
            else if (n == tail)
            {
                tail = n.prev;
                if (tail != null)
                    tail.next = null;
            }
            else
            {
                n.prev.next = n.next;
                n.next.prev = n.prev;
            }
          
        }
         
        // Vreči a pridruži vrečo b (that)
        public void join(Bag that)
        {
            Number a = this.head;
            Number b = that.head;

            while (a != null || b != null)
            {
                if (a == null)
                {
                    // dodamo vse elemente b-ja k a-kju
                    appendAll(b);
                    break;
                }

                if (b == null)
                    break;

                // elementa obeh vreč sta enaka
                if (a.E == b.E)
                {
                    a.N += b.N;
                    a = a.next;
                    b = b.next;
                }
                // element vreče b je manjši od elementa vreče a
                else if (b.E < a.E)
                {
                    // 5 
                    // 3 9
                    // [3] 5 9
                    // b element vstavimo na začetek a-ja
                    if (a == head)
                        prepend(new Number(b));
                    else
                    {
                        // prevezovanje kazalcev
                        // 5 -8- 9
                        // 6
                        // 5 <--> 6 <--> 8 9
                        Number tmp = new Number(b);
                        a.prev.next = tmp; // 5 --> 6
                        tmp.next = a; // 6 --> 8
                        tmp.prev = a.prev; 
                        a.prev = tmp; // 8 --> 6
                    }
                    b = b.next;
                }
                else // a.E < b.E
                {
                    a = a.next;
                }
            }
        }

        // V vreči this obdržimo skupne elemente z vrečo that.
        public void intersect(Bag that)
        {
            Number a = this.head;
            Number b = that.head;

            while (a != null || b != null)
            {
                if (a == null)
                    break;

                // ni skupnih elementov (vreča that je prazna), zaključimo
                if (b == null)
                {
                    // gremo za en element nazaj, tam kjer še obstaja b (10 postane konec seznama)
                    // 8 9 10 [15]
                    // 1 3 4
                    tail = a.prev;
                    if (tail != null)
                        tail.next = null;
                    break;
                }

                // elementa sta enaka
                if (a.E == b.E)
                {
                    // število ponovitev elementa spremenimo na najmanjšo ponovitev elementa od obeh vreč
                    a.N = Math.min(a.N, b.N);
                    a = a.next;
                    b = b.next;
                }
                else if (b.E < a.E)
                {
                    b = b.next;
                }
                else // b.E > a.E
                {
                    // odstranimo element vreče this (ker to zagotovo ni skupen element)
                    remove(a);
                    a = a.next;
                }
            }
        }

        // V vreči porežemo elemente do neke konstante (v vreči so elementi, ki se ponovijo kvečjemu <constant>)
        public void keepMax(int constant)
        {
            for (Number n = head; n != null; n = n.next)
                n.N = Math.min(n.N, constant);
        }

        // V vreči ohranimo le elemente, ki se ponovijo nad neko konstanto.
        public void keepMin(int constant)
        {
            for (Number n = head; n != null; n = n.next)
                if (n.N < constant)
                    remove(n);
        }
    }
}