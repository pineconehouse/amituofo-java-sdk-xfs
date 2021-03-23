package com.amituofo.xfs.service.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class FileLastModifiedTimeFilter implements ItemFilter {
	// private final SimpleDateFormat yyyyMMdd=new SimpleDateFormat("yyyy-MM-dd");

	private int days1 = -1;
	private int days2 = -1;
	private String datetime1 = null;
	private String datetime2 = null;
	private Operator operator;

	private Date datetime1InDate = null;
	private Date datetime2InDate = null;
	private SimpleDateFormat datetimeFormater;

	public FileLastModifiedTimeFilter(String datetime, String datetimeFormat, Operator operator) throws ParseException {
		super();
		this.datetime1 = datetime;
		this.operator = operator;

		this.datetimeFormater = new SimpleDateFormat(datetimeFormat);
		this.datetime1InDate = datetimeFormater.parse(datetime);
	}

	public FileLastModifiedTimeFilter(String datetime1, String datetime2, String datetimeFormat, Operator operator) throws ParseException {
		super();
		this.datetime1 = datetime1;
		this.datetime2 = datetime2;
		this.operator = operator;

		this.datetimeFormater = new SimpleDateFormat(datetimeFormat);
		this.datetime1InDate = datetimeFormater.parse(datetime1);
		this.datetime2InDate = datetimeFormater.parse(datetime2);
	}

	public FileLastModifiedTimeFilter(int lastModifiedDaysMoreThatXDays) {
		super();
		this.days1 = lastModifiedDaysMoreThatXDays;
		this.operator = Operator.moreThatDays;
	}

	public FileLastModifiedTimeFilter(int days1, int days2) {
		super();
		this.days1 = days1;
		this.days2 = days2;
		this.operator = Operator.between;
	}

	@Override
	public boolean accept(Item file) {
//		if (file.isDirectory()) {
//			return true;
//		}

		Date lastModified = new Date(file.getLastUpdateTime());

		switch (operator) {
			case equal:
				return datetimeFormater.format(lastModified).equals(datetime1);
			case greater:
				return lastModified.after(datetime1InDate);
			case less:
				return lastModified.before(datetime1InDate);
			case between:
				if (datetime2InDate != null) {
					return lastModified.after(datetime1InDate) && lastModified.before(datetime2InDate);
				} else if (days2 > 0) {
					int daysBetweenNow = daysBetween(Calendar.getInstance().getTime(), lastModified);
					return (daysBetweenNow >= days1 && daysBetweenNow <= days2);
				}
			case moreThatDays:
				int daysBetweenNow = daysBetween(Calendar.getInstance().getTime(), lastModified);
				return (daysBetweenNow >= days1);
		}
		
		return false;
	}

	private int daysBetween(Date smdate, Date bdate) {
		// smdate=yyyyMMdd.parse(yyyyMMdd.format(smdate));
		// bdate=yyyyMMdd.parse(yyyyMMdd.format(bdate));
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(smdate);
		// long time1 = cal.getTimeInMillis();
		// cal.setTime(bdate);
		// long time2 = cal.getTimeInMillis();
		// long between_days=(time2-time1)/(1000*3600*24);
		// return Integer.parseInt(String.valueOf(between_days));

		long stateTimeLong = smdate.getTime();
		long endTimeLong = bdate.getTime();
		// 结束时间-开始时间 = 天数
		int day = (int) ((endTimeLong - stateTimeLong) / (24 * 60 * 60 * 1000));

		return day;
	}

}
