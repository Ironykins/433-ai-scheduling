
package scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import java.io.BufferedReader;

public class EvalTest {

	private State testState; 
	private Evaluator testEval;
	private Assignable[] assignables = new Assignable[5];
	private Slot[] slots = new Slot[5];
	private Problem testProb;
	
	@Before
	public void init (){
		assignables[0] = new Assignable(0,"CPSC 123 LEC 01",true,1);
		assignables[1] = new Assignable(1,"CPSC 213 LEC 01 TUT 02",false,1);
		assignables[2] = new Assignable(2,"CPSC 123 LEC 02",true,1);
		assignables[3] = new Assignable(3,"CPSC 123 LEC 03",true,1);
		assignables[4] = new Assignable(4,"CPSC 321 LEC 01 LAB 01",false,1);
		

		
		slots[0] = new Slot(0, "MO", "08:00");
		slots[1] = new Slot(1, "MO", "09:00");
		slots[2] = new Slot(2, "WE", "08:00");
		slots[3] = new Slot(3, "FR", "08:00");
		slots[4] = new Slot(4, "TU", "08:00");
		

	}
	
	@Test
	public void minEval(){
			
		
		slots[0].setCourseMax(5);
		slots[0].setCourseMin(2);
		slots[0].setLabMax(5);
		slots[0].setLabMin(2);
		
		slots[1].setCourseMax(5);
		slots[1].setCourseMin(2);
		slots[1].setLabMax(5);
		slots[1].setLabMin(1);
		
		slots[2].setCourseMax(5);
		slots[2].setCourseMin(0);
		slots[2].setLabMax(5);
		slots[2].setLabMin(0);
		
		slots[3].setCourseMax(1);
		slots[3].setCourseMin(0);
		slots[3].setLabMax(1);
		slots[3].setLabMin(0);
		
		slots[4].setCourseMax(1);
		slots[4].setCourseMin(0);
		slots[4].setLabMax(1);
		slots[4].setLabMin(0);
		
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);		
		testEval.setPen_coursemin(1);
		testEval.setPen_labmin(1);
		testState.assign = new int[]{-1,-1,-1,-1,-1};
		assertEquals(2.0, testEval.evalMinFilled(testState), .001);
		
