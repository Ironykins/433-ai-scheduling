package scheduler;

import java.util.LinkedList;

public class Control {

		//contains main poblem we want to solve
		private Problem prob;
		//best running solution, starts as worst possible solution
		private State bestSol;
		//queue to hold working states of tree
		private LinkedList<State> stateQueue;
		
		/*
		 * Constructor creates new control for solving prob
		 * @param prob problem to solve
		 * 
		 */
		public Control(Problem prob){
			this.prob = prob;
			stateQueue = new LinkedList<State>();
			stateQueue.addFirst(prob.getPartAssign());
			
			
		}
}
