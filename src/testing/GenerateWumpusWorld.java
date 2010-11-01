import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Takes an input file formatted as follows
 * r c - number of rows and columns
 * x y - the starting location
 * Next follows r rows with c strings on each row
 * and each string has its own interpretation into
 * integers. Look at setUpTable() for details about
 * the transformation and in the input folder for
 * examples.
 * 
 * It then proccess it so that the file is in a way
 * that is useful for us in testing it in wumpus world.
 * It changes so that the coordinates are all relative
 * to the start location.
 * 
 * @author joacar
 *
 */
public class GenerateWumpusWorld {
	private static String inputFile;
	private static String outputFile;
	private static BufferedWriter bw;

	private int rows, cols, x, y;
	private String[][] wumpusWorldS;
	private HashMap<String, Integer> table;
	
	/**
	 * Entry point. 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.out.println("Usage \t java GenerateWumpusWorld <file_name>");
			System.exit(0);
		}
		
		inputFile = args[0];
		outputFile = inputFile.replace(".txt", "_converted.txt");
		new GenerateWumpusWorld(inputFile);
	}

	/**
	 * Basic constructor taking a file as argument
	 * 
	 * @param inputFile name of file
	 */
	public GenerateWumpusWorld(String inputFile) {
		table = new HashMap<String, Integer>();
		setUpTable();
		
		File file = null;
		try {
			file = new File(inputFile); 
		} catch(IOError e) {
			System.err.println("Error opening file "+inputFile);
			System.exit(0);
		}

		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch(IOException e) {
			System.err.println("Error at Scanner("+inputFile+")");
		}

		read(sc);
		printTransformed();
		write();
		finished();
	}

	/**
	 * Reads in the file
	 * 
	 * @param sc Scanner sc
	 */
	private void read(Scanner sc) {
		try {
			// Dimension of the world plus "out of world" border
			rows = sc.nextInt() + 2;
			cols = sc.nextInt() + 2;

			// Start location. The extra one comes from the border
			x = sc.nextInt() + 1;
			y = sc.nextInt() + 1;

			// Initialize the new world and create border
			wumpusWorldS = new String[rows][cols];
			for(int i = 0; i < cols; i++) wumpusWorldS[0][i] = wumpusWorldS[rows-1][i] = "X";
			for(int j = 0; j < rows; j++) wumpusWorldS[j][0] = wumpusWorldS[j][cols-1] = "X";
			// Read in the actual world
			for(int i = 1; i < rows-1; i++)
				for(int j = 1; j < cols-1; j++) 
					wumpusWorldS[i][j] = sc.next();

		} catch(NumberFormatException e) {
			System.err.println("Incorrectly formated input data!"+
			"Read the README file for further information");
		}
	}
	
	/**
	 * Help method to print the board
	 */
	private void print() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%s ",wumpusWorldS[i][j]);
			}
			System.out.printf("\n");
		}
	}
	
	/**
	 * Help method to print the transformed board
	 */
	private void printTransformed() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%d %d %d\n",(i-x),(j-y) ,table.get(wumpusWorldS[i][j]));
			}
			System.out.printf("\n");
		}
	}
	
	/**
	 * Sets up the transformations from strings
	 * to integers that should be performed
	 */
	private void setUpTable() {
		table.put("X", -1);
		table.put("0", 0);
		table.put("B", 1);
		table.put("S", 2);
		table.put("G", 3);
		table.put("BS", 4);
		table.put("BG", 5);
		table.put("SG", 6);
		table.put("BSG", 7);
		table.put("P", 10);
		table.put("W", 11);
		table.put("R", 12);
	}
	
	/**
	 * Writes the transformed file to outputFile
	 */
	private void write() {
		File file = null;
		try {
			file = new File(outputFile);
		} catch(IOError e) {
			System.err.println("Error occured while setting up file "
					+outputFile);
		}

		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch(IOException e) {
			System.err.println("Error while creating writer with file "
					+outputFile);
		}
		
		String s = null;
		try {
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < cols; j++) {
					s = wumpusWorldS[i][j];
					bw.write(s);
					bw.write(" ");
				}
				bw.write("\n");
			}
		} catch(IOException e) {
			System.err.println("Error while writing string "+s
					+" to file "+outputFile);
		}
	}
	
	private void finished() {
		System.out.println("Have successfully converted "+inputFile+" to "+
				outputFile+" containing testdata formatted for wumpus world");
	}
}