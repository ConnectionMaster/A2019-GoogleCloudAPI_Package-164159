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
@CommandPkg(label = "Detect Language", name = "deteclang",
        description = "Detect Language",
        node_label = "Detect Language of {{text}}", icon = "pkg.svg",  comment = true ,  text_color = "#f38750" , background_color =  "#f38750" ,      
        return_type=DataType.STRING, return_label="Detected Language", return_required=true)

public class DetectLanguage {
	
    @Sessions
    private Map<String, Object> sessions;
	   
	@Execute
    public  Value<String>  action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName ,
    							  @Idx(index = "2", type = AttributeType.TEXT)  @Pkg(label = "Text" , default_value_type = DataType.STRING) @NotEmpty String text
        			   	  ) throws Exception
     {
		String jsonPath = (String) this.sessions.get(sessionName);  
		String detected = TranslateText.detectlang(text, jsonPath);
		
		return new StringValue(detected);

     
     }
	
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
	
		
	
}