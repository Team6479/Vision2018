package clients;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

//class to access server on rio
public class DSClient {

	private Socket socket;
	private Thread thread;

	public DSClient(String host, int port) {
		
		image = null;
		
		thread = new Thread(() -> {
			try {
				socket = new Socket(host, port);
				
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				System.out.println("Connected to server at " + socket.getInetAddress());
				
				while(!thread.isInterrupted()) {
					
					if(image != null) {
						MatOfByte buf = new MatOfByte();
						MatOfInt quality = new MatOfInt(Imgcodecs.CV_IMWRITE_JPEG_QUALITY,60);
						boolean res = Imgcodecs.imencode(".jpg", image, buf, quality);
						out.write(buf.toArray());
						out.flush();
						
						byte resp[] = new byte[8]; 
						int rlen = in.read(resp);
						if (!(rlen>0 && resp[0]==0x01)) {
							//failure to repsond
						}
						
						image = null;
					}
					else {
						try {
							Thread.sleep(10);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
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
	public synchronized void sendImage(Mat image) {
		this.image = image;
	}
	private Mat image;
}
