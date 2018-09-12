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

public class Answer2
{
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
    Classifier classifier = new Classifier(yesHMM, noHMM, 0.5);

    // Compute the probability of being in class one for the first yes example
    // using the 1st time-averaged MFCC as the feature
    int incorrectValues = 0;
    double percentageError;
    System.out.println("=====================================================");
    System.out.println("          PERCENTAGE ERROR FOR DATA CLASSES          ");
    System.out.println("=====================================================");
    System.out.println("HERE THE YES CLASS");
    System.out.println("-----------------------------------------------------");
    for(int index = 0; index < dataClass1.getMfcc().size(); index++)
    {
      double answer = classifier.classify(dataClass1.getMfcc(index));
      if (answer < classifier.getpriorClass1())
        incorrectValues += 1;
    }
    percentageError = (100*incorrectValues)/
                      (double) dataClass1.getMfcc().size();
    incorrectValues = 0;
    System.out.println("The percentage of error for the data class 1 (yes) is "
                       + (float) percentageError);
    System.out.println("=====================================================");
    System.out.println("HERE THE NO CLASS");
    System.out.println("-----------------------------------------------------");
    for(int index = 0; index < dataClass2.getMfcc().size(); index++)
    {
      double answer = classifier.classify(dataClass2.getMfcc(index));
      if (answer >= classifier.getpriorClass1())
        incorrectValues += 1;
    }
    percentageError = (100*incorrectValues)/
                      (double) dataClass2.getMfcc().size();
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
        * From state 1 to itself and states 3 and 4, the probability of transition
        * is the probability given in the yesHMM
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
    System.out.println("| THIS RESULTS SHOW THE MOST LIKELY PATH THAT A  |\n" +
                       "| DATA SEQUENCE IS MOST LIKELY TO TAKE AND SINCE |\n" +
                       "| WE KNOW WHAT EACH STATE REPRESENTS THEN WE CAN |\n"+
                       "| CLASSIFY THE DATA IN YES OR NO                 |\n"+
                       "| WE CAN ALSO USE THE RESULTS TO CROP THE SEQUEN-|\n"+
                       "| CE'S PARTS THAT FOLLOW A 0 STATE PATH, ELIMI-  |\n"+
                       "| NATING IN THIS WAY THE SILENCE PARTS           |");
    System.out.println("===================================================");
    System.out.println("HERE THE VITERBI RESULTS FOR Data Class 1 (yes)");
    System.out.println("-----------------------------------------------------");
    int a;
    int[] result;
    boolean startAssigned = false;
    int start = 0;
    boolean endAssigned = false;
    int end = 0;
    result = combinedHMM.viterbi(dataClass1.getMfcc(1));
    for (int i = 0; i < result.length; i++)
    {
      System.out.print("-->" + result[i]);
      if (result[i] == 1 && !startAssigned)
      {
        start = i;
        startAssigned = true;
      }
      else if(result[i] == 3 && !endAssigned)
      {
        end = (i - 1);
        endAssigned = true;
      }
    }
    /*
    * This code is used to crop the data sequence
    * The length of the double array is equal to [feacutres number][new length]
    * The new length is calculate from the start index until the last index
    */
    double[][] array = dataClass1.getMfcc(1);
    double[][] modifiedSequence = new double[Data.noMfcc][];
    int length = (array[1].length - start);
    length = length - ((array[1].length - end) - 1);
    for(int i = 0; i < Data.noMfcc; i++)
    {
      // Here the starting index and end index is specified
      modifiedSequence[i] = new double[length];
      int index = 0;
      for(int j = start; j <= end; j++)
      {
        modifiedSequence[i][index] = array[i][j];
        index++;
      }
    }
    System.out.println();
    System.out.println("HERE VITERBI RESULTS FOR Data Class 1 (yes) AFTER\n"+
                       "CROP THE DATA SEQUENCE");
    System.out.println("-----------------------------------------------------");
    result = combinedHMM.viterbi(modifiedSequence);
    for (int i = 0; i < result.length; i++)
      System.out.print("-->" + result[i]);
    System.out.println();
    System.out.println("HERE THE VITERBI RESULTS FOR Data Class 2 (no)");
    System.out.println("-----------------------------------------------------");
    result = combinedHMM.viterbi(dataClass2.getMfcc(1));
    startAssigned = false;
    endAssigned = false;
    for (int i = 0; i < result.length; i++)
    {
      System.out.print("-->" + result[i]);
      // Here the starting index and end index is specified
      if (result[i] == 2 && !startAssigned)
      {
        start = i;
        startAssigned = true;
      }
      else if(result[i] == 3 && !endAssigned)
      {
        end = (i - 1);
        endAssigned = true;
      }
    }
    /*
    * This code is used to crop the data sequence
    * The length of the double array is equal to [feacutres number][new length]
    * The new length is calculate from the start index until the last index
    */
    array = dataClass2.getMfcc(1);
    modifiedSequence = new double[Data.noMfcc][];
    length = (array[1].length - start);
    length = length - ((array[1].length - end) - 1);
    for(int i = 0; i < Data.noMfcc; i++)
    {
      modifiedSequence[i] = new double[length];
      int index = 0;
      for(int j = start; j <= end; j++)
      {
        modifiedSequence[i][index] = array[i][j];
        index++;
      }
    }
    System.out.println();
    System.out.println("HERE VITERBI RESULTS FOR Data Class 2 (no) AFTER\n"+
                       "CROP THE DATA SEQUENCE");
    System.out.println("-----------------------------------------------------");
    result = combinedHMM.viterbi(modifiedSequence);
    for (int i = 0; i < result.length; i++)
      System.out.print("-->" + result[i]);
    System.out.println();
  }
}
