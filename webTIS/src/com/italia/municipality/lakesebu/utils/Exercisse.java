package com.italia.municipality.lakesebu.utils;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
public class Exercisse {
	public static void main(String[] args) {
		double grade = 0d;
		String[] subjects = {"Math","English","Science"};
		String str = "";
		for(String s : subjects) {
			String value = JOptionPane.showInputDialog(s);
			grade += Double.valueOf(value);
			str += s + " : " + value + "\n";
		}
		double average = grade/3;
		average = formatDouble(average);
		str = "Following are the grade for the following subjects \n" + str;
		str += "You have an average grade of " + average + "\n";
		str += "And you have received a mark of ";
		
		if(average>=70 && average<=79) {
			str +=  "Good";
		}else if(average>=80 && average<=89) {
			str += "Very Good";
		}else if(average>=90) {
			str += "Excellent";	
		}else {
			str += "Failed";
		}
		JOptionPane.showMessageDialog(null, str);
	}
	
	public static double formatDouble(double value){
		try{
		DecimalFormat df = new DecimalFormat("####0.00");
		value = Double.valueOf(df.format(value));
		}catch(Exception e){System.out.println("Error in formatDouble for value : " + value + " error : " + e.getMessage());}
		return value;
	}
	
}
