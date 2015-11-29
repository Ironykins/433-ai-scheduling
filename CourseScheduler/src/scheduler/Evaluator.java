package scheduler;

/**
 * This class houses logic for computing hard and soft constraints
 * It can compute from the ground up, or compute 'deltas' that efficiently
 * compute new values after a single transition.
 * 
 * One of these classes exists for the problem.
 * States and transitions to be evaluated are supplied as arguments.
 */
public class Evaluator {
	
	private final String NIGHT_TIME = "18:00";
	//Reference to our problem object.
	private final Problem prob;
	
	//Weightings of different componenets of Eval
	private double wMinFilled = 1.0;
	private double wPref = 1.0;
	private double wPair = 1.0;
	private double wSecDiff = 1.0;
	
	//Penalty for not meeting coursemin or labmin.
	private int pen_coursemin;
	private int pen_labmin;
	private int pen_section;
	private int pen_notpaired;
	
	public int getPen_coursemin() { return pen_coursemin; }
	public int getPen_labmin() { return pen_labmin; }
	public int getPen_section() { return pen_section; }
	public int getPen_notpaired() { return pen_notpaired; }
	public void setPen_coursemin(int newpen) { pen_coursemin = newpen; }
	public void setPen_labmin(int newpen) { pen_labmin = newpen; }
	public void setPen_section(int newpen) { pen_section = newpen; }
	public void setPen_notpaired(int newpen) { pen_notpaired = newpen; }
	
	//Getters and setters for the above.
	public double getwMinFilled() { return wMinFilled; }
	public void setwMinFilled(double wMinFilled) { this.wMinFilled = wMinFilled; }
	public double getwPref() { return wPref; }
	public void setwPref(double wPref) { this.wPref = wPref; }
	public double getwPair() { return wPair; }
	public void setwPair(double wPair) { this.wPair = wPair; }
	public double getwSecDiff() { return wSecDiff; }
	public void setwSecDiff(double wSecDiff) { this.wSecDiff = wSecDiff; }
	
	public Evaluator(Problem prob) {
		this.prob = prob;
	}
	
	/**
	 * Constr() will evaluate the 
	 * entire validity of state
	 * it checks that:
	 * 	-max number of courses has not been exceeded
	 *  -max number of labs has not been exceeded
	 *  -any course is not scheduled at the same time as an incompatible course
	 *  -any course is not in an unwanted slot
	 *  -any LEC 9 courses are scheduled at night (after 6pm)
	 *  
	 *  @param s The state we are checking for validity
	 *  @return True if the state is valid. False otherwise.
	 */
	public boolean Constr(State state){
		System.out.println(state.toString());
		System.out.printf("Constr Check\n############\nStateID = %d\nmaxCheck: %b\ncompatibleCheck: %b\nunwantedCheck: %b\nnightCheck: %b\n",state.stateId,maxCheck(state),compatibleCheck(state),unwantedCheck(state),nightCheck(state));
		return (maxCheck(state) && compatibleCheck(state) && unwantedCheck(state) && nightCheck(state) );

	}
	//Checks that any assignables with LEC 9 are scheduled after 18:00 (NIGHT_TIME)
	private boolean nightCheck(State state){
		for(int i = 0; i<state.assign.length; i++){
			if( (prob.Assignables[i].sectionNumber == 9) && (prob.Assignables[i].isCourse) && (state.assign[i] != -1)){
				if(prob.Slots[state.assign[i]].startTime.compareTo(NIGHT_TIME)<= 0){
					return false;
				}
			}
		}
		return true;
	}
	
	// Checks the labs and courses are not over the limit of any slot
	private boolean maxCheck(State state){
		for (int i = 0; i< prob.numberOfSlots; i++){
			if((state.numOfCourses[i] > prob.Slots[i].getCourseMax()) ||
				(state.numOfLabs[i] > prob.Slots[i].getLabMax())){
				return false;
			}
		}
		return true;
	}
	
