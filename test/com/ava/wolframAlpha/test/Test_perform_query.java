package com.ava.wolframAlpha.test;

import java.util.List;
import org.ava.pluginengine.AppCommand;

import com.ava.PluginEntry;

public class Test_perform_query {

	private static String query = "who is George Washington?"; 
	
	public static void main(String[] args) {
		
		System.out.println("Starting Test_perform_query");
		
		System.out.println("Init PluginEntry and start.");
		PluginEntry plugin = new PluginEntry(); 
		plugin.start();
		
		// This plug-in has only one command
		List<AppCommand> commandList = plugin.getApplicationCommands(); 
		AppCommand command = commandList.get(0); 
		
		command.execute(query);
	}

}
