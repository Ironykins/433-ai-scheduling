package scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author konrad
 *
 * This class parses an input file to produce a Problem definition, with all knowledge represented.
 */
public class Parser {
	private Problem prob;
	private Pattern slotPattern;
	private Pattern coursePattern;
	private Pattern labPattern;
	private int slotIndex;
	private int assignableIndex;
	
	public Parser() {
		prob = new Problem();
		
		//Matches and extracts lines of the form: DD, HH:MM, INT, INT
		slotPattern = Pattern.compile("^([A-Z]{2})[\\s]*,[\\s]*([0-9]{1,2}:[0-9]{2})[\\s]*,[\\s]*([0-9]*)[\\s]*,[\\s]*([0-9]*)[\\s]*$");
		
		//Matches and extracts lines of the form: CourseCode, CourseNum, LEC, LecNum
		coursePattern = Pattern.compile("^([A-Z]{4})[\\s]+([0-9]+)[\\s]+LEC[\\s]+([0-9]+)");
				
		//Matches and extracts lines of the form: SENG 311 (Optional: LEC 01) TUT 01
		labPattern = Pattern.compile("^([A-Z]{4})[\\s]+([0-9]+)[\\s]+(?:LEC[\\s]+([0-9]+)[\\s]+){0,1}(?:TUT|LAB)[\\s]+([0-9]+)");
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
	    	//Look for lines that match sections of the input.
	    	//For each of these lines, call the appropriate subroutine.
	    	switch(line) {
		    	case "Name:": parseName(br); break;
		    	case "Course slots:": parseCourseSlots(br); break;
		    	case "Lab slots:": parseLabSlots(br); break;
		    	case "Courses:": parseCourses(br); break;
		    	case "Labs:": parseLabs(br); break;
		    	//case "Not compatible:": parseNotCompatible(br); break;
		    	//case "Unwanted:": parseUnwanted(br); break;
		    	//case "Preferences:": parsePreferences(br); break;
		    	//case "Pair:": parsePairs(br); break;
		    	//case "Partial assignments:": parsePartAssign(br); break;
		    	
		    	default: 
		    		//If the line is not whitespace and we can't parse it, we have a problem.
		    		if (line.trim().length() > 0) return prob; //TODO: Remove this return statement when the parser is done. Uncomment next line.
		    			//throw new IOException(String.format("Could not parse line: %s", line));
	    	}
	    }
	    
	    return prob;
	}
	
	//Parse the name field of the input.
	public void parseName(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			if(line.isEmpty()) return;
			prob.setName(line);
		}
	}
	
	//Lines of format: 
	//Day, Start time, coursemax, coursemin
	//MO, 8:00, 3, 2
	public void parseCourseSlots(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = slotPattern.matcher(line);
			if(m.find()) {
				//Shouldn't need input sanitation here. The regex should only match things that are okay to parse.
				Slot newSlot = new Slot(slotIndex++, m.group(1), m.group(2));
				newSlot.setCourseMax(Integer.parseInt(m.group(3)));
				newSlot.setCourseMin(Integer.parseInt(m.group(4)));
				prob.Slots.add(newSlot);
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
	public void parseLabSlots(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = slotPattern.matcher(line);
			if(m.find()) {
				boolean found = false;
				//First, see if a slot like this one already exists.
				for(Slot s : prob.Slots) {
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
					prob.Slots.add(newSlot);
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
	public void parseCourses(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = coursePattern.matcher(line);
			if(m.find()) {
				Assignable newCourse = new Assignable(assignableIndex++, m.group(0), true);
				prob.Assignables.add(newCourse);
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
	public void parseLabs(BufferedReader br) throws IOException {
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = labPattern.matcher(line);

			if(m.find()) {
				Assignable newLab = new Assignable(assignableIndex++, m.group(0), false);
				prob.Assignables.add(newLab);
				//TODO: Make incompatible with the related course.
				//Groups: 1: Course code 2: Course Number 3: Lecture Number (NULL=LEC 01) 4: Lab/Tut Number
			}
			else { //If the line is not whitespace and we can't parse it, we have a problem.	
	    		if (line.trim().length() == 0) return;
    			throw new IOException(String.format("Could not parse line as lab: %s", line));
			}
		}
	}
	
	public Problem getProb() {
		return prob;
	}
}