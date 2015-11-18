package scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Assert.*;

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
    	String testCase = String.join("\n"
			, "Name:"
            , "ShortExample"
            , ""
            , "Course slots:"
            , "MO, 8:00, 3, 2"
            , "MO,9:00,3,2"
            , "TU, 9:30, 2,  1"
            , ""
            , "Lab slots:"
            , "MO, 8:00, 4, 2"
            , "TU, 10:00,2,1"
            , "FR, 10:00, 2, 1"
            , ""
            , "Courses:"
            , "CPSC 433 LEC 01"
            , "CPSC 433 LEC 02"
            , "SENG 311  LEC  01"
            , "CPSC 567 LEC 01"
            , ""
            , "Labs:"
            , "CPSC 433 LEC 01 TUT 01"
            , "CPSC 433 LEC  02 LAB   02"
            , "SENG 311 LEC 01 TUT 01"
            , "CPSC 567 TUT 01"
            , ""
            , "Not compatible:"
            , "CPSC 433 LEC 01 TUT 01, CPSC 433 LEC 02 LAB 02"
            , "CPSC 567 LEC 01, CPSC 433 LEC 01"
            , "CPSC 567 LEC 01, CPSC 433 LEC 02"
            , "CPSC 567 TUT 01, CPSC 433 LEC 02"
            , "CPSC 433 LEC 01, CPSC 567 TUT 01"
            , ""
            , "Unwanted:"
            , "CPSC 433 LEC 01, MO, 8:00"
            , ""
            , "Preferences:"
            , "TU, 9:30, CPSC 433 LEC 01, 10"
            , "MO, 8:00, CPSC 433 LEC 01 TUT 01, 3"
            , "TU, 9:30, CPSC 433 LEC 02, 10"
            , "TU, 10:00, CPSC 433 LEC 01 TUT 01, 5"
            , "MO, 8:00, CPSC 433 LEC 02 LAB 02, 1"
            , "MO, 9:00, CPSC 433 LEC 02 LAB 02, 7"
            , ""
            , "Pair:"
            , "SENG 311 LEC 01, CPSC 567  LEC    01"
            , ""
            , "Partial assignments:"
            , "SENG 311 LEC 01, MO, 8:00"
            , "SENG 311 LEC 01 TUT 01, FR, 10:00"
    	);
    	
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

}
