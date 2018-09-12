package naivebayes;
import java.util.*;
import javagently.*;

/**
 * Using a naive Bayes classifier to distinguish utterances of the word yes from the word no
 */

public class YesNoClassifier
{
  public static void main(String[] args)
  {
  	// Read in MFCC data

  	String mfccDataDirectory = "data/yesno/mfcc/";
  	Data yesData = new Data (mfccDataDirectory+"yes");
  	Data noData = new Data (mfccDataDirectory+"no");

  	// Build a naive Bayes classifier

  	Classifier classifier = new Classifier(yesData,noData);

  	// Compute the probability of being in class one for the first yes example
  	// using the 1st time-averaged MFCC as the feature
    int incorrectValues = 0;
    double percentageError;
    System.out.println("HERE THE YES SAMPLES");
    double [] numberErrors = new double[yesData.noMfcc];
    for(int index = 0; index < yesData.noMfcc; index++)
    {
      for(int index2 = 0; index2 < yesData.getNumberExamples(); index2++)
      {
        double answer = classifier.classify(yesData.getMeanMfcc(index2),index);
        if (answer < classifier.getpriorClass1())
          incorrectValues += 1;
      }
    percentageError = (100*incorrectValues) /
    (double)yesData.getNumberExamples();
    numberErrors[index] = incorrectValues;
    incorrectValues = 0;
    System.out.println("The percentage of error for the samples YES when using"+
                       " feacture " + (index + 1) + " is "
                       + (float) percentageError);
    }
    System.out.println("HERE THE NO SAMPLES");
    for(int index = 0; index < noData.noMfcc; index++)
    {
      for(int index2 = 0; index2 < noData.getNumberExamples(); index2++)
      {
        double answer = classifier.classify(noData.getMeanMfcc(index2),index);
        if (answer >= classifier.getpriorClass1())
          incorrectValues += 1;
      }
    percentageError = (100 * incorrectValues) /
    (double) noData.getNumberExamples();
    numberErrors[index] += incorrectValues;
    incorrectValues = 0;
    System.out.println("The percentage of error for the samples NO when using"+
                       " feacture " + (index + 1) + " is "
                       + (float) percentageError);
    }
    System.out.println("FEACTURES PERCENTAGE ERROR FOR ALL SAMPLES");
    for(int index = 0; index < noData.noMfcc; index++)
    {
      percentageError = 100 * numberErrors[index] /
           (double) (yesData.getNumberExamples()*2);
      System.out.println("The percentage of error for all the samples when " +
                         "using feacture " + (index + 1) + " is " +
                        (float) percentageError);
    }
    System.out.println("MULTIPLE FEACTURES APPROACH FOR YES");
    int incorrectValuesYes = 0;
    for(int index = 0; index < yesData.getNumberExamples(); index++)
    {
      double answer = classifier.classify(yesData.getMeanMfcc(index));
      if (answer < classifier.getpriorClass1())
      incorrectValuesYes += 1;
    }
    double percentageErrorYes = (100*incorrectValuesYes) /
    (double) (yesData.getNumberExamples()*2);
    System.out.println("The percentage of error for the samples YES when using"
                       + " multiple feacture approach is "
                       + (float) percentageErrorYes);
    System.out.println("MULTIPLE FEACTURES APPROACH FOR NO");
    int incorrectValuesNo = 0;
    for(int index = 0; index < noData.getNumberExamples(); index++)
    {
      double answer = classifier.classify(noData.getMeanMfcc(index));
      if (answer >= classifier.getpriorClass1())
      incorrectValuesNo += 1;
    }
    double percentageErrorNo = (100 * incorrectValuesNo) /
    (double) (noData.getNumberExamples() + yesData.getNumberExamples());
    System.out.println("The percentage of error for the samples NO when using"+
                       " multiple feacture approach is "
                        + (float) percentageErrorNo);
    percentageError = 100 * (incorrectValuesYes + incorrectValuesNo) /
            (double) ((noData.getNumberExamples()
                    + yesData.getNumberExamples())*2);
    System.out.println("The percentage of error for the all the samples " +
                       "when using multiple feacture approach is "
                       + (float) percentageError);
  }
}
