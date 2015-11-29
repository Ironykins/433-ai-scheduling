package scheduler;

import java.util.Stack;
import java.util.LinkedList;

public class Control {
		private Problem prob; //contains main problem we want to solve
		private State bestSol; //best running solution, starts as worst possible solution
		private Stack<State> stateStack; //queue to hold working states of tree
		private int headsPopped =1;
		/**
		 * Constructor creates new control for solving prob
		 * puts the partassign as the head of our queue
		 * makes our bestSol null to begin with
		 * @param prob problem to solve
		 */
		public Control(Problem prob){
			this.prob = prob;
			stateStack = new Stack<State>();
			State startState = prob.getPartAssign();
			startState.setValue(prob.evaluator.eval(startState));
			stateStack.push(startState);
			bestSol = null;
		}
		
		/**
		 * This function is to be run after set up. It will compute the tree and return the best answer
		 * @return State Solution
		 */
		public State solve(){
			//need a way to check if a solution is valid/complete.
			//will check for that, and then check that its the only element in the list
			int maxStates = 0;
			while(!stateStack.isEmpty()) {
				//this turns it into an or tree
				if(bestSol != null) break;
				if(stateStack.size()>maxStates) maxStates = stateStack.size();
				if(headsPopped % 10000000 == 0) System.out.printf("@@@@@@@@@@@@@@@@@@@@@@@@@@\nHead popping iteration number : %d\nMax States in the list :%d\nbestSol?: %b\n@@@@@@@@@@@@@@@@@@@@@@@@\n",headsPopped,maxStates,!(bestSol == null));

				expandHead();
				headsPopped++;
			}
			System.out.printf("max states in the list = %d\nwe popped %d heads\n",maxStates,headsPopped);
			return bestSol;
		}
		
		/**
		 * function takes the head of the list, creates its children, adds them to the front of the list
		 * TODO:
		 */
		private void expandHead(){
			//take the head of our list
			//System.out.println("States: "+ stateStack.size());
			State st = stateStack.pop();

			
			if(st.isFullSolution() ) {
				//If it's better than our best, it becomes our new best.
				if(bestSol == null || bestSol.getValue() > st.getValue()) {
					bestSol = st;
					System.out.printf("Best Solution Found!\n%s\nHeads popped = %d\nStates in list = %d\n", bestSol.toString(),headsPopped,stateStack.size());
				}
			} else {
				//generate a list of valid child states
				//add our children to the front of the queue, should probably order them before this
				/***************
				 * Here we need to order the children so the first element in the list after we add all of them is the child we want to expand
				***************/
				LinkedList<State> children = createChildren(st);
				if(children != null) stateStack.addAll(children);
			}
		}
		
		/**
		 * function takes a state and creates a list of children states
		 * @param state the state to be expanded
		 * @return The children created as a result of the expansion.
		 * TODO:
		 */
		private LinkedList<State> createChildren(State st){
			//declare the list of children to return
			LinkedList<State> children = new LinkedList<State>();
			//now we want to generate all valid children
			//this along with expand head should include pruning
			//we could generate all children satisfying hard constraints here and check eval in expandHead
			//I think it would be more efficient to create a child and then check its eval and constraint as we go
			
			//Get first unassigned assignable, all children will assign this assignable
			int aIndex = -1;
			for(int i=0;i<st.assign.length;i++)
				if(st.assign[i] == -1) {
					aIndex = i;
					break; 
				}
			//System.out.println(aIndex);
			//now we want to put it in every available slot.
			if(aIndex == -1) return null;
			if(aIndex == prob.numberOfAssignables-1)
				System.out.printf("State Number : %d\n%s\n",st.stateId,st.toString());
			
			for(int sIndex=0;sIndex<prob.Slots.length;sIndex++){
				//if this is a valid assignment
				if(prob.evaluator.deltaConstr(st, aIndex, sIndex))
					children.push(st.makeChild(aIndex, sIndex));
					
			}
			
			return orderChildren(children);
			
		}

		private LinkedList<State> orderChildren(LinkedList<State> children) {
			// TODO method to order children somehow. This is where we could change to the OR tree or not
			return children;
		}
}
