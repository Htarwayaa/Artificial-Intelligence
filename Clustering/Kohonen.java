import java.util.*;

// Adapted by Siger Steenstra and Davey Schilling.
// Train and Test methods were implemented.

public class Kohonen extends ClusteringAlgorithm
{
	// Size of clustersmap
	private int n;

	// Number of epochs
	private int epochs;
	
	// Dimensionality of the vectors
	private int dim;
	
	// Threshold above which the corresponding html is prefetched
	private double prefetchThreshold;

	private double initialLearningRate; 
	
	// This class represents the clusters, it contains the prototype (the mean of all it's members)
	// and a memberlist with the ID's (Integer objects) of the datapoints that are member of that cluster.  
	private Cluster[][] clusters;

	// Vector which contains the train/test data
	private Vector<float[]> trainData;
	private Vector<float[]> testData;
	
	// Results of test()
	private double hitrate;
	private double accuracy;
	
	static class Cluster
	{
			float[] prototype;

			Set<Integer> currentMembers;

			public Cluster()
			{
				currentMembers = new HashSet<Integer>();
			}
	}
	
	public Kohonen(int n, int epochs, Vector<float[]> trainData, Vector<float[]> testData, int dim)
	{
		this.n = n;
		this.epochs = epochs;
		prefetchThreshold = 0.5;
		initialLearningRate = 0.8;
		this.trainData = trainData;
		this.testData = testData; 
		this.dim = dim;       
		
		Random rnd = new Random();

		// Here n*n new cluster are initialized
		clusters = new Cluster[n][n];
		for (int i = 0; i < n; i++)  {
			for (int i2 = 0; i2 < n; i2++) {
				clusters[i][i2] = new Cluster();
				clusters[i][i2].prototype = new float[dim];
				for(int i3 = 0; i3 < dim; i3++) {
					///this for loop randomly initializes the prototypes
					///for a certain cluster.
					clusters[i][i2].prototype[i3]=rnd.nextInt(101)/100;
				}
			}
		}
	}

	
	public boolean train()
	{
		// Step 1: initialize map with random vectors (A good place to do this, is in the initialisation of the clusters)
		///check!
		// Repeat 'epochs' times:
		double learningRate;
		double squareSize;
		int[] BMU = new int[2];
		int length = trainData.get(0).length;
		int strln = 0;
		for(int i = 0; i < epochs ; i++) {
			int iplusone = i+1;
			String str = new String("I am at epoch: (" + iplusone + "/" + epochs + ")");
			strln = str.length();
			//System.out.println("I am at epoch: (" + i + "/" + epochs + ")");
			System.out.print(str);
			//System.out.println(str.length());
			learningRate = initialLearningRate*(1-(i/epochs));
			squareSize = ((n*n)/2)*(1-(i/epochs));
			for(int j = 0; j < trainData.size(); j++) {
				double dist = Math.sqrt(length);
				for(int n1 = 0; n1 < n ; n1++){
					for (int n2 = 0; n2 < n ; n2++) {
						double sum = 0.0;
						for (int k = 0; k < length; k++) {
							sum += Math.pow((clusters[n1][n2].prototype[k] - trainData.get(j)[k]),2.0);
						}
						sum = Math.sqrt(sum);
						if (sum<dist){
							BMU[0]=n1;
							BMU[1]=n2;
							dist=sum;
						}
					}
				}
				//System.out.println(BMU[0] + " " + BMU[1]);
				double cluster_distance = 0.0;
				for(int n1 = 0; n1 < n; n1++) {
					for(int n2 = 0; n2 < n; n2++) {
						//if ( n1 != BMU[0] || n2 != BMU[1] ) {
							double sum=0.0;
							for (int k = 0; k < length; k++){
								sum += Math.pow((clusters[n1][n2].prototype[k]) - (clusters[BMU[0]][BMU[1]].prototype[k]),2.0);
							}
							//sum = Math.sqrt(sum);
							//System.out.println(n1 + " " + n2 + " " + sum);
							if (sum<squareSize){
								for (int k = 0; k < length; k++){
									clusters[n1][n2].prototype[k]=(1-(float)learningRate)*clusters[n1][n2].prototype[k]+(float)learningRate*trainData.get(j)[k];
								}
							}
						//}
					}
				}
				
				
			}
			//System.out.println(trainData.get(0).length);
			while(strln>0){
					System.out.print("\b");
					strln--;
			}
		}
		System.out.println("");
			// Step 2: Calculate the squareSize and the learningRate, these decrease linearly with the number of epochs.
			// Step 3: Every input vector is presented to the map (always in the same order)
			// For each vector its Best Matching Unit is found, and :
				// Step 4: All nodes within the neighbourhood of the BMU are changed, you don't have to use distance relative learning.
		// Since training kohonen maps can take quite a while, presenting the user with a progress bar would be nice
		return true;
	}
	
	public boolean test()
	{
		// iterate along all clients
		int hits=0;
		int prefetched=0;
		int requests=0;
		int[] BMU = new int[2];
		int length = testData.get(0).length;
		for(int i = 0; i<testData.size(); i++){
			double dist = Math.sqrt(length);
			for(int n1 = 0; n1 < n ; n1++){
				for (int n2 = 0; n2 < n ; n2++) {
					double sum = 0.0;
					for (int k = 0; k < length; k++) {
						sum += Math.pow((clusters[n1][n2].prototype[k] - testData.get(i)[k]),2.0);
					}
					sum = Math.sqrt(sum);
					if (sum<dist){
						BMU[0]=n1;
						BMU[1]=n2;
						dist=sum;
					}
				}
			}
			for(int j = 0; j < length; j++) {
				if (clusters[BMU[0]][BMU[1]].prototype[j]>prefetchThreshold) {
					prefetched++;
					if(testData.get(i)[j]==1.0) {
						hits++;
					}
				}
				if(testData.get(i)[j]==1.0){
					requests++;
				}
			}
		}
		hitrate = (double) hits/(double) requests;
		accuracy = (double) hits/(double) prefetched;
		// for each client find the cluster of which it is a member
		// get the actual testData (the vector) of this client
		// iterate along all dimensions
		// and count prefetched htmls
		// count number of hits
		// count number of requests
		// set the global variables hitrate and accuracy to their appropriate value
		return true;
	}


	public void showTest()
	{
		System.out.println("Initial learning Rate=" + initialLearningRate);
		System.out.println("Prefetch threshold=" + prefetchThreshold);
		System.out.println("Hitrate: " + hitrate);
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Hitrate+Accuracy=" + (hitrate + accuracy));
	}
 
 
	public void showMembers()
	{
		for (int i = 0; i < n; i++)
			for (int i2 = 0; i2 < n; i2++)
				System.out.println("\nMembers cluster["+i+"]["+i2+"] :" + clusters[i][i2].currentMembers);
	}

	public void showPrototypes()
	{
		for (int i = 0; i < n; i++) {
			for (int i2 = 0; i2 < n; i2++) {
				System.out.print("\nPrototype cluster["+i+"]["+i2+"] :");
				
				for (int i3 = 0; i3 < dim; i3++)
					System.out.print(" " + clusters[i][i2].prototype[i3]);
				
				System.out.println();
			}
		}
	}

	public void setPrefetchThreshold(double prefetchThreshold)
	{
		this.prefetchThreshold = prefetchThreshold;
	}
}

