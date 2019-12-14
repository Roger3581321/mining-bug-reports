package mining_software_repositories;

import java.io.File;  
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import mining_software_repositories.Constants;

// mine bug reports
public class MiningBugReport {
	// rounded to the nearest 0.01
	private static DecimalFormat fixedDf = new DecimalFormat("0.00");

	// Bugzilla/Jira
	private static HashMap<String, Boolean> bugIdRestag = new HashMap<String, Boolean>();

	private static HashMap<String, Boolean> bugIdSevtag = new HashMap<String, Boolean>();

	private static HashMap<String, Boolean> bugIdIdAsgtag = new HashMap<String, Boolean>();
	
	private static HashMap<String, Long> bugIdUnResTime = new HashMap<String, Long>();

	@SuppressWarnings("unchecked")
	public static void miningMulFiles(File[] bugReports) {
		SAXReader saxreader = new SAXReader();
        Element elem = null;
		Element childElem = null;
		Element childChildElem = null;
		long created = 0L;
		long resolved = 0L;
		String bugSeverity = "";
		
		if (bugReports == null || bugReports.length <= 0) {
			return;
		}
		
		// traverse multiple bug report XML files under the directory
        for(File bugReport : bugReports) {
        	// extract the bug ID
        	String jiraId = bugReport.getName().split("_")[1].split("\\.")[0];
        	created = 0;
    		resolved = 0;
    		bugSeverity = "";
        	// build the Jira id, resolution time map
        	if (jiraId != null && !"".equals(jiraId)) {
        		bugIdRestag.put(jiraId, false);
        		bugIdIdAsgtag.put(jiraId, false);
        		
        		try {
        			Document document = saxreader.read(bugReport.getAbsoluteFile());
        			Element root 	  = document.getRootElement();
        			Iterator<Element> iterator = root.elementIterator();
        			
        			while(iterator.hasNext()) {
        				elem = (Element)iterator.next();
        				
        				if (elem != null) {					
        					Iterator<Element> childIterator = elem.elementIterator();
        					
        					while(childIterator.hasNext()) {
        						childElem = (Element)childIterator.next();
        						
        						if(childElem != null && "item".equals(childElem.getName())) {
                					Iterator<Element> childChildIterator = childElem.elementIterator();
                					
                					while(childChildIterator.hasNext()) {
                						childChildElem = (Element)childChildIterator.next();
                						
                						if (childChildElem != null && "priority".equals(childChildElem.getName())) {
                							bugSeverity = childChildElem.getTextTrim();
                							continue;
             						    }
                						
                						if(childChildElem != null && "assignee".equals(childChildElem.getName())) {
                							if (childChildElem.getTextTrim() != null && !"Unassigned".equals(childChildElem.getTextTrim())) {
                								bugIdIdAsgtag.put(jiraId, true);
                							}
                							continue;
                						}
                						
            							if(childChildElem != null && "created".equals(childChildElem.getName())) {
                							created = DateUtil.parseStrToLong(childChildElem.getTextTrim(), DateUtil.DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS); 
                							continue;
                						}
                						
                						if(childChildElem != null && "resolved".equals(childChildElem.getName())) {
                							bugIdRestag.put(jiraId, true);
                							resolved = DateUtil.parseStrToLong(childChildElem.getTextTrim(), DateUtil.DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS);
                							bugIdUnResTime.put(jiraId, null);	
                							continue;
                						} else {
                							// unresolved time logic
                							Long cur = new Date().getTime();
                							bugIdUnResTime.put(jiraId, ((cur - created) / Constants.MS_TO_D) - 1930);	
                							if(bugSeverity != null && Constants.BUG_SEVERITY.indexOf(bugSeverity) > -1) {
            						    		bugIdSevtag.put(jiraId, true);
                							}
            						    	else {
                						    	bugIdSevtag.put(jiraId, false);                 						    	
                 						    }
                							continue;
                						}
                						
                					}
        						}
        					}
        				}
        			}       		        		
        		} catch(Throwable t) {
        			System.out.println(t.getMessage());
        		}
        	}
        }
		doStatistics();
        
  		return;
	}
	
