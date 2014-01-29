package uk.ac.man.cs.img.dfq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/*
 * @author Javid Akhter
 * @author Suzanne M. Embury
 * 
 * The instance of this class represents the application being diagnosed. (There should be
 * only one instance created for each run of the application.)
 */
public class Application {

	// Constants
	
	private static final String TRACE_FILE_PROPERTY = "edu.gatech.cc.aristotle.tarantula.data.tarantula_xml";
	private static final String SOURCE_FILES_PROPERTY = "edu.gatech.cc.aristotle.tarantula.data.source_files";
	private static final String TEST_SOURCE_FILES_PROPERTY = "edu.gatech.cc.aristotle.tarantula.data.junit.source_files";
	
	
	// Static Members
	
	private static Application currentApplication;

	
	// Instance Members
	
	private String appName;
	private String propertiesFilePath;
	private Properties tarantulaProps;
	private ApplicationTrace applicationTrace;
	
	private List<ProductionClass> productionClasses = new ArrayList<ProductionClass>();
	private List<TestClass> testClasses = new ArrayList<TestClass>();

	
	// Static Methods
	
	public static Application currentApplication() {
		return currentApplication;
	}
	
	
	// Constructor
	public Application(String appName, String propFilePath) throws Exception {

		currentApplication = this;
		
		this.appName = appName;
		this.propertiesFilePath = propFilePath;	

		this.tarantulaProps = new Properties();
		try {
			InputStream	propFile = new FileInputStream(new File(getPropertiesFilePath()));
			tarantulaProps.load(propFile);
			if (!readPropertiesFile(tarantulaProps)) {
				throw new CouldNotReadPropertiesFileException();
			} else if(!requiredPropertiesExist(tarantulaProps)) { 				
				throw new RequiredPropertiesNotFoundException();
			}
		} catch (FileNotFoundException e) {
			throw new CouldNotFindPropertiesFileException();
		}

		String traceFilePath = tarantulaProps.getProperty(TRACE_FILE_PROPERTY);
		if (!traceFileExists(traceFilePath)) 
			throw new TraceFileNotFoundException(); 
		applicationTrace = new ApplicationTrace(traceFilePath);
	
		createClasses();
	}

	
	// Getters and setters

	public String getName() {
		return this.appName;
	}

	public String getPropertiesFilePath() {
		return this.propertiesFilePath;
	}

	public ApplicationTrace getTrace() {
		return applicationTrace;
	}

	public List<CodeClass> getClasses() {
		List<CodeClass> combinedClasses = new ArrayList<CodeClass>(productionClasses);
		combinedClasses.addAll(testClasses);
		return combinedClasses; 
	}

	public CodeClass getClassByName(String name) throws ClassWithGivenNameNotFoundException {
		for (CodeClass codeClass: this.getClasses()) {
			if (codeClass.getName().matches(name)){
				return codeClass;	
			}
		}
		throw new ClassWithGivenNameNotFoundException();
	}

	public List<ProductionClass> getProductionClasses() {
		return this.productionClasses;
	}

	public List<TestClass> getTestClasses() {
		return this.testClasses;
	}
	

	public TestMethod getTestMethodByName(String methodName) throws DFQException {	
		for (TestClass testClass:this.testClasses) {
			TestMethod testMethod = (TestMethod) testClass.getMethodByName(methodName);
			return testMethod;
		}
		throw new MethodWithGivenNameNotFoundException();
	}



	
	// Utility methods
	
	private void createClasses() throws DFQException {
		populateProductionClasses();
		populateTestClasses();
	}

	
	private void populateProductionClasses() throws DFQException  {
		String[] sourceFilePaths = fetchSourceFilePathsFromPropertyFile(SOURCE_FILES_PROPERTY);

		for (int i = 0; i < sourceFilePaths.length; i++) {
			if (isAJavaClassSourceFile(sourceFilePaths[i])) {
				ProductionClass prodClass = new ProductionClass(sourceFilePaths[i]);
				productionClasses.add(prodClass);
			}
		}
	}

	
	private void populateTestClasses() throws DFQException {
		String[] sourceFilePaths = fetchSourceFilePathsFromPropertyFile(TEST_SOURCE_FILES_PROPERTY);
		
		for (int i = 0; i < sourceFilePaths.length; i++) {
			if (isAJavaClassSourceFile(sourceFilePaths[i])) {
					TestClass testClass = new TestClass(sourceFilePaths[i]);
					testClasses.add(testClass);
				}
		}
	}
	
	
	private String[] fetchSourceFilePathsFromPropertyFile(String property) {
		return tarantulaProps.getProperty(property).split(File.pathSeparator);
	}
	
	
	private boolean isAJavaClassSourceFile(String sourceFilePath) {
		File classFile = new File(sourceFilePath);
		return classFile.isFile() && sourceFilePath.endsWith(CodeClass.JAVA_CLASS_FILE_SUFFIX);
	}

	
	private boolean requiredPropertiesExist(Properties tarantulaProps) {
		String[] requiredProperties = { SOURCE_FILES_PROPERTY,
										TEST_SOURCE_FILES_PROPERTY,
										TRACE_FILE_PROPERTY
									  };
		
		return tarantulaProps.stringPropertyNames().containsAll(Arrays.asList(requiredProperties));
	}
	
	
	private boolean traceFileExists(String traceFilePath) {
		File traceFile = new File(traceFilePath);
		return (traceFile.isFile() && traceFile.exists());
	}
	
	private boolean readPropertiesFile(Properties tarantulaProps) {
		return tarantulaProps.get(SOURCE_FILES_PROPERTY) != null; 
	}


	public List<CodeMethod> getAllMethods() {
		List<CodeMethod> methods = new ArrayList<CodeMethod>();
		
		for (CodeClass codeClass : this.getClasses())
			methods.addAll(codeClass.getMethods());
		
		return methods;
	}


	public List<Query> getSuspiciousQueries() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getDiagnosticReport() {
		// TODO Auto-generated method stub
		return null;
	}

}