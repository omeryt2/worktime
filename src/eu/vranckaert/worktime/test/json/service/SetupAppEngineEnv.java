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
import eu.vranckaert.worktime.json.request.service.ServiceRemovalRequest;
import eu.vranckaert.worktime.json.request.user.UserChangePermissionsRequest;
import eu.vranckaert.worktime.json.request.user.UserLoginRequest;
import eu.vranckaert.worktime.json.request.user.UserRegistrationRequest;
import eu.vranckaert.worktime.json.request.user.UserChangePasswordRequest;
import eu.vranckaert.worktime.json.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.model.Role;
import eu.vranckaert.worktime.model.ServicePlatform;

public class SetupAppEngineEnv {
	private static final String HOST = "http://localhost:8888/";
	private static final String REST = "rest/";
	
	private static String output;
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		String url = null;
		String inputType = null;
		String returnType = null;
 		String input = null;
 		
 		ObjectMapper mapper = new ObjectMapper();
 		String setupServiceKey = "68d997a9-1e73-4e8f-863b-f75a1fbd819b";
 		
 		/*url = HOST + REST + "user/login";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserLoginRequest ulr01 = new UserLoginRequest();
 		ulr01.setEmail("test@worktime-appengine.com");
 		ulr01.setPassword("worktime-init-root-user");
 		ulr01.setServiceKey(setupServiceKey);
 		input = mapper.writeValueAsString(ulr01);
 		execute("Login with the worktime setup user", url, inputType, returnType, input);
 		String sessionKey = output;
 		
 		url = HOST + REST + "service/createService";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		ServiceCreationRequest scr = new ServiceCreationRequest();
 		scr.setAppName("WorkTime for Android");
 		scr.setContact("dirkvranckaert@gmail.com");
 		scr.setPlatform(ServicePlatform.ANDROID);
 		scr.setServiceKey(setupServiceKey);
 		scr.setSessionKey(sessionKey);
 		scr.setEmail("test@worktime-appengine.com");
 		input = mapper.writeValueAsString(scr);
 		execute("Create service...", url, inputType, returnType, input);
 		String workTimeForAndroidServiceKey = output;
 		
 		url = HOST + REST + "service/removeService";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		ServiceRemovalRequest srr = new ServiceRemovalRequest();
 		srr.setServiceKeyForRemoval(setupServiceKey);
 		srr.setServiceKey(workTimeForAndroidServiceKey);
 		srr.setSessionKey(sessionKey);
 		srr.setEmail("test@worktime-appengine.com");
 		input = mapper.writeValueAsString(srr);
 		execute("Remove setup service...", url, inputType, returnType, input);
 		
 		url = HOST + REST + "user/changePassword";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserChangePasswordRequest urpr = new UserChangePasswordRequest();
 		urpr.setEmail("test@worktime-appengine.com");
 		urpr.setSessionKey(sessionKey);
 		urpr.setServiceKey(workTimeForAndroidServiceKey);
 		urpr.setOldPassword("worktime-init-root-user");
 		urpr.setNewPassword("azerty984");
 		input = mapper.writeValueAsString(urpr);
 		execute("Changing password for setup user", url, inputType, returnType, input);
 		sessionKey = output;
 		
 		url = HOST + REST + "user/register";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserRegistrationRequest urr = new UserRegistrationRequest();
 		urr.setServiceKey(workTimeForAndroidServiceKey);
 		urr.setEmail("dirkvranckaert@gmail.com");
 		urr.setFirstName("Dirk");
 		urr.setLastName("Vranckaert");
 		urr.setPassword("16wassen");
 		input = mapper.writeValueAsString(urr);
 		execute("Create new user...", url, inputType, returnType, input);
 		String newUserSessionKey = output;
 		
 		url = HOST + REST + "user/changePermissions";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserChangePermissionsRequest ucpr = new UserChangePermissionsRequest();
 		ucpr.setEmail("dirkvranckaert@gmail.com");
 		ucpr.setSessionKey(newUserSessionKey);
 		ucpr.setServiceKey(workTimeForAndroidServiceKey);
 		ucpr.setNewRole(Role.ADMIN);
 		ucpr.setUserToChange("dirkvranckaert@gmail.com");
 		input = mapper.writeValueAsString(ucpr);
 		execute("Change permissions with non-admin user", url, inputType, returnType, input);
 		
 		url = HOST + REST + "user/changePermissions";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserChangePermissionsRequest ucpr01 = new UserChangePermissionsRequest();
 		ucpr01.setEmail("test@worktime-appengine.com");
 		ucpr01.setSessionKey(sessionKey);
 		ucpr01.setServiceKey(workTimeForAndroidServiceKey);
 		ucpr01.setNewRole(Role.ADMIN);
 		ucpr01.setUserToChange("dirkvranckaert@gmail.com");
 		input = mapper.writeValueAsString(ucpr01);
 		execute("Change permissions...", url, inputType, returnType, input);*/
 		
 		url = HOST + REST + "user/login";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserLoginRequest ulr01 = new UserLoginRequest();
 		ulr01.setEmail("dirkvranckaert@gmail.com");
 		ulr01.setPassword("test123");
 		ulr01.setServiceKey(setupServiceKey);
 		input = mapper.writeValueAsString(ulr01);
 		execute("Login with dirkvranckaert@gmail.com", url, inputType, returnType, input);
 		/*url = HOST + REST + "user/changePassword";
 		inputType = MediaType.APPLICATION_JSON;
 		returnType = MediaType.TEXT_PLAIN;
 		UserChangePasswordRequest urpr = new UserChangePasswordRequest();
 		urpr.setEmail("dirkvranckaert@gmail.com");
 		urpr.setSessionKey("c4d797f5-9336-4230-83ed-c4777826cb6f");
 		urpr.setServiceKey(setupServiceKey);
 		urpr.setOldPassword("test123");
 		urpr.setNewPassword("16wassen");
 		input = mapper.writeValueAsString(urpr);
 		execute("Changing password for dirk vranckaert", url, inputType, returnType, input);*/
	}
	
	private static void execute(String testName, String url, String inputType, String returnType, String input) {
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
