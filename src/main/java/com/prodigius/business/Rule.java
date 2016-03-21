package com.prodigius.business;

import java.util.Date;

public class Rule {

	public static boolean areDatesValid(Date start, Date end) {
		
		if (start.after(end)) {
			return false;
		}
		return true;
	}
}
