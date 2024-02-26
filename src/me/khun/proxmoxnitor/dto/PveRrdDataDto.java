package me.khun.proxmoxnitor.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import me.khun.proxmoxnitor.pve.PveRrdData;

public class PveRrdDataDto {

	private PveRrdData data;
	
	public PveRrdDataDto(PveRrdData data) {
		this.data = data;
	}
	
	public float getCpuUsageInPercentage() {
		return formatDecimal(data.getCpuUsage() * 100);
	}
	
	public float getCpuUsage() {
		return formatDecimal(data.getCpuUsage());
	}
	
	public float getIoDelayInPercentage() {
		return formatDecimal(data.getIoDelay() * 100);
	}
	
	public float getIoDelay() {
		return formatDecimal(data.getIoDelay());
	}
	
	public long getTimestamp() {
		return data.getTime();
	}
	
	public String getTimestampFormatted() {
		LocalDateTime time =  LocalDateTime.ofInstant(Instant.ofEpochSecond(data.getTime()), TimeZone.getDefault().toZoneId());
		String pattern = "";
		switch (data.getType()) {
		case HOUR_AVERAGE:
		case HOUR_MAXIMUM:
		case DAY_AVERAGE:
		case DAY_MAXIMUM:
		case WEEK_AVERAGE:
		case WEEK_MAXIMUM:
		case MONTH_AVERAGE:
		case MONTH_MAXIMUM:
			pattern = "YYYY-MM-dd\nHH:mm";
			break;
		case YEAR_AVERAGE:
		case YEAR_MAXIMUM:
			pattern = "YYYY-MM-dd";
			break;
		}
		return time.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	private float formatDecimal(float value) {
		return Float.parseFloat(String.format("%.2f", value));
	}
}
