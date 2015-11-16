package scheduler;

public class Hard {
	
	boolean Constr(int[] assign){
		if(maxCheck(assign) && compatibleCheck(assign) && unwantedCheck(assign)){
			return true;
		}
		return false;
	}
	
	//Just combine these two so that we only have to loop through once.
	//boolean courseMaxCheck(int[] assign)
	//boolean labMaxCheck(int[] assign)
	boolean maxCheck(int[] assign){
		return false;
	}
	
	/* Rather than have a special check that
	 * looks at whether a lab is scheduled at the 
	 * same time as its course
	 * we are going to add this to the incompatible 
	 * list of the courses (or labs)
	 */
	
	boolean compatibleCheck(int[] assign){
		return false;
	}
	
	/* I don't think we need to check part assign
	 * 
	 */

	boolean unwantedCheck(int[] assign){
		return false;
	}
}
