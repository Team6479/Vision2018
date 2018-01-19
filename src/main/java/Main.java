
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;

//import edu.wpi.cscore.UsbCamera;

public class Main {
	public static void main(String[] args) {
		// Loads our OpenCV library. This MUST be included
		System.out.println("PATH: " + System.getProperty("java.library.path"));
		try {
		//System.loadLibrary("libopencv_core");
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
		System.load("/mnt/flashdrive/opencv/build/lib/libopencv_java340.so");
		try {
		//System.load("/usr/lib/libopencv_core.so");
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}

	    System.out.println("Welcome to OpenCV " + Core.VERSION);
	    Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
	    System.out.println("OpenCV Mat: " + m);
	    Mat mr1 = m.row(1);
	    mr1.setTo(new Scalar(1));
	    Mat mc5 = m.col(5);
	    mc5.setTo(new Scalar(5));
	    System.out.println("OpenCV Mat data:\n" + m.dump());
	}
}