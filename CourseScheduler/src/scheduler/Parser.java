package scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;

/**
 * @author konrad
 *
 * This class parses an input file to produce a Problem definition, with all knowledge represented.
 */
public class Parser {
	//For generating unique IDs for slots and assignables.
	private int slotIndex;
	private int assignableIndex;
	
	//Compiling patterns has some overhead, so we only do it once.
	private Pattern slotPattern;
	private Pattern coursePattern;
	private Pattern labPattern;
	private Pattern assignmentPattern;
	private Pattern preferencesPattern;
	private Pattern pairPattern;
	
	//Used for building the initial structures.
	//Later cast to arrays.
	private Vector<Assignable> assignables;
	private Vector<Slot> slots;
	private String name;
	private State partAssign;
	private int[][] preferences;
	
	public Parser() {
		assignables = new Vector<Assignable>();
		slots = new Vector<Slot>();
		
		//Matches and extracts lines of the form: DD, HH:MM, INT, INT
		slotPattern = Pattern.compile("^([A-Z]{2})[\\s]*,[\\s]*([0-9]{1,2}:[0-9]{2})[\\s]*,[\\s]*([0-9]*)[\\s]*,[\\s]*([0-9]*)[\\s]*$");
		
		//Matches and extracts lines of the form: CourseCode, CourseNum, LEC, LecNum
		coursePattern = Pattern.compile("^([A-Z]{4})[\\s]+([0-9]+)[\\s]+LEC[\\s]+([0-9]+)");
				
		//Matches and extracts lines of the form: SENG 311 (Optional: LEC 01) TUT 01
		labPattern = Pattern.compile("^([A-Z]{4})[\\s]+([0-9]+)[\\s]+(?:LEC[\\s]+([0-9]+)[\\s]+){0,1}(?:TUT|LAB)[\\s]+([0-9]+)");
		
		//Matches and extracts lines of the form: (Assignable Name), Day, Time
		assignmentPattern = Pattern.compile("^([0-9A-Za-z\\s]*)[\\s]*,[\\s]*([A-Z]{2})[\\s]*,[\\s]*([0-9]{1,2}:[0-9]{2})");
		
		//Matches and extracts lines of the form: DD,  HH:MM, (Assignable Name), INT
		preferencesPattern = Pattern.compile("^([A-Z]{2})[\\s]*,[\\s]*([0-9]{1,2}:[0-9]{2})[\\s]*,[\\s]*([0-9A-Za-z\\s]*)[\\s]*,[\\s]([0-9]*)");
		
		//Matches and extracts lines of the form: (Assignable Name), (Assignable Name)
		pairPattern = Pattern.compile("^([0-9A-Za-z\\s]*)[\\s]*,[\\s]*([0-9A-Za-z\\s]*)");	
	}
	
