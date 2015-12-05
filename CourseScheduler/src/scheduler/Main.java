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
		System.err.println("Usage: CourseScheduler [-pcm <pen_coursemin>] [-plm <pen_labmin>]  [-psd <pen_section> ] [-ppa <pen_notpaired>] \n" +
				"\t\t[-wmf <weight minFilled>] [-wpr <weight Preferences>] [-wpa <weight Pair>] [-wsd <weight secDiff>] <input filename>");
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
		int pen_section = 5;
		int pen_notpaired = 5;
		
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
					case "-psd": pen_section = Integer.parseInt(args[++i]); break;
					case "-ppa": pen_notpaired = Integer.parseInt(args[++i]); break;
					case "-wmf": wMinFilled = Double.parseDouble(args[++i]); break;
					case "-wpr": wPref = Double.parseDouble(args[++i]); break;
					case "-wpa": wPair = Double.parseDouble(args[++i]); break;
					case "-wsd": wSecDiff = Double.parseDouble(args[++i]); break;
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
		catch(IllegalStateException ex) {
			System.out.printf("Error while parsing file: \n%s\n", ex.getMessage());
			System.exit(1);
		}
		
		//Put our parsed penalties and eval weights in here.
		prob.evaluator.setPen_coursemin(pen_coursemin);
		prob.evaluator.setPen_labmin(pen_labmin);
		prob.evaluator.setPen_section(pen_section);
		prob.evaluator.setPen_notpaired(pen_notpaired);
		prob.evaluator.setwMinFilled(wMinFilled);
		prob.evaluator.setwPair(wPair);
		prob.evaluator.setwPref(wPref);
		prob.evaluator.setwSecDiff(wSecDiff);
		
		//This needs to be called here because I'm bad at programming.
		prob.computeBestMinPenalties();
		
		//Print out the problem.
		//TODO: Remove this when the system is done. It's kinda spammy.
		System.out.println(prob);
		
		//Do some initial checking on our partAssign.
		if(!prob.evaluator.Constr(prob.getPartAssign())) {
			System.out.println("Partial Assignment is Invalid\n");
			System.exit(1);
		}
		
		//Create a search control for our problem.
		Control searchControl = new Control(prob);
		
		//Run the search control to get our final solution.
		State finalState = searchControl.solve();
		
		//Show our solution.
		if(finalState == null)
			System.out.println("No solution was found.");
		else {
			System.out.println("Solution:");
			System.out.println(finalState);
		}
	}
}