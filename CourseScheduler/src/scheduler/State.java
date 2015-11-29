package scheduler;

/**
 * Represents a node in the and-tree-based search.
 * This is a partial or full assignment of courses with data 
 * attached describing the value.
 * ############################################################
 * why are we keeping track of the problem in state?
 * this seems pretty space inefficient, also it means partasign contains prob contains partasign contains prob....
 * I think that we should have a state tostring method in prob that takes a state as a parameter
 * this way we can save on redundancies. or instead just have a function in prob that lets state pull the info we need to print it our properly
 * 
 * A: I agree that it's not very clean, but there isn't any extra overhead here.
 * 	  prob is a pointer/reference to a problem object. We are still only storing a single problem object in memory.
 */
public class State implements Comparable<State> {
	//We may as well keep track of which problem this state is for.
	private Problem prob;
	
	//static state counter
	private static long stateCount;
	
	public Problem getProb() { return prob; }
	public void setProb(Problem prob) { this.prob = prob; }

	//This is indexed by course.
	//assign[i] = j means that course i is assigned to slot j.
	public int assign[];
	
	//These are indexed by slot. 
	//numOfCourses[i] = the number of courses scheduled in slot i.
	public int numOfCourses[];
	public int numOfLabs[];
	private double value; //AKA the Eval-Value
	public long stateId;
	private boolean fullSolution;

	//TODO: Function to find lab/course bases on time and day
	
	//Need to specify the number of assignables and number of slots.
	//Because having a pointer to a problem object is technically optional.
	public State(int numAssignables, int numSlots) {
		prob = null;
		assign = new int[numAssignables];
		numOfCourses = new int[numSlots];
		numOfLabs = new int[numSlots];
		stateId = stateCount = 0;
		// Use -1 instead of $.
		for(int i=0; i<numAssignables; i++)
			assign[i] = -1;
		fullSolution = false;
	}
	
	//This creates a deep copy of the current state.
	private State(State parent) {
		this.assign = new int[parent.assign.length];
		this.prob = parent.prob;
		this.numOfCourses = new int[parent.numOfCourses.length];
		this.numOfLabs = new int[parent.numOfLabs.length];
		this.fullSolution = false;
		this.value = parent.getValue();
		System.arraycopy(parent.assign, 0, this.assign, 0, this.assign.length);
		System.arraycopy(parent.numOfCourses, 0, this.numOfCourses, 0, this.numOfCourses.length);
		System.arraycopy(parent.numOfLabs, 0, this.numOfLabs, 0, this.numOfLabs.length);
		stateId = ++stateCount;
	}
	
	/**
	 * Gives a string representing a state.
	 * This is also used to print out the final (solution) state at the end.
	 * Conforms to the format shown here: 
	 * http://pages.cpsc.ucalgary.ca/~denzinge/courses/433-fall2015/assigninput.html
	 */
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(String.format("State-ID = : %d\n", stateId));
		strb.append(String.format("Eval-Value: %f\n", value));
		
		//We can only do full output if prob is specified.
		if(prob == null) {
			strb.append("No more data can be given without specifying a problem.\n");
			return strb.toString();
		}
		
		//For each assignment, add a line that pairs it to its slot.
		for(int i=0; i<assign.length; i++) {
			String name = prob.Assignables[i].name;
			strb.append(name);
			
			//For when tabs are NOT GOOD ENOUGH for lining up data.
			for(int j=name.length();j<25;j++) strb.append(' ');
			
			if(assign[i] != -1) {
				Slot thisSlot = prob.Slots[assign[i]];
				strb.append(String.format(": %s, %5s", thisSlot.day, thisSlot.startTime));
			}
			else 
				strb.append(": UNASSIGNED");
				
			
			strb.append("\n");
		}
		
		return strb.toString();
	}
	public boolean isFullSolution() {
		return fullSolution;
	}
	public void setIsFullSolution(boolean fullSolution) {
		this.fullSolution= fullSolution ;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Make some babies.
	 * Note: This does not evaluate the validity of the transition!
	 * 
	 * @param aIndex The assignable we are assigning
	 * @param i The slot we're assigning it to.
	 * @return A brand new state that is the same as this one, except for one new assignment.
	 */
	public State makeChild(int aIndex, int sIndex) {
		
		//System.out.println(this.toString());
		State child = new State(this);
		if(prob.Assignables[aIndex].isCourse)
			child.numOfCourses[sIndex]++;
		else
			child.numOfLabs[sIndex]++;
		
		child.assign[aIndex] = sIndex;
		
		child.fullSolution = (aIndex == assign.length-1 && prob.evaluator.Constr(child)); 
		
		//Get the new value of the child.
		child.setValue(prob.evaluator.deltaEval(this, aIndex, aIndex));
		/*System.out.println("#######################################################");
		System.out.println(this.toString());
		System.out.println(child.toString());
		System.out.println("#######################################################");*/
		return child;
	}
	
	@Override
	public int compareTo(State other) {
        if (this.value - other.getValue() > 0) return 1;
        else if(this.value - other.getValue() < 0) return -1;
        else return 0;
	}
}
