
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;

import clients.DSClient;
import clients.RioClient;
import communication.JetsonPacket.ModePacket.Mode;
import filter.CubeFilterContours;
import filter.GoalFilterContours;
import pipelines.CubeVisionPipe;
import pipelines.GoalVisionPipe;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Main {
	
    public static final String macAddrDS = "90:2e:1c:e1:96:aa";
    
	//load libararues
	static { 
		//System.out.println("Loading Libraries from " + System.getProperty("java.library.path"));
		System.loadLibrary("opencv_java340");
		
		//System.out.println("Connecting to RIO");
		rioClient = new RioClient("roboRIO-6479-FRC.local", 1182);
		
		
		/*try {
            System.out.println("Finding Driver Station IP");
            //run command 
            Process p = Runtime.getRuntime().exec("sh ipFinder.sh");
            p.waitFor();
            p.destroy();
            //read the file
            File ipAddrFile = new File("ipaddr");
            FileReader reader = new FileReader(ipAddrFile);
            String fileContents = "";
            int next;
            //-1 is end of stream
            while((next = reader.read()) != -1) {
                char c = (char)next;
                //if whitespace or newlines, dont put in
                if(c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                    continue;
                }
                fileContents += c;
            }
            reader.close();
            //if the file contents is no ip, dont connect the ds
            if(!fileContents.contains("NO_IP_FOUND")) {
                //System.out.println("Connecting to DS");
                //get dsclients ip
                dsClient = new DSClient(fileContents, 1183);
            }
            else {
                System.out.println("Could not connect to DS: could not find");
                dsClient = null;
            }
		}
		catch (IOException e) {
		    System.out.println("Error reading file: " + e.getMessage());
		}
        catch (InterruptedException e) {
            System.out.println("Error running process: " + e.getMessage());
        }*/
	}
	
	//the socket connection to the rio
	public static RioClient rioClient;
	//socket connection to ds
	public static DSClient dsClient;
	
	private static final double NO_CUBE_FOUND_CODE = 9999;
	
	public static void main(String[] args) throws InterruptedException {
		
		//open camera
		VideoCapture camera = new VideoCapture(0);
		if(camera.isOpened()) {
			System.out.println("Camera is opened");
		}
		else {
			return;
		}
		if(rioClient == null) {
			System.out.println("Not connected");
			return;
		}
		
		//make a mat that will be reused
		Mat capture = new Mat();
		
		//make pipeline, will be reused
		CubeVisionPipe cube = new CubeVisionPipe();
		GoalVisionPipe goal = new GoalVisionPipe();
		
		long framesPerSecond = 30;
		long refreshRate = 1000 / framesPerSecond;
		
		while(true) {
			Thread.sleep(refreshRate);
			boolean success = camera.read(capture);
			
			if(success) {
				/*if(dsClient != null)
				{
					dsClient.sendImage(capture);
				}*/
				
				Mode cameraMode = rioClient.getMode();
				System.out.println("MODE: " + cameraMode);
				//run image through pipeline
				if(cameraMode == Mode.CUBE) {
					cube.process(capture);
					//get the filtered countor output
					List<MatOfPoint> contours = cube.filterContoursOutput();
					//get the resized mat
					Mat smallerCapture = cube.cvResizeOutput();
					
					System.out.println("There are " + contours.size() + " contours avaible");
					
					MatOfPoint cubeOutline = CubeFilterContours.filter(smallerCapture, contours);
					
					//skip to the end of the loop, nothing more to be done here
					if(cubeOutline == null) {
						rioClient.setDistance(NO_CUBE_FOUND_CODE);
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
				else if(cameraMode == Mode.GOAL) {
					goal.process(capture);
					//get the filtered countor output
					List<MatOfPoint> contours = goal.filterContoursOutput();
					//get the resized mat
					Mat smallerCapture = goal.cvResizeOutput();
					
					System.out.println("There are " + contours.size() + " contours avaible");
					
					//filter
					GoalFilterContours.MatVector goalOutline = GoalFilterContours.filter(smallerCapture, contours);
					
					//skip to the end of the loop, nothing more to be done here
					if(goalOutline == null) {
						rioClient.setDistance(NO_CUBE_FOUND_CODE);
						continue;
					}
					
					//get the center of the camera
					Point center = new Point((smallerCapture.width() / 2), (smallerCapture.height() / 2));
					
					Rect object1Rect = Imgproc.boundingRect(goalOutline.left);
					Rect object2Rect = Imgproc.boundingRect(goalOutline.right);
					
					//find the point between the two with regards to the x coordinate
					//the y coordinate does not matter
					double goalCenterX;
					//calculation accurarcy relies on using left most object
					//this if will do that
					if (object1Rect.x < object1Rect.y) {
						//object one is left
						goalCenterX = object2Rect.x - (object1Rect.x + object1Rect.width);
					}
					else {
						//object one is right
						goalCenterX = object1Rect.x - (object2Rect.x + object2Rect.width);
					}
					
					//find the difference between the center and the obejcts center
					double diff = center.x - goalCenterX;
					System.out.println("Screen: "+center.x);
					System.out.println("Object: "+goalCenterX);
					System.out.println("Diff: "+diff + "\n\n");
					
					Imgproc.circle(smallerCapture, center, 2, new Scalar(0, 255, 0), 1);
					Imgproc.line(smallerCapture, new Point(goalCenterX, 0), new Point(goalCenterX, smallerCapture.height()), new Scalar(0, 0, 255), 1);
					
					Imgcodecs.imwrite("test.jpg", smallerCapture);
					
					rioClient.setDistance(diff);
				}
			}
		}
	}
}