import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BLAST {

  public Process run() {

    Process p = null;

    File f = new File("ncbi-blast-2.6.0+/bin");
    ProcessBuilder pb = new ProcessBuilder();
    pb.directory(f);

    String[] commands = {"./makeblastdb", "-in", "../../Output/scaffolds.fasta",
                         "-dbtype", "nucl"};
    pb.command(commands);

    try {
      p = pb.start();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    System.out.println("\n\nBLAST:");

    BufferedReader stdInput =
        new BufferedReader(new InputStreamReader(p.getInputStream()));

    BufferedReader stdError =
        new BufferedReader(new InputStreamReader(p.getErrorStream()));

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
