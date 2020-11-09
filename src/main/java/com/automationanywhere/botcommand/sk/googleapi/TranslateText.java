package com.automationanywhere.botcommand.sk.googleapi;


import java.io.FileInputStream;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.google.cloud.translate.*;


public class TranslateText {
	
	public static String translatetext(String text,String fromlangCode, String tolangCode, String jsonPath) throws Exception {
		
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));

		TranslateOptions translatesettings = TranslateOptions.newBuilder().setCredentials(credentialsProvider.getCredentials()).build();
	
		Translate translate = translatesettings.getService();

		Translation translation = translate.translate(text,        
			Translate.TranslateOption.sourceLanguage( fromlangCode),
	        Translate.TranslateOption.targetLanguage( tolangCode),
	        Translate.TranslateOption.model("base"));	

		return translation.getTranslatedText();
	}
	
	
	public static String detectlang(String text , String jsonPath) throws Exception {
		
		
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));

		TranslateOptions translatesettings = TranslateOptions.newBuilder().setCredentials(credentialsProvider.getCredentials()).build();
	
		Translate translate = translatesettings.getService();

		Detection detected = translate.detect(text);	
	
		return detected.getLanguage();
		
	}

}
