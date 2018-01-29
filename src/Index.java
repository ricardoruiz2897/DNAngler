import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Index {

	public Process run(){
		

		Process p = null; 
		
		ProcessBuilder pb = new ProcessBuilder();

		new File("Output/BAM").mkdir();
	
		pb.directory(new File("Samtools/"));
	
	
	    int max = new File("Output/SAM/").listFiles().length; 
	    
	    
		for(int i = 1; i <= max; i++) {
		        	
		        	
		        	String[] command = {"./samtools","index", "../Output/BAM/aln-pe"+i+".bam"};
		    		pb.command(command);
		    		
		    		try {
		    			p = pb.start();
		    		} catch (IOException e1) {
		    			// TODO Auto-generated catch block
		    			e1.printStackTrace();
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
