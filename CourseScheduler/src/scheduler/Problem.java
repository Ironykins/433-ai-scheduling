package scheduler;

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
	public final Assignable[] Assignables;
	public final Slot[] Slots;
	
	//This is the preferences array. Indexed by [assignable][slot].
	//Can fetch in constant time. Maybe there's a better way to do this. I dunno.
	private int[][] preferences;
	public int[][] getPreferences() { return preferences; }
	public void setPreferences(int[][] preferences) { 
		if(preferences.length != Assignables.length || preferences[0].length != Slots.length)
			throw new IllegalArgumentException("Preferences must be size [assignables][slots]");
		this.preferences = preferences; 
	}
	
	//The partial assignment for this problem. Serves as our starting state.
	private State partAssign;
	public State getPartAssign() { return partAssign; }
	public void setPartAssign(State partAssign) { this.partAssign = partAssign;	}	
	
	//Apparently the problem input specifies a problem instance name.
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	//We have an unchanging number of assignables and slots. Must be initialized with these.
	public Problem(Assignable[] assignables, Slot[] slots) {
		Assignables = assignables;
		Slots = slots;
	}
	
}
