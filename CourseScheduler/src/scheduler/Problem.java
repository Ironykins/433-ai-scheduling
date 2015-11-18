package scheduler;

import java.util.Vector;

/**
 * @author konrad
 *
 * This class represents a problem, as taken from an input file.
 * It contains a list of constraints, courses, labs, etc.
 * Should in some way represent all attributes that make up a parsed problem definition.
 * 
 * The data here should be static, and assigned when the program parses the input file.
 */
public class Problem {
	public final Vector<Assignable> Assignables;
	public final Vector<Slot> Slots;
	
	//The partial assignment for this problem. Serves as our starting state.
	private State partAssign;
	
	//Apparently the problem input allows us a problem instance name.
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Problem() {
		Assignables = new Vector<Assignable>();
		Slots = new Vector<Slot>();
	}
	
	public State getPartAssign() { return partAssign; }
	public void setPartAssign(State partAssign) { this.partAssign = partAssign;	}	
}
