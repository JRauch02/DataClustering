//Jonathan Rauch
//Link to Programming Procedures I am choosing to code with
//https://adevait.com/java/5-best-and-worst-practices-in-java-coding

package phase4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/*
 * This program is run through the command line. Enter the following arguments in this order to run
 * properly. File name, number of clusters, number of maximum iterations, convergence threshold,
 * and finally the number of run. This version of the program will ignore the number of clusters and
 * will instead run multiple times starting at 2 clusters and incrementing the number of clusters
 * each time. It will stop once it has hit the maximum number of clusters for that file which is
 * determined by itself. Some files will take some time to run.
 */

public class Phase4 {

	//Private data members that only this class will need to know
	private String fileName;
	private String tempFile;
	private String outFile;
	private int numClusters;
	private int maxIterations;
	private int numRuns;
	private double conThreshold;
	private PointCollection collection;
	private DualPrintStream output;


	public Phase4(String[] args) throws FileNotFoundException {
		//Initializing all of the private data members for this class
		fileName = args[0];
		//numClusters = Integer.parseInt(args[1]);
		maxIterations = Integer.parseInt(args[2]);
		conThreshold = Double.parseDouble(args[3]);
		numRuns = Integer.parseInt(args[4]);
		collection = new PointCollection(fileName);

		//Grabs the file name without the extension and creates a new file to write to
		int temp = fileName.lastIndexOf(".");
		if (temp > 0 && temp < (fileName.length() - 1)) { // If '.' is not the first or last character.
			tempFile = fileName.substring(0, temp);
		}

		//Makes an output file specific to each input file
		outFile = "./" + tempFile + "_output.txt";
		PrintStream outputFilePath = new PrintStream(outFile);
		output = new DualPrintStream(outputFilePath, System.out);
		System.setOut(output);

		//Lets the user know that the program is running correctly
		System.out.println("\nData sucessfully retrieved from " + fileName);
		System.out.println("Number of points in data set: " + collection.getNumPoints());
		System.out.println("Values per data point: " + collection.getNumValues());
		//System.out.println("Number of Clusters being created: " + numClusters);
		
		//Initializes the normalizer object to normalize the data
		Normalization normalizer = new Normalization(collection.getPoints());
		
		//Calls the normalizer for both 
		//randomSelection(normalizer);
		randomPartition(normalizer);

		//Exits the program when finished
		System.exit(0);
	}
	
	//THIS METHOD IS NOT USED FOR THIS PHASE (RANDOM SELECTION IS NOT NEEDED)
	//==================================================================================================================================================
	//Method to run the original random selection we did in phase 2
	private void randomSelection(Normalization normalizedPoints) {
		
		//Creates a new dynamically allocated list of points using the normalized data
		collection = new PointCollection(normalizedPoints.Normalize());
		
		//Create and instantiate Array Lists to use throughout the method
		ArrayList<Double> bestInitialSSE = new ArrayList<Double>();
		ArrayList<Double> bestFinalSSE = new ArrayList<Double>();
		ArrayList<Integer> bestNumIterations = new ArrayList<Integer>();
		
		//Calculates the best run executed
		for (int i = 0; i < numRuns; i++) {
			//Separates each run in output
			System.out.println("\nRun " + (i+1) + "\n------");
			
			//Sends the number of clusters to select the points to be the centers for the clusters
			collection.createCentroids(numClusters);

			//Calls the convergence method to run the rest of the clustering process then adds the best of each category
			collection.convergence(maxIterations, conThreshold);
			bestInitialSSE.add(collection.getBestInitialSSE());
			bestFinalSSE.add(collection.getBestFinalSSE());
			bestNumIterations.add(collection.getBestNumIterations());
		}
		
		//Creates some objects to calculate the best of each category
		double lowestInitialSSE = bestInitialSSE.get(0);
		double lowestFinalSSE = bestFinalSSE.get(0);
		int lowestNumIterations = bestNumIterations.get(0);
		int lowestInitialSSERun = 0;
		int lowestFinalSSERun = 0;
		int lowestNumIterationsRun = 0;
		
		//Runs through every run to find the best for each category
		for (int i = 0; i < numRuns; i++) {
			if (bestInitialSSE.get(i) < lowestInitialSSE) {
				lowestInitialSSE = bestInitialSSE.get(i);
				lowestInitialSSERun = i;
			}
			if (bestFinalSSE.get(i) < lowestFinalSSE) {
				lowestFinalSSE = bestFinalSSE.get(i);
				lowestFinalSSERun = i;
			}
			if (bestNumIterations.get(i) < lowestNumIterations) {
				lowestNumIterations = bestNumIterations.get(i);
				lowestNumIterationsRun = i;
			}
		}
		
		//Prints out the end results of the random selection run
		System.out.println("\nBest Initial SSE: Run " + (lowestInitialSSERun + 1) + " with SSE " + bestInitialSSE.get(lowestInitialSSERun));
		System.out.println("\nBest Final SSE: Run " + (lowestFinalSSERun + 1) + " with SSE " + bestFinalSSE.get(lowestFinalSSERun));
		System.out.println("\nBest Number of Iterations: Run " + (lowestNumIterationsRun + 1) + " with " + bestNumIterations.get(lowestNumIterationsRun) + " iterations");
	}
	//==================================================================================================================================================
	
