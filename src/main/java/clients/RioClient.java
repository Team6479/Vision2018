package clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import communication.JetsonPacket.CameraPacket;
import communication.JetsonPacket.ModePacket;

//class to access server on rio
public class RioClient {

	private Socket socket;
	private Thread thread;

	public RioClient(String host, int port) {
		
		dataRecieved = ModePacket.getDefaultInstance();
		dataOutput = CameraPacket.getDefaultInstance();
		
		thread = new Thread(() -> {
			try {
				SocketAddress addr = new InetSocketAddress(host, port);
				socket = new Socket();
				socket.connect(addr, 5000);
				
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				System.out.println("Connected to server at " + socket.getInetAddress());
				
				while(!thread.isInterrupted()) {
					
					//write camera data
					dataOutput.writeDelimitedTo(out);
					
					//read in from the current buffer
					dataRecieved = ModePacket.parseDelimitedFrom(in);
				}
			}
			catch (SocketTimeoutException e) {
				System.err.println("Could not connect to robo rio");
				System.exit(1);
			}
			catch (UnknownHostException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	public synchronized ModePacket.Mode getMode() {
		return dataRecieved.getMode();
	}

	public synchronized void setDistance(Double distance) {
		dataOutput = CameraPacket.newBuilder().setDistance(distance).build();
	}

	private ModePacket dataRecieved;
	private CameraPacket dataOutput;
}
