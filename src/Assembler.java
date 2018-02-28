import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Assembler{
	
	
	public Process run(String loc1, String loc2){
		
		
		System.out.println("Reverse paired-end reads location: " + loc2+ "\n");
		
		//System.out.println("\nPlease specify the k-mer sizes:");
		
		System.out.println("Assembling:");
		
		File f = new File("SPAdes-3.10.1-Linux/bin");
		ProcessBuilder pb = new ProcessBuilder();
        pb.directory(f);
        pb.command("./metaspades.py", "-t 43", "--pe1-1", loc1, "--pe1-2", loc2, "-o", "../../Output");
        Process p = null;
		try {
			p =  pb.start();
			InputStream stdOut = p.getInputStream();
			//Output Thread
			 new Thread(new Runnable(){
			    public void run(){
			       byte[] buffer = new byte[8192];
			       int len = -1;
			       //System.out.println("\n");
			        try {
						while((len = stdOut.read(buffer)) > 0){
						    System.out.write(buffer, 0, len);
							//System.out.print("|");
						
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    }
			}).start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

}
