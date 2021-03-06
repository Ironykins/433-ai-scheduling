package scheduler;

import java.util.Vector;

/**
 * Represents both labs and courses.
 */
public class Assignable {
	
	/* 
	 * Not sure if name is the best variable name
	 * In instructions it is referred to as "Course/Lab Identifier"
	 * Of the form course-code course-number section-number
	 * CPSC 433 LEC 01
	 * or if it is a lab course-code course-number lecture-section-number (tutorial-type section-number) can be repeated
	 * CPSC 433 LEC 02 TUT 01
	 * or CPSC 433 LEC 03 TUT 02 LAB 02
	 */
	public final String name;
	public final int id;
	public final int sectionNumber;
	private int labNumber = 0;
	private boolean isEvening;
	
	//false if lab, true if course
	//This will make it easier to test the constraints
	//And help decide if we inc a slot's course # or lab #
	public final boolean isCourse;
	
	public Assignable(int id, String name, boolean isCourse, int sectionNumber) {
		this.id = id;
		this.name = name;
		this.isCourse = isCourse;
		this.sectionNumber = sectionNumber;
		incompatible = new Vector<Integer>();
		unwanted = new Vector<Integer>();
		paired = new Vector<Integer>();
	}
	
	//A list of assignable ids that this course is incompatible with
	public final Vector<Integer> incompatible;
	
	//A list of slot ids that this course is incompatible with.
	public final Vector<Integer> unwanted;
	
	//A list of assignable ids that this course is paired with.
	public final Vector<Integer> paired;
	
	public String toString() {
		return String.format("Id: %d, Name: %s, Type: %s, Evening: %s", id, name, isCourse ? "Course" : "Lab", isEvening ? "Yes" : "No");
	}
	
	//We need to be able to compare assignables. Two of them are equal if they have the same name.
	public boolean equals(Object toCompare) {
        if (!(toCompare instanceof Assignable)) return false;
        Assignable other = (Assignable) toCompare;
        if(!other.name.equals(this.name)) return false;
        return true;
	}

	public int getLabNumber() {
		return labNumber;
	}

	public void setLabNumber(int labNumber) {
		this.labNumber = labNumber;
	}

	public boolean isEvening() {
		return isEvening;
	}

	public void setEvening(boolean isEvening) {
		this.isEvening = isEvening;
	}
}
