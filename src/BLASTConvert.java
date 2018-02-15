import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection; 
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


/*
The Process of creating the Homology Blocks from the Blast results is divided into three steps.
*/
public class BLASTConvert {

	public void run() throws IOException {

		
	File file = new File("Output/OPblast");

	  
    try{
	    PrintWriter writer = new PrintWriter("Output/BLASTConvertOPHS1.txt", "UTF-8");
	
	
	
	try (BufferedReader br = new BufferedReader(new FileReader(file))) {

		/*
		STEP 1: 
		PrintWriter's writer object creates a new text file called BLASTConvertOPHS1.txt to store the
		resuts from Step 1 of the Homology Creation. 
		BuffredReadres br object contains the resulting file of our BLAST algorithm.
		*/
		
	    String line;
	    
		/*
		STEP 1: 
		Creates two data structures, ll with typr LinkedList and hset with type HashSet
		*/
	    List<String> ll = new LinkedList<>();
	    Set<String> hset = new HashSet<>(); 
	  
	    while ((line = br.readLine()) != null) {
	    	
		    /*
			STEP 1: 
			Inside of a loop that reads each line inside of br and store into the line string "line = br.readLine()"
			*/
	    
	       String splitted[] = line.split("\\s+");

	        /*
			STEP 1: 
			Each line is splitted when ever it encounters a space and stored into an array of string named splitted. 
			splitted[0] contains name of left 
            Example : Blast O/P : NODE_1_length_4930_cov_3093.5	1	4930	NODE_1_length_4930_cov_3093.5	1	4930
					  splitted[0] = NODE_1_length_4930_cov_3093.5   splitted[3] = NODE_1_length_4930_cov_3093.5
			*/
	       
	       if(!splitted[0].equals(splitted[3])){
	    	   
	    	   /*
	    	   STEP 1:
	    	   Here we check if splitted[0] is equal to splitted[3] (i.e. Mapping to each other), if it is true, then we do
	    	   not add that line from the BLAST result to our Homology Block. */

	    	   if(!hset.contains("*" + splitted[0] + "    :    "+ splitted[3])) {
		    	   hset.add("*" + splitted[0] + "    :    "+ splitted[3]);
		    	   ll.add("*" + splitted[0] + "    :    "+ splitted[3]);
	    	   }
	    	   ll.add(splitted[1] +" "+ splitted[2] +"  :  "+  splitted[4] +" "+ splitted[5]);
	    	   
	       }
	       
	     
	    }
	    
	    ll.add("*"); 
	    
	    Iterator<String> it = ll.iterator();
	      while(it.hasNext()){
	    	  
	    	  writer.println(it.next());
	   
	      }
	    writer.close();
	    br.close();
	} 
    } catch (IOException e) {
 	   e.printStackTrace();
 	 
 	}

    /*STEP 1: Is Over, by now, our output of STEP 1 is stored at Output/BLASTConvertOPHS1.txt

    		We have converted the BLAST Output

    		NODE_1_length_4930_cov_3093.5	1	4930	NODE_1_length_4930_cov_3093.5	1	4930
			NODE_1_length_4930_cov_3093.5	4321	4487	NODE_18_length_437_cov_25.7277	437	273
			NODE_1_length_4930_cov_3093.5	2354	2475	NODE_19_length_420_cov_8.91507	420	300

			to 

			*NODE_1_length_4930_cov_3093.5    :    NODE_18_length_437_cov_25.7277
			4321 4487  :  437 273
			*NODE_1_length_4930_cov_3093.5    :    NODE_19_length_420_cov_8.91507
			2354 2475  :  420 300

			So far, we have remove the reads that map to themslves and chaged the formatting a little. 
    */

    
   // STEP 2 OF THE HOMOLOGY BLOCKS CREATION
	
	
	//The files that stores the results from STEP 1 is opened at file2
	File file2 = new File("Output/BLASTConvertOPHS1.txt");
	
	
   
	 
    try{
	    PrintWriter writer = new PrintWriter("Output/BLASTConvertOPHS2.txt", "UTF-8");
	
  
	
    try(BufferedReader br2 = new BufferedReader(new FileReader(file2))){
    	
    	String line = ""; 
    	
    	ArrayList<Integer> a = new ArrayList<>();
    	ArrayList<Integer> b = new ArrayList<>();
    	ArrayList<Integer> a1 = new ArrayList<>();
    	ArrayList<Integer> b1 = new ArrayList<>();

    	
    	
    	while((line = br2.readLine()) != null) {
    		
    		
    		
    		if(line.startsWith("*")) {

    	    int aa[] = convertIntegers(a);
    	    
    	    int ab[] = convertIntegers(b);
    	     
    	    int ba[] = convertIntegers(a1);
    	    
    	    int bb[] = convertIntegers(b1);

    	    ClassAlignment aln = new ClassAlignment();
    	 
    	    writer.println(aln.run(aa,ab)  + "   " + 	aln.run(ba, bb));
    	    
    	    writer.println(line);
    		  a.clear();
    		  b.clear();
    		  a1.clear();
    		  b1.clear();
    		  
    		} else {
    		
    		String splitted[] = line.split("\\s+");
    		
    	    a.add(Integer.parseInt(splitted[0]));
    	    b.add(Integer.parseInt(splitted[1]));
    	    a1.add(Integer.parseInt(splitted[3]));
    	    b1.add(Integer.parseInt(splitted[4]));
    	    
    		}
    	} 
    	
    	writer.close();
    	br2.close();
    } 
    } catch (IOException e) {
    	
  	   		e.printStackTrace();
  	   		
   	}  	
    	
   
	
  //Step 3 and Homology Creation
  
  
  //Creates a mapping of node to ATGC values
    
    Multimap<String, String> scaffMap = ArrayListMultimap.create();
    
    File file4 = new File("Output/scaffolds.fasta");
    
    try(BufferedReader br4 = new BufferedReader(new FileReader(file4))){
    	String line1; 
    	String key = ""; 
    	while((line1 = br4.readLine()) != null) {
    	
    		if(line1.startsWith(">")) {
    			key  = line1; 
    		} else {
    			scaffMap.put(key, line1);
    		}
    		
    	}
    } catch(IOException e) {
    	e.printStackTrace();
    }
    
    
    //Homlogy Creation
    
	File file5 = new File("Output/BLASTConvertOPHS2.txt");


	Multimap<String, String> multiMap = ArrayListMultimap.create();
	
    try(BufferedReader br3 = new BufferedReader(new FileReader(file5))){
    	String line; 
    	line = br3.readLine(); 
    	for(int i = 0; (line = br3.readLine())!=null; ++i) {
    		
    		if(!line.equals("\n") && !line.equals("*")) {
    			if(line.startsWith("*")) {
    			       String splitted[] = line.split("\\s+");
    			       String reads[] =  br3.readLine().split("\\s+"); 
    				   String key = (splitted[0].substring(1, splitted[0].length()));
    				   String value = (reads[0] + " : " + reads[2] + " " +  splitted[2] + " " + reads[3] + " : " + reads[5]);
    				   multiMap.put(key, value);
    			}
    		}
    	
    	}
    } catch(IOException e) {
    	e.printStackTrace();                                                        
 		   // do something
 		}
    
       int  i = 1; 
	
       new File("Output/HomologyBlocks").mkdir();
       
       ArrayList<Integer> x = new ArrayList<>();
       ArrayList<Integer> y = new ArrayList<>();
       
	   for(String key: multiMap.keySet()) {
		   
	    	
	    	try{
			    PrintWriter writer3 = new PrintWriter("Output/HomologyBlocks/"+i+".fasta", "UTF-8");
			  //  writer.println(key); 
			
	    	
	    	//writer.println("Homology Block #" + i);
	    	++i; 
	    
	    	Collection<String> values = multiMap.get(key); 
	    	Iterator<String> it = values.iterator(); 
	    	while(it.hasNext()) {
	    		String line2 = it.next();
	    		String splitted2[] = line2.split("\\s+");
	    	    
	    		//Addition of Read Lengths for Class Alignment
	    	    x.add(Integer.parseInt(splitted2[0]));
	    	    y.add(Integer.parseInt(splitted2[2]));
	    	    
	    	    
	    	    
	    	  /*  System.out.println(line2);
	    		
	    	    System.out.println(splitted2[3].substring(0, splitted2[3].length()));*/
	    	    
	    		printScaff(">" + splitted2[3].substring(0, splitted2[3].length()), scaffMap, splitted2[4], splitted2[6], writer3);
	 
	    	}
	    	
	    	ClassAlignment aln = new ClassAlignment();
	    	
	    	int ix[] = convertIntegers(x);
    	    
    	    int iy[] = convertIntegers(y);
	    	
    	    
	    	String keyRead = aln.run(ix, iy);
	    	
	    	String keySplit[] = keyRead.split("\\s+");
	    	
	    		    	
	    	printScaff(">" + key, scaffMap, keySplit[0], keySplit[2], writer3);
	    	
	    	x.clear();
	    	
	    	y.clear();
	    	
	    	writer3.close();
	    	} catch (IOException e) {
	 		   // do something
	    	}
	    } 
	}
	   
	  
	
