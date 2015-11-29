package scheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import java.io.BufferedReader;


public class EvaluatorTest{
	private State testState; 
	private Evaluator testEval;
	private Assignable[] assignables = new Assignable[5];
	private Slot[] slots = new Slot[5];
	private Problem testProb;
	
	@Before
	public void init (){
		assignables[0] = new Assignable(0,"CPSC 123 LEC 01",true,1);
		assignables[1] = new Assignable(1,"CPSC 213 LEC 01",true,1);
		assignables[2] = new Assignable(2,"CPSC 313 LEC 01",true,1);
		assignables[3] = new Assignable(3,"CPSC 321 LEC 01",true,1);
		assignables[4] = new Assignable(4,"CPSC 321 LEC 01 LAB 01",false,1);
		
		assignables[3].incompatible.add(4);
		assignables[4].incompatible.add(3);
		assignables[0].paired.add(1);
		assignables[2].unwanted.add(2);
		
		slots[0] = new Slot(0, "MO", "08:00");
		slots[1] = new Slot(1, "MO", "09:00");
		slots[2] = new Slot(2, "WE", "08:00");
		slots[3] = new Slot(3, "FR", "08:00");
		slots[4] = new Slot(4, "TU", "08:00");
		
		slots[0].setCourseMax(1);
		slots[0].setCourseMin(0);
		slots[0].setLabMax(1);
		slots[0].setLabMin(0);
		
		slots[1].setCourseMax(1);
		slots[1].setCourseMin(0);
		slots[1].setLabMax(1);
		slots[1].setLabMin(0);
		
		slots[2].setCourseMax(2);
		slots[2].setCourseMin(1);
		slots[2].setLabMax(1);
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
		
		
	}

	@Test
	public void evalsConstr(){

		testState.assign = new int[]{0,1,3,2,4};
		assertTrue(testEval.Constr(testState));
	}
	

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
/*	@Test
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