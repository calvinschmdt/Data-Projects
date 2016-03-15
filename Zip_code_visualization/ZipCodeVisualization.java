/*
 * File: ZipCodeVisualization.java
 * ===============================================================
 * A program to visualize where a list of zip codes in the continental US are on a map.
 * Repeated zip codes lead to larger circles.
 */

import acm.program.*;
import acm.graphics.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;


public class ZipCodeVisualization extends GraphicsProgram {	
	/* The names of the files. */
	private static final String COORD_FILE = "zip_code_db.csv";
	private static final String ZIP_FILE = "invitation_zip_code.csv";
	private static final String BACKGROUND_FILE = "US.png";
	
	/* Make the window large so that we can see more detail. */
	public static final int APPLICATION_WIDTH = 1200;
	public static final int APPLICATION_HEIGHT = 600;
	
	/* The viewpoint coordinates - the minimum and maximum longitude
	 * and latitude. Currently set up for the map of the continental US included
	 * with program.
	 */
	private static final double MIN_LONGITUDE = -136.6;
	private static final double MAX_LONGITUDE = -56.0;
	
	private static final double MIN_LATITUDE = +23.0;
	private static final double MAX_LATITUDE = +54.2;
	
	/**
	 * Visualizes where a list of zip codes fall on the map of the continental US. Does this by
	 * reading in a list of the locations for all zip codes, then reading in the test list
	 * and plotting those zip codes using their coordinates.
	 * (non-Javadoc)
	 * @see acm.program.GraphicsProgram#run()
	 */
	public void run() {
		// Draws the map in the background.
		drawBackground();
		
		// Gets the gps coordinates mapped to each zip code.
		HashMap<String, String[]> coordMap = readCoordCSV();
		// Gets the number of times a zip code appears mapped to each zip code.
		HashMap<String, Integer> zipMap = readZipCSV();
		
		// Iterates through each zip code, plotting it based on the coordinates and number of times 
		// that zip code appears.
		Iterator it = zipMap.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			Integer num = zipMap.get(pair.getKey());
			String[] zipLoc = coordMap.get(pair.getKey());
			plotOneCity(zipLoc[0], zipLoc[1], Color.RED, Math.sqrt(num * 16));
			it.remove();
		}
		
	}
	
	/**
	 * Given the longitude and latitude of a city (in string form), displays
	 * a dot for that city.
	 * 
	 * @param cityLat The latitude, in degrees north.
	 * @param cityLong The longitude, in degrees east.
	 */
	private void plotOneCity(String cityLat, String cityLong, Color color, double size) {
		/* Convert from a text form into real numbers. */
		double longitude = Double.parseDouble(cityLong);
		double latitude  = Double.parseDouble(cityLat);
		
		/* Determine where on screen the city should be drawn. */
		double x = longitudeToXCoordinate(longitude);
		double y = latitudeToYCoordinate(latitude);
		plotPixel(x, y, color, size);
	}

	/**
	 * Plots a pixel at the specified (x, y) coordinate.
	 * 
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 */
	private void plotPixel(double x, double y, Color color, double size) {
		/* Creates an oval of the given color and size. */
		GOval pixel = new GOval(x - (size / 2.0), y - (size / 2.0), size, size);
		pixel.setFilled(true);
		pixel.setFillColor(color);
		add(pixel);
	}
	
	/**
	 * Given a raw longitude, returns the screen x coordinate where
	 * it should be displayed.
	 * 
	 * @param longitude The longitude in question.
	 * @return Where it maps to as an x coordinate.
	 */
	private double longitudeToXCoordinate(double longitude) {
		return getWidth() * (longitude - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE); 
	}
	
	/**
	 * Given a raw latitude, returns the screen y coordinate where
	 * it should be displayed.
	 * 
	 * @param latitude The latitude in question.
	 * @return Where it maps to as a y coordinate.
	 */
	private double latitudeToYCoordinate(double latitude) {
		return getHeight() * (1.0 - (latitude - MIN_LATITUDE) / (MAX_LATITUDE - MIN_LATITUDE)); 
	}
	
	/**
	 * Given a line from a CSV file, parses each field out of the line and returns
	 * an ArrayList containing all of them.
	 * 
	 * @param line The line of the file to parse.
	 * @return A list of all the fields in that line.
	 */
	private ArrayList<String> fieldsIn(String line) {
		
		// Initializes a new list of strings.
		ArrayList<String> fields = new ArrayList<String>();
		
		String entry = "";
		boolean quoteProtected = false;
		// Iterates through each character in that string.
		for (int i = 0; i < line.length(); i++){
			// Starts new entry if there is a comma outside of quotes.
			if (line.charAt(i) == ',' && !quoteProtected) {
				fields.add(entry);
				entry = "";
			// Starts and ends protecting quotes.
			} else if (line.charAt(i) == '"' && !quoteProtected) {
				quoteProtected = true;
			} else if (line.charAt(i) == '"' && quoteProtected) {
				quoteProtected = false;
			// Builds the entry if no special cases.
			} else {
				entry += line.charAt(i);
			}
		}
		fields.add(entry);
		
		return fields;
	}
	
	/**
	 * Reads in the .csv file that contains gps coordinates for each zip code, mapping those
	 * coordinates to that zip code for easy recall.
	 * 
	 * @return A HashMap where the coordinates, as an array of strings, are mapped to the 
	 * zip code, as a string.
	 */
	private HashMap<String, String[]> readCoordCSV() {
		
		HashMap<String, String[]> coordMap = new HashMap<String, String[]>();
		
		try {
			/* Open the file for reading. */
			BufferedReader br = new BufferedReader(new FileReader(COORD_FILE));
			String firstLine = br.readLine();
			
			/* Reads through each line in the file. */
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				
				// Gets an ArrayList with a string for each comma separated variable.
				ArrayList<String> row = fieldsIn(line);
				// Assigns the latitude and longitude to an array.
				String[] coords = new String[2];
				coords[0] = row.get(1);
				coords[1] = row.get(2);
				
				// Maps that array if it has valid coordinates.
				if (coords[0] != "" && coords[1] != "") {
					coordMap.put(row.get(0), coords);
				}
				
			}
			
			br.close();
		} catch (IOException e) {
			println("Please enter a valid file.");
		}
		
		return coordMap;
	}
	
	/**
	 * Reads in the list of zip codes to be visualized. Counts how many times each zip code occurs
	 * by iterating up a list. Cannot handle values larger than 100000.
	 * 
	 * @return A HashMap where the count of each point is mapped to the string value of the zip code.
	 * Only includes zip codes that were in the file.
	 */
	private HashMap<String, Integer> readZipCSV() {
		
		// Initializes the list that tracks when a zip code shows up.
		int[] zipCount = new int[100000]; 
		
		try {
			// Reads in the input file.
			BufferedReader br = new BufferedReader(new FileReader(ZIP_FILE));
			
			while (true) {
				// Reads through each line, getting the zip code as the first value in the line.
				String line = br.readLine();
				if (line == null) {
					break;
				}
				ArrayList<String> row = fieldsIn(line);
				// Keeps track of the number of times that a zip code comes up by adding to 
				// that index in an array.
				if (row.get(0) != "") {
					Integer zip = Integer.parseInt(row.get(0));
					zipCount[zip] ++;
				}
				
				
			}
			
			br.close();
		} catch (IOException e) {
			println("Please enter a valid file.");
		}
		
		// Iterates through each value in the array, mapping the count to the zip code string.
		HashMap<String, Integer> zipMap = new HashMap<String, Integer>();
		for (int code = 0; code < 99999; code++) {
			if (zipCount[code] > 0) {
				zipMap.put(Integer.toString(code), zipCount[code]);
			}
		}
		
		return zipMap;
	}
	
	/**
	 * Draws the map in the background stretched over the whole field.
	 */
	private void drawBackground() {
		GImage map = new GImage(BACKGROUND_FILE, 0, 0);
		map.setBounds(0, 0, APPLICATION_WIDTH, APPLICATION_HEIGHT);
		add(map);
		
	}
	
}
