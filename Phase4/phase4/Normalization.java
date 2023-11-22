//Jonathan Rauch

package phase4;

import java.util.ArrayList;

public class Normalization {
	
	private ArrayList<Point> points;
	
	//Constructor for this class
	public Normalization(ArrayList<Point> points) {
		this.points = points;
	}
	
	//Method that normalizes the data from the txt file
	public ArrayList<Point> Normalize() {
		ArrayList<Double> normalizedColumn;
		ArrayList<ArrayList<Double>> allNormalizedColumns = new ArrayList<ArrayList<Double>>();
		
		//For loop that loops the number of times equal to the number of values a point has
		for (int i = 0; i < points.get(0).getDimensionality(); i++) {
			double min = Double.MAX_VALUE;
			double max = 0;
			
			//Instantiates one of the Array List
			normalizedColumn = new ArrayList<Double>();
			
			//Nested for loop to run for each point
			for (int j = 0; j < points.size(); j++) {
				//If the value of point j at dimension i is less than the minimum, min becomes that value
				if (points.get(j).getAttributes().get(i) < min) {
					min = points.get(j).getAttributes().get(i);
				}
				//If the value of point j at dimension i is greater than the maximum, max becomes that value
				if (points.get(j).getAttributes().get(i) > max) {
					max = points.get(j).getAttributes().get(i);
				}
			}
			
			//Runs a nested for loop for each point
			double normalizedValue = 0;
			for (int j = 0; j < points.size(); j++) {
				//If max minus min is not zero, the normalized value of each attribute of each point is calculated
				if ((max-min) != 0) {
					//Runs the calculations then adds the normalized value to a new Array List
					normalizedValue = ((points.get(j).getAttributes().get(i) - min) / (max - min));
					normalizedColumn.add(normalizedValue);
				}
				//If the before if statement is false, the value is set to zero and added to the new Array List
				else {
					normalizedValue = 0;
					normalizedColumn.add(normalizedValue);
				}
			}
			//An Array List meant to hold all the normalized data adds the Array List containing part of the new data
			allNormalizedColumns.add(normalizedColumn);
		}
		
		//Creates an Array List and initializes it, then runs a for loop by the number of times equal to the number of points
		ArrayList<Point> normalizedPoints = new ArrayList<Point>();
		for (int i = 0; i < allNormalizedColumns.get(0).size(); i++) {
			
			//Creates a new Point object
			Point normalizedPoint = new Point();
			
			//Nested for loop that runs for each point
			for (int j = 0; j < allNormalizedColumns.size(); j++) {
				//Adds the entire normalized point using the attributes stored in another Array List
				normalizedPoint.addAttribute(allNormalizedColumns.get(j).get(i));
			}
			//Adds the new point
			normalizedPoints.add(normalizedPoint);
		}
		
		//Returns the new Array List of points
		return normalizedPoints;
	}
}
