package markov;
import java.util.*;
import java.math.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class with main method for presenting the results of the lab. At the moment this just reads in the sequence data
 * and the corresponding state labels.
 */

public class Answer
{
  private static int[] result;
  private static boolean startAssigned = false;
  private static int start = 0;
  private static boolean endAssigned = false;
  private static int end = 0;
  private static Classifier classifier;
  public static void main(String[] args)
  {
  	String mfccDataDirectory = "data/yesno_uncut/mfcc/";
  	String labelDirectory = "data/yesno_uncut/labels/";

  	// Read in the MFCC data and state labels from each class

  	DataWithLabels dataClass1 = new DataWithLabels (mfccDataDirectory+"yes",
                                labelDirectory+"yes");
  	DataWithLabels dataClass2 = new DataWithLabels (mfccDataDirectory+"no",
                                labelDirectory+"no");

  	// Task 1
    //Create HHM models for yes and no data
    HiddenMarkovModel yesHMM = new HiddenMarkovModel (3, dataClass1.getMfcc(),
                              dataClass1.getLabels());
    HiddenMarkovModel noHMM = new HiddenMarkovModel (3, dataClass2.getMfcc(),
                              dataClass2.getLabels());

    //Now print the probability for each transition, from state i to state j
    //in the yes data
    System.out.println("=====================================================");
    System.out.println("      TRANSITIONS PROBABILITIES FOR DATA CLASSES     ");
    System.out.println("=====================================================");
    System.out.println("THESE DATA ARE FOR THE STATES OF YES DATA");
    System.out.println("-----------------------------------------------------");
    for(int i = 0; i < yesHMM.getNoStates() + 1; i++)
    {
      for(int j = 0; j < yesHMM.getNoStates() + 1; j++)
      {
        System.out.println("The probability for the transition from state " +
                          i + " to state " + j + " is " +
                          (float) yesHMM.getTransitionProbability(i,j));
      }
    }

    //Now print the probability for each transition, from state i to state j
    //in the yes data
    System.out.println("=====================================================");
    System.out.println("THESE DATA ARE FOR THE STATES OF NO DATA");
    System.out.println("-----------------------------------------------------");
    for(int i = 0; i < noHMM.getNoStates() + 1; i++)
    {
      for(int j = 0; j < noHMM.getNoStates() + 1; j++)
      {
        System.out.println("The probability for the transition from state "
                          + i + " to state " + j + " is " +
                           (float) noHMM.getTransitionProbability(i,j));
      }
    }

  	// Task 2
    //Create the classifier
    classifier = new Classifier(yesHMM, noHMM, 0.5);
    // Compute the probability of being in class one for the first yes example
    // using the 1st time-averaged MFCC as the feature
    double percentageError;
    System.out.println("=====================================================");
    System.out.println("          PERCENTAGE ERROR FOR DATA CLASSES          ");
    System.out.println("=====================================================");
    System.out.println("HERE THE YES CLASS");
    System.out.println("-----------------------------------------------------");
    //The percentageErrorCalculation method calculates the percentage of error
    //for the given class and need of this and the expected category
    percentageError = percentageErrorCalculation(dataClass1.getMfcc(), "yes");
    System.out.println("The percentage of error for the data class 1 (yes) is "
                       + (float) percentageError);
    System.out.println("=====================================================");
    System.out.println("HERE THE NO CLASS");
    System.out.println("-----------------------------------------------------");
    percentageError = percentageErrorCalculation(dataClass2.getMfcc(), "no");
    System.out.println("The percentage of error for the data class 2 (no) is "
                       + (float) percentageError);

  	// Task 3
    //Create the combined HMM
    HiddenMarkovModel combinedHMM = new HiddenMarkovModel (4);
    double probability = 0;
    //Creating the probabilities, followin the model in the figure 15
    for(int i = 0; i < combinedHMM.getNoStates() + 1; i++)
    {
      for(int j = 0; j < combinedHMM.getNoStates() + 1; j++)
      {
        //No all the the states follow the same logic
        /*
        * For state 0, the probability of transition from it to itself is the
        * probability given in the yesHMM and noHMM, added together and divided
        * by 2.
        * For the state 1(yes) and state 2(no) the transition is the probability
        * given in their correspondent HMMs divided BY 2
        */
        if (i == 0)
           switch(j)
           {
             case 0: probability = (noHMM.getTransitionProbability(i,j) +
                              yesHMM.getTransitionProbability(i,j)) /(double) 2;
                     break;
             case 1: probability = yesHMM.getTransitionProbability(i,j) /
                     (double) 2;
                      break;
             case 2: probability = noHMM.getTransitionProbability(i,j-1) /
                     (double) 2;
                      break;
             default: probability = 0.0;
           }
        /*
        * From state 1 to itself and states 3 and 4, the probability of
        * transition is the probability given in the yesHMM
        * For the state 0 and state 2(no) the transition probability is 0
        */
        else if (i == 1)
          switch(j)
          {
            case 0:;
            case 2: probability = 0.0;
                     break;
            case 1: probability = yesHMM.getTransitionProbability(i,j);
                    break;
            default: probability = yesHMM.getTransitionProbability(i,j - 1);
          }
        /*
        * From state 2 to itself and states 3 and 4, the probability of
        *  transition is the probability given in the noHMM
        * For the state 0 and state 1(yes) the transition probability is 0
        */
        if (i == 2)
          switch(j)
          {
            case 0:;
            case 1: probability = 0.0;
                    break;
            case 4: probability = noHMM.getTransitionProbability(i,j - 1);
                    break;
            default: probability = noHMM.getTransitionProbability(i - 1,j - 1);
          }
        /*
        * From state 3 to itself and state 4, the probability of transition
        * is the probability given in the yes and noHMM divided by 2
        * For the state 0 and states 1(yes) and 2(no) the transition probability
        * is 0
        */
        else if (i == 3)
          switch(j)
          {
            case 0:;
            case 1:;
            case 2: probability = 0.0;
                    break;
            default: probability = (noHMM.getTransitionProbability(i -1,j - 1)+
                                  yesHMM.getTransitionProbability(i -1,j - 1))/
                                  (double) 2;
          }
        /*
        * From state 4 to itself the probability of transition is 1.0
        * For the rest of the states the transition probability is 0
        */
        else if (i == 4)
          switch(j)
          {
            case 0: probability = 1.0;
                    break;
            default: probability = 0.0;
          }
        combinedHMM.setTransitionProbability(probability,i,j);
      }
    }
    System.out.println("=====================================================");
    System.out.println("                   COMBINED HMM                      ");
    System.out.println("=====================================================");
    System.out.println("HERE THE COMBINED HMM'S TRANSITION PROBABILITIES");
    System.out.println("-----------------------------------------------------");
    for(int i = 0; i < combinedHMM.getNoStates() + 1; i++)
    {
      for(int j = 0; j < combinedHMM.getNoStates() + 1; j++)
      {
        System.out.println("The probability for the transition from state "
                          + i + " to state " + j + " is " +
                           (float) combinedHMM.getTransitionProbability(i,j));
      }
    }
    //Set the emission densities following a smilar logic than before
    Normal emissionDensity = new Normal(1.0,1.0);
    for(int i = 0; i < combinedHMM.getNoStates(); i++)
    {
      for(int j = 0; j < Data.noMfcc; j++)
      {
        switch(i)
        {
          /*
          * For the state 0 and 3, the emission density will be equal the
          * combination of the emission densities the yesHMM and noHMM
          * For the state 1 the emision densition is equal to the one in
          * the yesHMM
          */
          case 0:emissionDensity = noHMM.getEmissionDensity(j,i).combine(
                                   yesHMM.getEmissionDensity(j,i));
                 break;
          case 1:emissionDensity = yesHMM.getEmissionDensity(j,i);
                 break;
          case 2:emissionDensity = noHMM.getEmissionDensity(j,i-1);
                 break;
          case 3:emissionDensity = noHMM.getEmissionDensity(j,i-1).combine(
                                   yesHMM.getEmissionDensity(j,i-1));
                 break;
          default:;
        }
        combinedHMM.setEmissionDensity(emissionDensity,j,i);
      }
    }
  	// Task 4
    System.out.println("===================================================");
    System.out.println("| THIS RESULTS SHOW THE PATH THAT A DATA SEQUENCE|\n" +
                       "| IS MOST LIKELY TO TAKE AND SINCE WE KNOW WHAT  |\n" +
                       "| EACH STATE REPRESENTS THEN WE CAN CLASSIFY THE |\n"+
                       "| DATA IN YES OR NO.                             |\n"+
                       "| WE CAN ALSO USE THE RESULTS TO CROP THE SEQUEN-|\n"+
                       "| CE'S PARTS THAT FOLLOW A 0 STATE PATH, ELIMI-  |\n"+
                       "| NATING IN THIS WAY THE SILENCE PARTS           |");
    System.out.println("===================================================");
    System.out.println("HERE THE VITERBI RESULTS FOR Data Class 1 (yes)");
    System.out.println("-----------------------------------------------------");
    //The first variable is the HMM tu use, second one the data sequence and
    //last one: if true, print the results, if false only determine crop indexes
    //the method returns the clasification of the data
    String dataType = viterbiResults(combinedHMM, dataClass1.getMfcc(1), true);
    /*
    * This code is used to crop the data sequence
    * The length of the double array is equal to [feacutres number][new length]
    * The new length is calculate from the start index until the last index
    */
    double[][] modifiedSequence = cropDataSequence(dataClass1.getMfcc(1));
    System.out.println("HERE VITERBI RESULTS FOR Data Class 1 (yes) AFTER\n"+
                       "CROP THE DATA SEQUENCE");
    System.out.println("-----------------------------------------------------");
    viterbiResults(combinedHMM, modifiedSequence, true);
    System.out.println();
    System.out.println("HERE THE VITERBI RESULTS FOR Data Class 2 (no)");
    System.out.println("-----------------------------------------------------");
    dataType = viterbiResults(combinedHMM, dataClass2.getMfcc(1), true);
    /*
    * This code is used to classify and crop the data sequence
    * The length of the double array is equal to [feacutres number][new length]
    * The new length is calculate from the start index until the last index
    */
    modifiedSequence = cropDataSequence(dataClass2.getMfcc(1));
    System.out.println("HERE VITERBI RESULTS FOR Data Class 2 (no) AFTER\n"+
                       "CROP THE DATA SEQUENCE");
    System.out.println("-----------------------------------------------------");
    viterbiResults(combinedHMM, modifiedSequence, true);
    /*
    * In this part I will evaluate the results of use viteri method to crop and
    * classify the data, creating a new List of data sequences and using this to
    * create new HMMs and testing the results
    */
    ArrayList <double[][]> yesSequence = new ArrayList<double[][]>();
    ArrayList <double[][]> noSequence = new ArrayList<double[][]>();
    for(int i = 0; i < dataClass1.getMfcc().size(); i++)
    {
      dataType=viterbiResults(combinedHMM, dataClass1.getMfcc(i), false);
      modifiedSequence = cropDataSequence(dataClass1.getMfcc(i));
      //Clasify the data has yes if the state yes is in the modifiedSequence
      //or like no otherwise
      if (dataType == "yes")
        yesSequence.add(modifiedSequence);
      else
        noSequence.add(modifiedSequence);
    }
     /*
     * Crop the data in the no data and test them with the classifier
     * to see if the new crop data has a lower percentage of error
     */
     for(int i = 0; i < dataClass1.getMfcc().size(); i++)
     {
       dataType=viterbiResults(combinedHMM,dataClass2.getMfcc(i), false);
       modifiedSequence = cropDataSequence(dataClass2.getMfcc(i));
       if (dataType == "yes")
         yesSequence.add(modifiedSequence);
       else
         noSequence.add(modifiedSequence);
     }
     percentageError = percentageErrorCalculation(yesSequence, "yes");
     System.out.println("Percentage of error for the new data class 1 (yes) is "
                        + (float) percentageError);
     percentageError = percentageErrorCalculation(noSequence, "no");
     System.out.println("Percentage of error for the new data class 2 (no) is "
                        + (float) percentageError);
  }
  /*
  * This method is used to calculate the percentage of error in the
  * clasification of the data.
  * The result is the probability that the given sequence is a yes sequence
  *
  * @param aDataSequence The data sequence, an ArrayList<double[][]> variable
  * @param aClass The expected class for the given data
  *
  * @return aPercentageError The percentage of error for the given sequence
  *
  */
  public static double percentageErrorCalculation(ArrayList<double[][]>
                                                 aDataSequence, String aClass)
  {
    int incorrectValues = 0;
    for(int index = 0; index < aDataSequence.size(); index++)
    {
      double answer = classifier.classify(aDataSequence.get(index));
      if (aClass == "yes" && answer < classifier.getpriorClass1())
        incorrectValues += 1;
      else if (aClass == "no" && answer >= classifier.getpriorClass1())
        incorrectValues += 1;
    }
    double aPercentageError = (100*incorrectValues)/
                      (double) aDataSequence.size();
    return aPercentageError;
  }
  /*
  * This method is used to calculate the viterbi results for a given data
  * sequence and use this information to classify the data and set crop indexes
  *
  * @param hmm The HMM that is used to calculate the viterbi results
  * @param aDataSequence The data sequence, a double[][] variable
  * @param print A boolen variable, if and only if true, print the results
  *
  * @return dataClasification String with the data sequence clasification
  *
  */
  public static String viterbiResults(HiddenMarkovModel hmm,
                                   double[][] aDataSequence,boolean print)
  {
    String dataClasification = "none";
    result = hmm.viterbi(aDataSequence);
    startAssigned = false;
    endAssigned = false;
    start = 0;
    end = 0;
    for (int i = 0; i < result.length; i++)
    {
      if(print)
        System.out.print("-->" + result[i]);
      // Here the starting index and end indexes are specified
      if (result[i] == 1 && !startAssigned)
      {
        dataClasification = "yes";
        start = i;
        startAssigned = true;
      }
      else if(result[i] == 2 && !startAssigned)
      {
        dataClasification = "no";
        start = i;
        startAssigned = true;
      }
      else if(result[i] == 3 && !endAssigned)
      {
        end = (i - 1);
        endAssigned = true;
      }
    }
    if(print)
      System.out.println();
    return dataClasification;
  }
  /*
  * This method is used to crop the given data sequence
  *
  * @param aDataSequence The data sequence, a double[][] variable
  *
  * @return newSequence The cropped data sequence
  *
  */
  public static double[][] cropDataSequence(double[][] aDataSequence)
  {
    double[][] sequence = aDataSequence;
    double[][] newSequence  = new double[Data.noMfcc][];
    int length = (sequence[1].length - start);
    if (end > 0)
      length = length - ((sequence[1].length - end) - 1);
    for(int i = 0; i < Data.noMfcc; i++)
    {
      newSequence[i] = new double[length];
      int index = 0;
      for(int j = start; j <= end; j++)
      {
        newSequence[i][index] = sequence[i][j];
        index++;
      }
    }
    return newSequence;
  }
}
