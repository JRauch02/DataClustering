//Jonathan Rauch

package phase4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PointCollection {

	private ArrayList<Point> points;
	private ArrayList<Point> centroids;
	private ArrayList<Cluster> clusters;
	private String fn;
	private String writefn;
	private int numPoints;
	private int numValues;
	private int bestIterations;
	private double bestInitialSSE;
	private double bestFinalSSE;
	
	//Constructor for the pointCollection class
	public PointCollection(String file) {
		points = new ArrayList<Point>();
		centroids = new ArrayList<Point>();
		clusters = new ArrayList<Cluster>();
		fn = file;
		writefn = "";
		readFile();
	}
	
	//Constructor to be used to create a new Array List using the normalized data
	public PointCollection(ArrayList<Point> points) {
		this.points = points;
		this.numPoints = points.size();
		this.numValues = points.get(0).getDimensionality();
		this.centroids = new ArrayList<Point>();
		this.clusters = new ArrayList<Cluster>();
	}
	
	//Method to run random partition to create the initial clusters
	public void randomPartition(int numClusters) {
		//Clears the centroids and clusters Array Lists
		clusters.clear();
		centroids.clear();
		
		//Creates a number a clusters equal to the amount specified by the user
		for (int i = 0; i < numClusters; i++) {
			clusters.add(new Cluster());
		}
		
		//Creates a random object for random numbers and an int for indexing
		Random rand = new Random();
		int index;
		
		//Puts each point into a cluster at random
		for (int i = 0; i < points.size(); i++) {
			//Sets index equal to a random number up to the number of clusters, then adds the point to that cluster index
			index = rand.nextInt(numClusters);
			clusters.get(index).addPoint(points.get(i));
		}
		
		//Calculates the mean of each cluster to create the first centroid
		for (int i = 0; i < numClusters; i++) {
			clusters.get(i).calculateMean();
		}
	}
	
	//Method to create the centroid of the clusters and then display them
	public void createCentroids(int numClusters) {
		Random rand = new Random();
		ArrayList<Integer> centroidIndex = new ArrayList<Integer>();
		int tempCenterValue;
    
		clusters.clear();
		centroids.clear();
		
		//For loop to create a random index based on number of points to select random points in the data as centers
		for (int i = 0; i < numClusters; i++) {
			tempCenterValue = rand.nextInt(this.numPoints);
			if (!centroidIndex.contains(tempCenterValue)) {
				centroids.add(this.points.get(tempCenterValue));
				centroidIndex.add(tempCenterValue);
				clusters.add(new Cluster());
				clusters.get(i).setCentroid(points.get(tempCenterValue));
			}
			else
				i--;
		}
	}
	
	//Method for calculating the SSE for each iteration
	public double calculateSSE() {
		double SSE = 0;
		double distance = 0;
		
		//For loop runs through each point's SSE
		for (int i = 0; i < points.size(); i++) {
			int closestIndex = findCentroid(points.get(i), centroids);
			Point closestCentroid = clusters.get(closestIndex).getCentroid();
			double temp = 0;
			
			//Resets the distance for each point, then calculates
			distance = 0;
			for (int j = 0; j < this.numValues; j++) {
				temp = points.get(i).getAttributes().get(j) - closestCentroid.getAttributes().get(j);
				distance += temp * temp;
			}
			
			//Calculates the total SSE
			SSE += distance;
		}
		
		//Returns the total SSE
		return SSE;
	}
	
	//Method to execute the clustering process, every part of the process is called in this class
	public double convergence(int iterations, double conThreshold) {
		int closestIndex = 0;
		int currentIteration = 0;
		double previousSSE = 0;
		double currentSSE = 0;
		double currentConRating = 0;
		double SSE = 0;
		
		bestInitialSSE = Double.MAX_VALUE;
		bestFinalSSE = Double.MAX_VALUE;
		bestIterations = iterations;
		
		//Loops until the max iterations is hit
		for (currentIteration = 0; currentIteration < iterations; currentIteration++) {
			
			//This removes all data from the clusters
			for (int i = 0; i < clusters.size(); i++) {
				clusters.get(i).removePoints();
			}
			
			//This calculates which cluster each point belongs to
			for (int i = 0; i < points.size(); i++) {
				closestIndex = findCentroid(points.get(i), centroids);
				clusters.get(closestIndex).addPoint(points.get(i));
			}
			
			//SSE is calculated here, then is used to determine if the next iteration should execute
			currentSSE = calculateSSE();
			if (currentIteration == 0) {
				previousSSE = currentSSE * 10;
				if (currentSSE < bestInitialSSE) {
					bestInitialSSE = currentSSE;
				}
			}
			
			//Not sure if this is fully correct or not, but I had some iterations return SSE values higher
			//than the previous iteration, and this was the only way I could stop that from happening
			if (currentSSE > previousSSE) {
				currentSSE = previousSSE;
				break;
			}
			
			//Calculates the current convergence rating
			currentConRating = Math.abs((previousSSE - currentSSE) / previousSSE);
			previousSSE = currentSSE;
			System.out.println("Iteration " + (currentIteration+1) + ": SSE = " + currentSSE);
			SSE = currentSSE;
			
			bestFinalSSE = currentSSE;
			
			//If threshold is hit, stop the run, if not, find new centroids
			if (currentConRating < conThreshold) {
				bestIterations = currentIteration + 1;
				break;
			}
			else
				calculateCentroids();
		}
		
		//Returns the final SSE value
		return SSE;
	}
	
	//Method to find the new centroids for each cluster
	public void calculateCentroids() {
		//Clears each centroid
		centroids.clear();
		//Calculates new centroids for each cluster based on the previous clusters
		for (int i = 0; i < clusters.size(); i++) {
			clusters.get(i).calculateMean();
			centroids.add(clusters.get(i).getCentroid());
		}	 
	}
	
	//Method to find out which cluster each point belongs to
	public int findCentroid(Point compare, ArrayList<Point> centroids) {
		int index = 0;
		double closest = 0.0, current = 0.0;
		
		//Loops for every centroid
		for (int i = 0; i < centroids.size(); i++) {
			
			//Loops for every value in a point and calculates the distance from the point to each cluster
			current = 0;
			for (int j = 0; j < compare.getDimensionality(); j++) {
				double temp = centroids.get(i).getAttributes().get(j) - compare.getAttributes().get(j);
				current += temp * temp;
			}
			
			//If it is the first centroid, it is automatically the closest, if not, test to see if it is closer
			if (i == 0) {
				closest = current;
			}
			else {
				if (closest > current) {
					closest = current;
					index = i;
				}
			}
		}
		
		//Return the index of the closest cluster
		return index;
	}
		
	//Method that adds a point to the collection of points
	private void addPoint(Point newPoint) {
		this.points.add(newPoint);
	}
	
	//Method for another class to retrieve a point from here
	public Point getPoint(int index) {
		return points.get(index);
	}
	
	//Method to retrieve all points in the collection
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	//Method to get the number of values in each line
	public int getNumValues() {
		return numValues;
	}
	
	//Method to get the number of points
	public int getNumPoints() {
		return numPoints;
	}
	
	//Method to retrieve the number of clusters
	public int getNumClusters() {
		return clusters.size();
	}
	
	//Method to retrieve the clusters
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}
	
	//Retrieves the best initial SSE
	public double getBestInitialSSE() {
		return bestInitialSSE;
	}
	
	//Retrives the best final SSE
	public double getBestFinalSSE() {
		return bestFinalSSE;
	}
	
	//retrieves the best number of iterations
	public int getBestNumIterations() {
		return bestIterations;
	}
	
	//Method for reading a file
	private void readFile () {
		BufferedReader lineReader = null;
		try {
			FileReader fr = new FileReader(fn);
			lineReader = new BufferedReader(fr);
			String line = null;
			
			//Reads and saves the first line in txt file
			if ((line = lineReader.readLine())!=null) {
				String firstLine[] = line.split(" ");
				numPoints = Integer.parseInt(firstLine[0]);
				numValues = Integer.parseInt(firstLine[1]);
			}
			//Reads the remaining lines and saves data
			while ((line = lineReader.readLine())!=null) {
				//Reads each value in the line
				String[] pieces = line.split(" ");
				//Creats a new point to be added to the collection later
				Point newPoint = new Point();
				
				//Runs through each value to save the data
				for (int i = 0; i < numValues; i++) {
					double temp = Double.parseDouble(pieces[i]);
					newPoint.addValue(temp);
				}
				//Adds the result of the values to the entire collection
				this.points.add(newPoint);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("there was a problem with the file reader, try different read type.");
			try {
				lineReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fn.substring(1))));
				String line = null;
				
				//Reads and saves the first line in txt file
				if ((line = lineReader.readLine())!=null) {
					String firstLine[] = line.split(" ");
					numPoints = Integer.parseInt(firstLine[0]);
					numValues = Integer.parseInt(firstLine[1]);
				}
				//Reads the remaining lines and saves data
				while ((line = lineReader.readLine())!=null) {
					//Reads each value in the line
					String[] pieces = line.split(" ");
					//Creates a new point to be added to the collection later
					Point newPoint = new Point();
					
					//Runs through each value to save the data
					for (int i = 0; i < numValues; i++) {
						double temp = Double.parseDouble(pieces[i]);
						newPoint.addValue(temp);
					}
					//Adds the result of the values to the entire collection
					this.points.add(newPoint);
				} 				
			} catch (Exception e2) {
				e2.printStackTrace();
				System.err.println("there was a problem with the file reader, try again.  either no such file or format error");
			} finally {
				if (lineReader != null)
					try {
						lineReader.close();
					} catch (IOException e2) {
						System.err.println("could not close BufferedReader");
					}
			}			
		} finally {
			if (lineReader != null)
				try {
					lineReader.close();
				} catch (IOException e) {
					System.err.println("could not close BufferedReader");
				}
		}
	}
	
	//Commented out for future use
	public void writeFile(String fn) {
		FileWriter fw;
		try {
			fw = new FileWriter(fn);
			BufferedWriter myOutFile = new BufferedWriter(fw);
			myOutFile.write(this.toString());
			myOutFile.flush();
			myOutFile.close();
		} catch (IOException e) {
			System.out.println("***There was an error writting the file***!");
			e.printStackTrace();
		}		
	}
}
