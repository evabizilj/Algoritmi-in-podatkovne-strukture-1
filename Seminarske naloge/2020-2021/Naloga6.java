import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

/*
Vozniki > vozila
Vsak voznik vozil vsaj eno vozilo.
Vsako vozilo je bilo uporabljeno vsaj enkrat.

Ideja:
Imamo 2 množici (vozniki, vozila), katera je katera, ugotovimo na koncu (množica voznikov je večja).
Ob branju vhodnih podatkov (parčkov) vsak par shranimo v obstoječo ali novo particijo (malo množico).
Na koncu particije združimo, tako da nam ostaneta 2 množici.
Večja množica predstavlja rešitev.

Naloga vsebuje:
    - Partition (HashSet)
    - Solution (ArrayList)
*/

public class Naloga6 
{
    static int numPairs = 0;
    public static void main(String[] args) throws IOException
    {
        // priprava vhoda
        InputStream in = System.in;
        if (args.length > 0)
            in = new FileInputStream(args[0]);
        
        // priprava izhoda
        PrintStream out = System.out;
        if (args.length > 1)
            out = new PrintStream(new FileOutputStream(args[1]));
        
        Solution sol = new Solution();
        
        // branje podatkov
        try (Scanner sc = new Scanner(in))
        {
            sc.useDelimiter("-|\\s+");

            numPairs = sc.nextInt();
            for (int i = 0; i < numPairs; ++i)
            {
                // voznik ali vozilo (vrstni red ni določen)
                String a = sc.next();
                String b = sc.next();
                
                // par shranimo v obstoječo particijo (del para znan) ali novo particijo (noben od para ni znan)
                sol.addPair(a, b);
            }
            
            // izpis rešitve
            sol.printFinal(out);
    
        } // konec branja podatkov
        catch (Exception ex)
        {
            // naloga ni rešljiva
            out.println("-1");
        }

       // zapremo vhod
       in.close(); 

       // zapremo izhod
       out.close();
    }

    /*  Hrani podatke o parih (uporaba HashSet)
        naloga: elementi v istem paru ne morejo biti istega tipa (to se ne more zgoditi voznik - voznik)
        ne rešuje: dvoumnosti ... pričakuje vsaj en znan člen v paru (del para se je že pojavil)

        Primer
        setA  setB
        |a|  |b| ... 1. particija
        |c|  |d| ... 2. particija
        
        Če se pojavi npr. |a| |d| se potem združi v |a| |b, d|.
    */

    // Set ... interface, HashSet ... implementation of interface
    public static class Partition
    {
        /// Uporaba SET ... ne želimo, da se elementi ponavljajo
        public Set<String> setA;
        public Set<String> setB;
        
        public Partition(String a, String b)
        {
            // Uporaba HASHSET ... hitro iskanje in dodajanje, množica kjer elementi niso urejeni
            setA = new HashSet<String>();
            setB = new HashSet<String>();
            setA.add(a);
            setB.add(b);
        }

        // dodajanje parov v množice/particije
        public boolean maybeAddPair(String a, String b) throws Exception
        {
            // prišlo je do protislovja v vhodnih podatkih
            // množica vsebuje tako voznike kot vozila, vozilo1 = vozilo1 ali voznik1 = voznik1
            if (setA.contains(a) && setA.contains(b) || setB.contains(a) && setB.contains(b) || a.equals(b))
                throw new Exception("Contradiction in input: " + a + " " + b);

            // par že poznamo (zaradi pogoja zgoraj vemo, da sta a in b različna)
            if (setA.contains(a) && setB.contains(b) || setB.contains(a) && setA.contains(b))
                return true;

            /* če imamo vsaj enega od para, si zapomnimo še drugega
               |a|  |b| ... 1. particija
               na vhodu a, d
               |a| |b, d| ... združimo v prvo particijo
            */

            if (setA.contains(a))
            {
                setB.add(b);
                return true;
            }
            if (setA.contains(b))
            {
                setB.add(a);
                return true;
            }
            if (setB.contains(a))
            {
                setA.add(b);
                return true;
            }
            if (setB.contains(b))
            {
                setA.add(a);
                return true;
            }

            return false;            
        }

        // testni izpis ...
        void print(PrintStream out)
        {
            out.println("Množica a:");
            for (String s : setA)
                out.println(s);
            out.println("Množica b:");
            for (String s : setB)
                out.println(s);
        }

        // izpišemo večjo množico, ki bo predstavljala voznike
        void printLargerSet(PrintStream out) throws Exception
        {
            if (setA.size() == setB.size())
                throw new Exception("Napaka. Množici ne moreta biti enaki ... navodila naloge.");
            Set<String> larger = setA.size() > setB.size() ? setA : setB;
            // sprehod po večji množici
            for (String s : larger)
                out.println(s);
        }

        // Združevanje particij/množic
        // Če se pojavi npr. |a| |d| se potem združi v |a| |b, d|.
        void mergeWith(Partition other, String a, String b)
        {
            Set<String> thisSetA; // tej množici pridružimo druge množice
            Set<String> thisSetB;
            Set<String> otherSetA; 
            Set<String> otherSetB;

            if (this.setA.contains(a))
            {
                thisSetA = setA;
                thisSetB = setB;
            }
            else
            {
                thisSetA = setB;
                thisSetB = setA;
            }
            if (other.setA.contains(a))
            {
                otherSetA = other.setA;
                otherSetB = other.setB;
            }
            else
            {
                otherSetA = other.setB;
                otherSetB = other.setA; 
            }

            // vse dodamo v thisSet
            thisSetA.addAll(otherSetA); 
            thisSetB.addAll(otherSetB);
        }
    }

    // skupek vseh znanih particij, z združevanjem rešujemo dvoumnosti (kam kateri niz paše)
    public static class Solution
    {
        List<Partition> partialSolutions;

        public Solution()
        {
            partialSolutions = new ArrayList<Partition>();
        }

        // par shranimo v obstoječo (del para znan) ali novo particijo (noben od para ni znan)
        void addPair(String a, String b) throws Exception
        {
            // seznam particij
            List<Partition> addedTo = new ArrayList<Partition>();
            for (Partition s : partialSolutions)
                if (s.maybeAddPair(a, b))
                    addedTo.add(s);

            // seznam particij je prazen
            if (addedTo.isEmpty())
            {
                partialSolutions.add(new Partition(a, b));
                return;
            }

            // združevanje
            Partition targetPartition = addedTo.get(0); // vedno vzamemo prvi element (množico) in pogledamo, če lahko združimo z drugo
            for (int i = addedTo.size() - 1; i > 0; --i)
            {
                Partition sourcePartition = addedTo.get(i);
                targetPartition.mergeWith(sourcePartition, a, b);
                partialSolutions.remove(sourcePartition); // uporabljen množico (particijo) izbrišemo
            }
        }

        // izpis rešitve (oznake voznikov)
        void print(PrintStream out)
        {
            for (Partition p : partialSolutions)
                p.print(out);
        }

        // končni izpis rešitve (izpišemo večjo množico)
        void printFinal(PrintStream out) throws Exception
        {
            for (Partition p : partialSolutions)
                p.printLargerSet(out);
        }
    }
}