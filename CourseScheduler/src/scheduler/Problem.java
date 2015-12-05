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
	public final Evaluator evaluator;
	public final Assignable[] Assignables;
	public final Slot[] Slots;
	public final int numberOfAssignables;
	public final int numberOfSlots;
	
	//The penalty for Lab and Course Minimum requirements, in the absolute best case.
	private int bestLabminPenalty;
	private int bestCourseminPenalty;

	public int getBestLabminPenalty() { return bestLabminPenalty; }
	public int getBestCourseminPenalty() { return bestCourseminPenalty; }
	
	//This is the preferences array. Indexed by [assignable][slot].
	//Can fetch in constant time. Maybe there's a better way to do this. I dunno.
	private int[][] preferences;
	public int[][] getPreferences() { return preferences; }
	public void setPreferences(int[][] preferences) { 
		if(preferences.length != Assignables.length || preferences[0].length != Slots.length)
			throw new IllegalArgumentException("Preferences must be size [assignables][slots]");
		this.preferences = preferences; 
	}
	
	//Here's the hack to get around time slots that overlap but are not the same slot.
	//Keep a 2d array of boolean values. overlap[i][j] == true iff slot i and slot j overlap.
	public final boolean[][] overlap;
	
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
		evaluator = new Evaluator(this);
		Assignables = assignables;
		Slots = slots;
		numberOfAssignables = assignables.length;
		numberOfSlots = slots.length;
		overlap = new boolean[slots.length][slots.length];
	}
	
	/**
	 * Computes the penalties for not meeting the minimum course/lab requirements in the _best_ case.
	 * Used for Tomas's incremental Eval function.
	 * 
	 * Requires that the evaluator knows the pen_coursemin.
	 */
	public void computeBestMinPenalties() {		
		int totalLabMin = 0;
		int totalCourseMin = 0;
		for(Slot s : Slots) {
			totalLabMin += s.getLabMin();
			totalCourseMin += s.getCourseMin();
		}
		
		int totalLabs = 0;
		int totalCourses = 0;
		for(Assignable a : Assignables) {
			if(a.isCourse) totalCourses++;
			else totalLabs++;
		}
		
		int labDifference = totalLabs - totalLabMin;
		int courseDifference = totalCourses - totalCourseMin;
		
		bestLabminPenalty = labDifference < 0 ? 0 : labDifference * evaluator.getPen_labmin();
		bestCourseminPenalty = courseDifference < 0 ? 0 : courseDifference * evaluator.getPen_coursemin();
	}
	
	/**
	 * Gets an assignable's ID from its name.
	 * 
	 * @param name The name of the assignable
	 * @return The index of the assignable. -1 if we can't find it.
	 */
	public int getAssignableId(String name) {
		for(int i=0; i<Assignables.length; i++)
			if(Assignables[i].name.equals(name))
				return i;
		return -1;
	}
	
	/**
	 * Gets a slot's ID from its day and time.
	 * 
	 * @param day The day of the slot eg. 'MO'
	 * @param time The starting time of the slot. eg. '8:00'
	 * @param isCourse Whether the slot is for courses or labs.
	 * @return The index of the slot. -1 if we can't find it.
	 */
	public int getSlotId(String day, String time, boolean isCourse) {
		for(int i=0; i<Slots.length; i++)
			if(Slots[i].day.equals(day) && Slots[i].startTime.equals(time))
				if(Slots[i].getCourseMax() > 0 && isCourse || Slots[i].getLabMax() > 0 && !isCourse)
					return i;
		return -1;
	}
	
	/**
	 * Returns a human-readable summary of all items in the problem.
	 */
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("Problem Name: ");
		strb.append(name);
		strb.append('\n');
		
		strb.append("Slots:\n");
		for(Slot s : Slots) {
			strb.append(s);
			strb.append('\n');
		}
		
		strb.append("Assignables:\n");
		for(Assignable a : Assignables) {
			strb.append(a);
			strb.append('\n');
			
			for(int inc : a.incompatible) 
				strb.append(String.format("\tIncompatible With: %s\n", Assignables[inc]));

			for(int inc : a.paired) 
				strb.append(String.format("\tPaired With: %s\n", Assignables[inc]));
			
			for(int inc : a.unwanted) 
				strb.append(String.format("\tUnwanted in: %s\n", Slots[inc]));
		}
		
		return strb.toString();
	}
}
