package clients;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import communication.JetsonPacket.*;

//class to access server on rio
public class RioClient {

	private SocketChannel socket;

	public RioClient(String host, int port) {
		try {
			socket = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(host), port));
			//dont block
			socket.configureBlocking(false);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//gets the filtering mode the robot should be doing
	public ModePacket.Mode getMode() {
		
		//if socket is not connected, return
		if(!socket.isConnected()) {
			return ModePacket.Mode.UNRECOGNIZED;
		}
		
		
		try {
			//read in from the current buffer
			ByteBuffer input = ByteBuffer.allocate(1024);
			socket.read(input);
			input.flip();
			ModePacket packet = ModePacket.parseFrom(input);
			return packet.getMode();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		//could not read, unreconginzed
		return ModePacket.Mode.UNRECOGNIZED;
	}
	//send the ditance in pixels to center of camera of the object
	public boolean sendDistance(Double distance) {
		
		//if socket is not connected, return
		if(!socket.isConnected()) {
			return false;
		}
		
		try {
			//write distance to socket
			CameraPacket data = CameraPacket.newBuilder().setDistance(distance).build();
			ByteBuffer output = ByteBuffer.allocate(1024);
			output.put(data.toByteArray());
			output.flip();
			socket.write(output);
			//succesfully written
			return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
