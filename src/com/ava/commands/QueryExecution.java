package com.ava.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ava.eventhandling.SpeakEvent;
import org.ava.eventhandling.TTSEventBus;
import org.ava.pluginengine.AppCommand;

import com.ava.WAExecuter;
import com.wolfram.alpha.WAQueryResult;

public class QueryExecution implements AppCommand{

	private final Logger log = LogManager.getLogger(QueryExecution.class);
	
	private WAExecuter executer; 
	
	private String command = "*"; 
	
	public QueryExecution(WAExecuter executer) {
		log.debug("Command 'QueryExecution' initialized.");
		if( executer != null )
			this.executer = executer; 
		else {
			log.fatal("Error while initialize QueryExecution. Argument is null.");
			throw new IllegalArgumentException(); 
		}
	}
	
	@Override
	public void execute(String arg) {
		log.debug("Starting command execution with given parameter '" + arg + "'.");
		if( arg == null ) {
			log.error("Argument is null. Abort command execution.");
			return; 
		}
		
		WAQueryResult tmp = this.executer.sendQuery(arg); 
		String tmpResult = this.executer.getResultString(tmp); 
		log.debug("Get result from Wolfram Alpha REST API: '" + tmpResult + "'.");
		
		// String result = "Wolfram Alpha answered " + tmpResult;  
		
		// For testing
		//System.out.println("Wolfram Alpha answered: " + tmpResult);
		
		TTSEventBus.getInstance().fireSspeakEvent(new SpeakEvent(tmpResult)); 
	}

	@Override
	public String getCommand() {		
		return this.command;
	}

}
