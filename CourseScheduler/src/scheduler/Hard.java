package scheduler;

public class Hard {
	
	
	/*I need these values, but I am not sure if this
	 * is the best way to get them.
	 */
	private Problem prob;
	private State state;

	Hard(State s, Problem p){
		state = s;
		prob = p;
	}
	
	
	/* Constr() will evaluate the 
	 * entire validity of state
	 * it checks that:
	 * 	-max number of courses has not been exceeded
	 *  -max number of labs has not been exceeded
	 *  -any course is not scheduled at the same time as an incompatible course
	 *  -any course is not in an unwanted slot
	 */
	public boolean Constr(){
		if(maxCheck() && compatibleCheck() && unwantedCheck()){
			return true;
		}
		return false;
	}
	
	// Checks the labs and courses are not over the limit of any slot
	private boolean maxCheck(){
		for (int i = 0; i< prob.numberOfSlots; i++){
			if((state.numOfCourses[i] > prob.Slots[i].getCourseMax()) ||
				(state.numOfLabs[i] > prob.Slots[i].getLabMax())){
				return false;
			}
		}
		return true;
	}
	
	// Checks that the all of the currently scheduled assignables are compatible with each other
	private boolean compatibleCheck(){
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
	private boolean unwantedCheck(){
		for(int i = 0; i < prob.numberOfAssignables; i++){
			if( prob.Assignables[i].unwanted.contains(state.assign[i])){
				return false;
			}
		}
		return true;
	}
	
//=============deltaConstr===========
	
	
	private int assignable;
	private int slot;
	
	/* deltaConstr() will evaluate the 
	 * validity of a single new assignment
	 * (In hopes to speed up the validity check
	 *  for each additional course assignment)
	 * it checks that:
	 * 	-max number of courses has not been exceeded in the given slot
	 *  -max number of labs has not been exceeded in the given slot
	 *  -the given course is not scheduled at the same time as an incompatible course
	 *  -the given course is not in an unwanted slot
	 */
	public boolean deltaConstr(int i, int s){
		assignable = i;
		slot = s;
		
		if(deltaMaxCheck() && deltaCompatibleCheck() && deltaUnwantedCheck()){
			return true;
		}
		return false;
	}
	
	// Checks the labs and courses are not over the limit of the given slot
	private boolean deltaMaxCheck(){

		if((state.numOfCourses[slot] > prob.Slots[slot].getCourseMax()) ||
			(state.numOfLabs[slot] > prob.Slots[slot].getLabMax())){
			return false;
		}
		return true;
	}
	
	// Checks that the all of the currently scheduled assignables are compatible with the given assignable
	private boolean deltaCompatibleCheck(){

		for( int unwanted : prob.Assignables[assignable].unwanted){
			if(state.assign[unwanted] == slot){
				return false;
			}
		}
		return true;
	}

	// Check that the given assignable is not in an unwanted slot
	private boolean deltaUnwantedCheck(){
		if( prob.Assignables[assignable].unwanted.contains(slot)){
			return false;
		}
		return true;
	}
}
