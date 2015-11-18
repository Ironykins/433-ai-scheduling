package scheduler;

import java.io.IOException;

/**
 * Main Class
 * test comment, please ignore
 */
public class Main {

	/**
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		
		try {
			Parser parse = new Parser();
			Problem prob = parse.parseFile("input.txt");
			
			System.out.println("Problem Name: " + prob.getName());
			
			System.out.println("Slots:");
			for(Slot s : prob.Slots) {
				System.out.println(s);
			}
			
			System.out.println("Assignables:");
			for(Assignable a : prob.Assignables) {
				System.out.println(a);
			}
	
		}
		catch(IOException ex) {
			System.out.println(ex.toString());
		}
		
	}
}