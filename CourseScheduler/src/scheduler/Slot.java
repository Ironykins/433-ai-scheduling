package scheduler;

/**
 * This represents a slot into which labs and courses can both be scheduled.
 */
public class Slot {
	
	//Question: We should create a time variable?
	//Konrad Says: Nah. The time only matters for naming purproses.
	public final String startTime;
	
	//Is of the form "MO" or "TU" for courses
	//or "MO", "TU", and "FR" for Labs
	public final String day;
	public final int id;
	
	private int labMax;
	private int courseMax;
	private int labMin;
	private int courseMin;
	
	//Question: should we refer to Courses/Labs as index or other value?
	//A list of course indexes that are unwanted in this time slot
	int unwanted[];

	public Slot(int index, String day, String startTime) {
		this.id = index;
		this.day = day;
		this.startTime = startTime;
	}
	
	//We need to be able to compare slots. Two slots are equal if they have the same Day and Time.
	public boolean equals(Object toCompare) {
        if (!(toCompare instanceof Slot)) return false;
        Slot other = (Slot) toCompare;
        if(!other.startTime.equals(this.startTime)) return false;
        if(!other.day.equals(this.day)) return false;
        return true;
	}

    //Getters and setters.
	public int getLabMax() { return labMax; }
	public void setLabMax(int labMax) { this.labMax = labMax; }
	public int getCourseMax() { return courseMax; }
	public void setCourseMax(int courseMax) { this.courseMax = courseMax; }
	public int getLabMin() { return labMin; }
	public void setLabMin(int labMin) { this.labMin = labMin; }
	public int getCourseMin() { return courseMin; }
	public void setCourseMin(int courseMin) { this.courseMin = courseMin; }
}
