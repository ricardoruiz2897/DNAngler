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
The Process of creating the Homology Blocks from the Blast results is divided
into three steps.
*/
public class BLASTConvert {

  public void run() throws IOException {

    File file = new File("Output/OPblast");

    /*
    STEP 1 of the Hommology Blocks aims to eliminate reads that map to
    themselves.
    */

    try {
      PrintWriter writer =
          new PrintWriter("Output/BLASTConvertOPHS1.txt", "UTF-8");

      try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        /*
        STEP 1:
        PrintWriter's writer object creates a new text file called
        BLASTConvertOPHS1.txt to store the
        resuts from Step 1 of the Homology Creation.
        BuffredReadres br object contains the resulting file of our BLAST
        algorithm.
        */

        String line;

        /*
        STEP 1:
        Creates two data structures, ll with typr LinkedList and hset with type
        HashSet
        */
        List<String> ll = new LinkedList<>();
        Set<String> hset = new HashSet<>();

        while ((line = br.readLine()) != null) {

          /*
              STEP 1:
              Inside of a loop that reads each line inside of br and store into
             the line string "line = br.readLine()"
              */

          String splitted[] = line.split("\\s+");

          /*
                  STEP 1:
                  Each line is splitted when ever it encounters a space and
      stored into an array of string named splitted.
                  splitted[0] contains name of left
      Example : Blast O/P : NODE_1_length_4930_cov_3093.5	1	4930
      NODE_1_length_4930_cov_3093.5	1	4930
                                    splitted[0] = NODE_1_length_4930_cov_3093.5
      splitted[3] = NODE_1_length_4930_cov_3093.5
                  */

          if (!splitted[0].equals(splitted[3])) {

            /*
            STEP 1:
            Here we check if splitted[0] is equal to splitted[3] (i.e. Mapping
            to each other), if it is true, then we do
            not add that line from the BLAST result to our Homology Block. */

            if (!hset.contains("*" + splitted[0] + "    :    " + splitted[3])) {
              hset.add("*" + splitted[0] + "    :    " + splitted[3]);
              ll.add("*" + splitted[0] + "    :    " + splitted[3]);
            }
            ll.add(splitted[1] + " " + splitted[2] + "  :  " + splitted[4] +
                   " " + splitted[5]);
          }
        }

        ll.add("*");

        Iterator<String> it = ll.iterator();
        while (it.hasNext()) {

          writer.println(it.next());
        }
        writer.close();
        br.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    /*STEP 1: Is Over, by now, our output of STEP 1 is stored at
       Output/BLASTConvertOPHS1.txt

                We have converted the BLAST Output

                NODE_1_length_4930_cov_3093.5	1	4930
       NODE_1_length_4930_cov_3093.5	1	4930
                        NODE_1_length_4930_cov_3093.5	4321	4487
       NODE_18_length_437_cov_25.7277	437	273
                        NODE_1_length_4930_cov_3093.5	2354	2475
       NODE_19_length_420_cov_8.91507	420	300

                        to

                        *NODE_1_length_4930_cov_3093.5    :
       NODE_18_length_437_cov_25.7277
                        4321 4487  :  437 273
                        *NODE_1_length_4930_cov_3093.5    :
       NODE_19_length_420_cov_8.91507
                        2354 2475  :  420 300

                        So far, we have remove the reads that map to themslves
       and chaged the formatting a little.
    */

    /* STEP 2 OF THE HOMOLOGY BLOCKS CREATION
           This step aims to convert output from STEP 1

           *NODE_1_length_4930_cov_3093.5    :    NODE_18_length_437_cov_25.7277
            4321 4487  :   273 300
           *NODE_1_length_4930_cov_3093.5    :    NODE_18_length_437_cov_25.7277
            4321 4487  :   250 600

            TO

           *NODE_1_length_4930_cov_3093.5    :    NODE_18_length_437_cov_25.7277
            4321 4487  :  273 600

         */

    // The files that stored the results from STEP 1 is opened at file2
    File file2 = new File("Output/BLASTConvertOPHS1.txt");

    // The files that stores the results from STEP 2 is created and called
    // Output/BLASTConvertOPHS2.txt

    try {
      PrintWriter writer =
          new PrintWriter("Output/BLASTConvertOPHS2.txt", "UTF-8");

      try (BufferedReader br2 = new BufferedReader(new FileReader(file2))) {

        String line = "";

        ArrayList<Integer> a = new ArrayList<>();
        ArrayList<Integer> b = new ArrayList<>();
        ArrayList<Integer> a1 = new ArrayList<>();
        ArrayList<Integer> b1 = new ArrayList<>();

        while ((line = br2.readLine()) != null) {

          // Here, we are inside a loop that reads the output from STEP 1, line
          // by line

          if (line.startsWith("*")) {

            int aa[] = convertIntegers(a);

            int ab[] = convertIntegers(b);

            int ba[] = convertIntegers(a1);

            int bb[] = convertIntegers(b1);

            ClassAlignment aln = new ClassAlignment();

            writer.println(aln.run(aa, ab) + "   " + aln.run(ba, bb));

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

    /* HASH MAP CREATION FOR THE FINAL STEP LEADING TO THE HOMOLOGY BLOCKS
    CREATION
    Creates a mapping of node to ATGC values
    This function makes a Hash Map from the scaffolds.fasta file created with
    the metaspades
    with keys as the Node Name and value as the reads for faster search or the
    reads while
    creating the Homology Blocks.
    Example:
    KEY: NODE_3_length_1748_cov_262.91  VALUE: ATGCATTCGCAGTTAA
    The name of the map is scaffMap. (Scaffold Map)
    */

    Multimap<String, String> scaffMap = ArrayListMultimap.create();

    File file4 = new File("Output/scaffolds.fasta");

    try (BufferedReader br4 = new BufferedReader(new FileReader(file4))) {
      String line1;
      String key = "";
      while ((line1 = br4.readLine()) != null) {

        if (line1.startsWith(">")) {
          key = line1;
        } else {
          scaffMap.put(key, line1);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // FINAL STEP OF THE HOMOLOGY BLOCKS CREATION

    // The output from STEP 2 of the Homology Block creation in opened file5.

    File file5 = new File("Output/BLASTConvertOPHS2.txt");

    // A multi map is a Hash Map that can have multiple values for a key.

    Multimap<String, String> multiMap = ArrayListMultimap.create();

    try (BufferedReader br3 = new BufferedReader(new FileReader(file5))) {
      String line;
      line = br3.readLine();
      for (int i = 0; (line = br3.readLine()) != null; ++i) {

        // This loop iterates through each line of the result computed from STEP
        // 2 of the Homology Creation Process.
        /*The main function of this loop is to provide a blueprint (i.e. the
        location of reads and node name for a later process to fetch data from
        the
        scaffMap we created) This blue print is stored in multiMap with the
        format show in Now:

                Previously:

                *NODE_1_length_4930_cov_3093.5    :
        NODE_18_length_437_cov_25.7277
                4321 : 4487   437 : 273
                *NODE_1_length_4930_cov_3093.5    :
        NODE_19_length_420_cov_8.91507
                2354 : 2475   420 : 300
                *NODE_1_length_4930_cov_3093.5    :
        NODE_7_length_1581_cov_723.689
                2709 : 2766   1581 : 1524
                *NODE_1_length_4930_cov_3093.5    :
        NODE_14_length_754_cov_8.96567
                1296 : 1530   521 : 705
                *NODE_2_length_1975_cov_890.819    :
        NODE_45_length_285_cov_1.02609
                883 : 1121   284 : 47
                *NODE_2_length_1975_cov_890.819    :
        NODE_58_length_237_cov_2.57692
                379 : 619   3 : 236
                *NODE_2_length_1975_cov_890.819    :
        NODE_36_length_323_cov_3.33209
                1742 : 1874   196 : 323

                Now:
                Homology Block #1
                (Key)
                *NODE_1_length_4930_cov_3093.5
                (Value)
                4321 : 4487  NODE_18_length_437_cov_25.7277  437 : 273
                2354 : 2475  NODE_19_length_420_cov_8.91507  420 : 300
                2709 : 2766  NODE_7_length_1581_cov_723.689  1581 : 1524
                1296 : 1530  NODE_14_length_754_cov_8.96567  521 : 705
                Homology Block #2
                (Key)
                *NODE_2_length_1975_cov_890.819
                (Value)
                883 : 1121   NODE_45_length_285_cov_1.02609  284 : 47
                379 : 619    NODE_58_length_237_cov_2.57692  3 : 236
                1742 : 1874  NODE_36_length_323_cov_3.33209  196 : 323
         */

        if (!line.equals("\n") && !line.equals("*")) {
          if (line.startsWith("*")) {
            String splitted[] = line.split("\\s+");
            String reads[] = br3.readLine().split("\\s+");
            String key = (splitted[0].substring(1, splitted[0].length()));
            String value = (reads[0] + " : " + reads[2] + " " + splitted[2] +
                            " " + reads[3] + " : " + reads[5]);
            multiMap.put(key, value);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      // do something
    }

    /*
    FINAL STEP
    A new file named HomologyBlocks in created.
    */

    int i = 1;

    new File("Output/HomologyBlocks").mkdir();

    ArrayList<Integer> x = new ArrayList<>();
    ArrayList<Integer> y = new ArrayList<>();

    for (String key : multiMap.keySet()) {

      /* This loop iterates over each key in the multiMap and each key and ints
         corresponding values constitutes a Homology Block
               Thus a new file is created "Output/HomologyBlocks/"+i+".fasta"
         for each key value */

      try {
        PrintWriter writer3 =
            new PrintWriter("Output/HomologyBlocks/" + i + ".fasta", "UTF-8");

        ++i; // This increments the file name for each Homology Block
             // Left Node is the Key
             //       Left Read    Right Mapped Node               Right Node
             //       Location
        Collection<String> values =
            multiMap.get(key); // Gets the values for the corresponding key.
                               // Example: 1296 : 1530
                               // NODE_14_length_754_cov_8.96567  521 : 705
        Iterator<String> it = values.iterator();
        while (it.hasNext()) {

          // This Loop iterates through each value.

          String line2 = it.next();

          // Splits the values when encountering spaces
          String splitted2[] = line2.split("\\s+");

          // Addition of Read Lengths for Class Alignment
          x.add(Integer.parseInt(splitted2[0]));
          y.add(Integer.parseInt(splitted2[2]));

          /* The below line calls a function named printScaff that creates the
          homology blocks adding all the values by fetching the coressponding
          values from the scaffMap we made from scaffolds.fasta
              */
          printScaff(">" + splitted2[3].substring(0, splitted2[3].length()),
                     scaffMap, splitted2[4], splitted2[6], writer3);
        }

        ClassAlignment aln = new ClassAlignment();

        int ix[] = convertIntegers(x);

        int iy[] = convertIntegers(y);

        String keyRead = aln.run(ix, iy);

        String keySplit[] = keyRead.split("\\s+");

        /* The below line calls a function named printScaff that creates and
           completes the homology blocks by adding the key to the end of the
           values by fetching the coressponding
           values from the scaffMap we made from scaffolds.fasta, we call this
           printScaff seperately for the key since we have the same key with
           many read lengths.
        */
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

  public static int[] convertIntegers(List<Integer> integers) {

    if (integers.isEmpty()) {
      int[] re = new int[0];
      return re;
    }

    int[] ret = new int[integers.size()];

    Iterator<Integer> iterator = integers.iterator();
    for (int i = 0; i < ret.length; i++) {
      ret[i] = iterator.next().intValue();
    }
    return ret;
  }

  // This function does the final conversion by fetching the reads from
  // scaffolds.fasta files and writing it into the Homology Block.

  public static void printScaff(String key, Multimap<String, String> scaffMap,
                                String start, String end, PrintWriter writer7) {

    // System.out.print(key);
    int s = Integer.parseInt(start);

    // To start from index 0
    s = s - 1;

    int e = Integer.parseInt(end);

    writer7.println(key);

    Collection<String> values = scaffMap.get(key);

    Iterator<String> it = values.iterator();

    String scaff = "";

    while (it.hasNext()) {

      scaff += it.next();
    }

    if (e > s) {

      writer7.print(scaff.substring(s, e) + "\n");

    } else {

      StringBuilder rev = new StringBuilder();

      rev.append(scaff.substring(e, s));

      rev = rev.reverse();

      Map<Character, Character> revMap = new HashMap<>();
      revMap.put('A', 'T');
      revMap.put('T', 'A');
      revMap.put('G', 'C');
      revMap.put('C', 'G');

      for (int i = 0; i < rev.length(); ++i) {

        char ch = rev.charAt(i);

        rev.setCharAt(i, revMap.get(ch));
      }

      writer7.print(rev.toString() + "\n");
    }
  }
}