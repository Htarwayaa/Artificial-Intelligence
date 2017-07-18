import java.io.*;
import java.util.*;

public class Bigram
{
    // This defines the two types of messages we have.
    static enum MessageType
    {
        NORMAL, SPAM
    }

    // This a class with two counters (for regular and for spam)
    static class Multiple_Counter
    {
        int counter_spam    = 0;
        int counter_regular = 0;

        // Increase one of the counters by one
        public void incrementCounter(MessageType type)
        {
            if ( type == MessageType.NORMAL )
			{
                ++counter_regular;
            } else {
                ++counter_spam;
            }
        }
    }
	
    // This is a class with two log probabilities (for regular and spam) 
    static class Multiple_Logprob
    {
		double prob_regular = 0;
		double prob_spam = 0;
	
		public void setLogProb(double prob, String type)
		{
			if (type == "regular")
			{
				prob_regular = prob;
			} else {
				prob_spam=prob;
			}
		}
    }

    /// Listings of the two subdirectories (regular/ and spam/)
    private static File[] listing_regular = new File[0];
    private static File[] listing_spam = new File[0];

    /// A hash table for the vocabulary (bigram searching is very fast in a hash table)
    private static Hashtable <String, Multiple_Counter> vocab = new Hashtable <String, Multiple_Counter> ();
 
    /// Creates a hashtable for the vocabulary and their log probabilities
    private static Hashtable <String, Multiple_Logprob> vocab2 = new Hashtable <String, Multiple_Logprob> ();
    
    /// These variables are used in the program to compute the probabilities
    private static double aPrioriRegular;
    private static double aPrioriSpam;
    private static int nbigramsRegular;
    private static int nbigramsSpam;
    
	/// This function calculates the apriori probability
    private static double aPriori(File[] a)
    {
		double length = a.length;
		double lengthTotal = listing_regular.length + listing_spam.length;
		return length/lengthTotal;
    }
    
