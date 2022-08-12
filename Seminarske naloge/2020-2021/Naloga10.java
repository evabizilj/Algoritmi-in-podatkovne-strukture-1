import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List; 

/*
Iz vmesnega (inorder) izpisa elementov binarnega drevesa sestavi drevo in izpiši njegove elemente po nivojih.

Ideja:
Maksimalni element je koren drevesa.
Elementi na levi strani maksimalnega elementa so levo poddrevo in
elementi na desni strani maksimalnega elementa so desno poddrevo.

Naloga vsebuje:
    - Node
    - BinaryTree
*/

public class Naloga10
{
    // vmesni (inorder) izpis elementov binarnega drevesa
    static int inorder[];
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
    
        // podatki shranjeni v ArrayListu namesto v arrayu, ker je količina podatkov neznana
        List<Integer> inorder = new ArrayList<Integer>();
    
        // branje podatkov
        try (Scanner sc = new Scanner(in))
        {
            sc.useDelimiter(",|\\s+");
            // dodajanje števil oz. točk drevesa v arrayList
            while (sc.hasNextInt())
               inorder.add(sc.nextInt());
        }

        BinaryTree tree = new BinaryTree();
        
        Node node = tree.build(inorder, 0, inorder.size() - 1, tree.root); 
        
        // izpis rešitve
        tree.printLevelOrder(node, out); 

        out.println();

        // zapremo vhod
        in.close();

        // zapremo izhod
        out.close();
    }

    // razred Node
    static class Node 
    {
        int data;
        Node left; // levi sin
        Node right; // desni sin

        Node (int value)
        {
            this.data = value;
            this.left = null;
            this.right = null;
        }
    }
    
    // razred BinaryTree
    static class BinaryTree  
    { 
        Node root; 
      
        Node build(List<Integer> inorder, int start, int end, Node node)  
        { 
            // 1. robni pogoj
            if (start > end) 
                return null; 
            
            // maksimalni element
            int i = max(inorder, start, end); 
      
            node = new Node(inorder.get(i)); // poiščem maksimalni element v arrayu ("koren")
    
            // če je to edini element, ga vrni
            // 2. robni pogoj
            if (start == end) 
               return node; 

            // konstrukcija levega in desnega poddrevesa
            node.left = build(inorder, start, i - 1, node.left); // zmanjšujem end (gremo v levo)
            node.right = build(inorder, i + 1, end, node.right); // povečujem začetek (gremo v desno)

            /*
            7, 5, 10, 3, 2, 17, 1, 12, 14
            koren (max. element) = 17
            levo poddrevo: 7, 5, 10, 3, 2 --> 10, 7, 3, 2
            desno poddrevo: 1, 12, 14 --> 14, 12, 1
            */
            
            return node; 
        } 
      
        // iskanje maksimalnega elementa
        int max(List<Integer> array, int start, int end)  
        { 
            int max = array.get(start);
            int maxi = start;
            for (int i = start + 1; i <= end; ++i)
            {
                if (array.get(i) > max)
                {
                    max = array.get(i);
                    maxi = i;
                }
            }
            return maxi;
        } 

        // izračun višine drevesa
        int height(Node node) 
        {
		    if (node == null)
			    return 0;
		    return Math.max(height(node.left), height(node.right)) + 1;
	    }

        // pomožna metoda za izračun višine drevesa
        int height() 
        {
		    return height(root);
        }
        
        // izpis drevesa po nivojih
        void printLevelOrder(Node root, PrintStream out)
        {
            List<Integer> numbers = levelOrder(root);
            for (int i = 0; i < numbers.size(); ++i)
                out.printf("%s%d", i > 0 ? "," : "", numbers.get(i));
        }

        // pomožna metoda za izpis po nivojih
        List<Integer> levelOrder(Node root) 
        {
            List<Integer> numbers = new ArrayList<Integer>();
            for (int i = 1; i <= height(root); i++) 
                numbers.addAll(givenLevel(root, i));
            return numbers;
        }

        // pomožna metoda za dani nivo
        List<Integer> givenLevel(Node root, int level) 
        {
            List<Integer> numbers = new ArrayList<Integer>();
            if (root == null || level < 1)
                return numbers;

            if (level == 1)
                numbers.add(root.data);
            else
            {
                numbers.addAll(givenLevel(root.left, level - 1));
                numbers.addAll(givenLevel(root.right, level - 1));
            }
            return numbers;
        } 
    }
}

	