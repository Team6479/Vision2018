import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//class to access server on rio
public class Client {

	private Thread clientThread;

	public Client() {
		//run in a thread
		clientThread = new Thread(() -> {
		
			// init all resources that must be closed here
			try (Socket socket = new Socket("10.64.79.1", 1182);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

				// print diagnoastic info about connection
				System.out.println("Connected to server on rio");
				
				// loop until thread is stopped
				while (!clientThread.isInterrupted()) {
				
				}
			}
			catch (IOException e) {
				System.err.println("An error occured: " + e.getMessage());
				System.exit(1);
			}
		});
		clientThread.setDaemon(true);
	}
	
	public void startClient() {
		clientThread.start();
	}
	public void stopClient() {
		clientThread.interrupt();
	}

}
