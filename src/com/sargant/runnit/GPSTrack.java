package com.sargant.runnit;

import java.util.Vector;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;

public class GPSTrack {

	private Vector<GPSPoint> track;
	
	public GPSTrack() {
		track = new Vector<GPSPoint>();
	}
	
	public void addPoint(GPSPoint p) {
		track.add(p);
	}
	
	public Integer getTrackLength() {
		return track.size();
	}
	
	public GPSPoint getPoint(int i) {
		if(i<0) i=0;
		return track.get(i);
	}
	
	public GPSPoint getFirstPoint() {
		return (getTrackLength() > 0) ? getPoint(0) : null;
	}
	
	public GPSPoint getLastPoint() {
		return (getTrackLength() > 0) ? getPoint(getTrackLength() - 1) : null;
	}
	
	public Double getTrackTotalDistance() {
		
		Double totalDistance = 0.0;
		
		// Start at 1, always look back at previous point
		for(int i = 1; i < track.size(); i++) {
			totalDistance += GPSPoint.distanceBetween(track.get(i-1),track.get(i));
		}
		
		return totalDistance;
	}
	
	public Double getTrackTotalClimb() {
		Double totalClimb = 0.0;
		
		// Start at 1, always look back at previous point
		for(int i = 1; i < track.size(); i++) {
			
			Double deltaHeight = track.get(i).getElevation() - track.get(i-1).getElevation();
			
			if(deltaHeight > 0)
				totalClimb += deltaHeight;
		}
		
		return totalClimb;
	}
	
	public Period getTrackTotalDuration() {
		return new Period(getFirstPoint().getTime(), getLastPoint().getTime(), PeriodType.time());
	}
	
	
	public Double getAverageSpeed() {
		// returns meters per second
		Seconds s = getTrackTotalDuration().toStandardSeconds();
		return (getTrackTotalDistance()/Double.valueOf(s.getSeconds()));
	}
	
	public Period getAveragePace() {
		// returns minutes and seconds per kilometer
		Double seconds = Double.valueOf(getTrackTotalDuration().toStandardSeconds().getSeconds());
		Double seconds_per_m = (seconds / (getTrackTotalDistance()));
		
		Period p = new Period(Math.round(seconds_per_m * 1000000.0), PeriodType.forFields(new DurationFieldType[] {DurationFieldType.seconds(), DurationFieldType.minutes()}));
		return p;
	}
	
	public Double getStepSpeed(int i) {
		
		if(i < 1 || i > getTrackLength()) 
			return 0.0;
			
		// Get time period between two points
		Period interval = new Period(getPoint(i-1).getTime(), getPoint(i).getTime(), PeriodType.time());
		Double distance = GPSPoint.distanceBetween(getPoint(i-1), getPoint(i));
		
		return (distance/Double.valueOf(interval.toStandardSeconds().getSeconds()));
	}
}