	/// This function calculates the number of bigrams for regular and spam
    private static int nbigrams(String type)
    {	
		int n_bigrams = 0;
		if(type == "regular"){
			for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;)
			{   
				String bigram;
				Multiple_Counter counter;
				bigram = e.nextElement();
				counter = vocab.get(bigram);
				n_bigrams += counter.counter_regular;
			}
		} else {
			for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;)
			{   
				String bigram;
				Multiple_Counter counter;
				bigram = e.nextElement();
				counter = vocab.get(bigram);
				n_bigrams += counter.counter_spam;
			}
		}
		return n_bigrams;
    }
 
    /// Add a bigram to the vocabulary
    private static void addBigram(String bigram, MessageType type)
    {
        Multiple_Counter counter = new Multiple_Counter();

        if ( vocab.containsKey(bigram) )
		{                  			/// if bigram exists already in the vocabulary..
            counter = vocab.get(bigram);                /// get the counter from the hashtable
        }
        counter.incrementCounter(type);                 /// increase the counter appropriately
        vocab.put(bigram, counter);                     /// put the bigram with its counter into the hashtable
    }

    /// Creates hash table with log probabilities
    private static void addLog()
    {
		double tuner = 0.001;			/// The tuner is used for not getting 0-valued probabilities
		int frequency_treshold = 7;
		for (Enumeration<String> e = vocab.keys() ; e.hasMoreElements() ;)
		{   
			Multiple_Counter counter = new Multiple_Counter();
			Multiple_Logprob logprob = new Multiple_Logprob();
            String bigram;
            bigram = e.nextElement();
            counter = vocab.get(bigram);
            double logprob_regular;
            double logprob_spam;
            if(counter.counter_regular+counter.counter_spam>=frequency_treshold){
				if(counter.counter_regular > 0)
				{
					logprob_regular = Math.log((double)counter.counter_regular/(double)nbigramsRegular);
				} else {
					logprob_regular = Math.log(tuner/(double)(nbigramsRegular+nbigramsSpam));
				}
				
				logprob.setLogProb(logprob_regular, "regular");
			
				if(counter.counter_spam > 0)
				{
					logprob_spam = Math.log((double)counter.counter_spam/(double)nbigramsSpam);
				} else {
					logprob_spam = Math.log(tuner/(double)(nbigramsRegular+nbigramsSpam));
				}
				
				logprob.setLogProb(logprob_spam, "spam");
				vocab2.put(bigram, logprob);
			}
        }
    }
    
	/// This function reads a test message, calculates the sum of the posteri probability and concludes if it is regular or spam
    private static String testMessage(FileInputStream i_s, String type)
    throws IOException
    {
		double sumPosteriRegular=0;
		double sumPosteriSpam=0;
		int wordLength_treshold=4;
		int bigramLength_treshold=2*wordLength_treshold+1;
		
        BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
        String line;
		String token1 = "";
		String token2 = "";
        String bigram;
            
        while ((line = in.readLine()) != null)                      /// read a line
        {
            StringTokenizer st = new StringTokenizer(line);         
			if (st.hasMoreTokens())
				{
					token1 = st.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();	/// First part of the bigram is saved
				}
            while (st.hasMoreTokens())                  			
            {
				token2 = st.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();
				bigram = token1 + " " + token2; 	
                if (token1.length()>=wordLength_treshold && token2.length()>=wordLength_treshold && vocab2.containsKey(bigram)){
					sumPosteriRegular += vocab2.get(bigram).prob_regular;
					sumPosteriSpam += vocab2.get(bigram).prob_spam;    
				}	
            }
			token1 = token2;
        }
		in.close();
        if (sumPosteriRegular > sumPosteriSpam)
		{
			return "regular!";
		} else {
			return "spam!";
		}
    }

    // List the regular and spam messages
    private static void listDirs(File dir_location)
    {
        // List all files in the directory passed
        File[] dir_listing = dir_location.listFiles();

        // Check that there are 2 subdirectories
        if ( dir_listing.length != 2 )
        {
            System.out.println( "- Error: specified directory does not contain two subdirectories.\n" );
            Runtime.getRuntime().exit(0);
        }

        listing_regular = dir_listing[0].listFiles();
        listing_spam    = dir_listing[1].listFiles();
    }

    
    /// Print the current content of the vocabulary
    private static void printVocab()
    {
        Multiple_Counter counter = new Multiple_Counter();

        for (Enumeration<String> e = vocab2.keys() ; e.hasMoreElements() ;)
        {   
            String bigram;
            bigram = e.nextElement();
            counter  = vocab.get(bigram);
            System.out.println(bigram + " | in regular: " + counter.counter_regular + " in spam: " + counter.counter_spam);
		}
    }


    /// Read the bigrams from messages and add them to your vocabulary. The boolean type determines whether the messages are regular or not  
    private static void readMessages(MessageType type)
    throws IOException
    {
        File[] messages = new File[0];

        if (type == MessageType.NORMAL){
            messages = listing_regular;
        } else {
            messages = listing_spam;
        }
        
        for (int i = 0; i < messages.length; ++i)
        {
            FileInputStream i_s = new FileInputStream( messages[i] );
            BufferedReader in = new BufferedReader(new InputStreamReader(i_s));
            String line;
			String token1 = "";
			String token2 = "";
            String bigram;
            int wordLength_treshold=4;
            
            while ((line = in.readLine()) != null)                      					/// read a line
            {
                StringTokenizer st = new StringTokenizer(line);         					/// parse it into bigrams
				if (st.hasMoreTokens())
				{
					token1 = st.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();/// First part of the bigram is saved
				}
                while (st.hasMoreTokens())     								/// while there are stil bigrams left..
                {
	token2 = st.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase();;				/// Second part of the bigram is saved
					bigram = token1 + " " + token2;					/// Bigram is created
                    if (token1.length()>=wordLength_treshold && token2.length()>=wordLength_treshold) {	
						addBigram(bigram, type);			/// to lowercase. If words in the bigram have length 4 or more
					}							/// then it is added to the vocabulary.
                }
				token1 = token2;
            }

            in.close();
        }
    }
   
    public static void main(String[] args)
    throws IOException
    {
	// Location of the directory (the path) taken from the cmd line (first arg)
        File dir_location = new File( args[0] );
        
        // Check if the cmd line arg is a directory
        if ( !dir_location.isDirectory() )
        {
            System.out.println( "- Error: cmd line arg not a directory.\n" );
            Runtime.getRuntime().exit(0);
        }

        // Initialize the regular and spam lists
        listDirs(dir_location);

        // Read the e-mail messages
        readMessages(MessageType.NORMAL);
        readMessages(MessageType.SPAM);

		/// Calculate the a priori and nbigrams for regular and spam messages
		aPrioriRegular = Math.log(aPriori(listing_regular));
		aPrioriSpam = Math.log(aPriori(listing_spam));
		nbigramsRegular = nbigrams("regular");
		nbigramsSpam = nbigrams("spam");
	
        /// Creates hash table with log probabilities
        addLog();
        
        /// Print out the hash table
		printVocab();

		/// Print the a priori and nbigrams for regular and spam messages
        System.out.println("a priori (log) regular message: " + aPrioriRegular);
        System.out.println("a priori (log) spam    message: " + aPrioriSpam);
        System.out.println("nbigrams in regular: " + nbigramsRegular);
        System.out.println("nbigrams in spam: " + nbigramsSpam);

		/// Location of the directory taken from the command line (second argument)
        File test_location = new File( args[1] );
        if ( !test_location.isDirectory() )
        {
            System.out.println( "- Error: cmd line arg not a directory.\n" );
            Runtime.getRuntime().exit(0);
        }
		
        /// Initialize the regular and spam lists from the test files 
        listDirs(test_location);
        
	/// Variables used to generate a confusion matrix
        int regular_true = 0;
        int regular_false = 0;
        int spam_true = 0;
        int spam_false = 0;
        
        /// Read all the files in the test folder and classify them as regular or spam
        for (int i=0; i < listing_regular.length; i++)
		{
			FileInputStream i_s = new FileInputStream( listing_regular[i] );
			if (testMessage(i_s, "regular").equals("spam!"))
			{
				regular_false++;
			}
		}
		for (int i=0; i < listing_spam.length; i++){
			FileInputStream i_s = new FileInputStream( listing_spam[i] );
			if (testMessage(i_s, "spam").equals("regular!"))
			{
				spam_false++;
			}
		}
		
		/// Calculate the correctly classified messages
		regular_true=listing_regular.length-regular_false;
		spam_true=listing_spam.length-spam_false;
		
		/// Prints the confusion matrix, with the percentage of correctly classified messages per category
		System.out.println("Correctly regular: " + regular_true + " | Falsely regular: " + spam_false + " | " + 100 * ((double)regular_true/(((double)spam_false)+(double)regular_true)) + "%");
		System.out.println("Correctly spam: " + spam_true + " | Falsely spam: " + regular_false + " | " + 100 * ((double)spam_true/((double)regular_false+(double)spam_true)) + "%");
    }
}
