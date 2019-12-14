package mining_software_repositories;

// constans for programming exercise
public class Constants {
	// path prefix of Hbase log and jira reports
	private static final String PATH_PREFIX = "C:/Users/Thinkpad/Desktop/computer_sci_tech/projects/workspace/roger/log/";
		
	// bug reports path
	public static String HBASE_BUG_REPORT_DIR = PATH_PREFIX + "hbaseBugReport";

	public static String CRIMSON_XML_PATH = PATH_PREFIX + "crimson/all_bugs.xml";

	public static String TOMCAT_7_XML_PATH = PATH_PREFIX + "tomcat7/all_bugs.xml";

	public static String HTTPD_13_XML_PATH = PATH_PREFIX + "httpd_1.3/all_bugs.xml";

	public static String LOG4J_XML_PATH = PATH_PREFIX + "log4j2/all_bugs.xml";
			
	// unit milliseconds to hour
	public static final long MS_TO_H = 1000 * 60 * 60;

	// unit milliseconds to day
	public static final long MS_TO_D = 1000 * 60 * 60 * 24;
	
	// bug severity definition
	public static final String BUG_SEVERITY = "blocker, critical, major|Blocker, Critical, Major";

}
