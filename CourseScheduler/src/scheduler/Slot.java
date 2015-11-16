package scheduler;

public class Slot {
	
	//Question: We should create a time variable?
	String startTime;
	//Is of the form "MO" or "TU" for courses
	//or "MO", "TU", and "FR" for Labs
	String day;
	int labMax;
	int courseMax;
	int labMin;
	int courseMin;
	
	//Question: should we refer to Courses/Labs as index or other value?
	//A list of course indexes that are unwanted in this time slot
	int unwanted[];

}
