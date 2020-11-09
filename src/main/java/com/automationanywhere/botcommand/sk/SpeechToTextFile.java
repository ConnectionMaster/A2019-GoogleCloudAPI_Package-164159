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
import com.automationanywhere.commandsdk.annotations.rules.FileExtension;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.botcommand.sk.googleapi.SpeechToText;

/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Speech-To-Text File", name = "speechtotextfile",
        description = "Speech-To-Text from Audio File",
        node_label = "Speech-To-Text File", icon = "pkg.svg",   comment = true ,  text_color = "#f38750" , background_color =  "#f38750" ,  
        return_type=DataType.STRING, return_label="Text", return_required=true)
public class SpeechToTextFile {
	
    @Sessions
    private Map<String, Object> sessions;

	   
	@Execute
    public Value<String> action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName ,
    							@Idx(index = "2", type = AttributeType.FILE)  @Pkg(label = "Audio File (FLAC & WAV only)" , default_value_type = DataType.FILE) @FileExtension("wav,flac") @NotEmpty String audioFile,
    							@Idx(index = "3", type = TEXT)  @Pkg(label = "Language Code" , default_value_type = STRING) @NotEmpty String languageCode,
    		    			    @Idx(index = "4", type = AttributeType.RADIO, options = {
    										@Idx.Option(index = "4.1", pkg = @Pkg(label = "Mono", value = "1")),
    										@Idx.Option(index = "4.2", pkg = @Pkg(label = "Stereo", value = "2")),
    		    				   			}) @Pkg(label = "No of channels", default_value = "1", default_value_type = STRING) @NotEmpty String channels
    						) throws Exception
     {
		String jsonPath = (String) this.sessions.get(sessionName);  
		String text = SpeechToText.speech2textFromFile(audioFile,languageCode,jsonPath,Integer.parseInt(channels));

		return new StringValue(text); 
     
     }
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
		
	
}