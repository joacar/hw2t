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
 * to the start location and prints out a transformed
 * file as:
 * x y - start location
 * x y v - location of a square with value v
 * 
 * It contains, after in total 1+(r+2)*(c+2) lines
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
		//printTransformed();
		//printAbsolute();
		printRelative();
		writeTransformed();
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
					wumpusWorldS[j][i] = sc.next();

		} catch(NumberFormatException e) {
			System.err.println("Incorrectly formated input data!"+
			"Read the README file for further information");
		}
	}
	
	/**
	 * Help method to print the board
	 */
	private void printAbsolute() {
		System.out.printf("Start is [%d,%d]\n",x,y);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%s[%d,%d] ",wumpusWorldS[j][i], j ,i);
			}
			System.out.printf("\n");
		}
	}
	
	/**
	 * Help method to print the board
	 */
	private void printRelative() {
		System.out.printf("Start is [%d,%d]\n",0,0);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%s[%d,%d] ",wumpusWorldS[j][i], (j-x) ,(i-y));
			}
			System.out.printf("\n");
		}
	}
	
	/**
	 * Help method to print the transformed board
	 */
	private void printTransformed() {
		// Start location
		System.out.printf("%d %d\n",x,y);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%d %d %d\n",(j-x),(i-y) ,table.get(wumpusWorldS[j][i]));
			}
			//System.out.printf("\n");
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
					s = wumpusWorldS[j][i];
					bw.write(s+" ");
				}
				bw.write("\n");
			}
			bw.flush();
			bw.close();
		} catch(IOException e) {
			System.err.println("Error while writing string "+s
					+" to file "+outputFile);
		}
	}
	
	/**
	 * Writes the transformed file to outputFile
	 */
	private void writeTransformed() {
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
			// Write start location
			bw.write(x+" "+y+"\n");
			bw.flush();
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < cols; j++) {
					s = (j-x)+" "+(i-y)+" "+table.get(wumpusWorldS[j][i]);
					bw.write(s+"\n");
				}
			}
			bw.flush();
			bw.close();
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