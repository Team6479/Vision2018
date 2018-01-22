
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import clients.RioClient;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.video.Video;

//import edu.wpi.cscore.UsbCamera;

public class Main {
	
	//load libararues
	static { 
		System.out.println("Loading Libraries from " + System.getProperty("java.library.path"));
		System.loadLibrary("opencv_java340");
		rioClient = new RioClient("roboRIO-6479-FRC.local", 1182);
	}
	
	//the socket connection to the rio
	public static RioClient rioClient;
	
	public static void main(String[] args) {
		
		//start the output socket
		System.out.println("Starting Rio Client");
		rioClient.startClient();
		
		//open camera
		VideoCapture camera = new VideoCapture(0);
		if(camera.isOpened()) {
			System.out.println("Camera is opened");
		}
		//make a mat that will be reused
		Mat capture = new Mat();
		
		while(rioClient.isAlive()) {
			boolean success = camera.read(capture);
			if(success) {
				rioClient.sendDistance(5.0);
			}
			//success = Imgcodecs.imwrite("test.jpg", capture);
			/*if(success) {
				System.out.println("Wrote an image succesfully");
			}*/
		}
	}
}