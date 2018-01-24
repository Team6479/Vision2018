package clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import communication.JetsonPacket.*;

//class to access server on rio
public class RioClient {

	private Socket socket;
	private Thread thread;

	public RioClient(String host, int port) {
		
		dataRecieved = ModePacket.getDefaultInstance();
		dataOutput = CameraPacket.getDefaultInstance();
		
		thread = new Thread(() -> {
			try {
				socket = new Socket(host, port);
				
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
