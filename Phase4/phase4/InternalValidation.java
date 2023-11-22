//Jonathan Rauch

package phase4;

import java.util.ArrayList;

public class InternalValidation {
	
	private PointCollection collection;
	
	//Constructor
	public InternalValidation(PointCollection collection) {
		this.collection = collection;
	}
	
	//Method to get the collection for other classes to access
	public PointCollection getCollection() {
		return collection;
	}
	
	//Sets the collection to be used in this class
	public void setCollection(PointCollection collection) {
		this.collection = collection;
	}
	
	//Method that runs the Calinski-Harabasz Index Method
	public double CH(double lowestSSE) {
		//Variables for calculations
		double ratio = 0.0;
		double interDispersion = 0.0;
		double intraDispersion = 0.0;
		Point dataCenter = new Point();
		Cluster dataSet = new Cluster();
		
		//Finds the centroid of all points in the file
		dataSet.addAllPoints(collection.getPoints());
		dataSet.calculateMean();
		dataCenter = dataSet.getCentroid();
		
		//Nested for loop to determine the total distance from each cluster centroid to the centroid of entire data set
		for (int i = 0; i < collection.getNumClusters(); i++) {
			double SOSDistance = 0.0;
			for (int j = 0; j < dataCenter.getAttributes().size(); j++) {
				//Calculates the sum of square distance from a cluster centroid to entire data set centroid
				SOSDistance += ((collection.getClusters().get(i).getCentroid().getAttributes().get(j) - dataCenter.getAttributes().get(j)) 
						* (collection.getClusters().get(i).getCentroid().getAttributes().get(j) - dataCenter.getAttributes().get(j)));
			}
			
			//Calculates the inter dispersion
			interDispersion += collection.getClusters().get(i).getPoints().size() * SOSDistance;
		}
		
		//Sets the intra dispersion
		intraDispersion = lowestSSE;
		
		//Calculates the ratio of the calculations above
		ratio = (interDispersion / intraDispersion) * ((collection.getNumPoints() - collection.getNumClusters()) / (collection.getNumClusters() - 1));
		
		//Returns the ending ratio
		return ratio;
	}
	
	//Main method for calculating the Silhouette Width method
	public double SW() {
		
		//Variable for calculations
		double ratio = 0.0;
		
		//Cohesions
		ArrayList<Double> cohesions = new ArrayList<Double>();
		ArrayList<Double> separations = new ArrayList<Double>();
		ArrayList<Double> silhouetteCoefficients = new ArrayList<Double>();
		
		//For loop for finding the cohesion, separation, and silhouette coefficient for each point in the entire data set
		for (int i = 0; i < collection.getNumPoints(); i++) {
			//Finds the cohesion and separation for a point
			cohesions.add(findCohesion(collection.getPoint(i)));
			separations.add(findSeparation(collection.getPoint(i)));
			
			//If else chain to determine if the coefficient should be divided by the cohesion or separation
			if (cohesions.get(i) > separations.get(i)) {
				silhouetteCoefficients.add((separations.get(i) - cohesions.get(i)) / cohesions.get(i));
			}
			else if (cohesions.get(i) < separations.get(i)) {
				silhouetteCoefficients.add((separations.get(i) - cohesions.get(i)) / separations.get(i));
			}
			else {
				silhouetteCoefficients.add((separations.get(i) - cohesions.get(i)) / separations.get(i));
			}
		}
		
		//For loop to add up all coefficients calculated above
		double sum = 0.0;
		for (int i = 0; i < silhouetteCoefficients.size(); i++) {
			sum += silhouetteCoefficients.get(i);
		}
		
		//Calculates the ratio from the above calculations
		ratio = (1 / (double)collection.getNumPoints()) * sum;
		
		//Returns that ratio
		return ratio;
	}
	
	//Method to find the cohesion of a point passed in
	public double findCohesion(Point point) {
		
		//Variables for calculations
		double cohesion = 0.0;
		int clusterIndex = 0;
		
		//For loop to find the cluster that the point passed in belongs to and stores the index of that cluster
		for (int i = 0; i < collection.getNumClusters(); i++) {
			if (collection.getClusters().get(i).getPoints().contains(point)) {
				clusterIndex = i;
				break;
			}
		}
		
		//Creates an array list to hold the same data as the cluster saved
		ArrayList<Point> clusterPoints = new ArrayList<Point>();
		clusterPoints.addAll(collection.getClusters().get(clusterIndex).getPoints());
		//For loop to remove the point from the copied cluster
		for (int i = 0; i < clusterPoints.size(); i++) {
			if (clusterPoints.get(i).equals(point)) {
				clusterPoints.remove(i);
				break;
			}
		}
		
		//For loop to calculate the sum of square distance from the point removed to all other points in its cluster
		double distance = 0.0;
		for (int i = 0; i < clusterPoints.size(); i++) {
			distance += point.calculateDistance(clusterPoints.get(i));
		}
		//If else statement to check if the size of the copied cluster is 0
		if (clusterPoints.size() == 0) {
			cohesion = 0.0;
		}
		//If not, then calculate the cohesion
		else {
			cohesion = distance / (double)clusterPoints.size();
		}
		
		//return the cohesion value
		return cohesion;	
	}
	
	//Method for finding the separation using the point passed in
	public double findSeparation(Point point) {
		//Array list created to copy the clusters already made
		ArrayList<Cluster> clusters = new ArrayList<Cluster>();
		clusters.addAll(collection.getClusters());
		ArrayList<Double> averageDistance = new ArrayList<Double>();
		
		//For loop to find the cluster the point passed in belongs to and removes it from the Array List
		for (int i = 0; i < clusters.size(); i++) {
			if (clusters.get(i).getPoints().contains(point)) {
				clusters.remove(i);
				break;
			}
		}
		
		//Nested for loop to calculate the total distance from the point passed in to each point one cluster at a time
		for (int i = 0; i < clusters.size(); i++) {
			int pointCount = clusters.get(i).getPoints().size();
			double distance = 0.0;
			for (int j = 0; j < pointCount; j++) {
				//Calculates the total distance using sum of square
				distance += point.calculateDistance(clusters.get(i).getPoints().get(j));
			}
			//Total distance for each cluster is added to this array list
			averageDistance.add(distance / (double)pointCount);
		}
		
		//For loop to find the closest distance from the point
		double closestDistance = Double.MAX_VALUE;
		for (int i = 0; i < averageDistance.size(); i++) {
			//If statement that runs if the current average distance is smaller than the current closest
			if (averageDistance.get(i) < closestDistance) {
				//If true, sets closest distance equal to that value
				closestDistance = averageDistance.get(i);
			}
		}
		
		//Returns the closest distance
		return closestDistance;		
	}
}
