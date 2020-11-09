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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;

import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.Sessions;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.botcommand.sk.googleapi.DetectedObject;
import com.automationanywhere.botcommand.sk.googleapi.ObjectDetection;




/**
 * @author Stefan Karsten
 *
 */

@BotCommand
@CommandPkg(label = "Detect Objects", name = "detecobjects",
        description = "Detect Objects",
        node_label = "Detect Objects in Image", icon = "pkg.svg",  comment = true ,  text_color = "#f38750" , background_color =  "#f38750" ,   
        return_type=DataType.RECORD,  return_sub_type=DataType.STRING , return_label="Detected Objects", return_required=true)

public class DetectObjects {
	
    @Sessions
    private Map<String, Object> sessions;
	   
	@Execute
    public  Value<Record>  action(@Idx(index = "1", type = TEXT)  @Pkg(label = "Session name" , default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName ,
	          					  @Idx(index = "2", type = AttributeType.FILE)  @Pkg(label = "Image File" , default_value_type = DataType.FILE) @NotEmpty  String file

        			   	  ) throws Exception
     {
		
		Value<Record> valuerecord = new RecordValue();
		Record record;
    	List<Schema> schemas = new ArrayList<Schema>();
		List<Value> values = new ArrayList<Value>();
		
		String jsonPath = (String) this.sessions.get(sessionName);  
		List<DetectedObject> detectedObj = ObjectDetection.detectObjects(file, jsonPath);
		

    	for (int i = 0; i < detectedObj.size() ; i++) {
    		 String type = detectedObj.get(i).getName();
    		 String conf = String.valueOf(detectedObj.get(i).getConfidenence());

    		 Schema schema =  new Schema();
    		 schema.setName(type);	 
    		 schemas.add(schema);
    		 values.add(new StringValue(conf));
		}

    	record = new Record();
    	record.setSchema(schemas);
    	record.setValues(values);
    	valuerecord.set(record);
		return valuerecord;

     
     }
	
	
	
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
	
		
	
}