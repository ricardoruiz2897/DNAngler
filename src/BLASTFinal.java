import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BLASTFinal {

	public Process run(){
		
		Process p = null; 
		
		File f = new File("ncbi-blast-2.6.0+/bin");
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(f);
		
		String[] commands = {"./blastn", "-query", "../../Output/scaffolds.fasta", "-db", "../../Output/scaffolds.fasta", "-outfmt", "6 qseqid qstart qend sseqid sstart send", "-out", "../../Output/OPblast"};
		pb.command(commands);
		
		try {
			p = pb.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("\nRunning the BLAST Algorithm:");
		
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

		return p; 
	}
	
	
		
}
