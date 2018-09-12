package robot;

import java.util.*;
import java.lang.Math.*;

public class RobotBeliefState{

  /**
     This is the class representing the robot's belief state. Some of
     the methods contain dummy code, however. Functioning versions of
     these methods can be found in the final subclass
     SolutionRobotBeliefState

  */


  protected double[][][] beliefMatrix;
  protected double[][] positionBeliefMatrix;
  protected double[] orientationBeliefMatrix;

  protected double maxPositionProbability;
  protected double maxOrientationProbability;

  protected double[][][] workMatrix;

  protected String statusString;

  public WorldMap map;             // Accurate map of the world

  protected ProbActionToggler probActionToggler;


  // Set up constants

    public RobotBeliefState(WorldMap m, ProbActionToggler probActionToggler1){

    // Set map
    map= m;
    statusString= "Carlos Cerda Veloz code";
    initializeMatrices();
    probActionToggler= probActionToggler1;
  }

  public void initializeMatrices(){

    // Initialize matrices
    beliefMatrix=
      new double[RunRobot.SIZE][RunRobot.SIZE][RunRobot.SIZE];
    workMatrix=
      new double[RunRobot.SIZE][RunRobot.SIZE][RunRobot.SIZE];
    positionBeliefMatrix=
      new double[RunRobot.SIZE][RunRobot.SIZE];
    orientationBeliefMatrix=
      new double[RunRobot.SIZE];

    /*
	        ************** Dummy code follows **************

    // The following code does not work. In its current state, it initializes
       the probability of any given pose to 1.

       The method should actually set the probability distribution
       stored in beliefMatrix[][][] to a flat distribution over poses
       corresponding to *unoccupied* squares.  (If (i,j) is an
       occupied square, then beliefMatrix[i][j][k] should be zero for all k.)

    */
    //Determine how many positions are unoccupied
    int numberUnoccupied = 0;
    for(int i= 0;i < RunRobot.SIZE; i++)
      for(int j= 0;j < RunRobot.SIZE; j++)
        for(int k= 0;k < RunRobot.SIZE; k++)
          if(!map.isOccupied(i, j))
            numberUnoccupied = numberUnoccupied + 1;

    //Filling the position with normalized values
    double valueNormalize = 1 / (double) numberUnoccupied;
    for(int i= 0;i < RunRobot.SIZE; i++)
      for(int j= 0;j < RunRobot.SIZE; j++)
        for(int k= 0;k < RunRobot.SIZE; k++)
        {
          if(map.isOccupied(i, j))
            beliefMatrix[i][j][k]= 0;
          else
            beliefMatrix[i][j][k] = valueNormalize;
        }

    updateMaxProbabilities(); // Update member variables used by public access
                              // functions. (Do not change this line.)
  }


  public double getPoseProbability(Pose pose){
    /**
	beliefMatrix[i][j][k]
    */
    return beliefMatrix[pose.x][pose.y][pose.theta];
  }

  public double getPositionProbability(int x, int y){
    /**
	Return the probability that the robot is currently in position (x,y)
    */

    return positionBeliefMatrix[x][y];
  }

  public double getOrientationProbability(int t){
    /**
	Return the probability that the robot currently has orientation theta
    */
    return orientationBeliefMatrix[t];
  }

  protected void fixWorkMatrix(Observation o){

  }

  public void updateProbabilityOnObservation(Observation o){

    /**
	Revise beliefMatrix by conditionalizing on Observation o
    */

    /*

	        ************** Dummy code follows **************

    // The following code does not work. In its current state, it sets
       the probability of any given pose to 1.


      The method should actually revise the probability distribution
      stored in beliefMatrix[][][] by conditionalizing on the observation o.

    */
    //Created a new project for the new pose
    Pose poseObject = new Pose();

    //Total probability of the robot positions --> p(o)
    double totalProbability = 0;
    for(int i= 0;i < RunRobot.SIZE; i++)
      for(int j= 0;j < RunRobot.SIZE; j++)
        for(int k= 0;k < RunRobot.SIZE; k++)
        {
          poseObject.x = i;
          poseObject.y = j;
          poseObject.theta = k;
          totalProbability = totalProbability
                            + (map.getObservationProbability(poseObject, o)
                            * beliefMatrix[i][j][k]);
        }

    //Determine the new probability of each position
    for(int x = 0;x < RunRobot.SIZE; x++)
      for(int y = 0;y < RunRobot.SIZE; y++)
	      for(int t = 0;t < RunRobot.SIZE; t++)
        {
          poseObject.x = x;
          poseObject.y = y;
          poseObject.theta = t;
          beliefMatrix[x][y][t]=(map.getObservationProbability(poseObject, o) *
	                                  beliefMatrix[x][y][t] ) / totalProbability;
        }

    updateMaxProbabilities();  // Update member variables used by public access
                               // functions. (Do not change this line.)
  }


