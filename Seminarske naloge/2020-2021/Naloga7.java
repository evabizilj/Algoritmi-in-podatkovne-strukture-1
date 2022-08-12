import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;

/*
Ideja
Najprej ustvarimo graf z mesti in povezavami.

Izleta sta različna, če množici obiskanih mest nista enaki.

Uporaba algoritma BFS (Breadth-first search). Začnemu v korenu -- na začetku -- startCity, uporaba vrste.
Izlete hranimo v vrsti.

Vsak nedokončan izlet vzamemo iz vrste, ga razširimo z vsemi možnimi razširjenimi izleti in vstavimo nazaj v vrsto.
Slej kot prej nam takih izletov zmanjka, saj
    - nas izleti vodijo do izhodišča, kjer jih zaključimo
    - nam zmanjka mest za razširitev
    - nam zmanjka goriva za širitev
tako je naš iskalni prostor končen in se zanka vedno zaključi.

Uporaba: Queue (ArrayDeque) - BFS, HashSet (hitrost, vrstni red ni pomemben), HashMap (shranimo elemente glede na ključ (hitrejše iskanje))

Naloga vsebuje: 
    - Road
    - City (ArrayList<Road>)
    - Graph (HashMap<Integer, City>)
    - Trip (HashSet ... cities).
*/

