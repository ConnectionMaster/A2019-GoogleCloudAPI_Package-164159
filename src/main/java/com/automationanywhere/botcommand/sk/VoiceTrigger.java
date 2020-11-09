/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */

package com.automationanywhere.botcommand.sk;

import java.io.BufferedReader;
import java.io.File;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automationanywhere.bot.service.TriggerException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.StartListen;
import com.automationanywhere.commandsdk.annotations.StopAllTriggers;
import com.automationanywhere.commandsdk.annotations.StopListen;
import com.automationanywhere.commandsdk.annotations.TriggerId;
import com.automationanywhere.commandsdk.annotations.TriggerRunnable;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;

/**
 * 
 * 
 * @author Stefan Karsten
 *
 */
@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "VoiceTrigger", description = "Google Voice Trigger",icon = "pkg.svg",   comment = true ,  text_color = "#f38750" , background_color =  "#f38750" , name = "voicetrigger")
public class VoiceTrigger {
	

	// Map storing multiple MessageListenerContainer
	private static final Map<String, Process> taskMap = new ConcurrentHashMap<>();
	
	@TriggerId
	private String triggerUid;
	@TriggerRunnable
	private Runnable runnable;
	

	
	private static Logger logger = LogManager.getLogger(VoiceTrigger.class);
	
	//This method is called by MessageListenerContainer when a message arrives.
	// We will enable the trigger at this point 


	/*
	 * Starts the trigger.
	 * 
	 * We will use this method to setup the trigger, i.e. setup the MessageListenerContainer and start it.
	 */
	@StartListen
	public void startTrigger(@Idx(index = "1", type = AttributeType.FILE) @Pkg(label = "Google API Authentication File (JSON)", default_value_type = DataType.FILE) @NotEmpty String jsonauthpath, 
							 @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Wake Word",default_value_type = DataType.STRING) @NotEmpty String wakeword,
							 @Idx(index = "3", type = AttributeType.TEXT) @Pkg(label = "Language Code",default_value_type = DataType.STRING) @NotEmpty String languageCode,
							 @Idx(index = "4", type = AttributeType.NUMBER) @Pkg(label = "Fuzzy Threshold",default_value_type = DataType.NUMBER) @NotEmpty Number threshold) throws Exception {
		

	    String property = "java.io.tmpdir";
	    String triggerid=this.triggerUid;
	    try {
		    String tempDir = System.getProperty(property);
		    File file = new File(tempDir + "/GoogleWake.jar");
		    if (!file.exists()) {
		      
		      InputStream wakejar = getClass().getResourceAsStream("/jars/GoogleWake.zip");
		      Files.copy(wakejar , file.getAbsoluteFile().toPath(), new java.nio.file.CopyOption[0]);
		      wakejar.close();
		    } 

		    String wakejarpath =  file.getAbsolutePath();
			String javaExecutablePath = ProcessHandle.current()
				    .info()
				    .command()
				    .orElseThrow();
			javaExecutablePath = javaExecutablePath.replace("javaw.exe","java");
			Integer thresholdValue = threshold.intValue();
			Runtime rt = Runtime.getRuntime();
			String fileauthpath = new File(jsonauthpath).getAbsolutePath();
			String commands[] = {"cmd.exe", "/c", "\"\""+javaExecutablePath+"\"","-cp","\""+wakejarpath+"\"","googlewake.Main","-a","\""+fileauthpath+"\"","-w",wakeword,"-l",languageCode,"-t",thresholdValue.toString(),"\""};			
			logger.info("Voice Trigger Cmd "+commands[0]);
			Runnable wakerunner = new Runnable(){
				        public void run(){
				        	try {
							Process proc = rt.exec(commands);
							taskMap.put(triggerid, proc);
				        	proc.waitFor();
							if (proc.exitValue() != 0)
							{
								BufferedReader stdError = new BufferedReader(new  InputStreamReader(proc.getErrorStream()));
								// Read any errors from the attempted command
								String error = "";
								String s = null;
								while ((s = stdError.readLine()) != null) {
									 error = error + s; 
								}
								throw new TriggerException("Wake Word Listener Exception "+error);
							}
							else {
								stopAllTriggers();
								runnable.run();
							}
				        }
				    	catch (Exception e)
				        {
				   	    	throw new TriggerException("Wake Word Listener Exceptionc "+e.getMessage());
				  	    }
				    }
			};
			Thread thread = new Thread(wakerunner);
			thread.start();
	    }
	    catch (Exception e)
	    {
	    	throw new TriggerException("Wake Word Listener Exceptionc "+e.getMessage());
	    }
	  
	}

	/*
	 * Cancel all the task and clear the map.
	 */
	@StopAllTriggers
	public void stopAllTriggers() {
		taskMap.forEach((k, v) -> {
			v.destroy();
			taskMap.remove(k);
		});
	}

	/*
	 * Cancel the task and remove from map
	 *
	 * @param triggerUid
	 */
	@StopListen
	public void stopListen(String triggerUid) {
		taskMap.get(triggerUid).destroy();
		taskMap.remove(triggerUid);
	}

	public String getTriggerUid() {
		return triggerUid;
	}

	public void setTriggerUid(String triggerUid) {
		this.triggerUid = triggerUid;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

}