	// Checks that the all of the currently scheduled assignables are compatible with each other
	private boolean compatibleCheck(State state){
		for(int i = 0; i < prob.numberOfAssignables; i++){
			for(int j = 0; j<prob.Assignables[i].incompatible.size();j++){
				int k = prob.Assignables[i].incompatible.elementAt(j);
				if(state.assign[k] == state.assign[i] && state.assign[i] != -1){
					return false;
				}
			}
		}
		return true;
	}

	// Check that all assignables are not in an unwanted slot
	private boolean unwantedCheck(State state){
		for(int i = 0; i < prob.numberOfAssignables; i++){
			if(prob.Assignables[i].unwanted.contains(state.assign[i])){
				return false;
			}
		}
		return true;
	}
	
//=============deltaConstr===========
	
	/**
	 *  deltaConstr() will evaluate the 
	 * validity of a single new assignment
	 * (In hopes to speed up the validity check
	 *  for each additional course assignment)
	 * it checks that:
	 * 	-max number of courses has not been exceeded in the given slot
	 *  -max number of labs has not been exceeded in the given slot
	 *  -the given course is not scheduled at the same time as an incompatible course
	 *  -the given course is not in an unwanted slot
	 *  
	 *  @param state The state as it is before the assignment
	 *  @param aIndex The index of the assignable
	 *  @param sIndex The index of the slot we wish to assign to.
	 *  @return True if the assignment can be made without violating hard constraints.
	 */
	public boolean deltaConstr(State state, int aIndex, int sIndex){
	
		return  deltaMaxCheck(state, aIndex, sIndex) && 
				deltaCompatibleCheck(state, aIndex, sIndex) && 
				deltaUnwantedCheck(aIndex, sIndex) &&
				deltaNightCheck(state, aIndex, sIndex);
	}

	
	// Lecture 9 has to be at night (Delta version)
	private boolean deltaNightCheck(State state, int aIndex, int sIndex)
	{
		if( prob.Assignables[aIndex].sectionNumber == 9)
		{
			if(prob.Slots[sIndex].startTime.compareTo(NIGHT_TIME) <= 0)
				return false;
		}
		
		return true;
	}
	
	
	// Checks the labs or courses will not be over the limit if we assign.
	private  boolean deltaMaxCheck(State state, int aIndex, int sIndex) {
		if(prob.Assignables[aIndex].isCourse)
			return (state.numOfCourses[sIndex]+1) <= prob.Slots[sIndex].getCourseMax();
		else
			return state.numOfLabs[sIndex] < prob.Slots[sIndex].getLabMax();
	}
	
	// Checks that the all of the currently scheduled assignables are compatible with the given assignable
	private  boolean deltaCompatibleCheck(State state, int aIndex, int sIndex){
		for(int i=0; i<state.assign.length; i++)
			//If an assignment has the same slot, it can't be incompatible with this assignable.
			if(state.assign[i] == sIndex && prob.Assignables[i].unwanted.contains(aIndex))
				return false;
				
		return true;
	}

	// Check that the given assignable is not in an unwanted slot
	private  boolean deltaUnwantedCheck(int aIndex, int sIndex){
		return !(prob.Assignables[aIndex].unwanted.contains(sIndex));
	}
	
	/**
	 * Performs a complete evaluation on a state.
	 * Takes into account weightings of each evaluated value.
	 * This can be a double, simply because the weightings can be doubles.
	 * It feels wrong, but Jorg says this is okay.
	 * 
	 * @param state The state to evaluate.
	 * @return The total eval-value of the state.
	 */

	public double eval(State state) {
		return evalMinFilled(state) * wMinFilled + 
				evalPref(state) * wPref +
				evalPair(state) * wPair + 
				evalSecDiff(state) * wSecDiff;
	}
	
