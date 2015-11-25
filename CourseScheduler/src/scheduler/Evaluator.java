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
	private Problem prob;
	
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
	 *  
	 *  @param s The state we are checking for validity
	 *  @return True if the state is valid. False otherwise.
	 */
	public boolean Constr(State state){
		if(maxCheck(state) && compatibleCheck(state) && unwantedCheck(state)){
			return true;
		}
		return false;
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
			for( int incompatible : prob.Assignables[i].incompatible){
				if(state.assign[incompatible] == state.assign[i]){
					return false;
				}
			}
		}
		return true;
	}

	// Check that all assignables are not in an unwanted slot
	private boolean unwantedCheck(State state){
		for(int i = 0; i < prob.numberOfAssignables; i++){
			if( prob.Assignables[i].unwanted.contains(state.assign[i])){
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
		return deltaMaxCheck(state, aIndex, sIndex) && 
				deltaCompatibleCheck(state, aIndex, sIndex) && 
				deltaUnwantedCheck(aIndex, sIndex);
	}
	
	// Checks the labs or courses will not be over the limit if we assign.
	private boolean deltaMaxCheck(State state, int aIndex, int sIndex) {
		if(prob.Assignables[aIndex].isCourse)
			return state.numOfCourses[sIndex] < prob.Slots[sIndex].getCourseMax();
		else
			return state.numOfLabs[sIndex] < prob.Slots[sIndex].getLabMax();
	}
	
	// Checks that the all of the currently scheduled assignables are compatible with the given assignable
	private boolean deltaCompatibleCheck(State state, int aIndex, int sIndex){
		for(int i=0; i<state.assign.length; i++)
			//If an assignment has the same slot, it can't be incompatible with this assignable.
			if(state.assign[i] == sIndex && prob.Assignables[i].unwanted.contains(aIndex))
				return false;
				
		return true;
	}

	// Check that the given assignable is not in an unwanted slot
	private boolean deltaUnwantedCheck(int aIndex, int sIndex){
		return prob.Assignables[aIndex].unwanted.contains(sIndex);
	}
}