	//Method to run the k-means algorithm using random partition as the initial start of the algorithm
	private void randomPartition(Normalization normalizedPoints) {
		
		//Creates a new dynamically allocated list of points using the normalized data
		collection = new PointCollection(normalizedPoints.Normalize());
		
		//Create and instantiate Array Lists to use throughout the method
		ArrayList<Double> bestInitialSSE = new ArrayList<Double>();
		ArrayList<Double> bestFinalSSE = new ArrayList<Double>();
		ArrayList<Integer> bestNumIterations = new ArrayList<Integer>();
		
		//More variables so do store results
		double bestCHIndex = 0.0;
		double bestSWCoefficient = -1;
		int bestCHCluster = 0;
		int bestSWCluster = 0;
		int numPoints = collection.getNumPoints();
		int kMax = (int)Math.round(Math.sqrt(numPoints/2));
		for (int numClusters = 2; numClusters <= kMax; numClusters++) {
			//Creates a line to show the breakpoint between the random selection and random partition runs
			System.out.println("\n\n\n===================================================================================\n\n\n");
			System.out.println("Number of Clusters this Run: " + numClusters);
			
			//Creates the initial clusters then calls convergence to finish the clustering process
			for (int i = 0; i < this.numRuns; i++) {
				//Separates each run in output
				System.out.println("\nRun " + (i+1) + "\n------");
				
				//Calls the methods to create initial clusters and the rest of the clustering process
				collection.randomPartition(numClusters);
				collection.convergence(maxIterations, conThreshold);
				
				//Adds the values of each category to an Array List to be used later
				bestInitialSSE.add(collection.getBestInitialSSE());
				bestFinalSSE.add(collection.getBestFinalSSE());
				bestNumIterations.add(collection.getBestNumIterations());
			}
			
			//Creates some objects to calculate the best of each category
			double lowestInitialSSE = bestInitialSSE.get(0);
			double lowestFinalSSE = bestFinalSSE.get(0);
			int lowestNumIterations = bestNumIterations.get(0);
			int lowestInitialSSERun = 0;
			int lowestFinalSSERun = 0;
			int lowestNumIterationsRun = 0;
			
			//Runs through every run to find the best for each category
			for (int i = 0; i < numRuns; i++) {
				if (bestInitialSSE.get(i) < lowestInitialSSE) {
					lowestInitialSSE = bestInitialSSE.get(i);
					lowestInitialSSERun = i;
				}
				if (bestFinalSSE.get(i) < lowestFinalSSE) {
					lowestFinalSSE = bestFinalSSE.get(i);
					lowestFinalSSERun = i;
				}
				if (bestNumIterations.get(i) < lowestNumIterations) {
					lowestNumIterations = bestNumIterations.get(i);
					lowestNumIterationsRun = i;
				}
			}
			
			//Perform the validation of the data
			InternalValidation dataValidation = new InternalValidation(collection);
			double calinskiHarabaszIndex = dataValidation.CH(lowestFinalSSE);
			double silhouetteWidthCoefficient = dataValidation.SW();
			
			//If statements to see determine the best indices
			if (bestCHIndex < calinskiHarabaszIndex) {
				bestCHIndex = calinskiHarabaszIndex;
				bestCHCluster = numClusters;
			}
			if (bestSWCoefficient < silhouetteWidthCoefficient) {
				bestSWCoefficient = silhouetteWidthCoefficient;
				bestSWCluster = numClusters;
			}
			
			//Prints out the end results of the random selection run
			System.out.println("\nBest Initial SSE: Run " + (lowestInitialSSERun + 1) + " with SSE " + bestInitialSSE.get(lowestInitialSSERun));
			System.out.println("\nBest Final SSE: Run " + (lowestFinalSSERun + 1) + " with SSE " + bestFinalSSE.get(lowestFinalSSERun));
			System.out.println("\nBest Number of Iterations: Run " + (lowestNumIterationsRun + 1) + " with " + bestNumIterations.get(lowestNumIterationsRun) + " iterations");
			
			//Print the results of the validations
			System.out.println("\nCalinski_Harabasz Index: " + calinskiHarabaszIndex);
			System.out.println("\nSilhouette Width Coefficient: " + silhouetteWidthCoefficient);
		}
		//Prints the results of the best indices
		System.out.println("\n==========================================================================================================================================");
		System.out.println("\nBest Calinski_Harabasz Index: " + bestCHIndex + " with " + bestCHCluster + " clusters");
		System.out.println("\nBest Silhouette Width Coefficient: " + bestSWCoefficient + " with " + bestSWCluster + " clusters");
		
	}
	
	//Main method that jump starts the entire program
	public static void main(String[] args) throws FileNotFoundException
	{
		//Reads the arguments from the command line
		new Phase4(args);
	}
}
