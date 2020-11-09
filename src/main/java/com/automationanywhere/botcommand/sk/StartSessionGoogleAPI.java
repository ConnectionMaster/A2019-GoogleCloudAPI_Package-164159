/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 * 
 */
package com.automationanywhere.botcommand.sk;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.Map;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;


/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Start session", name = "startGoogleAPISession", description = "Start new session", 
icon = "pkg.svg", node_label = "start session {{sessionName}}|" ,comment = true ,  text_color = "#f38750" , background_color =  "#f38750" ) 
public class StartSessionGoogleAPI {
 
    @Sessions
    private Map<String, Object> sessions;
    
    private static final Messages MESSAGES = MessagesFactory
			.getMessages("com.automationanywhere.botcommand.demo.messages");
    
    
	private String JsonAuthFile;

    
    @Execute
    public void start(@Idx(index = "1", type = TEXT) @Pkg(label = "Session name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
    		          @Idx(index = "2", type = AttributeType.FILE)  @Pkg(label = "Google API Authentication File (JSON)" , default_value_type = DataType.FILE) @NotEmpty  String jsonPath
    		) throws Exception {
 
        // Check for existing session
        if (sessions.containsKey(sessionName))
            throw new BotCommandException(MESSAGES.getString("Session name in use ")) ;
       
        
        
        this.JsonAuthFile = jsonPath;
        this.sessions.put(sessionName, this.JsonAuthFile);
    }
 
    
    
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    

    
 
    
    
}