		testState.numOfCourses[1] = 3;
		testState.numOfLabs[1] = 2;
		testState.assign = new int[]{1,1,1,1,1};
		assertEquals(4.0, testEval.evalMinFilled(testState), .001);
	}
	
	@Test
	public void minDeltaEval(){
			
		
		slots[0].setCourseMax(5);
		slots[0].setCourseMin(2);
		slots[0].setLabMax(5);
		slots[0].setLabMin(2);
		
		slots[1].setCourseMax(5);
		slots[1].setCourseMin(2);
		slots[1].setLabMax(5);
		slots[1].setLabMin(1);
		
		slots[2].setCourseMax(5);
		slots[2].setCourseMin(0);
		slots[2].setLabMax(5);
		slots[2].setLabMin(0);
		
		slots[3].setCourseMax(1);
		slots[3].setCourseMin(0);
		slots[3].setLabMax(1);
		slots[3].setLabMin(0);
		
		slots[4].setCourseMax(1);
		slots[4].setCourseMin(0);
		slots[4].setLabMax(1);
		slots[4].setLabMin(0);
		
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);		
		testEval.setPen_coursemin(1);
		testEval.setPen_labmin(1);
		testState.assign = new int[]{-1,-1,-1,-1,-1};
		assertEquals(2.0, testEval.evalMinFilled(testState), .001);
		

		testState.assign = new int[]{-1,-1,-1,-1,-1};
		assertEquals(1.0, testEval.deltaEvalMinFilled(testState, 0, 4), .001);
		
		testState.assign = new int[]{-1,-1,-1,-1,-1};
		assertEquals(0.0, testEval.deltaEvalMinFilled(testState, 0, 0), .001);
	}
	
	@Test
	public void secDiff(){
			
			
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);	
		
		testEval.setPen_section(1);
		testState.assign = new int[]{1,1,1,1,1};
		assertEquals(3.0, testEval.evalSecDiff(testState), .001);
		
		testState.numOfCourses[1] = 3;
		testState.numOfLabs[1] = 2;
		testState.assign = new int[]{0,1,2,3,4};
		assertEquals(0.0, testEval.evalSecDiff(testState), .001);
	}
	
	@Test
	public void deltaSecDiff(){
			
			
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);	
		
		testEval.setPen_section(1);
		testState.assign = new int[]{1,1,-1,1,1};
		assertEquals(2.0, testEval.deltaEvalSecDiff(testState,2,1), .001);
		
		testState.numOfCourses[1] = 3;
		testState.numOfLabs[1] = 2;
		testState.assign = new int[]{0,1,-1,3,4};
		assertEquals(0.0, testEval.deltaEvalSecDiff(testState, 2, 2), .001);
	}
	
	@Test
	public void evalPrefTest(){
			
			
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);	
		
		int[][] testPref = new int[5][5];
		testPref[0][0] = 1;
		testPref[1][1] = 1;
		testPref[2][2] = 1;
		testPref[3][3] = 1;
		testPref[4][4] = 1;

		testProb.setPreferences(testPref);
		
		testEval.setwPref(1.0);

		testState.assign = new int[]{0,1,2,3,4};
		assertEquals(5.0, testEval.evalPref(testState), .001);

		testState.assign = new int[]{4,2,3,1,0};
		assertEquals(0.0, testEval.evalPref(testState), .001);
	}
	
	@Test
	public void deltaEvalPrefTest(){
			
			
		testProb = new Problem(assignables, slots);		
		testState = new State(assignables.length, slots.length);		
		testEval = new Evaluator(testProb);	
		
		int[][] testPref = new int[5][5];
		testPref[0][0] = 1;
		testPref[1][1] = 1;
		testPref[2][2] = 1;
		testPref[3][3] = 1;
		testPref[4][4] = 1;

		testProb.setPreferences(testPref);
		
		testEval.setwPref(1.0);

		testState.assign = new int[]{0,1,2,3,-1};
		assertEquals(1.0, testEval.deltaEvalPref(testState, 4, 4), .001);

		testState.assign = new int[]{4,2,3,1,-1};
		assertEquals(0.0, testEval.deltaEvalPref(testState, 4, 0), .001);
	}
/*	

	@Test
	public void evalsConstr2(){

		testState.assign = new int[]{0,0,0,0,0};
		assertFalse(testEval.Constr(testState));
	}
	

	@Test
	public void evalsConstr3(){

		testState.assign = new int[]{0,2,1,4,4};
		assertFalse(testEval.Constr(testState));
	}
	

	@Test
	public void evalsConstr4(){

		testState.assign = new int[]{0,1,2,3,4};
		assertFalse(testEval.Constr(testState));
	}
	
	@Test
	public void evalsConstr5(){

		testState.assign = new int[]{-1,-1,-1,-1,4};
		assertTrue(testEval.deltaConstr(testState, 2, 3));
	}
	@Test
	public void evalsConstr6(){

		testState.assign = new int[]{-1,-1,-1,-1,4};
		assertFalse(testEval.deltaConstr(testState, 2, 2));
	}
	
	@Test
	public void evalsConstr7(){

		testState.assign = new int[]{0,-1,-1,-1,4};
		testState.numOfCourses[0] = 1;
		assertFalse(testEval.deltaConstr(testState, 2, 0));
	}
	@Test
	public void evalsMaxCheck(){
		assertTrue(testEval.maxCheck(testState));
	}@Test
	public void evalsNightCheck(){
		assertTrue(testEval.nightCheck(testState));
	}@Test
	public void evalsUnwantedCheck(){
		assertTrue(testEval.unwantedCheck(testState));
	}@Test
	public void evalsCompatibleCheck(){
		assertTrue(testEval.compatibleCheck(testState));
	}
	
	*/

}