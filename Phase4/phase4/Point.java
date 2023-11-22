//Jonathan Rauch

package phase4;

import java.util.ArrayList;

public class Point {
	
	private ArrayList<Double> pointAttributes;
	
	//Default Constructor if no information is passed
	public Point() {
		this.pointAttributes = new ArrayList<Double>();
	}

	//Method to add each value of a line to the point holding that lines values
	public void addValue(double temp) {
		this.pointAttributes.add(temp);
	}
	
	//Returns all values of the point as a string
	public String toString() {
		String toReturn = new String();
		for (int i = 0; i < pointAttributes.size(); i++) {
			toReturn += Double.toString(pointAttributes.get(i)) + " ";
		}
		return toReturn;
	}
	
	//Returns dimensionality of a point, the number of values it has
	public int getDimensionality() {
		return pointAttributes.size();
	}
	
	//Returns the values for a point
	public ArrayList<Double> getAttributes() {
		return this.pointAttributes;
	}
	
	//Adds a value to a point
	public void addAttribute(double attribute) {
		this.pointAttributes.add(Double.valueOf(attribute));
	}
	
	//Method that calculates the distance from one point to another using sum of square
	public double calculateDistance(Point point) {
		//Variables for calculating
		double distance = 0.0;
		int attributesCount = 0;
		
		//If else chain to retrieve the number of attributes depending on which point has the higher amount
		if (this.pointAttributes.size() < point.getAttributes().size()) {
			attributesCount = point.getAttributes().size();
		}
		else if (this.pointAttributes.size() > point.getAttributes().size()) {
			attributesCount = this.pointAttributes.size();
		}
		else {
			attributesCount = this.pointAttributes.size();
		}
		
		//For loop to add up total distance between each attribute
		for (int i = 0; i < attributesCount; i++) {
			//Add the value of the attribute of the point passed in if this point has a value of null
			if (this.getAttributes().get(i) == null) {
				distance += point.getAttributes().get(i) * point.getAttributes().get(i);
			}
			//Add the value of the attribute of this point if the point passed in has a value of null
			else if (point.getAttributes().get(i) == null) {
				distance += this.getAttributes().get(i) * this.getAttributes().get(i);
			}
			//Finds the distance squared between the two values if both have values for the current attribute
			else {
				distance += (this.getAttributes().get(i) - point.getAttributes().get(i)) * (this.getAttributes().get(i) - point.getAttributes().get(i));
			}
		}
		
		//Returns the total distance found
		return distance;
	}
}