	@SuppressWarnings("unchecked")
	public static void miningOneFile(Element root, Boolean isJira) throws Exception {
		Element elem = null;
		Element childElem = null;
		String bugStatus = "";
		String bugId = "";	
		String assignedTo = "";
		String bugSeverity = "";
		long resolved = 0L;
		
		long created = 0L;

		if(root == null) {
			return;
		}
		Iterator<Element> iterator = root.elementIterator();			
		
		if (isJira) {
			iterator = iterator.next().elementIterator();
		} 
		
		try {
			// traverse every child node bug of root node bugzilla 
			while(iterator.hasNext()) {
				bugStatus = "";
				bugId = "";
				assignedTo = "";
				bugSeverity = "";
				created = 0L;
				
				elem = (Element)iterator.next();
				
				boolean loopCon = true; 
				if (!isJira) {
					loopCon = elem != null;
				} else {
					loopCon = elem != null && "item".equals(elem.getTextTrim());
				}
					
				if (!isJira) {
					if (elem != null) {
						Iterator<Element> childIterator = elem.elementIterator();
						while(childIterator.hasNext()) {
							childElem = (Element)childIterator.next();
							
							if(childElem != null && "bug_id".equals(childElem.getName())) {
								bugId = childElem.getTextTrim();
								
								if(bugId != null && !"".equals(bugId)) {
									bugIdRestag.put(bugId, null);
									bugIdIdAsgtag.put(bugId, null);
									bugIdUnResTime.put(bugId, null);
								}
								continue;
							}
							
							// bug unresolved time 
							if(childElem != null && "creation_ts".equals(childElem.getName())) {
								if(childElem.getTextTrim() != null && !"".equals(childElem.getTextTrim())) {
									created = DateUtil.parseStrToLong(childElem.getTextTrim(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);	
								}
							}
							
							// bug status
							if(childElem != null && "bug_status".equals(childElem.getName())) {
								bugStatus = childElem.getTextTrim();
								
								if(bugStatus != null) {
									if(!"".equals(bugStatus) && "RESOLVED".equals(bugStatus)) {
										bugIdRestag.put(bugId, true);
									} else {
										bugIdRestag.put(bugId, false);
									}
								}
							}
							
							// assigned to node
							if(childElem != null && "assigned_to".equals(childElem.getName())) {
								assignedTo = childElem.getTextTrim();
								
								if(assignedTo != null && !"".equals(assignedTo)) {
									bugIdIdAsgtag.put(bugId, true);
								} else {
									bugIdIdAsgtag.put(bugId, false);															
								}
							}
							
							// unresolved bug severity logic
						    if(!"".equals(bugStatus) && !"RESOLVED".equals(bugStatus)) {
						    	if (childElem != null && "bug_severity".equals(childElem.getName())) {
							    	bugSeverity = childElem.getTextTrim();
						    		if(bugSeverity != null && Constants.BUG_SEVERITY.indexOf(bugSeverity) > -1) {
							    		bugIdSevtag.put(bugId, true);													    			
						    		} else {
							    		bugIdSevtag.put(bugId, false);													    								    			
						    		}
								}
							}
							
							// unresolved time logic
						    if(!"".equals(bugStatus) && !"RESOLVED".equals(bugStatus)) {
								Long cur = new Date().getTime();
						    	bugIdUnResTime.put(bugId, (cur - created) / Constants.MS_TO_D);	
							} else {
								bugIdUnResTime.put(bugId, null);	
							}
						}
				}
			} else {
				if (elem != null && "item".equals(elem.getTextTrim())) {
					Iterator<Element> childIterator = elem.elementIterator();
					
					System.out.println(elem.getTextTrim());

					
					while(childIterator.hasNext()) {
						childElem = (Element)childIterator.next();
						
						if(childElem != null && "key".equals(childElem.getName())) {
							bugId = childElem.getTextTrim();
							
							if(bugId != null && !"".equals(bugId)) {
								bugIdRestag.put(bugId, false);
								bugIdIdAsgtag.put(bugId, false);
								bugIdUnResTime.put(bugId, null);
							}
							continue;
						}
						
						System.out.println(bugId);
						
						if (childElem != null && "priority".equals(childElem.getName())) {
							bugSeverity = childElem.getTextTrim();
							continue;
						    }
						
						if(childElem != null && "assignee".equals(childElem.getName())) {
							if (childElem.getTextTrim() != null && !"Unassigned".equals(childElem.getTextTrim())) {
								bugIdIdAsgtag.put(bugId, true);
							}
							continue;
						}
						
						if(childElem != null && "created".equals(childElem.getName())) {
							created = DateUtil.parseStrToLong(childElem.getTextTrim(), DateUtil.DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS); 
							continue;
						}
						
						if(childElem != null && "resolved".equals(childElem.getName())) {
							bugIdRestag.put(bugId, true);
							resolved = DateUtil.parseStrToLong(childElem.getTextTrim(), DateUtil.DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS);
							bugIdUnResTime.put(bugId, null);	
							continue;
						} else {
							// unresolved time logic
							Long cur = new Date().getTime();
							bugIdUnResTime.put(bugId, ((cur - created) / Constants.MS_TO_D) - 1930);	
							if(bugSeverity != null && Constants.BUG_SEVERITY.indexOf(bugSeverity) > -1) {
					    		bugIdSevtag.put(bugId, true);
							}
					    	else {
						    	bugIdSevtag.put(bugId, false);                 						    	
 						    }
							continue;
						}					
					}
				}
			}
		  }
		  doStatistics();
		} catch(Throwable t) {
			System.out.println(t.getMessage());
		}
				
		return;
	}
	
	// do statistics on the Hashmaps
	private static void doStatistics() {
		Double resRate = 0.00d;
		Double asgRate = 0.00d;
		Double sevRate = 0.00d;
		long maxUnresolvedTime = 0L;
        long minUnresolvedTime = Long.MAX_VALUE;
        long avgUnresolvedTime = 0L;
		int resSum = 0;
		int asgSum = 0;
		int sevSum = 0;
		Long unresolvedTimeSum = 0L;

		// bug resolution rate 
		for(Entry<String, Boolean> entry : bugIdRestag.entrySet()) {
			if (entry.getValue() != null) {
				if(entry.getValue() == true) {
					resSum++;					
				}
			}						
	    }

		// assigned rate 
		for(Entry<String, Boolean> entry : bugIdIdAsgtag.entrySet()) {
			if (entry.getValue() != null) {
				if(entry.getValue() == true) {
					asgSum++;					
				}
			}						
		}
		
		// bug severity rate 
		for(Entry<String, Boolean> entry : bugIdSevtag.entrySet()) {
			if (entry.getValue() != null) {
				if(entry.getValue() == true) {
					sevSum++;					
				}
			}						
		}

		// bug unresolved time 
		for(Entry<String, Long> entry : bugIdUnResTime.entrySet()) {
			if (entry.getValue() != null) {
				unresolvedTimeSum += entry.getValue();
			
				if (entry.getValue() > maxUnresolvedTime) {
					maxUnresolvedTime = entry.getValue();
				}
				if (entry.getValue() < minUnresolvedTime) {
					minUnresolvedTime = entry.getValue();
				}
			}
		}
		
		resRate = Double.parseDouble(fixedDf.format((double)resSum / bugIdRestag.size()));
		asgRate = Double.parseDouble(fixedDf.format((double)asgSum / bugIdIdAsgtag.size()));
		sevRate = Double.parseDouble(fixedDf.format((double)sevSum / bugIdSevtag.size()));
		avgUnresolvedTime = unresolvedTimeSum / bugIdUnResTime.size();

		printResults(resRate, asgRate, sevRate, maxUnresolvedTime, minUnresolvedTime, avgUnresolvedTime);
		return;
	}
	
	// format output 
	private static void printResults(Double resRate, Double asgRate, Double sevRate, long maxUnresolvedTime, long minUnresolvedTime, long avgUnresolvedTime) {
		System.out.println("resRate " + resRate);
		System.out.println("asgRate " + asgRate);
		System.out.println("sevRate " + sevRate);
		System.out.println("bugIdUnResTime ");
		System.out.println("maxUnresolvedTime " + maxUnresolvedTime);
		System.out.println("minUnresolvedTime " + minUnresolvedTime);
		System.out.println("avgUnresolvedTime " + avgUnresolvedTime);
		bugIdRestag.clear();
		bugIdIdAsgtag.clear();
		bugIdUnResTime.clear();	
		bugIdSevtag.clear();
		return;
	}
	
}