	/**
	 * Parses an input file, returns our problem representation. 
	 * 
	 * @param inFile The input file, as a file path.
	 * @return The problem representation
	 */
	public Problem parseFile(String inFile) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
			return this.parseBuffer(br);
		}
	}
	
	/**
	 * Parses input from a buffered reader.
	 * 
	 * @param br The buffered reader.
	 * @return The problem representation.
	 */
	public Problem parseBuffer(BufferedReader br) throws IOException {
		String line;
	    while ((line = br.readLine()) != null) {
	    	String trimmed = line.trim();
	    	//Look for lines that match sections of the input.
	    	//For each of these lines, call the appropriate subroutine.
	    	switch(trimmed) {
		    	case "Name:": parseName(br); break;
		    	case "Course slots:": parseCourseSlots(br); break;
		    	case "Lab slots:": parseLabSlots(br); break;
		    	case "Courses:": parseCourses(br); break;
		    	case "Labs:": parseLabs(br); break;
		    	case "Not compatible:": parseNotCompatible(br); break;
		    	case "Unwanted:": parseUnwanted(br); break;
		    	case "Preferences:": parsePreferences(br); break;
		    	case "Pair:": parsePairs(br); break;
		    	case "Partial assignments:": parsePartassign(br); break;
		    	default: //If the line is not whitespace and we can't parse it, we have a problem.
		    		if (trimmed.length() > 0)
		    			throw new IOException(String.format("Could not parse line: %s", line));
	    	}
	    }
	    
	    //Return a new problem with all this information.
	    Problem prob = new Problem(assignables.toArray(new Assignable[0]), slots.toArray(new Slot[0]));
	    prob.setName(name);
	    prob.setPartAssign(partAssign);
	    prob.setPreferences(preferences);
	    return prob;
	}
	
	//Parse the name field of the input.
	private void parseName(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			if(line.isEmpty()) return;
			name = line;
		}
	}
	
	//Lines of format:
	//Day, Start time, coursemax, coursemin
	//MO, 8:00, 3, 2
	private void parseCourseSlots(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = slotPattern.matcher(line);
			if(m.find()) {
				//Shouldn't need input sanitation here. The regex should only match things that are okay to parse.
				Slot newSlot = new Slot(slotIndex++, m.group(1), m.group(2));
				newSlot.setCourseMax(Integer.parseInt(m.group(3)));
				newSlot.setCourseMin(Integer.parseInt(m.group(4)));
				slots.add(newSlot);
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
	    		throw new IOException(String.format("Could not parse line as course slot: %s", line));
			}
	    }
	}
	
	//Lines of format: 
	//Day, Start time, coursemax, coursemin
	//MO, 8:00, 3, 2
	private void parseLabSlots(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = slotPattern.matcher(line);
			if(m.find()) {
				boolean found = false;
				//First, see if a slot like this one already exists.
				for(Slot s : slots) {
					//If this slot already exists, just use it.
					if(s.day.equals(m.group(1)) && s.startTime.equals(m.group(2))) { 
						s.setLabMax(Integer.parseInt(m.group(3)));
						s.setLabMin(Integer.parseInt(m.group(4)));
						found = true;
						break;
					}
				}
				
				//If the slot doesn't exist, add it.
				if(!found) { 
					Slot newSlot = new Slot(slotIndex++, m.group(1), m.group(2));
					newSlot.setLabMax(Integer.parseInt(m.group(3)));
					newSlot.setLabMin(Integer.parseInt(m.group(4)));
					slots.add(newSlot);
				}
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as lab slot: %s", line));
			}
		}
	}

	//Lines of format:
	//Course-Code Course-Number LEC Lecture-number.
	//CPSC 433 LEC 01
	private void parseCourses(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = coursePattern.matcher(line);
			if(m.find()) {
				String name = m.group(0).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
				Assignable newCourse = new Assignable(assignableIndex++, name, true);
				assignables.add(newCourse);
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as course: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Course-Code Course-Number LEC Lecture-number (TUT|LAB) Lab-Number
	//SENG 311 LEC 01 TUT 01
	private void parseLabs(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = labPattern.matcher(line);

			if(m.find()) {
				//Groups: 1: Course code 2: Course Number 3: Lecture Number (NULL=LEC 01) 4: Lab/Tut Number
				String name = m.group(0).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
				Assignable newLab = new Assignable(assignableIndex++, name, false);
				assignables.add(newLab);
				String courseName = String.format("%s %s LEC %s", m.group(1), m.group(2), m.group(3) != null ? m.group(3) : "01");
				
				//Find the related lecture. Make them both incompatible with one another.
				boolean found = false;
				for(Assignable ass : assignables) {
					if(ass.name.equals(courseName)) { 
						ass.incompatible.add(newLab.id);
						newLab.incompatible.add(ass.id);
						found = true;
						break;
					}
				}
				
				if(!found) 
					throw new IllegalStateException(String.format("Tried to add %s labs, could not find the related course!", line));
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as lab: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Assignable Name, Assignable Name
	//SENG 311 LEC 01 TUT 01, CPSC 433 LEC 01
	private void parseNotCompatible(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = pairPattern.matcher(line);

			if(m.find()) {
				//Group1 contains first one. Group2 contains second one.
				//Add First's ID to Second's incompatible vector. And vice versa.
				Assignable a1 = null;
				Assignable a2 = null;
				
				//First, see if a slot like this one already exists.
				for(Assignable ass : assignables) {
					String name1 = m.group(1).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					String name2 = m.group(2).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					
					if(ass.name.equals(name1)) { a1 = ass; };
					if(ass.name.equals(name2)) { a2 = ass; };
					if(a1 != null && a2 != null) break;
				}
				
				//Mark them as incompatible.
				if(a1 != null && a2 != null) {
					a1.incompatible.add(a2.id);
					a2.incompatible.add(a1.id);
				}
				else
					throw new IllegalStateException(String.format("Tried to add %s to incompatible, but one of the assignables does not exist!", line));
				
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as not-compatible: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Assignable Name, Day, Time
	//CPSC 433 LEC 01, MO, 8:00
	private void parseUnwanted(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = assignmentPattern.matcher(line);

			if(m.find()) {
				//Group1 contains assignable name. Group2 contains day. Group 3 contains time.
				
				int slotIndex = -1;
				for(Slot s : slots) {
					if(s.day.equals(m.group(2)) && s.startTime.equals(m.group(3))) {
						slotIndex = s.id;
					}
				}
				
				if(slotIndex == -1) 
					throw new IllegalStateException(String.format("Tried to add %s to unwanted, but slot does not exist!", line));
				
				boolean found = false;
				for(Assignable ass : assignables) {
					String name = m.group(1).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					if(ass.name.equals(name)) {
						ass.unwanted.add(slotIndex);
						found = true;
						break;
					}
				}
				
				if(!found)
					throw new IllegalStateException(String.format("Tried to add %s to unwanted, but assignable does not exist!", line));
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as unwanted: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Day, Time, Assignable Name, Preference Value
	private void parsePreferences(BufferedReader br) throws IOException {
		String line;
		preferences = new int[assignableIndex][slotIndex];
		
		while((line = br.readLine()) != null) {
			Matcher m = preferencesPattern.matcher(line);

			if(m.find()) {
				//Group1 contains day, 2 contains time, 3 contains assignable, 4 contains value.
				
				//Find our slot.
				int sid = -1;
				for(Slot s : slots) {
					if(s.day.equals(m.group(1)) && s.startTime.equals(m.group(2))) {
						sid = s.id;
					}
				}
				
				//Find our assignable.
				int aid = -1;
				for(Assignable ass : assignables) {
					String name = m.group(3).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					if(ass.name.equals(name)) {
						aid = ass.id;
						break;
					}
				}
				
				if(sid == -1) 
					throw new IllegalStateException(String.format("Could not add pair %s to preferences. Could not find slot.", line));
				if(aid == -1)
					throw new IllegalStateException(String.format("Could not add pair %s to preferences. Could not find assignable.", line));
				
				preferences[aid][sid] = Integer.parseInt(m.group(4));
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as preference: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Assignable Name, Assignable Name
	private void parsePairs(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = pairPattern.matcher(line);

			if(m.find()) {
				//Group1 contains an assignable. Group 2 contains an assignable.
				//Add First's ID to Second's incompatible vector. And vice versa.
				Assignable a1 = null;
				Assignable a2 = null;
				
				//First, see if a slot like this one already exists.
				for(Assignable ass : assignables) {
					String name1 = m.group(1).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					String name2 = m.group(2).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					
					if(ass.name.equals(name1)) { a1 = ass; };
					if(ass.name.equals(name2)) { a2 = ass; };
					if(a1 != null && a2 != null) break;
				}
				
				//Mark them as incompatible.
				if(a1 != null && a2 != null) {
					a1.paired.add(a2.id);
					a2.paired.add(a1.id);
				}
				else
					throw new IllegalStateException(String.format("Tried to add %s to pairs, but one of the assignables does not exist!", line));
				
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as pair: %s", line));
			}
		}
	}
	
	//Lines of format:
	//Assignable Name, Day, Time
	private void parsePartassign(BufferedReader br) throws IOException {
		String line;
		partAssign = new State(assignableIndex, slotIndex);
		
		while((line = br.readLine()) != null) {
			Matcher m = assignmentPattern.matcher(line);

			if(m.find()) {
				//Group1 contains an assignable. Group 2 contains a day, group 3 contains a time.
				//Creates a state object called partassign.
				
				//Find our assignable.
				Assignable toAssign = null;
				for(Assignable ass : assignables) {
					String name = m.group(1).trim().replaceAll(" +", " "); //Remove duplicate whitespace.
					if(ass.name.equals(name)) {
						toAssign = ass;
						break;
					}
				}
				
				//Find our slot.
				int slotIndex = -1;
				for(Slot s : slots) {
					if(s.day.equals(m.group(2)) && s.startTime.equals(m.group(3))) {
						slotIndex = s.id;
					}
				}
				
				if(slotIndex == -1 || toAssign == null) 
					throw new IllegalStateException(String.format("Could not add pair %s to partAssign. Could not find assignable or slot.", line));
				
				//Add to the partial assignment.
				//NOTE: This does not check that this is a valid assignment. A call to eval() may be necessary.
				partAssign.assign[toAssign.id] = slotIndex;
				if(toAssign.isCourse) partAssign.numOfCourses[slotIndex]++;
				else partAssign.numOfLabs[slotIndex]++;
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as partial assignment: %s", line));
			}
		}
	}
}