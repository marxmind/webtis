package com.italia.municipality.lakesebu.reports;

import java.util.ArrayList;
import java.util.List;

import com.italia.municipality.lakesebu.enm.EmployeeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Week {

	private int id;
	private int sunday;
	private int monday;
	private int tuesday;
	private int wednesday;
	private int thursday;
	private int friday;
	private int saturday;
	
	private String sundayStyle;
	private String mondayStyle;
	private String tuesdayStyle;
	private String wednesdayStyle;
	private String thursdayStyle;
	private String fridayStyle;
	private String saturdayStyle;
	
	private int month;
	private int year;
	
	public static void showOutput(List<Week> weeks){
		for(Week w : weeks) {
			System.out.println(
					w.getSunday() + " " +
					w.getMonday() + " " +
					w.getTuesday() + " " +
					w.getWednesday() + " " +
					w.getThursday() + " " +
					w.getFriday() + " " +
					w.getSaturday()
					);
		}
	}
	public static int workingDay(List<Week> weeks, int holiday, EmployeeType type) {
		int count=0;
		for(Week w : weeks) {
			if(w.getMonday()>0) {
				count++;
			}
			if(w.getTuesday()>0) {
				count++;
			}
			if(w.getWednesday()>0) {
				count++;
			}
			if(w.getThursday()>0) {
				count++;
			}
			if(w.getFriday()>0) {
				count++;
			}
		}
		System.out.println("Count Day:" + count + " holiday:" + holiday);
		if(EmployeeType.JO.getId()==type.getId()) {
			count-= holiday;
		}
		
		return count;
	}
}
