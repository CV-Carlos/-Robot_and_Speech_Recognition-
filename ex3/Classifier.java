package markov;
import java.math.*;

/**
 * This class stores an hmm for each of two classes and has a classify method to classify data according to the
 * most likely class using Bayes theorem. This method should be completed as part of question 2.
 */

public class Classifier {

    private double priorClass1; // p(C1) - prior probability for Class 1
    private double priorClass2; // p(C2) - prior probability for Class 2
    private HiddenMarkovModel hmmClass1; // HMM for class 1
    private HiddenMarkovModel hmmClass2; // HMM for class 2

    /**
     * This constructor method takes two hidden Markov models and the prior probability for class 1
     */

   public Classifier (HiddenMarkovModel hmm1, HiddenMarkovModel hmm2, double pC1)
   {
	   priorClass1 = pC1;
	   priorClass2 = 1.0 - pC1;  // The prior probabilities for each class must sum to one
	   hmmClass1 = hmm1;
	   hmmClass2 = hmm2;
   }

  /**
  * This method should use the forward algorithms and Bayes theorem in order to work
  * out the probability that a dataSequence corresponds to class 1 (yes).
  */
  public double getpriorClass1()
  {
    return priorClass1;
  }

  public double getpriorClass2()
  {
    return priorClass2;
  }

  public double classify (double[][] dataSequence)
  {
    BigDecimal numerator = hmmClass1.forward(dataSequence).multiply(
                          new BigDecimal(priorClass1));

    // Denominator of Bayes' theorem

    BigDecimal denominator = numerator.add(hmmClass2.forward(
                          dataSequence).multiply(new BigDecimal(priorClass2)));

    return (numerator.divide(denominator, 2, RoundingMode.HALF_UP)).doubleValue();
  }
}
