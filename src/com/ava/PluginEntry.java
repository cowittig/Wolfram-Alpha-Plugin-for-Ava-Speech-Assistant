package com.ava;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ava.pluginengine.AppCommand;
import org.ava.pluginengine.AppPlugin;
import org.ava.util.PropertiesFileLoader;

import com.ava.commands.QueryExecution;


/**
 * This class is the main entry point for the Wolfram Alpha plugin for the speech assistant Ava.
 * With this plugin you can perform queries with question. The questions will be sent via internet to the Wolfram
 * Alpha backend and a answer will be delivered.
 *
 * Use the <code>start</code> method to initialize the plugin. With the <code>getApplicationCommands</code> method you can get all commands that can be executed by this plugin.
 *
 * @author Kevin
 * @since 2016-04-10
 * @version 1
 */
public class PluginEntry implements AppPlugin {

	private final Logger log = LogManager.getLogger(PluginEntry.class);

	private String pluginName = "Wolfram Alpha Plugin";
	private Path CONFIG_PATH = null;
	private String CONFIG_NAME = "wa.properties";

	private WAExecuter executer;

	private List<AppCommand> pluginCommands;

	public PluginEntry() {	}

	@Override
	public void start() {
		log.info("Plugin " + this.pluginName + " starting.");
		String appID = this.getAppIDFromPropertieFile();

		if( appID == null ) {
			return;
		}

		try {
			if( this.executer == null )
				this.executer = new WAExecuter(appID);
			else
				log.debug(this.pluginName + " with WAExecuter is already initialized.");
		} catch(IllegalArgumentException e) {
			log.fatal("An error occured while initialize PluginEntry class. Plugin will be closed.");
			log.catching(Level.DEBUG, e);
			return;
		}

		this.initCommands();
	}


	@Override
	public List<AppCommand> getApplicationCommands() {
		if( this.pluginCommands == null ) {
			log.error("An error occured. No commands initialized.");
		}

		return this.pluginCommands;
	}


	/**
	 * This method initialize the commands of this plug-in and add these to the command list.
	 */
	private void initCommands() {
		log.debug("Initialize commands...");
		// init the ArrayList, witsh will contain the commands
		this.pluginCommands = new ArrayList<AppCommand>();

		QueryExecution qeuryExecution = new QueryExecution(this.executer);

		// add command to list
		this.pluginCommands.add(qeuryExecution);
		log.debug("Commands initialied. ");
	}

	/**
	 * This method loads the wa.properties file and returns the required application id.
	 *
	 * @return String The application id for the Wolfram Alpha API. Source is the wa.properties file.
	 */
	private String getAppIDFromPropertieFile() {
		ArrayList<String> propList = new ArrayList<String>();
		propList.add("appID");

		PropertiesFileLoader loader = null;

		// initialize the config file path
		try {
		    Path basePath = new File(PluginEntry.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath().getParent();
		    CONFIG_PATH = Paths.get(basePath.toString(), "/res/" + this.CONFIG_NAME);
		} catch (URISyntaxException e) {
		    log.error("Error while creating the wolfram alpha configuration file path: " + e.getMessage());
		    log.catching(Level.DEBUG, e);
		}

		try {
			loader = new PropertiesFileLoader(this.CONFIG_PATH);
		} catch (NullPointerException e) {
			log.catching(Level.DEBUG, e);
			return null;
		}

		if( !loader.readPropertiesFile() ) {
			log.fatal("No properties file to initialize the Wolfram Alpha plugin found. Plugin will terminate.");
			return null;
		}

		return loader.getPropertie("appID");
	}




	/**
	 * This method is not used in this plugin.
	 */
	@Override
	public void stop() {
		log.info(this.pluginName + " plugin stopped.");
	}


	/**
	 * This method is not used in this plugin.
	 */
	@Override
	public void continueExecution() {
		// TODO Auto-generated method stub

	}


	/**
	 * This method is not used in this plugin.
	 */
	@Override
	public void interruptExecution() {
		// TODO Auto-generated method stub

	}
}
