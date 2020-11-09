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


import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.botcommand.sk.googleapi.TextToSpeech;

/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Text-to-Speech", name = "texttospeech",
        description = "Text-to-Speech",
        node_label = "Text-to-Speech", icon = "pkg.svg", comment = true ,  text_color = "#f38750" , background_color =  "#f38750" )
public class TextToVoice {
	
    @Sessions
    private Map<String, Object> sessions;
	   
	@Execute
    public void action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName ,
    				   @Idx(index = "2", type = AttributeType.TEXT)  @Pkg(label = "Text" , default_value_type = DataType.STRING) @NotEmpty String text,
    				   @Idx(index = "3", type = TEXT)  @Pkg(label = "Language Code" , default_value_type = STRING) @NotEmpty String languageCode, 
    				   @Idx(index = "4", type = AttributeType.RADIO, options = {
								@Idx.Option(index = "4.1", pkg = @Pkg(label = "Female", value = "female")),
								@Idx.Option(index = "4.2", pkg = @Pkg(label = "Male", value = "male")),
								@Idx.Option(index = "4.3", pkg = @Pkg(label = "Neutral", value = "neutral"))
    				   			}) @Pkg(label = "Voice", default_value = "female", default_value_type = STRING) @NotEmpty String voice,
    				   @Idx(index = "5", type = AttributeType.FILE)  @Pkg(label = "Audio Output File (WAV)" , default_value_type = DataType.FILE) @NotEmpty  String outputFile,
    				   @Idx(index = "6", type = AttributeType.BOOLEAN)  @Pkg(label = "Use SSML" , default_value_type = DataType.BOOLEAN, default_value = "false")  Boolean ssml,
    				   @Idx(index = "7", type = AttributeType.BOOLEAN)  @Pkg(label = "Play Text" , default_value_type = DataType.BOOLEAN , default_value = "false")  Boolean play 
    			   	  ) throws Exception
     {
		boolean playVoice = (play != null) ? play : false;
		boolean useSSML = (ssml != null) ? ssml: false;
		String jsonPath = (String) this.sessions.get(sessionName);  
		TextToSpeech.synthesizeText(text,languageCode, voice, jsonPath,outputFile, useSSML,playVoice);

     
     }
	
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
	
		
	
}