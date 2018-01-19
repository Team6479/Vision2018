
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

//import edu.wpi.cscore.UsbCamera;

public class Main {
	
	//load libararues
	static { 
		System.out.println("Loading Libraries from " + System.getProperty("java.library.path"));
		System.loadLibrary("opencv_java340");
	}
	
	public static void main(String[] args) {
		//open camera
		VideoCapture camera = new VideoCapture(0);
		if(camera.isOpened()) {
			System.out.println("Opened Camera succsefully");
		}
		Mat capture = new Mat();
		boolean success = camera.read(capture);
		if(success) {
			System.out.println("Got an image succesfully");
		}
		success = Imgcodecs.imwrite("test.jpg", capture);
		if(success) {
			System.out.println("Wrote an image succesfully");
		}
	}
}