public class Naloga7
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
        
        Graph graph = new Graph();
        City startCity; // izlet zaključimo v izhodiščnem mestu
        double maxDistance; // količina goriva, ki ga imamo

        // branje podatkov
        try (Scanner sc = new Scanner(in))
        {
            sc.useDelimiter(",|\\s+");
            int numRoads = sc.nextInt();

            for (int i = 0; i < numRoads; ++i)
            {
                int cityIdA = sc.nextInt();
                int cityIdB = sc.nextInt();
                double distance = sc.nextDouble(); // razdalja med cityA in cityB
                
                // postopoma gradimo graf
                graph.addRoad(cityIdA, cityIdB, distance);
            }

            // graf je konstruiran

            int startCityId = sc.nextInt();
            
            // grafu dodamo še začetno (in končno mesto)
            startCity = graph.getCity(startCityId);
            maxDistance = sc.nextDouble();

        } // konec branja podatkov

        /*
        Vrsta izletov, ki jih še lahko opravimo
            uporaba vrste:
            začnemo pri začetnem vozlišču (startCity) in v vrsto dodamo sosedna vozlišča (vozlišča z "razdaljo" 1), ... naslednji element (izlet) iz vrste in ga postavimo na konec vrste
            BFS išče vozlišča glede na oddaljenost od vozlišča.
            Vsak nedokončan izlet vzamemo iz vrste, ga razširimo z vsemi možnimi razširjenimi izleti in vstavimo nazaj v vrsto.
            Slej kot prej nam takih izletov zmanjka, saj
                - nas izleti vodijo do izhodišča, kjer jih zaključimo
                - nam zmanjka mest za razširitev
                - nam zmanjka goriva za širitev
            tako je naš iskalni prostor končen in se zanka vedno zaključi.
        */

        // ArrayDeque ... posebna oblika growable tabele, ki dovoli, da dodajamo in brišemo elemente iz obeh strani 
        Queue<Trip> trips = new ArrayDeque<>();
        
        // množica izletov, ki se zaključijo v izhodiščnem mestu
        // uporaba HashSet: hitrost, vrstni red ni pomemben
        Set<Trip> finishedTrips = new HashSet<>();
        
        // prvi in edini izlet, ki ga lahko opravimo, se začne in zaključi v startCity
        trips.add(new Trip(startCity));
        
        while (!trips.isEmpty())
        {
            // vrne in izbriše prvi nedokončani izlet (first: startCity)
            Trip trip = trips.poll();

            Trip newTrip = null;

            // vsak nezaključen izlet lahko razširimo z vsemi povezavami (cestami), ki vodijo iz njegovega ciljnega mesta (trip.end.roads) --> na konec vrste dodamo nedokončane izlete 
            for (Road road : trip.end.roads)
            {
                // izlet podaljšamo preko ceste, ki pelje do mesta (road.to)
                newTrip = trip.visit(road.to, road.distance);

                // ne obiščemo že obiskanih mest, predolge izlete preskočimo, če smo mesto že obiskali (trip.visit == null)
                if (newTrip == null || newTrip.distance > maxDistance)
                    continue;
                
                // zaključimo izlet, ko končamo v izhodiščnem mestu
                if (newTrip.start == newTrip.end)
                    finishedTrips.add(newTrip);
                // sicer ga dodamo med nedokončane (dodamo na konec vrste)
                else
                    trips.add(newTrip);
            }
        }

        // zapremo vhod
        in.close();

        // izpis števila končanih izletov v izhodiščnem mestu
        out.println(finishedTrips.size());

        // zapremo izhod
        out.close();
    
    }

    // razred Road 
    public static class Road
    {
        public City from;
        public City to;
        public double distance;

        public Road(City a, City b, double d)
        {
            this.from = a;
            this.to = b;
            this.distance = d;
        }
    }

    // razred City
    public static class City
    {
        public int cityId;
        List<Road> roads; // seznam vseh cest, ki so povezane z mestom (this.cityId)

        public City(int id)
        {
            this.cityId = id;
            this.roads = new ArrayList<Road>();
        }

        // mestu dodamo cesto
        public void addRoad(Road r)
        {
            roads.add(r);
        }
    }
    
    // razred Graph
    // HashMap (value, key) ... shranimo elemente glede na ključ (hitrejše iskanje)
    public static class Graph
    {
        Map<Integer, City> cities;

        public Graph()
        {
            this.cities = new HashMap<Integer, City>();
        }

        // dodajanje povezav oz. cest
        public void addRoad(int cityA, int cityB, double distance)
        {
            // če tega mesta še ne poznamo ga ustvarimo v mapi, drugače pa ga pa poiščemo (key, function)
            City a = this.cities.computeIfAbsent(cityA, k -> new City(k));
            City b = this.cities.computeIfAbsent(cityB, k -> new City(k));

            // ustvarimo dvosmerne povezave
            a.addRoad(new Road(a, b, distance));
            b.addRoad(new Road(b, a, distance));
        }

        // dobimo "kazalec" na mesto
        public City getCity(int cityId)
        {
            return this.cities.get(cityId);
        }
    }


    /* HashSet
        - metoda equals() za preverjanje enakosti 
        - metoda hashCode() - da ni podvojenih vrednosti v naši množici
    */

    // razred Trip
    public static class Trip
    {
        public City start;
        public City end;
        public double distance;
        Set<Integer> cities; // množica obiskanih mest na izletu

        public Trip(City start)
        {
            this.start = start;
            this.end = start;
            this.distance = 0; // na začetku 0
            this.cities = new HashSet<Integer>();
        }

        // primerjava enakosti izletov
        // dva izleta sta enaka, če obiščeta enako množico mest ne glede na prevoženo razdaljo
        @Override
        public boolean equals(Object o)
        {
            if (o == this)
                return true; 
            if (!(o instanceof Trip))
                return false;
            
            Trip trip = (Trip) o;
            return this.cities.equals(trip.cities);
        }

        // za učinkovito štetje različnih izletov uporabimo HashSet<Trip>
        @Override
        public int hashCode()
        {
            return cities.hashCode(); // ker imajo mesta različne id-je, lahko uporabimo hash funkcijo nad mesti
        }

        // podaljšanje izleta preko mesta to
        public Trip visit(City to, double d)
        {
            // obiskanih mest ne obiščemo ponovno ... mesto obiščemo največ enkrat
            // izlet do mesta <to> tako ni možen, saj smo ga na poti že obiskali
            if (this.cities.contains(to.cityId))
                return null;

            // vsak razširjeni izlet začnemo v izhodiščnem mestu
            Trip trip = new Trip(start);
            trip.end = to;
            trip.distance = this.distance + d; // že prevožena radalja + razdalja, ki jo naredimo z podaljšanjem izleta

            // podaljšani izlet vsebuje dosedanja mesta
            trip.cities.addAll(this.cities);

            // dodamo še novo ciljno mesto
            trip.cities.add(to.cityId);
            
            return trip;
        }
    }
}

