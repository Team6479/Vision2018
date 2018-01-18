
import org.opencv.core.Mat;

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
		System.load("/usr/lib/libopencv_core.so");
		try {
		//System.load("/usr/lib/libopencv_core.so");
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
		}

		Mat mat = new Mat();
		//UsbCamera camera = new UsbCamera("Camera", 0);
	}
}