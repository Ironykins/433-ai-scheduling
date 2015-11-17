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

	public Parser() {
		prob = new Problem();
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
		    	//case "Lab slots:": parseLabSlots(br); break;
		    	//case "Courses:": parseCourses(br); break;
		    	//case "Labs:": parseLabs(br); break;
		    	//case "Not compatible:": parseNotCompatible(br); break;
		    	//case "Unwanted:": parseUnwanted(br); break;
		    	//case "Preferences:": parsePreferences(br); break;
		    	//case "Pair:": parsePairs(br); break;
		    	//case "Partial assignments:": parsePartAssign(br); break;
		    	
		    	default: 
		    		//If the line is not whitespace and we can't parse it, we have a problem.
		    		if (line.trim().length() > 0) 
		    			throw new IOException(String.format("Could not parse line: %s", line));
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
		//Matches and extracts lines of the form: DD, HH:MM, INT, INT
		Pattern p = Pattern.compile("^([A-Z]{2})[\\s]*,[\\s]*([0-9]{1,2}:[0-9]{2})[\\s]*,[\\s]*([0-9]*)[\\s]*,[\\s]*([0-9]*)[\\s]*$");
		String line;
		while((line = br.readLine()) != null) {
			Matcher m = p.matcher(line);
			if(m.find()) {
				System.out.println("Day: " + m.group(1));
				System.out.println("Time: " + m.group(2));
				System.out.println("Coursemax: " + m.group(3));
				System.out.println("CourseMin: " + m.group(4));
			}
			else //If the line is not whitespace and we can't parse it, we have a problem.
	    		if (line.trim().length() > 0) 
	    			throw new IOException(String.format("Could not parse line: %s", line));
		}
	}

	public Problem getProb() {
		return prob;
	}
}