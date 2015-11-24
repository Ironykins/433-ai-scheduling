package scheduler;

import java.io.IOException;

/**
 * Main Class
 *
 * Responsible for parsing command line arguments and launching into main program logic.
 * Also for displaying final output.
 */
public class Main {

	/**
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		// Weightings of different parts of the Eval function.
		double wminFilled = 1.0;
		double wminPref = 1.0;
		double wminPair = 1.0;
		double wminSecDiff = 1.0;
		
		// Penalties for not meeting minimum number of courses.
		int pen_coursemin = 5;
		int pen_labmin = 5;
		
		Parser parse = new Parser();
		
		try {
			Problem prob = parse.parseFile("input.txt");
			System.out.println(prob);			
		}
		catch(IOException ex) {
			System.out.println(ex.toString());
		}
		
	}
}