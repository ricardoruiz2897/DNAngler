import java.util.ArrayList;
import java.util.List;

public class ProcessExitDetector {
  /** The process for which we have to detect the end. */
  private Process process;
  /** The associated listeners to be invoked at the end of the process. */
  private List<ProcessListener> listeners = new ArrayList<ProcessListener>();

  /**
   * Starts the detection for the given process
   * @param process the process for which we have to detect when it is finished
   */
  public ProcessExitDetector(Process process) { this.process = process; }

  /** @return the process that it is watched by this detector. */
  public Process getProcess() { return process; }

  public void run() {
    try {
      // wait for the process to finish
      process.waitFor();
      // invokes the listeners
      for (ProcessListener listener : listeners) {
        listener.processFinished(process);
      }
    } catch (InterruptedException e) {
    }
  }

  /** Adds a process listener.
   * @param listener the listener to be added
   */
  public void addProcessListener(ProcessListener listener) {
    listeners.add(listener);
  }

  /** Removes a process listener.
   * @param listener the listener to be removed
   */
  public void removeProcessListener(ProcessListener listener) {
    listeners.remove(listener);
  }
}
