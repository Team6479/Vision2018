package clients;

import java.io.*;
import java.net.Socket;

//class to access server on driver station
public class DSClient {

	private Thread clientThread;

	public DSClient(String host, int port) {
		
		//run in a thread
		clientThread = new Thread(() -> {
		
			// init all resources that must be closed here
			try (Socket socket = new Socket(host, port);
				 OutputStream out = socket.getOutputStream();
				 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

				// print diagnoastic info about connection
				System.out.println("Connected to server on driver station");
				
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
	public boolean isAlive() {
		return !clientThread.isInterrupted();
	}

}

