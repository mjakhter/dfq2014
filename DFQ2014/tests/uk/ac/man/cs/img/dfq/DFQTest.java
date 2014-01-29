package uk.ac.man.cs.img.dfq;

import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* @author Suzanne M. Embury
 * 
 * This is a helper class for the test classes on the DFQ system.  It contains
 * helper functions for writing tests and (at the moment) fixture details for
 * the various test systems under use, in forms that are specific to the different
 * testing environments we need to test the system in.
 * 
 * This is a quick fix, for convenience.  It should be replaced by a more principled
 * solution soon.
 * 
 */

public class DFQTest {
	
	// To test the system, set the current user so that the correct file paths are used.
	private static final String currentUser = getCurrentUser();
	private static final String javid = "muhammad.akhter", 
	                            suzanne = "embury";
	
	private static final String WORKSPACE_JAVID = "C:\\Users\\muhammad.akhter\\PhD_Project\\Hibernate_Project\\HibernateWorkSpace\\";
	private static final String WORKSPACE_SUZANNE = "/Users/embury/Documents/workspace-javid/";
	
	private static final Map<String, String> mapOfAppNamesToPropertyFiles = initialiseAppToPropFileMap();
	private static final Map<String, String> mapOfAppNamesToTraceFiles = initialiseAppToTraceFileMap();


	private static Map<String, String> initialiseAppToPropFileMap() {
		Map<String, String> map = new HashMap<String, String>();
		if (currentUser.equals(javid)) {
			map.put("testOnePassOneFail", WORKSPACE_JAVID + "testOnePassOneFail\\testOnePassOneFail.properties");
			map.put("testSimpleContactDB", WORKSPACE_JAVID + "testSimpleContactDB\\testSimpleContactDB.properties");
			map.put("testSimpleContactDBWithFaults", WORKSPACE_JAVID + "testSimpleContactDBWithFaults\\testSimpleContactDBWithFaults.properties");

		} else if (currentUser.equals(suzanne)) {
			map.put("testOnePassOneFail", WORKSPACE_SUZANNE + "testOnePassOneFail/testOnePassOneFail.properties");
			map.put("testSimpleContactDB", WORKSPACE_SUZANNE + "testSimpleContactDB/testSimpleContactDB.properties");
			map.put("testSimpleContactDBWithFaults", WORKSPACE_SUZANNE + "testSimpleContactDBWithFaults/testSimpleContactDBWithFaults.properties");

		} else
			throw new RuntimeException("Current user not recognised: " + currentUser);
		return map;
	}

	private static String getCurrentUser() {
		return System.getProperty("user.name");
	}

	private static Map<String, String> initialiseAppToTraceFileMap() {
		Map<String, String> map = new HashMap<String, String>();
		if (currentUser.equals(javid)) {
			map.put("testOnePassOneFail", WORKSPACE_JAVID + "testOnePassOneFail\\testOnePassOneFail.xml");
			map.put("testSimpleContactDB", WORKSPACE_JAVID + "testSimpleContactDB\\testSimpleContactDB.xml");
			map.put("testSimpleContactDBWithFaults", WORKSPACE_JAVID + "testSimpleContactDBWithFaults\\testSimpleContactDBWithFaults.xml");

		} else if (currentUser.equals(suzanne)) {
			map.put("testOnePassOneFail", WORKSPACE_SUZANNE + "testOnePassOneFail/testOnePassOneFail.xml");
			map.put("testSimpleContactDB", WORKSPACE_SUZANNE + "testSimpleContactDB/testSimpleContactDB.xml");
			map.put("testSimpleContactDBWithFaults", WORKSPACE_SUZANNE + "testSimpleContactDBWithFaults/testSimpleContactDBWithFaults.xml");
		} else
			throw new RuntimeException("Current user not recognised: " + currentUser);
		return map;
	}
	
	
	
	// Getters and Setters
	
	public static String getPropertiesFile(String appName) {
		return mapOfAppNamesToPropertyFiles.get(appName);
	}
	
	public static String getTraceFile(String appName) {
		return mapOfAppNamesToTraceFiles.get(appName);
	}

	public static String nonPropertiesFilePath() {
		if (currentUser.equals(javid))
			return "resources\\testData\\notAPropertiesFile.txt";
		else if (currentUser.equals(suzanne))
			return "resources/testData/notAPropertiesFile.txt";
		else
			throw new RuntimeException("Current user not recognised: " + currentUser);
	}
	
	public static String getPropertiesFileWithNonExistentTraceFile() {
		if (currentUser.equals(javid))
			return "resources\\testData\\propFileWithNonExistentTraceFile.properties";
		else if (currentUser.equals(suzanne))
			return "resources/testData/propFileWithNonExistentTraceFile.properties";
		else
			throw new RuntimeException("Current user not recognised: " + currentUser);
	}
	
	
	// Utility functions to make writing test cases easier
	
	/*
	 * Sorts a list of strings into alphabetical order.
	 * This method is used to facilitate comparison of lists on their content,
	 * and not on their ordering.  (There must be a better way of doing this...)
	 */
	protected void sortAlphabetically(List<String> list) {
		Collections.sort(list, Collator.getInstance());
	}
	
}
