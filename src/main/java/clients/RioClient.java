package clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import communication.JetsonPacket.*;

//class to access server on rio
public class RioClient {

	private Thread clientThread;
	private CameraPacket dataOutput;
	private ModePacket dataRecieved;

	public RioClient(String host, int port) {
		
		//init the dataoutput
		dataOutput = CameraPacket.newBuilder().setDistance(0).build();
		
		//run in a thread
		clientThread = new Thread(() -> {
		
			// init all resources that must be closed here
			try (Socket socket = new Socket(host, port);
				OutputStream out = socket.getOutputStream();
				InputStream in = socket.getInputStream()) {

				// print diagnoastic info about connection
				System.out.println("Connected to server on rio");
				
				// loop until thread is stopped
				while (!clientThread.isInterrupted()) {
					dataRecieved = ModePacket.parseFrom(in);
					
					out.write(dataOutput.toByteArray());
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
	//gets the filtering mode the robot should be doing
	public synchronized ModePacket.Mode getMode() {
		return dataRecieved.getMode();
	}
	//send the ditance in pixels to center of camera of the object
	public synchronized void sendDistance(Double distance) {
		dataOutput = CameraPacket.newBuilder().setDistance(distance).build();
	}

}
