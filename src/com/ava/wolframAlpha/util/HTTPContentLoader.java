package com.ava.wolframAlpha.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPContentLoader {

	final Logger log = LogManager.getLogger();
	
	private URL url; 
	
	public HTTPContentLoader(String url) {
		if( url != null && !url.equals("") ) {
			
			try {
				this.url = new URL(url);
				
			} catch (MalformedURLException e) {
				log.catching(Level.DEBUG, e);
			} 
			
		} else {
			throw new IllegalArgumentException("An argument is null or empty."); 
		}
	}
	
	public String getContent() {
		
		String text = ""; 
		String result = ""; 
		
		
		
		//String t = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exsentences=1&exintro=&explaintext=&exsectionformat=plain&"; 
		String t = "http://en.wikipedia.org/w/api.php?action=query&pageids=37274&format=xml";
		URL a = null; 
		try {
			a = new URL(t + "curid=25967059");
		} catch (MalformedURLException e1) {
			log.catching(Level.DEBUG, e1);
		}
		
		
		
		InputStream urlInputStream;
		BufferedReader br; 
		try {
			urlInputStream = a.openConnection().getInputStream();

			br = new BufferedReader(new InputStreamReader(urlInputStream)); 
		   
			String line = null;
		    while (null != (line = br.readLine())) {
		        line = line.trim();
		        if (true) {
		            text += line;
		        }
		    }
		    
		    br.close(); 
		    urlInputStream.close();
		    
		    result = this.extractResultFromJSON(text);
		
		} catch (IOException e) {
			log.catching(Level.DEBUG, e);
		} catch (JSONException e) {
			log.catching(Level.DEBUG, e);
		} 
		
		return result; 
	}
	
	private String extractResultFromJSON(String text) throws JSONException {
		
		JSONObject json = new JSONObject(text);
		JSONObject query = json.getJSONObject("query");
		JSONObject pages = query.getJSONObject("pages"); 
//		for(String key: pages.keys()) {
//		    System.out.println("key = " + key);
//		    JSONObject page = pages.getJSONObject(key);
//		    String extract = page.getString("extract");
//		    System.out.println("extract = " + extract);
//		}
		
		return ""; 
	}
}
