package filter;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class GoalFilterContours {
	
	public static class MatVector {
		public MatVector(MatOfPoint left, MatOfPoint right) {
			this.left = left;
			this.right = right;
		}
		public MatOfPoint left, right;
	}
	
	//the ratio of the size of the tape strip
	//this is a percentage
	public static final double IDEAL_TAPE_RATIO = 13.07;
	
	//filter out the most likely conoturs
	//will always return an array of size two, gaurnteed
	public static MatVector filter(Mat src, List<MatOfPoint> contours) {
		
		//if not enough contours, return null
		if(contours.size() <= 1) {
			return null;
		}
		
		//if there are two contours, find left and right and return no filtering need
		if(contours.size() == 2) {
			return new MatVector(contours.get(0), contours.get(1));
		}
		
		//keeps track of the score of each object, gets points for how close it is
		//score is out of 100
		int[] score = new int[contours.size()];
		
		//current index
		int currentIndex = 0;
		//sort trough contours and find the correct one
		for(MatOfPoint test: contours) {
			
			//get the rectangle suronding the contour
			Rect rect = Imgproc.boundingRect(test);
			//get ratio and coonvert to percent
			double ratio = rect.width / rect.height;
			int percent = (int) Math.round(ratio * 100);
			//the percent is the score
			
			//add the score
			score[currentIndex] = percent;
			
			currentIndex++;
		}
		
		//biggest
		int maxIndex1 = 0;
		//second biggest
		int maxIndex2 = 1;
		//find the largest 2 scores and store there indices
		for(int i = 2; i < score.length; i++) {
			//if it beats the biggest score
			if(score[maxIndex1] < score[i]) {
				//now the largest becomes the second largest
				maxIndex2 = maxIndex1;
				//and the new number becomes the largest
				maxIndex1 = i;
			}
			//if it beats the second biggest score
			else if(score[maxIndex2] <= score[i]) {
				//the new number replaces the second max
				maxIndex2 = i;
				//the biggest max remians untouched
			}
		}
		
		//the contour maxes
		MatOfPoint max1 = contours.get(maxIndex1);
		MatOfPoint max2 = contours.get(maxIndex2);
		
		return new MatVector(max1, max2);
	}

}
