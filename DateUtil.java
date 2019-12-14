package mining_software_repositories;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// util for converting date types
public class DateUtil {	
	// date format in Jira report 
	public static final String DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS = "E dd MMM yyyy HH:mm:ss";  
	
	public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";  
	
	public static final String DATE_TIME_FORMAT_YYYYMMDD = "yyyyMMddHHmmss";  
	
	// parse string date to long
	public static long parseStrToLong(String date, String timeFromat) throws Throwable {
		String _date = date.replace("+0000", "").replace(",", "").trim();
		DateFormat dateFormat = new SimpleDateFormat(timeFromat);  
		return dateFormat.parse(_date).getTime();
	}
	
	public static String parseDateToStr(Date time, String timeFromat) {  
		DateFormat dateFormat = new SimpleDateFormat(timeFromat);  
	    return dateFormat.format(time);  
	} 
	
	public static void main(String[] args) {
		String str1 = "Fri, 18 Apr 2014 23:21:23 +0000";
		String str2 = "Wed, 21 May 2014 02:03:46 +0000";
		String str3 = "2009-12-23 03:33:13 +0000";
		try {
			//long l1 = parseStrToLong(str1, DATE_TIME_FORMAT_E_DD_MMM_YYYY_HHMMSS);
			long l = parseStrToLong(str3, DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
			//long l = l2 - l1;
			Date d = new Date(l);
			
			System.out.println(parseDateToStr(d, DATE_TIME_FORMAT_YYYYMMDD));
			System.out.println(l/1000/60/60/24);

		} catch(Throwable t) {
			
		}
	}
}
