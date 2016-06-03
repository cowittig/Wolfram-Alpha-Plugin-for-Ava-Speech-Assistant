package com.ava;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;
import com.wolfram.alpha.visitor.Visitable;


/**
 * The class <code>WAExecuter</code> abstracts the access to the Wolfram Alpha API. 
 * 
 * Important! You have to get a application id from the Wolfram Alpha developer 
 * website to perform queries with this API. 
 * 
 * @author Kevin
 * @since 2016-04-10
 * @version 1
 */
public class WAExecuter {

	final Logger log = LogManager.getLogger();
	
	/**
	 * The application id is used for queries. It is required by the Wolfram Alpha API. 
	 */
	private String appID = null;
	
	private WAEngine engine; 
	
	private ArrayList<String> pattern; 
	
	
	/**
	 * This is the Constructor of the <code>WAExecuter</code> class
	 * 
	 * @param appID The application id you get from the Wolfram Alpha developer website. The id is required by the API. 
	 */
	public WAExecuter(String appID) {
		
		log.debug("Initialize WAExecuter class. Arguemnts: String appid : " + appID);		

		this.engine = new WAEngine(); 
		log.debug("Wolfram Alpha engine initialized.");
		try{
			this.setAppID(appID);
		} catch(IllegalArgumentException e) {
			this.engine = null; 
			throw e; 
		}
		
		this.engine.addFormat("plaintext");
		//this.engine.addFormat("HTML");
		
		this.pattern = new ArrayList<>(); 
		this.pattern.add("BasicDefinitionPod:?\\w+"); 
		this.pattern.add("Definition:?\\w+");
		this.pattern.add("Description:?\\w+");
		this.pattern.add("DecimalApproximation");
		this.pattern.add("NotableFacts:?\\w+");
		this.pattern.add("Result");
//		this.pattern.add("WikipediaSummary:?\\w+");
		
		log.debug("WAExecuter initialized.");
	}
	
	
	/**
	 * Method for setting the application id. 
	 * 
	 * @param appID The appID for the queries. 
	 */
	public void setAppID(String appID) {
		if( appID.equals("") || appID == null) {
			throw new IllegalArgumentException("An error occured while init class WAExecuter. IllegalArguemnt: appID is null or empty.");
		}
		
		this.appID = appID; 
		if(this.engine != null)
			this.engine.setAppID(this.appID);
	}
	
	
	/**
	 * Method to send a query with a specific question to the Wolfram Alpha backend. 
	 * 
	 * @param question The question you want to get a answer for from the backend. 
	 * @return WAQueryResult Returns a <code>WAQueryResult</code> object with the answer from the backend. If an error occured the method returns null. 
	 */
	public WAQueryResult sendQuery(String question) {
		log.info("Create Wolfram Alpha a query for the question: " + question);
		WAQuery query = this.engine.createQuery(question); 
		
		try {
			log.debug("Query URL: " + this.engine.toURL(query));
			WAQueryResult queryResult = engine.performQuery(query);
			
			if( queryResult.isError() ) {
				log.error("There occured an error while performing the query '" + query.getInput() + "'.");
				log.error(">> error code: " + queryResult.getErrorCode());
				log.error(">> error message: " + queryResult.getErrorMessage());
				return null; 
				
			} else if( queryResult.isSuccess() ) {
				log.debug("Query succesful performed.");
				log.debug("Returnd amount of pods " + queryResult.getNumPods());
				log.debug("Needed time for query: " + queryResult.getTiming() + " sec."); 
				
				return queryResult; 
			}
			
		} catch (WAException e) {
			log.error("There occured an error while performing the query '" + query.getInput() + "'.");
			log.catching(Level.DEBUG, e);
		}
		
		return null; 		
	}
	
	
	/**
	 * This method extracts the answer as String from an given <code>WAQueryResult</code>. 
	 * 
	 * @param result A <code>WAQueryResult</code> object received from the Wolfram Alpha backend. 
	 * @return String The extracted answer as String from the given <code>WAQueryResult</code> object. 
	 */
	public String getResultString(WAQueryResult result) {
		log.debug("Extract result string from WAQueryResult object.");
		
		if( result == null ) {
			log.error("There is no result. Result is null.");
			return null; 
		}
		
		boolean definitionAvailable = false; 
    	for(int i = 0; i < result.getPods().length; i++) {
    		String podID = result.getPods()[i].getID(); 
    		log.debug("Received pod from Wolfram ALpha backend: " + podID);
    		if( Pattern.matches("BasicDefinitionPod:?\\w+", podID) )
    			definitionAvailable = true; 
    	}
		
    	String errorText = "An error occured while extracting a result string from Wolfram Alpha response."; 
    	String resultString = "No response from Wolfram Alpha could be utilized."; 
    	boolean match = false; 
		for (WAPod pod : result.getPods()) {
            if (!pod.isError()) {
            	
            	for( int i = 0; i < this.pattern.size(); i++ ) {
            		if( Pattern.matches(this.pattern.get(i), pod.getID()) ) {
            			resultString = this.getResultString(pod);
            			match = true; 
            			break; // TODO evtl RESULT pattern extra behandeln!!
            		}
            	}
            	
            	if(match)
            		break; 
            	
//            	if( Pattern.matches("WikipediaSummary:?\\w+", pod.getID()) ) {
//            		//http://en.wikipedia.org/wiki?curid=25967059
//            		HTTPContentLoader loader = new HTTPContentLoader("http://en.wikipedia.org/wiki?curid=25967059");
//            		resultString = loader.getContent(); 
//            		break;
//            	}

            } else {
            	log.error(errorText);
            	return errorText; 
            }
        }
		
		return resultString; 
	}
	
	
	/**
	 * Extracts a String with the result from an subpod. The given pod contains the subpod. 
	 * 
	 * @param pod
	 * @return String
	 */
	private String getResultString(WAPod pod) {
		
        for (WASubpod subpod : pod.getSubpods()) {
        	
        	Visitable[] t = subpod.getContents();
        	if( t.length == 0 )
        		break; 
        	
        	WAPlainText text = (WAPlainText) t[0]; 
        	String result = text.getText(); 
        	return result.replace("\n", ". ");
        }
        return "No response from Wolfram Alpha could be utilized."; 
	}
	
//	private String extractURLfromPod(WAPod pod) {
//		return "";
//	}
}
