package scheduler;

public class State {

	/*These arrays will all be the same size
	 * where the index represents a single course or lab
	 * and all the indexes match between the three arrays
	 * E.g. If currentSolution[x] maps to course A, 
	 * then numOfCourses[x] and numOfLabs[x] map to A as well
	 */
	public int assign[];
	
	public int numOfCourses[];
	public int numOfLabs[];
	public boolean fullSolution;

	//TODO: Function to find lab/course bases on time and day
	
	public State(int numAssignables, int numSlots) {
		assign = new int[numAssignables];
		numOfCourses = new int[numSlots];
		numOfLabs = new int[numSlots];
		
		// Use -1 instead of $.
		for(int i=0; i<numAssignables; i++)
			assign[i] = -1;
	}
}