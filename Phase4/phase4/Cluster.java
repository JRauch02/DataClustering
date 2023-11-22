//Jonathan Rauch

package phase4;

import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Point> points;
	private Point centroid;
	
	//Constructor for the Cluster class
	public Cluster() {
		points = new ArrayList<Point>();
		centroid = new Point();
	}
	
	//Method to turn a cluster and its centroid into a string to output
	public String toString() {
		String toReturn = "Cluster: " + points.size() + " points with centroid " + centroid.toString();
		return toReturn;
	}
	
	//Methid to calculate the mean of a cluster, which is used to find the new centroid for the next iteration
	public void calculateMean() {
		Point mean = new Point();
		
		//Makes sure that there are points in the cluster, the loops for the number of values a point has
		if (points.size() != 0) {
			for (int i = 0; i < points.get(0).getDimensionality(); i++) {
				double sumAttributes = 0;
				double meanAttributes = 0;
				
				//Loops for the number of points in the cluster, adding the values of each cluster together
				for (int j = 0; j < points.size(); j++) {
					sumAttributes += points.get(j).getAttributes().get(i);
				}
				
				//Calculates the average
				meanAttributes = sumAttributes / (double)points.size();
				mean.addAttribute(meanAttributes);
			}
		}
		else {
			mean = centroid;
		}
		
		//Sets the new centroid equal to the mean found
		this.centroid = mean;
	}
	
	//Method to add a new point to the cluster
	public void addPoint(Point point) {
		points.add(point);
	}
	
	//Retrieves all points found in this cluster
	public void addAllPoints(ArrayList<Point> points) {
		this.points.addAll(points);
	}
	
	//Method to retrieve a point from the cluster
	public Point getPoint(int index) {
		return points.get(index);
	}
	
	//Method to retrieve all points in the cluster
	public ArrayList<Point> getPoints() {
		return this.points;
	}
	
	//Method to set the centroid
	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}
	
	//Method to get the centroid
	public Point getCentroid() {
		return centroid;
	}
	
	//Method to get the cluster
	public ArrayList<Point> getCluster() {
		return points;
	}
	
	//Method to retrieve the amount of points in the cluster
	public int getSize() {
		return points.size();
	}
	
	//Method to erase all points in the cluster
	public void removePoints() {
		this.points.clear();
	}
}
