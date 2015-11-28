package scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Unit/Integration tests for the parser
 * This is terrible I'm sorry.
 * 
 * Uses the input file that denzinger provides.
 * (With all the errors present in it corrected.)
 * 
 * @author konrad
 */
public class ParserTest {
	private Problem parsedProb;
	private Parser parse;
	
    @Before
    public void setUp() {
    	//I'm sorry this is horrible.
    	//Java doesn't have multi line strings and I just
    	String testCase = new StringBuilder()
			.append("Name:\n")
            .append("ShortExample\n")
            .append("\n")
            .append("Course slots:\n")
            .append("MO, 8:00, 3, 2\n")
            .append("MO,9:00,3,2\n")
            .append("TU, 9:30, 2,  1\n")
            .append("\n")
            .append("Lab slots:\n")
            .append("MO, 8:00, 4, 2\n")
            .append("TU, 10:00,2,1\n")
            .append("FR, 10:00, 2, 1\n")
            .append("\n")
            .append("Courses:\n")
            .append("CPSC 433 LEC 01\n")
            .append("CPSC 433 LEC 02\n")
            .append("SENG 311  LEC  01\n")
            .append("CPSC 567 LEC 01\n")
            .append("\n")
            .append("Labs:\n")
            .append("CPSC 433 LEC 01 TUT 01\n")
            .append("CPSC 433 LEC  02 LAB   02\n")
            .append("SENG 311 LEC 01 TUT 01\n")
            .append("CPSC 567 TUT 01\n")
            .append("\n")
            .append("Not compatible:\n")
            .append("CPSC 433 LEC 01 TUT 01, CPSC 433 LEC 02 LAB 02\n")
            .append("CPSC 567 LEC 01, CPSC 433 LEC 01\n")
            .append("CPSC 567 LEC 01, CPSC 433 LEC 02\n")
            .append("CPSC 567 TUT 01, CPSC 433 LEC 02\n")
            .append("CPSC 433 LEC 01, CPSC 567 TUT 01\n")
            .append("\n")
            .append("Unwanted:\n")
            .append("CPSC 433 LEC 01, MO, 8:00\n")
            .append("\n")
            .append("Preferences:\n")
            .append("TU, 9:30, CPSC 433 LEC 01, 10\n")
            .append("MO, 8:00, CPSC 433 LEC 01 TUT 01, 3\n")
            .append("TU, 9:30, CPSC 433 LEC 02, 10\n")
            .append("TU, 10:00, CPSC 433 LEC 01 TUT 01, 5\n")
            .append("MO, 8:00, CPSC 433 LEC 02 LAB 02, 1\n")
            .append("MO, 9:00, CPSC 433 LEC 02 LAB 02, 7\n")
            .append("\n")
            .append("Pair:\n")
            .append("SENG 311 LEC 01, CPSC 567  LEC    01\n")
            .append("\n")
            .append("Partial assignments:\n")
            .append("SENG 311 LEC 01, MO, 8:00\n")
            .append("SENG 311 LEC 01 TUT 01, FR, 10:00\n")
            .toString();
  	
    	StringReader sr= new StringReader(testCase); // Wrap up the string
		BufferedReader br= new BufferedReader(sr); // Wrap it DEEPER.
		
		parse = new Parser();
		
		try {
			parsedProb = parse.parseBuffer(br);
		}
		catch(IOException ex) {
			fail("Hit I/O Exception when parsing: " + ex.toString());
		}
    }
	
    @Test
    public void parsesName() {
    	assertEquals(parsedProb.getName(), "ShortExample");
    }
    
	@Test
	public void parsesSlots() {
		assertEquals(parsedProb.Slots.length, 5);
		
		for(Slot s : parsedProb.Slots) {
			assertNotNull(s.day);
			assertNotNull(s.startTime);
			assertTrue(s.getCourseMax() + s.getCourseMin() + s.getLabMax() + s.getLabMin() > 0);
		}
	}
	
	@Test
	public void parsesAssignables() {
		assertEquals(parsedProb.Assignables.length, 8);
		for(Assignable a : parsedProb.Assignables) {
			assertNotNull(a.name);
			if(a.name.contains("LAB") || a.name.contains("TUT"))
				assertFalse(a.isCourse);
			else
				assertTrue(a.isCourse);
		}
	}
	
	@Test 
	public void parsesUnwanted() {
		for(Assignable a : parsedProb.Assignables) {
			if(a.name.equals("CPSC 433 LEC 01")) {
				Slot s = parsedProb.Slots[a.unwanted.firstElement()];
				assertEquals(s.day, "MO");
				assertEquals(s.startTime, "8:00");
			}
		}
	}
	
	@Test
	public void parsesIncompatible() {
		for(Assignable a : parsedProb.Assignables) {
			for(int i : a.incompatible) {
				Assignable s = parsedProb.Assignables[i];
				assertTrue(s.incompatible.contains(a.id));
			}
		}
	}
	
	@Test
	public void parsesPairs() {
		for(Assignable a : parsedProb.Assignables) {
			for(int i : a.paired) {
				Assignable s = parsedProb.Assignables[i];
				assertTrue(s.paired.contains(a.id));
			}
		}
	}
	
	@Test
	public void parsesPreferences() { //Full lazy.
		int[][] prefs = parsedProb.getPreferences();
		assertEquals(prefs[0][2], 10);
	}
	
	@Test
	public void parsesPartAssign() { //Lazy. Should throw an index out of bounds exception if additional assignments are present.
		State passign = parsedProb.getPartAssign();
		for(int i=0;i<passign.assign.length; i++) {
			if(parsedProb.Assignables[i].name.equals("SENG 311 LEC 01")) {
				assertEquals(parsedProb.Slots[passign.assign[i]].day, "MO");
				assertEquals(parsedProb.Slots[passign.assign[i]].startTime, "8:00");
			}
			else if(parsedProb.Assignables[i].name.equals("SENG 311 LEC 01 TUT 01")) {
				assertEquals(parsedProb.Slots[passign.assign[i]].day, "FR");
				assertEquals(parsedProb.Slots[passign.assign[i]].startTime, "10:00");
			}
		}
	}
}
