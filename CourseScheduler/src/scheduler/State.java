package scheduler;

public class State {

	/*These arrays will all be the same size
	 * where the index represents a single course or lab
	 * and all the indexes match between the three arrays
	 * E.g. If currentSolution[x] maps to course A, 
	 * then numOfCourses[x] and numOfLabs[x] map to A as well
	 */
	int assign[];
	//Question: Should un-assigned courses be '-1' because it is an array of int's and I don't think $ will work
	int numOfCourses[];
	int numOfLabs[];
	boolean fullSolution;

	//TODO: Function to find lab/course bases on time and day

}
