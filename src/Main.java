import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

/****This the the Main Class that sequentally excecutes each process in the DNAngler tool.******/

public class Main   {
	
	//This line stores the location of the fastq files which were provided by the user
    static String loc1, loc2;
	
	//This in the main function inside the Main class which is required to excecute the program. 
	public static void main(String[] args) throws InterruptedException {
		
    //This part prompts the user to enter information about the location of the fastq input files. 

	System.out.println("DNAnglerPipeline");
	
	System.out.println("\nPlease specify file with forward paired-end reads:");
	
	Scanner in = new Scanner(System.in);
	
	loc1 = in.nextLine();
	
	/*final JFileChooser fcl = new JFileChooser();
	
	int returnVal1 = fcl.showOpenDialog(null);
	if (returnVal1 == JFileChooser.APPROVE_OPTION) {
	    // Retrieve the selected file
	    File file = fcl.getSelectedFile();
	    //loc1 recives the forwards read .fastq location
	    loc1 = file.getAbsolutePath(); 
	}*/
	
	System.out.println("Forward paired-end reads location: " + loc1);

	
	System.out.println("\nPlease specify file with reverse paired-end reads:");
	
	loc2 = in.nextLine();
	
	final JFileChooser fcr = new JFileChooser();

	/*int returnVal2 = fcr.showOpenDialog(null);
	if (returnVal2 == JFileChooser.APPROVE_OPTION) {
	    File file = fcr.getSelectedFile();
	   	//loc2 recives the reverse read .fastq location
	    loc2 = file.getAbsolutePath(); 
	} */
	
	System.out.println("Reverse paired-end reads location: " + loc2);

	/*
	This section calls the meataspades assembler. In order to gain more insight into the working
	of the assembler please open the Assembler.java file
	Commannds: pb.command("./metaspades.py", "-t 20", "--pe1-1", loc1, "--pe1-2", loc2, "-o", "../../Output");
	*/
	
	Assembler asm = new Assembler();
	
	Process p = asm.run(loc1, loc2);
	
	//See if the process has finished executing
	ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
	processExitDetector.addProcessListener(new ProcessListener() {
	    public void processFinished(Process process) {
	     printOutput();
	    	
	    }
	}); 
	processExitDetector.run(); 
	
	}
	
	
	/*This function is invoked after the Metaspades assembly is complete and prints out the scaffolds.fasta file 
	after which it calls the BLAST program. (Not Important)
	*/
	
	public static void printOutput(){
		System.out.println("\n\nOutput (Scaffolds):");
		
		final String FILENAME = "Output/scaffolds.fasta";
		
		BufferedReader br = null;
		FileReader fr = null;
		
		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);
                          
			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {

			e.printStackTrace();

		}finally {
				blast();
			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		

	}
	
	/*This section runs the BLAST program and creates the BLAST Database after which is calls the function that
	invokes the BLASTn program.
	Commands: "./makeblastdb", "-in",  "../../Output/scaffolds.fasta", "-dbtype", "nucl"
	In order to gain more insight into the working of the BLAST program please open the BLAST.java file*/
	public static void blast(){
		BLAST blast = new BLAST();
		Process p = blast.run();
		
		ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		processExitDetector.addProcessListener(new ProcessListener() {
		    public void processFinished(Process process) {
		     finalBLAST();
		    }
		});
		processExitDetector.run();
		
	}
	
		

	/*This section runs the BLAST Program. After which it invokes the blastConverter() function. 
	Commands: {"./blastn", "-query", "../../Output/scaffolds.fasta", "-db", "../../Output/scaffolds.fasta", "-outfmt", "6 qseqid qstart qend sseqid sstart send", "-out", "../../Output/OPblast"}
	In order to gain more insight into the working of the BLAST program please open the BLASTFinal.java file.
	*/
	public static void finalBLAST(){
		
		BLASTFinal finalblast = new BLASTFinal();
		Process p = finalblast.run();
		
		ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		processExitDetector.addProcessListener(new ProcessListener() {
		    public void processFinished(Process process) {
		    	System.out.println("\nSucessfully completed the BLAST Sequence.\n");
		    	blastConverter();
		        
		    }
		});
		processExitDetector.run();

		
	}
	
	/* OUR MAIN ALGORITHM (Homology Blocks Creation)
	This section runs the blastConverter function which is designed to generate the Homology Blocks and stored in Output/Homology Blocks. 
	For more information into the workings of this function please visit the BLASTConvert.java file. 
	After the creation of the Homology Block, this function calls the indexing function of Burrows-Wheeler Aligner.
	*/
	public static void blastConverter(){
		System.out.println("\nBlast Conversion:");
		BLASTConvert blastConverter = new BLASTConvert();
		try {
			blastConverter.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Created Homology Blocks.");
		alignment(); 
	}
	
	/*This section performs the indexing of each Homology blocks. 
	Commands Used: "./bwa", "index", "../Output/HomologyBlocks/"+ i +".fasta"
	After all Homology Block are indexed, the function calls the Burrows-Wheeler Aligner function finalalignment()
	*/
	public static void alignment(){
		Alignment alg = new Alignment();
		Process p = alg.run();
		ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		processExitDetector.addProcessListener(new ProcessListener() {
		    public void processFinished(Process process) {
		     System.out.println("\nIndexing Complete\n");
		     finalalignment();
		    }
		});
		processExitDetector.run();
		
	}

	/*This section performs the Burrows-Wheeler Alignment and stores the resulting SAM files in Output/SAM
	Commands Used: {"./bwa","mem", "../Output/HomologyBlocks/"+i+".fasta", loc1, loc2, "-o", "../Output/SAM/aln-pe"+i+".sam"};
	After the alignment, this function invokes bwaconversion() function . 
	*/
	
	public static void finalalignment(){
		 AlignmentFinal algf = new AlignmentFinal();
	     Process p =  algf.run(loc1, loc2);
	     ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		 processExitDetector.addProcessListener(new ProcessListener() {
			    public void processFinished(Process process) {
			     System.out.println("\nAlignment Complete\n");
			     bwaconversion();
			    }
			});
			processExitDetector.run();
	}

	/*This function sorts the .sam using samtools to .bam files and stores the resulting output into Output/BAM folder.
	Commands Used: {"./samtools","sort", "../Output/SAM/aln-pe"+i+".sam", "-o", "../Output/BAM/aln-pe"+i+".bam"}
	Afer the .sam to .bam conversion, the function samIndexing is called. 
	*/
	
	public static void bwaconversion() {
		Conversion convert = new Conversion();
		 Process p =  convert.run();
		 ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		 processExitDetector.addProcessListener(new ProcessListener() {
			    public void processFinished(Process process) {
			     System.out.println("\nBWA Conversion Complete\n");
			     samIndexing();
			    }
			});
			processExitDetector.run();
	}

	// This function indexes the the resulting bam file for display in the tablet software and then terminates the program.
	
	public static void samIndexing() {
		 Index index = new Index();
		 Process p =  index.run();
		 ProcessExitDetector processExitDetector  = new ProcessExitDetector(p);
		 processExitDetector.addProcessListener(new ProcessListener() {
			    public void processFinished(Process process) {
			     System.out.println("\nBWA Indexing Complete\n");
			    }
			});
			processExitDetector.run();
	}
	

}
