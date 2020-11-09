package com.automationanywhere.botcommand.sk.googleapi;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;


public class ObjectDetection {
	

	
	public static List<DetectedObject> detectObjects(String filePath,String jsonPath) throws Exception, Exception {
		
		
		List<DetectedObject> results = new ArrayList<DetectedObject>();
	
		List<AnnotateImageRequest> requests = new ArrayList<>();

	    ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
	    
		CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(new FileInputStream(jsonPath)));

	    
	    Image img = Image.newBuilder().setContent(imgBytes).build();
	      AnnotateImageRequest request =
	          AnnotateImageRequest.newBuilder()
	              .addFeatures(Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION))
	              .setImage(img)
	              .build();
	      requests.add(request);
	    

	      ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
	      ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);
	      
	      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
	      List<AnnotateImageResponse> responses = response.getResponsesList();
	      
	      for (AnnotateImageResponse res : responses) {
	          for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
	        	  results.add(new DetectedObject(entity.getName(), entity.getScore()));
	        }

	      }

	      
	      return results;
	}

}
