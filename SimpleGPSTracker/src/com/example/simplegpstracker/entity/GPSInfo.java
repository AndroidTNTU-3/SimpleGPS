package com.example.simplegpstracker.entity;

public class GPSInfo {
	
	private int Id;
	private Double latitude;
	private Double longitude;
	private float accuracy;
	private String title;
	private long time;
	
	public GPSInfo(){};
	
	public GPSInfo(Double longitude, Double latitude){
		setLongitude(longitude);
		setLatitude(latitude);
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
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
	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}



}
