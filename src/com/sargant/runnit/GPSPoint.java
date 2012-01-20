package com.sargant.runnit;

import org.joda.time.DateTime;

public class GPSPoint {

	private DateTime time;
	private Double latitude;
	private Double longitude;
	private Double elevation;
	
	public GPSPoint(Double lat, Double lon, Double alt, DateTime time) {
		setTime(time);
		setLatitude(lat);
		setLongitude(lon);
		setElevation(alt);
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double altitude) {
		this.elevation = altitude;
	}

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
	
	public Double distanceTo(GPSPoint p1) {
		return distanceBetween(this, p1);
	}
	
	public static Double distanceBetween(GPSPoint p1, GPSPoint p2) {
		
		Double R = 6371000.0;
		
		Double deltaLat = Math.toRadians(p2.getLatitude()-p1.getLatitude());
		Double deltaLon = Math.toRadians(p2.getLongitude()-p1.getLongitude());
		Double deltaAlt = p2.getElevation() - p1.getElevation();
		
		Double a = Math.sin(deltaLat/2.0) * Math.sin(deltaLat/2.0) +
		        	Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude())) *
		        	Math.sin(deltaLon/2.0) * Math.sin(deltaLon/2.0);
		
		Double crowDistance = 2 * R * Math.asin(Math.sqrt(a));
		
		return Math.sqrt((deltaAlt * deltaAlt) + (crowDistance * crowDistance));
	}
}
