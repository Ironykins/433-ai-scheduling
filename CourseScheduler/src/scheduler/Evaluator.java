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
	private final int NIGHT_HOUR = 18;
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
		//System.out.println(state.toString());
		//System.out.printf("Constr Check\n############\nStateID = %d\nmaxCheck: %b\ncompatibleCheck: %b\nunwantedCheck: %b\nnightCheck: %b\n",state.stateId,maxCheck(state),compatibleCheck(state),unwantedCheck(state),nightCheck(state));
		return (maxCheck(state) && compatibleCheck(state) && unwantedCheck(state) && nightCheck(state) );

	}
	//Checks that any assignables with LEC 9 are scheduled after 18:00 (NIGHT_HOUR)
	private boolean nightCheck(State state){
		for(int i = 0; i<state.assign.length; i++){
			if( (prob.Assignables[i].isEvening()) && (prob.Assignables[i].isCourse) && (state.assign[i] != -1)){
				int hourDigit = Integer.parseInt(prob.Slots[state.assign[i]].startTime.substring(0,prob.Slots[state.assign[i]].startTime.indexOf(':')));
				if(hourDigit < NIGHT_HOUR){
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
				//If they are assigned to the same slot, or the slots overlap.
				if((state.assign[i] != -1 && state.assign[k] != -1 && (state.assign[k] == state.assign[i] || prob.overlap[state.assign[k]][state.assign[i]]))){
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
		if( prob.Assignables[aIndex].isEvening())
		{
			int hourDigit = Integer.parseInt(prob.Slots[sIndex].startTime.substring(0,prob.Slots[sIndex].startTime.indexOf(':')));
			if(hourDigit < NIGHT_HOUR)
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
	private boolean deltaCompatibleCheck(State state, int aIndex, int sIndex){
		for(int i=0; i<prob.Assignables[aIndex].incompatible.size();i++){
			int incompatibleIndex = state.assign[prob.Assignables[aIndex].incompatible.elementAt(i)];
			if(incompatibleIndex != -1) {
				if(incompatibleIndex == sIndex) return false;
				
				//Don't assign into overlapping slots if they're incompatible.
				if(prob.overlap[incompatibleIndex][sIndex]) return false;
			}
		}
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
		double minFilled = evalMinFilled(state) * wMinFilled;
		double Pref	= evalPref(state) * wPref;
		double Pair = evalPair(state) * wPair;
		double secDiff = evalSecDiff(state) * wSecDiff;
		System.out.printf("Values for Node\nminFilled = %f\nPref = %f\nPair = %f\nsecDif = %f\n\n",minFilled,Pref,Pair,secDiff);
		return minFilled + Pref + Pair + secDiff;
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
	
	/**
	 * Counts the number of courses and labs that are unassigned.
	 * @param st The state we are checking.
	 * @return A tuple of (unassignedCourses, unassignedLabs)
	 */
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
	public double evalPref(State st){
		double dPrefTotal = 0;		
		for(int i = 0; i < prob.numberOfAssignables; i++){
			if( st.assign[i] != -1 ){
				for(int j = 0; j < prob.getPreferences()[i].length; j++){
					dPrefTotal += prob.getPreferences()[i][j];
				}
				dPrefTotal -= prob.getPreferences()[i][st.assign[i]];
			}
		}
		return dPrefTotal;	
	}
	
	
	/**
	 * Computes the eval-penalty resulting from having different sections taught in the same slot.
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalSecDiff(State st) {
		double dSecDiffTotal = 0;

		for(int i = 0; i < prob.numberOfAssignables; i++){
			if(( st.assign[i] != -1) && (prob.Assignables[i].isCourse)) {
				for(int j = i; j < prob.numberOfAssignables; j++){
					if(( st.assign[j] != -1) && (prob.Assignables[j].isCourse)) {
						//Slots can be the same, or slots can overlap. Both apply the penalty.
						if(i != j && st.assign[j] != -1 && ( st.assign[j] == st.assign[i] || prob.overlap[st.assign[j]][st.assign[i]] )){
							if( prob.Assignables[i].name.substring(0, 8).equals(prob.Assignables[j].name.substring(0, 8)) ){
								dSecDiffTotal++;
							}
						}
					}
				}	
			}
		} //Weeee
		
		return dSecDiffTotal*pen_section;
	}
	
	/**
	 * Computes the eval-penalty resulting from not having paired courses in the same slot.
	 * 
	 * @param st The state to evaluate
	 * @return The total eval-value for this domain.
	 */
	public double evalPair(State st){
		double notPaired = 0;		
		// Loop through the current states assigned courses/labs
		for(int i = 0; i < prob.numberOfAssignables; i++){
			// If this course/lab hasn't been assigned, ignore it
			if( st.assign[i] != -1 ){
				// Loop through this course/labs Pair vector, for each pairing check if it's paired
				for( int j = 0; j < prob.Assignables[i].paired.size(); j++ ){
					int iPair = prob.Assignables[i].paired.get( j );
					if( st.assign[i] != st.assign[iPair] && st.assign[iPair] != -1 && !prob.overlap[st.assign[i]][st.assign[iPair]] ){
						notPaired++;
					}
				}
			}
		}		
		
		return ((notPaired/2) * pen_notpaired);
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
		double DminFilled = deltaEvalMinFilled(st, aIndex, sIndex) * wMinFilled;
		double Dpref = deltaEvalPref(st, aIndex, sIndex) * wPref ;
		double Dpair = deltaEvalPair(st, aIndex, sIndex) * wPair ;
		double DsecDiff = deltaEvalSecDiff(st, aIndex, sIndex) * wSecDiff;
		//System.out.printf("Delta Values for new Node\nParent val = %f\nDminFilled = %f\nDPref = %f\nDPair = %f\nDsecDif = %f\n\n", st.getValue(),DminFilled,Dpref,Dpair,DsecDiff);
		return  st.getValue() + DminFilled + Dpref + Dpair + DsecDiff;
	}
	
	/**
	 * Calculate the change in Eval due to sections of a course being taught together.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalSecDiff(State st, int aIndex, int sIndex){
		double dSecDiffTotal = 0.0;
		if(!prob.Assignables[aIndex].isCourse) return 0; //Only apply this penalty to courses.
		
		for(int j = 0; j < prob.numberOfAssignables; j++){
			//If another course is assigned to the same slot
			if(prob.Assignables[j].isCourse && ( st.assign[j] == sIndex  || (st.assign[j] != -1 && prob.overlap[st.assign[j]][sIndex]) ) && (aIndex != j)) {
				
				//Compare the course name to see if they're equal.
				if( prob.Assignables[aIndex].name.substring(0, 8).equals(prob.Assignables[j].name.substring(0, 8)) ){
					dSecDiffTotal++;
				}
			}
		}
		return dSecDiffTotal * pen_section;
	}
	
	/**
	 * Calculate the change in Eval due to paired courses/labs
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	private double deltaEvalPair(State st, int aIndex, int sIndex) {
		double notPaired = 0;		
		// Loop through the current states assigned courses/labs
		// Loop through this course/labs Pair vector, for each pairing check if it's paired
		for( int j = 0; j < prob.Assignables[aIndex].paired.size(); j++ ){
			int iPair = prob.Assignables[aIndex].paired.get( j );
			if( st.assign[iPair] != -1 &&  (sIndex != st.assign[iPair] || !prob.overlap[sIndex][st.assign[iPair]]) ){
				notPaired++;
			}
		}
		return (notPaired * pen_notpaired);
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
		double dPrefTotal = 0;
			for(int j = 0; j < prob.getPreferences()[aIndex].length; j++){
				dPrefTotal += prob.getPreferences()[aIndex][j];
			}
			dPrefTotal -= prob.getPreferences()[aIndex][sIndex];
		
		return dPrefTotal;
	}

	
	/**
	 * Calculate the change in Eval due to minimum constraints being filled.
	 * 
	 * @param st The state as it is before the change
	 * @param aIndex The index of the assignable
	 * @param sIndex The index of the slot we are assigning to
	 * @return The change in eval-value. This can be negative.
	 */
	public double deltaEvalMinFilled(State st, int aIndex, int sIndex) {
		int missingCourses = 0;
		int missingLabs = 0;
		
		//Find the number of courses and labs that we still need to assign to meet minimums
		for(int i = 0; i < prob.numberOfSlots; i++){
			if((prob.Slots[i].getCourseMin() > st.numOfCourses[i])){
				missingCourses += (prob.Slots[i].getCourseMin() - st.numOfCourses[i]);
			}
			if(((prob.Slots[i].getLabMin() > st.numOfLabs[i]))){
				missingLabs += (prob.Slots[i].getLabMin() - st.numOfLabs[i]);
			}
		}
		
		//Find the number of unassigned courses and labs.
		Pair remaining = countRemaining(st);
		
		//If we're assigning a course, and the number of missing courses is greater than
		//the number of courses left to assign, we get worse by pen_coursemin.
		//Similar for pen_labmin.
		if(prob.Assignables[aIndex].isCourse){
			if(missingCourses >= remaining.first){
				if(prob.Slots[sIndex].getCourseMin() <= st.numOfCourses[sIndex]){
					return pen_coursemin;
				}
			}
		}else{
			if(missingLabs >= remaining.second){
				if(prob.Slots[sIndex].getLabMin() <= st.numOfLabs[sIndex]){
					return pen_labmin;
				}
			}
			
		}			
		return 0;
	}
	
}
