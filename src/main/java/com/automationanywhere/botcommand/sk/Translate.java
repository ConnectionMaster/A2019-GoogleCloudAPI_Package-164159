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

import com.automationanywhere.botcommand.data.Value;

import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.botcommand.sk.googleapi.TranslateText;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Translate", name = "translatetext",
        description = "Translate",
        node_label = "Translate {{text}}",icon = "pkg.svg",  comment = true ,  text_color = "#f38750" , background_color =  "#f38750" ,        
        return_type=DataType.STRING, return_label="Translation", return_required=true)

public class Translate {
	
    @Sessions
    private Map<String, Object> sessions;
	   
	@Execute
    public  Value<String>  action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName ,
    				   @Idx(index = "2", type = AttributeType.TEXT)  @Pkg(label = "Text" , default_value_type = DataType.STRING) @NotEmpty String text,
    				   @Idx(index = "3", type = TEXT)  @Pkg(label = "From Language" , default_value_type = STRING) @NotEmpty String fromlanguageCode,
    				   @Idx(index = "4", type = TEXT)  @Pkg(label = "To Language" , default_value_type = STRING) @NotEmpty String tolanguageCode
        			   	  ) throws Exception
     {
		String jsonPath = (String) this.sessions.get(sessionName);  
		String translation = TranslateText.translatetext(text, fromlanguageCode, tolanguageCode, jsonPath);
		
		return new StringValue(translation);

     
     }
	
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
	
		
	
}