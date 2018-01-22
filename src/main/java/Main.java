
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import clients.RioClient;
import communication.JetsonPacket.ModePacket.Mode;
import pipelines.CubeVisionPipe;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
		
		//make a capture so that can get the size of the camera view
		camera.read(capture);
		
		Rect fullRect = Imgproc.boundingRect((MatOfPoint) capture);
		//print the center rect
		System.out.printf("Full x: %s y: %s width: %s height: %s\n", fullRect.x, fullRect.y, fullRect.width, fullRect.height);
		Point center = new Point(fullRect.width / 2, fullRect.height / 2);
		
		//make pipeline, will be reused
		CubeVisionPipe cube = new CubeVisionPipe();
		
		while(rioClient.isAlive()) {
			boolean success = camera.read(capture);
			if(success) {
				System.out.println("Image succesfully taken");
				
				//run image through pipeline
				if(rioClient.getMode() == Mode.CUBE) {
					cube.process(capture);
					//get the filtered countor output
					List<MatOfPoint> contours = cube.filterContoursOutput();
					
					System.out.println("There are " + contours.size() + " contours avaible");
					
					//get the first contour
					MatOfPoint first = contours.get(0);
					Rect objectRect = Imgproc.boundingRect(first);
					Point objectCenter = new Point(objectRect.width / 2, objectRect.height / 2);
					//print the center rect
					System.out.printf("Object x: %s y: %s width: %s height: %s\n", objectRect.x, objectRect.y, objectRect.width, objectRect.height);
				}
			}
			//success = Imgcodecs.imwrite("test.jpg", capture);
			/*if(success) {
				System.out.println("Wrote an image succesfully");
			}*/
		}
	}
}