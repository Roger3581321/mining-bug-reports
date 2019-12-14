package mining_software_repositories;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Main {
	
	public static void main(String[] args) {
		// mine multiple-file bug reports
		File dir = new File(Constants.HBASE_BUG_REPORT_DIR);
				
        if (!dir.exists() || !dir.isDirectory()) {  
            return;  
        }
        
        File[] bugReports = dir.listFiles();
        if(bugReports != null && bugReports.length > 0) {
        	MiningBugReport.miningMulFiles(bugReports);           
        }
        
        // mine one-file bug reports
		SAXReader saxreader = new SAXReader();
		try {
			String[] onePaths = {
				Constants.CRIMSON_XML_PATH,				
				Constants.TOMCAT_7_XML_PATH,
				Constants.HTTPD_13_XML_PATH,
				Constants.LOG4J_XML_PATH
			};
			
			for (String onePath : onePaths) {
				Document document = saxreader.read(onePath);
				Element root 	  = document.getRootElement();	
				if (root != null &&  !Constants.LOG4J_XML_PATH.equals(onePath)) {
					MiningBugReport.miningOneFile(root, false);				
				} else {
					MiningBugReport.miningOneFile(root, true);									
				}
			}
		} catch(Throwable t) {
			System.out.println(t.getMessage());
		}        
        return;
	}

}
