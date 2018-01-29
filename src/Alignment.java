import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Alignment {
	
	public Process run(){
		
		System.out.println("\nAligner (Burrows-Wheeler)");
		
		System.out.println("\nIndexing:\n");
		File f = new File("bwa/");
		Process p = null; 
        int size = new File("Output/HomologyBlocks/").listFiles().length; 
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(f);
		
		
		for(int i = 1; i <= size; i++) {
		pb.command("./bwa", "index", "../Output/HomologyBlocks/"+ i +".fasta");
		
		try {
			p = pb.start();
			
		} catch (IOException e) {
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
		//System.out.println("Standard error of the command (if any):\n");
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