  public void updateProbabilityOnAction(Action a)
  {

    /**
	Revise beliefMatrix by conditionalizing on the knowledge that
	Action a has been performed. Assume deterministic actions for
	the moment.
    */
    //Initialization of a new matrix
    if(!probActionToggler.probActions())
    {
      for(int i= 0;i < RunRobot.SIZE; i++)
        for(int j= 0;j < RunRobot.SIZE; j++)
          for(int k= 0;k < RunRobot.SIZE; k++)
            workMatrix[i][j][k] = 0;

     //Calculating the probability of p(l'i.j.t|a)
      Pose tempPose = new Pose();
      for(int i= 0;i < RunRobot.SIZE; i++)
        for(int j= 0;j < RunRobot.SIZE; j++)
          for(int t= 0;t < RunRobot.SIZE; t++)
          {
            tempPose.x = i;
            tempPose.y = j;
            tempPose.theta = t;
            map.fillPoseOnAction(tempPose, i, j, t, a);
            workMatrix[tempPose.x][tempPose.y][tempPose.theta] = workMatrix
                                        [tempPose.x][tempPose.y][tempPose.theta]
                                        + beliefMatrix[i][j][t];
          }

      //Copy the workMatrix to beliefMatrix
      for(int x= 0;x < RunRobot.SIZE; x++)
        for(int y= 0;y < RunRobot.SIZE; y++)
	        for(int t= 0;t < RunRobot.SIZE; t++)
	          beliefMatrix[x][y][t]= workMatrix[x][y][t];
    }
    else
    {
      //Initialization of a new matrix
      for(int i= 0;i < RunRobot.SIZE; i++)
        for(int j= 0;j < RunRobot.SIZE; j++)
          for(int k= 0;k < RunRobot.SIZE; k++)
            workMatrix[i][j][k] = 0;

      //Object that are going to be used
      Action realAction = new Action();
      Pose tempPose = new Pose();

      //Calculating the probability of p(l'i.j.t|a)
      for(int i= 0;i < RunRobot.SIZE; i++)
        for(int j= 0;j < RunRobot.SIZE; j++)
          for(int t= 0;t < RunRobot.SIZE; t++)
          {
            tempPose.x = i;
            tempPose.y = j;
            tempPose.theta = t;
            for(int index = 0; index <= 20; index++)
            {
              //Creation of a new action with u value
              realAction.type = a.type;
              realAction.parameter = index;
              //Find the position (l'i j t)
              map.fillPoseOnAction(tempPose, i, j, t, realAction);
              workMatrix[tempPose.x][tempPose.y][tempPose.theta] =
              workMatrix[tempPose.x][tempPose.y][tempPose.theta] +
              //Apply special formula for p(l i j t|a and l i' j' t')
              beliefMatrix[i][j][t] * map.probabilify(10, index);
            }
          }
      //Copying the workMatrixmatrix to the beliefMatrixmatrix
      for(int x= 0;x < RunRobot.SIZE; x++)
        for(int y= 0;y < RunRobot.SIZE; y++)
	        for(int t= 0;t < RunRobot.SIZE; t++)
	          beliefMatrix[x][y][t]= workMatrix[x][y][t];
    }




    updateMaxProbabilities();  // Update member variables used by public access
                               // functions. (Do not change this line.)
  }

  public double getMaxPositionProbability(){
    return maxPositionProbability;
  }

  public double getMaxOrientationProbability(){
    return maxOrientationProbability;
  }

  protected void updateMaxProbabilities(){

    double temp;
    maxPositionProbability= 0;
    for(int x= 0; x< RunRobot.SIZE; x++)
      for(int y= 0; y< RunRobot.SIZE; y++){
	temp= 0;
	for(int k= 0; k< RunRobot.SIZE; k++)
	  temp+=beliefMatrix[x][y][k];
	positionBeliefMatrix[x][y]= temp;
	if(positionBeliefMatrix[x][y]>maxPositionProbability)
	  maxPositionProbability= positionBeliefMatrix[x][y];
      }

    maxOrientationProbability= 0;
    for(int t= 0; t< RunRobot.SIZE; t++){
	temp= 0;
	for(int i= 0; i< RunRobot.SIZE; i++)
	  for(int j= 0; j< RunRobot.SIZE; j++)
	    temp+=beliefMatrix[i][j][t];
	orientationBeliefMatrix[t]= temp;
	if(orientationBeliefMatrix[t]>maxOrientationProbability)
	  maxOrientationProbability= orientationBeliefMatrix[t];
    }
  }

}
