package com.italia.marxmind.appUtils;
/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */
public class StringUtils {
	private static final String BACK_SLASH = "\\";
	private static final String SLASH = "/";
	private static final String SINGLE_QUOTE = "'";
	private static final String DOUBLE_QUOTE = "\"";
	
	public static String removeSpecialChar(String val){
		
		String chars[] = new String[]{"\\","/","'","\"",";"};
		
		
		for(int i=0; i<chars.length;i++){
			val = val.replace(chars[i], "");
		}
		
		return val;
	}
}
