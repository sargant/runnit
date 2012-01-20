package com.sargant.runnit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
//import java.net.URLDecoder;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RunnitApp {

	private String filename;
	private Vector<GPSTrack> tracks;
	private int activeTrack;
	
	public RunnitApp() {
		tracks = new Vector<GPSTrack>();
		activeTrack = 0;
	}
	
	public String getJarURI() {
		
		String path = null;
		
		try {
			File p = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			if(p.getParent() == null) {
				path =  "file://" + URLDecoder.decode(p.getCanonicalPath().toString(), "UTF-8") + "/";				
			} else {
				path =  "file://" + URLDecoder.decode(p.getParent().toString(), "UTF-8") + "/";
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) throws SAXException, IOException {
		this.filename = filename;
		parseGPX();
	}
	
	public Integer getTrackCount() {
		return tracks.size();
	}
	
	public void setActiveTrack(int i) {
		activeTrack = i;
	}
	
	public GPSTrack getActiveTrack() {
		return getTrack(activeTrack);
	}
	
	public GPSTrack getTrack(int i) {
		return tracks.get(i);
	}
	
	private void parseGPX() throws SAXException, IOException {
		
		// Construct the DOM
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		
		Document d = db.parse(filename);
		
		// Get a list of tracks
		NodeList xmlTracks = d.getElementsByTagName("trk");
		tracks.setSize(xmlTracks.getLength());
		
		for(int i = 0; i < xmlTracks.getLength(); i++) {
			
			tracks.setElementAt(new GPSTrack(), i);
			
			for(Node xmlTrackNode = xmlTracks.item(i).getFirstChild(); xmlTrackNode != null; xmlTrackNode = xmlTrackNode.getNextSibling()) {
				if(xmlTrackNode.getNodeName().equalsIgnoreCase("trkseg")) {
					for(Node xmlTrackPoint = xmlTrackNode.getFirstChild(); xmlTrackPoint != null; xmlTrackPoint = xmlTrackPoint.getNextSibling()) {
						if(xmlTrackPoint.getNodeName().equalsIgnoreCase("trkpt")) {
							
							Double altitude = null;
							DateTime time = null;
							
							for(Node xmlPointData = xmlTrackPoint.getFirstChild(); xmlPointData != null; xmlPointData = xmlPointData.getNextSibling()) {
								if(xmlPointData.getNodeName().equalsIgnoreCase("ele")) {
									altitude = Double.valueOf(xmlPointData.getTextContent());
								}
								if(xmlPointData.getNodeName().equalsIgnoreCase("time")) {
									time = DateTime.parse(xmlPointData.getTextContent());
								}
							}
							
							NamedNodeMap nmap = xmlTrackPoint.getAttributes();
							
							Double latitude = Double.valueOf(nmap.getNamedItem("lat").getNodeValue());
							Double longitude = Double.valueOf(nmap.getNamedItem("lon").getNodeValue());
							
							tracks.get(i).addPoint(new GPSPoint(latitude, longitude, altitude, time));
						}
					}
				}
			}
		}
		
		setActiveTrack(0);
	}
	
}
