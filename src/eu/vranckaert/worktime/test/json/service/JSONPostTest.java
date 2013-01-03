package eu.vranckaert.worktime.test.json.service;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.vranckaert.worktime.json.request.service.ServiceCreationRequest;
import eu.vranckaert.worktime.json.request.user.UserLoginRequest;
import eu.vranckaert.worktime.json.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.model.ServicePlatform;

public class JSONPostTest {
	private static final String HOST = "https://worktime-web.appspot.com/";
	private static final String REST = "rest/";
	
	private static String output;
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		String url = null;
		String inputType = null;
		String returnType = null;
 		String input = null;
 		
 		ObjectMapper mapper = new ObjectMapper();
 		
 		url = HOST + REST + "user/login";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserLoginRequest ulr01 = new UserLoginRequest();
 		ulr01.setEmail("dirkvranckaert@gmail.com");
 		ulr01.setPassword("test123");
 		ulr01.setServiceKey("9b83163f-c1a1-4737-9a42-4fc92fd68bf8");
 		//ulr01.setServiceKey("test-d609-4390-a7cc-ec2bc3b3ddcc");
 		input = mapper.writeValueAsString(ulr01);
 		doTest("Login with the user dirkvranckaert@gmail.com", url, inputType, returnType, input);
 		String sessionKey = output;
	}
	
	private static void doTest(String testName, String url, String inputType, String returnType, String input) {
		System.out.println("##########################################################################################");
		System.out.println("RUNNING TEST: " + testName);
		System.out.println("##########################################################################################");
		
		System.out.println("URL: " + url);
		System.out.println("Input type: " + inputType);
		System.out.println("return type: " + returnType);
		System.out.println("Input: " + input);
		
		try {
			Client client = Client.create();
	 		WebResource webResource = client.resource(url);
	 
	 		if (returnType != null)
	 			webResource.accept(returnType);
	 		
			ClientResponse response = webResource.type(inputType).post(ClientResponse.class, input);
	 
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
	 
			System.out.println("Output from Server:");
			output = response.getEntity(String.class);
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
	}
}
