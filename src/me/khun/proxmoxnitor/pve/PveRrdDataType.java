package me.khun.proxmoxnitor.pve;

public enum PveRrdDataType {
	HOUR_AVERAGE("hour", "AVERAGE"),
	HOUR_MAXIMUM("hour", "MAX"),
	DAY_AVERAGE("day", "AVERAGE"),
	DAY_MAXIMUM("day", "MAX"),
	WEEK_AVERAGE("week", "AVERAGE"),
	WEEK_MAXIMUM("week", "MAX"),
	MONTH_AVERAGE("month", "AVERAGE"),
	MONTH_MAXIMUM("month", "MAX"),
	YEAR_AVERAGE("year", "AVERAGE"),
	YEAR_MAXIMUM("year", "MAX");
	
	private String timeFrame;
	private String cf;

	PveRrdDataType(String timeFrame, String cf) {
		this.timeFrame = timeFrame;
		this.cf = cf;
	}

	public String getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}
	
}
