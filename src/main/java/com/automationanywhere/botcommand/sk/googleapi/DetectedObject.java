package com.automationanywhere.botcommand.sk.googleapi;


public class DetectedObject {
	private String name;
	private float confidence;
	
	public DetectedObject(String name, float confidence) {
		this.name  = name;
		this.confidence = confidence;
	}
	
	public String getName() {
		return this.name;
	}
	
	public float getConfidenence() {
		return this.confidence;
	}
	
}