	/**
	 * Computes the eval-penalty resulting from not meeting the coursemin or labmin 
	 * requirements on slots.
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalMinFilled(State st) {
		int missingCourses = 0;
		int missingLabs = 0;
		for(int i = 0; i < prob.numberOfSlots; i++){
			if((prob.Slots[i].getCourseMin() > st.numOfCourses[i])){
				missingCourses += (prob.Slots[i].getCourseMin() - st.numOfCourses[i]);
			}
			if(((prob.Slots[i].getLabMin() > st.numOfLabs[i]))){
				missingLabs += (prob.Slots[i].getLabMin() - st.numOfLabs[i]);
			}
		}
		Pair remaining = countRemaining(st);
		int missingTotal = 0;
		if(missingCourses > remaining.first){
			missingTotal += (missingCourses - remaining.first)*pen_coursemin;
		}
		if(missingLabs > remaining.second){
			missingTotal += (missingLabs - remaining.second)*pen_labmin;
		}
		return missingTotal;
	}
	
	private Pair countRemaining(State st){
		int courses = 0;
		int labs = 0;
		for(int i =0; i < prob.numberOfAssignables; i++){
			if(st.assign[i] == -1){
				if(prob.Assignables[i].isCourse){
					courses++;
				}else{
					labs++;
				}
			}
			
		}
		
		Pair left = new Pair(courses, labs);
		return left;
	}
	
	/**
	 * Computes the eval-penalty resulting from the assignable/timeslot preferences of professors
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalPref(State st)
	{
		double dPrefTotal = 0;
		int iIndex = 0;
		
		while( iIndex < st.assign.length )
		{
			if( st.assign[iIndex] == -1 )
			{
				iIndex++;
				continue;
			}
			
			dPrefTotal += prob.getPreferences()[iIndex][st.assign[iIndex]];
			
			iIndex++;
		}
		
		return dPrefTotal;
	}
	
	
	/**
	 * Computes the eval-penalty resulting from having different sections taught in the same slot.
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalSecDiff(State st) 
	{
		double dSecDiffTotal = 0;
		int iIndex = 0;
		
		while( iIndex < st.assign.length )
		{
			if( st.assign[iIndex] == -1 )
			{
				iIndex++;
				continue;
			}
			
			// TODO figure this shit out
			//dSecDiffTotal;
			
			iIndex++;
		}
		
		return dSecDiffTotal;
	}
	
	/**
	 * Computes the eval-penalty resulting from not having paired courses in the same slot.
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalPair(State st)
	{
		double dPairTotal = 0;
		int iIndex = 0;
		
		// Loop through the current states assigned courses/labs
		while( iIndex < st.assign.length )
		{
			// If this course/lab hasn't been assigned, ignore it
			if( st.assign[iIndex] == -1 )
			{
				iIndex++;
				continue;
			}
			
			// Loop through this course/labs Pair vector, for each pairing check if it's paired
			for( int iPairIndex = 0; iPairIndex < prob.Assignables[iIndex].paired.size(); iPairIndex++ )
			{
				int iPair = prob.Assignables[iIndex].paired.get( iPairIndex );
				
				if( st.assign[iIndex] == st.assign[iPair] )
					dPairTotal++;
			}
			
			iIndex++;
		}		
		
		return ((dPairTotal/2) * pen_notpaired);
	}
	
	/**
	 * Computes the change in eval-values for a given transition.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	public double deltaEval(State st, int aIndex, int sIndex) {
		return deltaEvalMinFilled(st, aIndex, sIndex) * wMinFilled + 
				deltaEvalPref(st, aIndex, sIndex) * wPref +
				deltaEvalPair(st, aIndex, sIndex) * wPair + 
				deltaEvalSecDiff(st, aIndex, sIndex) * wSecDiff;
	}
	
	/**
	 * Calculate the change in Eval due to sections of a course being taught together.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalSecDiff(State st, int aIndex, int sIndex)
	{
		
		
		return 1;
	}
	
	/**
	 * Calculate the change in Eval due to paired courses.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalPair(State st, int aIndex, int sIndex) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	/**
	 * Calculate the change in Eval due to preferences
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalPref(State st, int aIndex, int sIndex) {
		// TODO Auto-generated method stub
		return 1;
	}
	
	/**
	 * Calculate the change in Eval due to minimum constraints being filled.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalMinFilled(State st, int aIndex, int sIndex) {
		// TODO Auto-generated method stub
		return 1;
	}
}
