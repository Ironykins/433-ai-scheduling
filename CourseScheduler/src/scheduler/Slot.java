package scheduler;

/**
 * This represents a slot into which labs and courses can both be scheduled.
 */
public class Slot {
	//Is of the form "MO" or "TU" for courses
	//or "MO", "TU", and "FR" for Labs
	public final String day;
	public final String startTime;
	
	public final int id;
	
	private int labMax;
	private int courseMax;
	private int labMin;
	private int courseMin;
	
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
	
	//Nice for debugging.
	public String toString() {
		return String.format(
				"Id: %d, Day: %s, Time: %s, LabMax: %d, LabMin: %s, CourseMax: %d, CourseMin: %s",
				id, day, startTime, labMax, labMin, courseMax, courseMin);
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
