
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;

import clients.RioClient;
import communication.JetsonPacket.ModePacket.Mode;
import pipelines.CubeFilterContours;
import pipelines.CubeVisionPipe;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
	
	public static void main(String[] args) throws InterruptedException {
		
		//open camera
		VideoCapture camera = new VideoCapture(0);
		if(camera.isOpened()) {
			System.out.println("Camera is opened");
		}
		
		//make a mat that will be reused
		Mat capture = new Mat();
		
		//make pipeline, will be reused
		CubeVisionPipe cube = new CubeVisionPipe();
		
		while(true) {
			Thread.sleep(100);
			boolean success = camera.read(capture);
			if(success) {
				System.out.println("MODE: " + rioClient.getMode());
				//run image through pipeline
				if(rioClient.getMode() == Mode.CUBE) {
					cube.process(capture);
					//get the filtered countor output
					List<MatOfPoint> contours = cube.filterContoursOutput();
					//get the resized mat
					Mat smallerCapture = cube.cvResizeOutput();
					
					System.out.println("There are " + contours.size() + " contours avaible");
					
					MatOfPoint cubeOutline = CubeFilterContours.filter(smallerCapture, contours);
					
					//skip to the end of the loop, nothing more to be done here
					if(cubeOutline == null) {
						continue;
					}
					
					//get the center of the camera
					Point center = new Point((smallerCapture.width() / 2), (smallerCapture.height() / 2));
					
					Rect objectRect = Imgproc.boundingRect(cubeOutline);
					Point objectCenter = new Point(objectRect.x + (objectRect.width / 2), objectRect.y + (objectRect.height / 2));
					
					//find the difference between the center and the obejcts center
					double diff = center.x - objectCenter.x;
					System.out.println("Screen: "+center.x);
					System.out.println("Object: "+objectCenter.x);
					System.out.println("Diff: "+diff + "\n\n");
					
					Imgproc.rectangle(smallerCapture, new Point(objectRect.x, objectRect.y), 
							new Point(objectRect.x + objectRect.width, objectRect.height + objectRect.y), new Scalar(0, 0, 255), 1);
					Imgproc.circle(smallerCapture, center, 2, new Scalar(0, 255, 0), 1);
					Imgproc.circle(smallerCapture, objectCenter, 2, new Scalar(255, 0, 0), 1);
					
					Imgcodecs.imwrite("test.jpg", smallerCapture);
					
					rioClient.setDistance(diff);
				}
			}
		}
	}
}