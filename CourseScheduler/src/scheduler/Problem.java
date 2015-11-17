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
	public final Vector<Course> Assignables;
	public final Vector<Slot> Slots;
	
	//Apparently the problem input allows us a problem instance name.
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Problem() {
		Assignables = new Vector<Course>();
		Slots = new Vector<Slot>();
	}	
}
