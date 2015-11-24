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
		 * puts the partassign as the head of our queue
		 * makes our bestSol null to begin with
		 * @param prob problem to solve
		 * 
		 */
		public Control(Problem prob){
			this.prob = prob;
			stateQueue = new LinkedList<State>();
			stateQueue.addFirst(prob.getPartAssign());
			bestSol = null;
		}
		
		/*
		 * This function is to be run after set up. It will compute the tree and return the best answer
		 * @returns State Solution
		 */
		public State solve(){
			return stateQueue.getFirst();
		}
		/*
		 * function takes the head of the list and creates all of its successor states
		 * TODO:
		 */
		private void expandHead(){
			State node = stateQueue.removeFirst();
			LinkedList<State> children = new LinkedList<State>();
			
			
		}
		
}