	// Helper Functions
	   
	public static int[] convertIntegers(List<Integer> integers)
	{

		if(integers.isEmpty()) {
		    int[] re = new int[0];
			return re; 
		}

	    int[] ret = new int[integers.size()];
		
	    Iterator<Integer> iterator = integers.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
	    return ret;
	}
		
   
  
   
	
	
	 public static void printScaff(String key,  Multimap<String, String> scaffMap, String start, String end, PrintWriter writer7) {
		   
		 //System.out.print(key);
		    int s = Integer.parseInt(start); 
		    
		    //To start from index 0 
		    s--; 
		    
		    int e = Integer.parseInt(end); 
		   // writer3.println(s + " " + e);
		    writer7.println(key);
			
			Collection<String> values = scaffMap.get(key); 
		
			Iterator<String> it = values.iterator(); 
			String scaff = ""; 
			while(it.hasNext()) {
				scaff += it.next();
				
			}
			
			if(e > s) {
			
			writer7.print(scaff.substring(s, e)+ "\n");
			
			} else {

				StringBuilder rev = new StringBuilder();
				
				rev.append(scaff.substring(e, s)); 
				
			//	writer3.println(rev.toString());
				
				rev = rev.reverse();
				
			//	writer3.println(rev.toString());

				
				Map<Character, Character> revMap = new HashMap<>(); 
				revMap.put('A', 'T'); 
				revMap.put('T', 'A'); 
				revMap.put('G', 'C'); 
				revMap.put('C', 'G'); 

				
				for(int i = 0; i < rev.length(); ++i) {
					
					char ch = rev.charAt(i);
					
					rev.setCharAt(i, revMap.get(ch));
					
					
				}
				
				writer7.print(rev.toString() + "\n");
			}
			
			
		   	}
}