import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

/*
Imamo množico N-točk (x, y) v 2D prostoru, ki jih razdelimo v K skupin (glede na maksimalno oddaljenost med skupinami).

Ideja:
Na začetku vsaka točka predstavlja svojo skupino, nato združujemo najbližje skupine dokler ne dobimo zahtevanega števila skupin.

Uporaba Set (TreeSet), urejenost skupin in točk po naraščajočem vrstnem redu.

Naloga vsebuje: 
    - Solution (TreeSet<Group>)
    - Group (TreeSet<Point>)
    - Point
*/

public class Naloga9
{
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

        // branje podatkov
        try (Scanner sc = new Scanner(in))
        {
            sc.useDelimiter(",|\\s+"); 

            // število točk
            int numPoints = sc.nextInt();

            Solution solution = new Solution();

            for (int i = 1; i <= numPoints; ++i) // id točk se začne z 1 in ne 0 !
            {
                // preberemo koordinate vsake točke
                double x = sc.nextDouble();
                double y = sc.nextDouble();

                // točka (idTočke, 1. koordinata, 2. koordinata)
                Point p = new Point(i, x, y);

                // skupina (indeksSkupine, točka) ... na začetku je vsaka točka v svoji skupuni
                Group g = new Group(i, p);

                // dodajanje skupin v TreeSet
                solution.addGroup(g);
            }

            // število zahtevanih skupin
            int numGroups = sc.nextInt();

            // združujemo najbližje skupine, dokler ne dobimo zahtevanega števila skupin ... št. združitev: št. točk - št. zahtevanih skupin
            for (int i = 0; i < numPoints - numGroups; ++i)
                solution.mergeMin();

            /*
            K vrstic, v vsaki izpisane oznake točk (v naraščajočem vrstnem redu), ki pripadajo isti skupini.
            Vrstni red skupin urejen naraščajoče glede na najmanjšo oznako točke, ki pripada skupini.  
            --- potrebna uporaba TreeSeta, da zagotovimo "urejenost"
            */

            out.print(solution);
    
        } // konec branja podatkov

        // zapremo vhod
        in.close(); 

        // zapremo izhod
        out.close();
    
    }

    // razred Solution, ki vsebuje TREESET skupin
    public static class Solution
    {
        Set<Group> groups;

        public Solution()
        {
            this.groups = new TreeSet<Group>();
        }

        // dodajanje skupine
        public void addGroup(Group other)
        {
            this.groups.add(other);
        }

        // združevanje najbližjih skupin
        public void mergeMin()
        {
            double minDistance = Double.MAX_VALUE;
            Group a = null;
            Group b = null;
            
            // sprehanje po 2 skupinah
            for (Group g1 : this.groups)
            {
                for (Group g2 : this.groups)
                {
                    // točke so urejene po id-jih --> s tem zmanjšamo količino preiskovanja
                    if (g1.getGroupID() >= g2.getGroupID())
                        continue;
                    
                    double distance = g1.distanceTo(g2);
                    
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        a = g1;
                        b = g2;
                    }   
                }
            }
            // izbrišemo skupini a, b
            this.groups.remove(a); 
            this.groups.remove(b);
            
            // naredimo novo skupino, kjer so točke iz skupine a in b
            Group g = new Group(a.getGroupID(), a, b);
            this.addGroup(g);
        }

        // formatiran izpis rešitve (kliče metodo group.toString za izpis točk v skupini)
        public String toString()
        {
            String sol = "";
            for (Group group : this.groups)
                sol += group.toString() + "\n";
            return sol;
        }
    }

    // razred Group, ki vsebuje TREESET točk
    public static class Group implements Comparable<Group> // potrebujemo Comparable, da lahko primerjamo točke v skupinah (da je možna uporaba TreeSet-a)
    {
        int id;
        Set<Point> points;

        // ustvarjanje skupine: na začetku je v skupini samo 1 točka
        public Group(int id, Point p)
        {
            this.id = id;
            points = new TreeSet<Point>();
            points.add(p);
        }

        // združevanje skupin
        public Group(int id, Group a, Group b)
        {
            this.id = id;
            this.points = new TreeSet<Point>();
            this.points.addAll(a.points);
            this.points.addAll(b.points);
        }

        public int getGroupID()
        {
            return id;
        }

        // izračun razdalje med skupinami (v obeh skupinah poišči najbližjo točko)
        public double distanceTo(Group other)
        {
            double minDistance = Double.MAX_VALUE;
            for (Point point1 : points)
                for (Point point2 : other.points)
                    minDistance = Math.min(minDistance, point1.distanceTo(point2));
            return minDistance;
        }

        // formatiran izpis rešitve (naraščajoč vrstni red točk v skupini)
        public String toString()
        {
            String sol = "";
            boolean first = true;
            for (Point point : points)
            {
                if (!first)
                    sol += ',';
                sol += point.getPointID();
                first = false;
            }
            return sol;
        }

        /* Java Comparable interface in metoda public int compareTo(Object obj) vrne:
                1 ... trenutni objekt je večji od specifičnega objekta
              - 1 ... trenutni objekt je manjši od specifičnega
                0 ... objekta sta enaka
        */
        public int compareTo(Group other)
        {
            // iterator.next() ... dobimo 1. element iz Set-a
            Point a = this.points.iterator().next();
            Point b = other.points.iterator().next();
            return a.compareTo(b);
        }
    }

    public static class Point implements Comparable<Point> // potrebujemo Comparable, da lahko primerjamo točke med seboj
    {
        int id;
        double x;
        double y;

        public Point(int id, double x, double y)
        {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public int getPointID()
        {
            return this.id;
        }

        // izračun evklidske razdalje med točkama this in other
        public double distanceTo(Point other)
        {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        }

        /* Java Comparable interface in metoda public int compareTo(Object obj) 
        */
        public int compareTo(Point other)
        {
            return Integer.compare(this.id, other.id);
        }
    }
}