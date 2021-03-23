package com.amituofo.xfs.service.filter;

import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemFilter;

public class FileDateFilter implements ItemFilter {
	public static enum DateType {
		CREATE_DATE, MODIFIED_DATE;// , ACCESS_DATE
	}

	private DateType dateType = DateType.MODIFIED_DATE;
	private long datetime1, datetime2;
	private Operator operator;

	public FileDateFilter(DateType dateType, Long fromDate, Long toDate) {
		super();
		this.dateType = dateType;
		
		if (fromDate != null && toDate != null) {
			this.datetime1 = fromDate;
			this.datetime2 = toDate;
			this.operator = Operator.between;
		} else if (fromDate == null && toDate != null) {
			this.datetime1 = toDate;
			this.operator = Operator.less;
		} else if (fromDate != null && toDate == null) {
			this.datetime1 = fromDate;
			this.operator = Operator.greater;
		} else {
			operator = null;
		}
	}
	
//	public DSBFileDate(DateType dateType, String fromDateYYYYMMDDHHMMSS, Long toDateYYYYMMDDHHMMSS) {
//		super();
//		this.dateType = dateType;
//		this.fromDate = DateUtils.toDate(yyyymmdd);
//		this.toDate = toDate;
//	}

	@Override
	public boolean accept(Item file) {
//		if (file.isDirectory()) {
//			return true;
//		}
		
		if(operator==null) {
			return true;
		}
			
		Long dateTime;
		switch (dateType) {
			case CREATE_DATE:
				dateTime = file.getCreateTime();
				break;
			case MODIFIED_DATE:
				dateTime = file.getLastUpdateTime();
				break;
			default:
				dateTime = file.getLastUpdateTime();
		}

		if (dateTime == null) {
			return false;
		}
		
		long time = dateTime.longValue();
		
//		System.out.println("file=" + file + " time=" + time + " min=" + datetime1 + " max=" + datetime2 + " operator="+operator);
		
		switch (operator) {
			case greater:
				return time >= datetime1;
			case less:
				return time <= datetime1;
			case between:
				return time >= datetime1 && time <= datetime2;
			default:
				return false;
		}

		// between
//		if (fromDate != null && toDate != null) {
//			return (fromDate <= dateTime) && (toDate >= dateTime);
//			// after
//		} else if (fromDate != null && toDate == null) {
//			return (fromDate <= dateTime);
//			// before
//		} else if (fromDate == null && toDate != null) {
//			return (toDate >= dateTime);
//		}

//		throw new ServiceException("From Date or To Date must be specified");
	}

}
