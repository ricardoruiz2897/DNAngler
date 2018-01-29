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

public class Main   {
	
    static String loc1, loc2;
	
	
	public static void main(String[] args) throws InterruptedException {
		
	System.out.println("DNAnglerPipeline");
	
	System.out.println("\nPlease specify file with forward paired-end reads:");
	
	Scanner in = new Scanner(System.in);
	
	loc1 = null; 
	
	final JFileChooser fcl = new JFileChooser();
	// Open the dialog using null as parent component if you are outside a
	// Java Swing application otherwise provide the parent comment instead
	int returnVal1 = fcl.showOpenDialog(null);
	if (returnVal1 == JFileChooser.APPROVE_OPTION) {
	    // Retrieve the selected file
	    File file = fcl.getSelectedFile();
	    loc1 = file.getAbsolutePath(); 
	}
	
	System.out.println("Forward paired-end reads location: " + loc1);

	
	System.out.println("\nPlease specify file with reverse paired-end reads:");
	
	loc2 = null; 
	
	final JFileChooser fcr = new JFileChooser();
	// Open the dialog using null as parent component if you are outside a
	// Java Swing application otherwise provide the parent comment instead
	int returnVal2 = fcr.showOpenDialog(null);
	if (returnVal2 == JFileChooser.APPROVE_OPTION) {
	    // Retrieve the selected file
	    File file = fcr.getSelectedFile();
	    loc2 = file.getAbsolutePath(); 
	} 
	
	System.out.println("Forward paired-end reads location: " + loc1);

	
	
	
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
