import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AlignmentFinal {

	public Process run(String loc1, String loc2){
		

		Process p = null; 
		
		new File("Output/SAM").mkdir();
		
		File f = new File("bwa/");
		//File f1 = new File("/home/robinronson96/workspace/DNAnglerPipeline/Output/SAM");
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(f);
		
        int size = new File("Output/HomologyBlocks/").listFiles().length; 

        size = size / 6;
        
        System.out.println("\n\n\nHomology Size : " + size);
        
		for(int i =  1; i <=size; i++) {
	    
		String[] commands = {"./bwa","mem", "../Output/HomologyBlocks/"+i+".fasta", loc1, loc2, "-o", "../Output/SAM/aln-pe"+i+".sam"};
		pb.command(commands);
		
		try {
			p = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
		
		//Code for Converting and Indexing .sam to .bam files 
		
		//ProcessBuilder pb2 = new ProcessBuilder();
		
		new File("Output/BAM").mkdir();

		pb.directory(new File("Samtools/"));


        int max = new File("Output/SAM/").listFiles().length; 
        
        
        for(int i = 1; i <= max; i++) {
        	
        	
        	String[] command = {"./samtools","sort", "../Output/SAM/aln-pe"+i+".sam", "-o", "../Output/BAM/aln-pe"+i+".bam"};
    		pb.command(command);
    		
    		try {
    			p = pb.start();    			
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    		
        } 
        
		for(int i = 1; i <= max; i++) {
		        	
		        	
		        	String[] command = {"./samtools","index", "../Output/BAM/aln-pe"+i+".bam"};
		    		pb.command(command);
		    		
		    		try {
		    			p = pb.start();
		    		} catch (IOException e1) {
		    			// TODO Auto-generated catch block
		    			e1.printStackTrace();
		    		}
		    		
		    		try {
						p.waitFor();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

		        }
		
	
		BufferedReader stdInput = new BufferedReader(new 
			     InputStreamReader(p.getInputStream()));
		
		BufferedReader stdError = new BufferedReader(new 
			     InputStreamReader(p.getErrorStream()));
		
		String s = null;
		try {
			while ((s = stdInput.readLine()) != null) {
			    System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// read any errors from the attempted command
	System.out.println("Here is the standard error of the command (if any):\n");
		try {
			while ((s = stdError.readLine()) != null) {
			    System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;
		
	}
}
