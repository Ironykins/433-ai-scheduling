package scheduler;
//Labs and Courses seem pretty much the same,
//So I think it would be ok to only have one object representing both
public class Assignable {
	
	/* Not sure if name is the best variable name
	 * In instructions it is referred to as "Course/Lab Identifier"
	 * Of the form course-code course-number section-number
	 * CPSC 433 LEC 01
	 * or if it is a lab course-code course-number lecture-section-number (tutorial-type section-number) can be repeated
	 * CPSC 433 LEC 02 TUT 01
	 * or CPSC 433 LEC 03 TUT 02 LAB 02
	 */
	public final String name;
	public final int id;
	
	//false if lab, true if course
	//This will make it easier to test the constraints
	//And help decide if we inc a slot's course # or lab #
	public final boolean isCourse;
	
	public Assignable(int id, String name, boolean isCourse) {
		this.id = id;
		this.name = name;
		this.isCourse = isCourse;
	}
	
	//Question: should we refer to other courses by index, by their object or Name or some other value?
	//A list of index that this course is incompatible
	int incompatable[];
	
	public String toString() {
		return String.format("Id: %d, Name: %s, Type: %s", id, name, isCourse ? "Course" : "Lab");
	}
}
