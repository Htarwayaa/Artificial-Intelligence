import java.util.*;
// Adapted by Siger Steenstra & Davey Schilling.
// The train and test methods were implemented.

public class KMeans extends ClusteringAlgorithm
{
	// Number of clusters
	private int k;

	// Dimensionality of the vectors
	private int dim;
	
	// Threshold above which the corresponding html is prefetched
	private double prefetchThreshold;
	
	// Array of k clusters, class cluster is used for easy bookkeeping
	private Cluster[] clusters;
	
	// This class represents the clusters, it contains the prototype (the mean of all it's members)
	// and memberlists with the ID's (which are Integer objects) of the datapoints that are member 
	// of that cluster.
	// You also want to remember the previous members so you can check if the clusters are stable.
	static class Cluster
	{
		float[] prototype;

		Set<Integer> currentMembers;
		Set<Integer> previousMembers;
		  
		public Cluster()
		{
			currentMembers = new HashSet<Integer>();
			previousMembers = new HashSet<Integer>();
		}
	}
	// These vectors contains the feature vectors you need; the feature vectors are float arrays.
	// Remember that you have to cast them first, since vectors return objects.
	private Vector<float[]> trainData;
	private Vector<float[]> testData;

	// Results of test()
	private double hitrate;
	private double accuracy;
	
	public KMeans(int k, Vector<float[]> trainData, Vector<float[]> testData, int dim)
	{
		this.k = k;
		this.trainData = trainData;
		this.testData = testData; 
		this.dim = dim;
		prefetchThreshold = 0.5;
		
		// Here k new cluster are initialized
		clusters = new Cluster[k];
		for (int ic = 0; ic < k; ic++)
			clusters[ic] = new Cluster();
	}


	public boolean train()
	{

		// Step 1: Select an initial random partioning with k clusters
		Random rand = new Random();
		for(int i = 0; i < trainData.size(); i++)
		{				
			clusters[rand.nextInt(k)].currentMembers.add(i);
		}
		///The following piece of code calculates the prototypes of the clusters.
		///We actually need this before we can proceed with step 2.
		for(int i = 0; i<k; i++)
		{
			clusters[i].prototype = new float[dim];
			Iterator iter=clusters[i].currentMembers.iterator();
			while(iter.hasNext()){
				float[] proto = (float[]) trainData.get(((int) iter.next()));
				for(int j=0; j<dim; j++){
					clusters[i].prototype[j] += proto[j];
					if(!iter.hasNext()){
						clusters[i].prototype[j] /= clusters[i].currentMembers.size();
					}
				}
			}
		}		
		// Step 4: repeat until clustermembership stabilizes
		/// We realised this by using a while loop with a boolean stable as its requirement.
		boolean stable=false;
		while(!stable){
			// Step 2: Generate a new partition by assigning each datapoint to its closest cluster 
			// center
			for(int i=0; i<k; i++){
				clusters[i].previousMembers.clear();
				Iterator iter=clusters[i].currentMembers.iterator();
				while(iter.hasNext()){
					clusters[i].previousMembers.add((int) iter.next());
				}
					clusters[i].currentMembers.clear();
			}
			for(int h=0; h<trainData.size(); h++){
				float[] data=(float[])trainData.get(h);
				int bestCluster=0;
				double closestDist=1000000;
				for (int i=0; i<k; i++){
					double dist = 0;
					for (int j = 0; j<dim ; j++){					
						dist += Math.abs(data[j]-clusters[i].prototype[j]);
					}
					if (dist<closestDist){
						closestDist=dist;
						bestCluster=i;
					}
				}
				clusters[bestCluster].currentMembers.add(h);
			}
			// Step 3: recalculate cluster centers
			for(int i = 0; i<k; i++){
				for(int j=0; j<dim; j++){
					clusters[i].prototype[j]=0;
				}
				Iterator iter=clusters[i].currentMembers.iterator();
				while(iter.hasNext()){
					float[] proto = (float[]) trainData.get(((int) iter.next()));
					for(int j=0; j<dim; j++){
						clusters[i].prototype[j] += proto[j];
						if(!iter.hasNext()){
							clusters[i].prototype[j] /= clusters[i].currentMembers.size();
						}
					}
				}
			}
			/// Checks for all clusters if the current members are the same as
			/// the previous members.
			int stableCount=0;
			for(int i=0; i<k; i++){
				if(clusters[i].currentMembers.equals(clusters[i].previousMembers)){
					stableCount++;
				}
			}
			if(stableCount==k){
				stable=true;
			}
		}
		return false;
	}

	public boolean test()
	{
		// iterate along all clients. Assumption: the same clients are in the same order as in the testData
		// for each client find the cluster of which it is a member
		// get the actual testData (the vector) of this client
		// iterate along all dimensions
		// and count prefetched htmls
		// count number of hits
		// count number of requests
		// set the global variables hitrate and accuracy to their appropriate value
		int hits=0;
		int prefetched=0;
		int requests=0;
		for(int i=0; i<k; i++){
			Iterator iter=clusters[i].currentMembers.iterator();
			while(iter.hasNext()){
				float[] indiv = (float[]) testData.get(((int) iter.next()));
				for(int j=0; j<dim; j++){
					if(clusters[i].prototype[j]>prefetchThreshold){
						prefetched++;
						if(indiv[j]==1.0){
							hits++;
						}
					}
					if(indiv[j]==1.0){
					requests++;
					}
				}
			}
		}
		hitrate = (double) hits/(double) requests;
		accuracy= (double) hits/(double) prefetched;
		return true;
	}


	// The following members are called by RunClustering, in order to present information to the user
	public void showTest()
	{
		System.out.println("Prefetch threshold=" + this.prefetchThreshold);
		System.out.println("Hitrate: " + this.hitrate);
		System.out.println("Accuracy: " + this.accuracy);
		System.out.println("Hitrate+Accuracy=" + (this.hitrate + this.accuracy));
	}
	
	public void showMembers()
	{
		for (int i = 0; i < k; i++)
			System.out.println("\nMembers cluster["+i+"] :" + clusters[i].currentMembers);
	}
	
	public void showPrototypes()
	{
		for (int ic = 0; ic < k; ic++) {
			System.out.print("\nPrototype cluster["+ic+"] :");
			
			for (int ip = 0; ip < dim; ip++)
				System.out.print(clusters[ic].prototype[ip] + " ");
			
			System.out.println();
		 }
	}

	// With this function you can set the prefetch threshold.
	public void setPrefetchThreshold(double prefetchThreshold)
	{
		this.prefetchThreshold = prefetchThreshold;
	}
}
