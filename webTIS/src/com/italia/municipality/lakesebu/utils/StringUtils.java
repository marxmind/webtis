package com.italia.municipality.lakesebu.utils;

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
		
		/*val = val.replace(BACK_SLASH, "");
		val = val.replace(SLASH, "");
		val = val.replace(SINGLE_QUOTE, "");
		val = val.replace(DOUBLE_QUOTE, "");*/
		
		return val;
	}
	
	/**
	 * 
	 * @param str
	 * @return converted first char into uppercase
	 */
	public static String convertFirstCharToUpperCase(String str) {
		try {
		char ch[] = str.toCharArray();
		
		for(int i = 0; i < str.length(); i++) {
			if(i == 0 && ch[i] != ' ' || ch[i] !=' ' && ch[i-1] == ' ') {
				if(ch[i] >= 'a' && ch[i] <= 'z') {
					ch[i] = (char)(ch[i] - 'a' + 'A');
				}
			}else if(ch[i] >= 'A' && ch[i] <= 'Z') {
				ch[i] = (char) (ch[i] + 'a' - 'A');
			}
		}
		String st = new String(ch);
		
		return st;
		}catch(Exception e) {}
		
		return str;
	}
	
	public static void main(String[] args) {
		System.out.println(convertFirstCharToUpperCase("geeek For The cause"));
	}
	
}
