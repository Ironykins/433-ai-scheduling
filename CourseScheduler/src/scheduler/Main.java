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
	 * Prints the blurb telling you how to use the program, and exits.
	 */
	public static void printUsage() {
		System.err.println("Usage: CourseScheduler [-pcm <pen_coursemin>] [-plm <pen_labmin>] [-w <weight minFilled> <weight Preferences> <weight Pair> <weight secDiff>] <input filename>");
		System.exit(1);
	}
	/**
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		// Weightings of different parts of the Eval function.
		// These are set relative to one another.
		// eg. If wminFilled is 0.25 and wminPref is 0.50 then wminFilled is weighted half as much.
		// The same is true if one is 1.0 and the other is 2.0.
		double wMinFilled = 1.0;
		double wPref = 1.0;
		double wPair = 1.0;
		double wSecDiff = 1.0;
		String fileName = "input.txt";
		
		// Penalties for not meeting minimum number of courses.
		// These are just default values.
		int pen_coursemin = 5;
		int pen_labmin = 5;
		
		Parser parse = new Parser();
		Problem prob = null;
		
		// Parse command line arguments.
		// Error handling is done incredibly lazy here. Deal w/ it.
		if(args.length < 1) printUsage();
		try {
			for(int i=0;i<args.length-1; i++) {
				switch(args[i]) {
					case "-pcm": pen_coursemin = Integer.parseInt(args[++i]); break;
					case "-plm": pen_labmin = Integer.parseInt(args[++i]); break;
					case "-w": 
						wMinFilled = Double.parseDouble(args[++i]);
						wPref = Double.parseDouble(args[++i]);
						wPair = Double.parseDouble(args[++i]);
						wSecDiff = Double.parseDouble(args[++i]);
						break;
					default:
						printUsage();
				}
			}
		}
		catch(Exception ex) {
			printUsage();
		}

		//Our filename must always be the last argument.
		fileName = args[args.length-1];
		
		// Parse the input file.
		try {
			prob = parse.parseFile(fileName);
		}
		catch(IOException ex) {
			System.out.println(ex.toString());
			System.exit(1);
		}
		
		//Put our parsed penalties and eval weights in here.
		prob.setPen_coursemin(pen_coursemin);
		prob.setPen_labmin(pen_labmin);
		prob.setwMinFilled(wMinFilled);
		prob.setwPair(wPair);
		prob.setwPref(wPref);
		prob.setwSecDiff(wSecDiff);
		
		//Print out the problem.
		//TODO: Remove this when the system is done. It's kinda spammy.
		System.out.println(prob);
		
		//Create a search control for our problem.
		Control searchControl = new Control(prob);
		
		//Run the search control to get our final solution.
		State finalState = searchControl.solve();
		
		//Show our solution.
		System.out.println("Solution:");
		System.out.println(finalState);		
	}
}