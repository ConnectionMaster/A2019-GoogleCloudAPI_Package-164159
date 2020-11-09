package com.automationanywhere.botcommand.sk.googleapi;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;



public class TextToSpeech {
	
	public static void synthesizeText(String text,String langCode, String gender, String jsonPath,String outputFile,Boolean useSSML, Boolean play) throws Exception {
		
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));
		TextToSpeechSettings settings = TextToSpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	    // Instantiates a client
	    try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
	      // Set the text input to be synthesized
	     SynthesisInput input;
	     if (useSSML) {
	    	   input = SynthesisInput.newBuilder().setSsml(text).build();
	     }
	     else  {
	    	  input = SynthesisInput.newBuilder().setText(text).build();
	     }
	      // Build the voice request
	      SsmlVoiceGender genderVoice ;
	      switch (gender) {
	      	case "neutral":
	      		genderVoice = SsmlVoiceGender.NEUTRAL;
				break;
	      	case "male":
	      		genderVoice = SsmlVoiceGender.MALE;
				break;
	      	default:
	      		genderVoice = SsmlVoiceGender.FEMALE;
	      		break;
	      }

	      VoiceSelectionParams voice =
	          VoiceSelectionParams.newBuilder()
	              .setLanguageCode(langCode) 
	              .setSsmlGender(genderVoice) 
	              .build();

	      // Select the type of audio file you want returned
	      AudioConfig audioConfig =
	          AudioConfig.newBuilder()
	              .setAudioEncoding(AudioEncoding.LINEAR16) 
	              .setSampleRateHertz(16600)
	              .build();

	      // Perform the text-to-speech request
	      SynthesizeSpeechResponse response =
	          textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

	      // Get the audio contents from the response
	      ByteString audioContents = response.getAudioContent();

	      // Write the response to the output file.
	      try (OutputStream out = new FileOutputStream(outputFile)) {
	        out.write(audioContents.toByteArray());
	        System.out.println("Audio content written to file "+outputFile);
	      }
	      
	      if (play) {
	    	  play(outputFile);
	      }
	    }
	  }
	
	public static void play(String file) throws Exception, IOException{
	        AudioPlayer player = new AudioPlayer();
	        player.play(file);

	}
}
