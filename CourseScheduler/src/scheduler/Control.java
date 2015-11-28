package scheduler;

import java.util.Stack;
import java.util.LinkedList;

public class Control {
		private Problem prob; //contains main problem we want to solve
		private State bestSol; //best running solution, starts as worst possible solution
		//queue to hold working states of tree
		private Stack<State> stateStack;
		
		/*
		 * Constructor creates new control for solving prob
		 * puts the partassign as the head of our queue
		 * makes our bestSol null to begin with
		 * @param prob problem to solve
		 * 
		 */
		public Control(Problem prob){
			this.prob = prob;
			stateStack = new Stack<State>();
			stateStack.push(prob.getPartAssign());
			bestSol = null;
		}
		
		/*
		 * This function is to be run after set up. It will compute the tree and return the best answer
		 * @returns State Solution
		 */
		public State solve(){
			bestSol = stateStack.peek();
			//need a way to check if a solution is valid/complete.
			//will check for that, and then check that its the only element in the list
			while(!stateStack.isEmpty())
				expandHead();
			
			return bestSol;
		}
		/*
		 * function takes the head of the list, creates its children, adds them to the front of the list
		 * TODO:
		 */
		private void expandHead(){
			//take the head of our list
			State ex = stateStack.pop();
			//generate a list of valid child states
			LinkedList<State> children = createChildren(ex);
			//add our children to the front of the queue, should probably order them before this
			/***************
			 * Here we need to order the children so the first element in the list after we add all of them is the child we want to expand
			***************/
			stateStack.addAll(0,children);
			
		}
		
		/*
		 * function takes a state and creates a list of children states
		 * @param state the state to be expanded
		 * TODO:
		 */
		private LinkedList<State> createChildren(State st){
			//declare the list of children to return
			LinkedList<State> children = new LinkedList<State>();
			//now we want to generate all valid children
			//this along with expand head should include pruning
			//we could generate all children satisfying hard constraints here and check eval in expandHead
			//I think it would be more efficient to create a child and then check its eval and constraint as we go	
			
			return children;
			
		}
}
