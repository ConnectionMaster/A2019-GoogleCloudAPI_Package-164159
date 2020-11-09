package com.automationanywhere.botcommand.sk.googleapi;



import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.TargetDataLine;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionConfig.Builder;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

public class SpeechToText {
	
	private static final Logger logger = LogManager.getLogger(SpeechToText.class);
	
	private static String text ;
	private static String speechEvent = "UNKNOWN";
	  
	public static String speech2textFromFile(String fileName, String languageCode, String jsonPath, int channels) throws Exception {
		
		 CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));
		SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
		SpeechClient speechClient = SpeechClient.create(settings);


	    //  int sampleRateHertz = 44100;
	      
	      text = "";

		    // Encoding of audio data sent. This sample sets this explicitly.
		    // This field is optional for FLAC and WAV audio formats.
		 //   RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.FLAC;
		    RecognitionConfig config =
		        RecognitionConfig.newBuilder()
		            .setLanguageCode(languageCode)
		     //       .setSampleRateHertz(sampleRateHertz)
		    //        .setEncoding(encoding)
		            .setAudioChannelCount(channels)
		            .build();
		    
		    Path path = Paths.get(fileName);
		    byte[] data = Files.readAllBytes(path);
		    ByteString audioBytes = ByteString.copyFrom(data);
		    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();
		    RecognizeRequest request =
		        RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
		    RecognizeResponse response = speechClient.recognize(request);
		    for (SpeechRecognitionResult result : response.getResultsList()) {
		      // First alternative is the most probable result
		      SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
		      text = text+" "+ alternative.getTranscript();
		    }
		    
		    return text;

	}
	
	
	public static String speech2textFromStream(String languageCode,Integer duration, String jsonPath) throws Exception {
	
		 CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));
		SpeechSettings settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();

		int maxduration = duration*1000;
		
		 text = "";
	     speechEvent = "UNKNOWN";
		 
		  ResponseObserver<StreamingRecognizeResponse> responseObserver = null;

		  SpeechClient client = SpeechClient.create(settings);

			    responseObserver =

		          new ResponseObserver<StreamingRecognizeResponse>() {
		            ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

		            public void onStart(StreamController controller) {}

		            public void onResponse(StreamingRecognizeResponse response) {
		            	String eventName = response.getSpeechEventType().name();
		            	logger.info("Event Name 1"+eventName);
		            	if (!eventName.equals("END_OF_SINGLE_UTTERANCE")) {
		            		speechEvent = eventName;
		            	}
		              responses.add(response);
		            }

		            public void onComplete() {
		              for (StreamingRecognizeResponse response : responses) {
		            	  String eventName = response.getSpeechEventType().name();
		            	  	  logger.info("Event Name 2"+eventName);
		            	  if (!eventName.equals("END_OF_SINGLE_UTTERANCE")) {
		            		  com.google.cloud.speech.v1.StreamingRecognitionResult result = response.getResultsList().get(0);
		            		  com.google.cloud.speech.v1.SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
		            		  text = text+" "+ alternative.getTranscript();
		            		  //     	logger.info("Text "+ text);
		            	  }
		                }
		              client.shutdown();
		              
		            }

		            public void onError(Throwable t) {
		            	client.shutdown();
		              logger.info("Google Error:"+t.getLocalizedMessage());
		            }
		          };

		      ClientStream<StreamingRecognizeRequest> clientStream =
		          client.streamingRecognizeCallable().splitCall(responseObserver);

		      RecognitionConfig recognitionConfig =
		          RecognitionConfig.newBuilder()
		              .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
		              .setLanguageCode(languageCode)
		              .setSampleRateHertz(16000)
		              .build();
		      Builder builder= StreamingRecognitionConfig.newBuilder();
		      builder.setSingleUtterance(true);
		      StreamingRecognitionConfig streamingRecognitionConfig =  builder.setConfig(recognitionConfig).build();


		      StreamingRecognizeRequest request =
		          StreamingRecognizeRequest.newBuilder()
		              .setStreamingConfig(streamingRecognitionConfig)
		              .build(); // The first request in a streaming call has to be a config

		      clientStream.send(request);
		      // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
		      // bigEndian: false
		      AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
		      DataLine.Info targetInfo =
		          new Info(
		              TargetDataLine.class,
		              audioFormat); // Set the system information to read from the microphone audio stream

		      if (!AudioSystem.isLineSupported(targetInfo)) {
		    	  	  logger.info("Microphone not supported");
		    	 
		        System.exit(0);
		      }
		      // Target data line captures the audio stream the microphone produces.
		      TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
		      targetDataLine.open(audioFormat);
		      targetDataLine.start();
		      //  logger.info("Start speaking");
		      long startTime = System.currentTimeMillis();
		      // Audio Input Stream
		      AudioInputStream audio = new 	AudioInputStream(targetDataLine);
		      while (true) {
		          long estimatedTime = System.currentTimeMillis() - startTime;
		          byte[] data = new byte[6400];
		          audio.read(data);
		          if (!speechEvent.equals("UNKNOWN") || estimatedTime > maxduration) {
		        	  // 	logger.info("Stop speaking.");
		            targetDataLine.stop();
		            targetDataLine.close();
		            break;
		          }
		          request =
		              StreamingRecognizeRequest.newBuilder()
		                  .setAudioContent(ByteString.copyFrom(data))
		                  .build();
		          clientStream.send(request);
		        }
	  
	  
		   responseObserver.onComplete();

		   return text;
	
		 }
	

		  

}
