package pipelines;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class CubeFilterContours {
	
	
	//filter out the most likely conotur
	public static MatOfPoint filter(Mat src, List<MatOfPoint> contours) {
		
		//if no contours, return null
		if(contours.size() == 0) {
			return null;
		}
		
		//get the area of the whole mat as a refrence
		double referenceArea = src.width() * src.height();
		
		//keeps track of the score of each object, gets points for how close it is
		//score is out of 200
		//both area and squareness are weighted equally
		int[] score = new int[contours.size()];
		
		//current index
		int currentIndex = 0;
		//sort trough contours and find the correct one
		for(MatOfPoint test: contours) {
			
			//get ratio of both and convert to percent
			//get the rectangle suronding the contour
			Rect rect = Imgproc.boundingRect(test);
			//determine how close to a cube the rectangle is
			double cRatio = rect.width / rect.height;
			int cPercent = (int) Math.round(cRatio * 100);
			
			//get the area
			double area = rect.area();
			//get the percent area
			double aRatio = area / referenceArea;
			int aPercent = (int) Math.round(aRatio * 100);
			
			//add the score
			score[currentIndex] = cPercent + aPercent;
			
			currentIndex++;
		}
		
		int maxIndex = 0;
		//find the largest score and store its index
		for(int i = 1; i < score.length; i++) {
			if(score[maxIndex] < score[i]) {
				maxIndex = i;
			}
		}
		
		//the max contour
		MatOfPoint max = contours.get(maxIndex);
		
		return max;
	}

}
