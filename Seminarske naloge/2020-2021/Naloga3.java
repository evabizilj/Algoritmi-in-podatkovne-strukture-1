import java.io.*;
import java.util.Scanner;

public class Naloga3
{
    public static void main(String[] args) throws IOException
    {
        InputStream in = System.in;
        if (args.length > 0)
            in = new FileInputStream(args[0]);

        PrintStream out = System.out;
        if (args.length > 1)
            out = new PrintStream(new FileOutputStream(args[1]));
        
        NumberList number = new NumberList();

        // branje podatkov
        try (Scanner sc = new Scanner(in); )
        {
            // podatke mejita vejica ali 1+ presledkov (vejica ALI nova vrstica: \, 1 ali več znakov)
            sc.useDelimiter(",|\\s+");
            
            // preberem seznam
            String[] line = sc.nextLine().split(",");
            for (int i = 0; i < line.length; ++i)
                number.append(Integer.parseInt(line[i]));
            int numOperations = sc.nextInt();

            for (int i = 0; i < numOperations; ++i)
            {
                char operation = sc.next().charAt(0);
                char operator;
                int val;
                switch (operation)
                {
                    // operation = ohrani
                case 'o':
                    operator = sc.next().charAt(0);
                    val = sc.nextInt();
                    number.ohrani(operator, val);
                    out.println(number);
                    break;
                    // operation = preslikaj
                case 'p':
                    operator = sc.next().charAt(0);
                    val = sc.nextInt();
                    number.preslikaj(operator, val);
                    out.println(number);
                    break;
                case 'z':
                    // operation = zdruzi
                    operator = sc.next().charAt(0);
                    number.zdruzi(operator);
                    out.println(number);
                    break;
                default:
                    System.out.println("Napaka.");
                }
            }
        } 

        in.close(); 
        out.close();
    }

    // razred, ki opisuje elemente - števila
    static class Number
    {
        public int value;
        public Number next;
        public Number prev;

        public Number(int n)
        {
            value = n;
        }
    }

    // razred, ki opisuje seznam sestavljen iz števil
    static class NumberList
    {
        public Number head;
        public Number tail;

        public NumberList()
        {
            head = null;
            tail = null;
        }

        public boolean isEmpty()
        {
            return head == null;
        }

        // seznamu pripnemo število
        public void append(int n)
        {
            Number number = new Number(n);
            if (isEmpty())
                head = number;
            number.next = null;
            number.prev = tail;
            tail = number;
            if (number.prev != null)
                number.prev.next = number;
        }

        // Vsakemu elementu seznama bodisi prišteje vrednost val bodisi ga z val pomnoži.
        public void preslikaj(char op, int val)
        {
            for (Number n = head; n != null; n = n.next)
            {
                if (op == '+')
                    n.value = n.value + val;
                else
                    n.value = n.value * val;
            }
        }
           
        // Obdržimo le tiste elemente seznama, ki so večji, manjši oziroma enaki parametru val.
        public void ohrani(char op, int val)
        {
            for (Number n = head; n != null; n = n.next)
            {
                // obdržimo
                boolean keep = true;
                switch (op)
                {
                case '>':
                    keep = n.value > val;
                    break;
                case '<':
                    keep = n.value < val;
                    break;
                case '=':
                    keep = n.value == val;
                    break;
                }
                if (keep)
                    continue;
                
                // brisanje elementov
                if (head == n && tail == n)
                {
                    head = null;
                    tail = null;
                }
                else if (head == n)
                {
                    head = head.next;
                    head.prev = null;
                }
                else if (tail == n)
                {
                    tail = tail.prev;
                    tail.next = null;
                }
                else
                {
                    n.prev.next = n.next;
                    n.next.prev = n.prev;
                }
            }
        }

        // Bodisi sešteje bodisi zmnoži vse elemente seznama, v odvisnosti od vrednosti parametra op ('+' ali '*'). 
        // Ob zaključku seznam vsebuje en sam element, ki predstavlja rezultat operacije (seštevek ali zmnožek elementov).
        public void zdruzi(char op)
        {
            Number sumResult = new Number(0);
            Number mulResult = new Number(1);
            for (Number n = head; n != null; n = n.next)
            {
                sumResult.value += n.value;
                mulResult.value *= n.value;
            }
            switch (op)
            {
            case '+':
                head = sumResult;
                tail = sumResult;
                break;
            case '*':
                head = mulResult;
                tail = mulResult;
                break;
            }
        }

        // formatiran izpis za rešitev
        public String toString()
        {
            String str = "";
            boolean first = true;
            for (Number n = head; n != null; n = n.next)
            {
                if (!first)
                    str += ",";
                str += n.value;
                first = false;
            }
            return str;
        }
    }
}