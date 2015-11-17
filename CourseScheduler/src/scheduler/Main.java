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
		System.out.println("Hello World!");
		
		try {
			Parser parse = new Parser();
			Problem prob = parse.parseFile("input.txt");
			
			System.out.println(prob.getName());
	
		}
		catch(IOException ex) {
			System.out.println(ex.toString());
		}
		
	}
}