package eu.vranckaert.worktime.test.json.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import eu.vranckaert.worktime.json.request.sync.WorkTimeSyncRequest;
import eu.vranckaert.worktime.json.request.user.UserLoginRequest;
import eu.vranckaert.worktime.json.response.sync.WorkTimeSyncResponse;
import eu.vranckaert.worktime.json.response.user.AuthenticationResponse;
import eu.vranckaert.worktime.model.Project;
import eu.vranckaert.worktime.model.Task;
import eu.vranckaert.worktime.model.TimeRegistration;
import eu.vranckaert.worktime.model.sync.SyncConflictConfiguration;

public class SyncTest {
	private static String setupServiceKey = "9939d741-0468-4605-820a-e13cc74886ff";
	
	public static ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		String userEmail = "worktime-test@vranckaert.eu";
		String userPwd = "TEST123";
		String sessionKey = "";
		
		// Login
		UserLoginRequest loginRequest = new UserLoginRequest();
		loginRequest.setEmail(userEmail);
		loginRequest.setPassword(userPwd);
		loginRequest.setServiceKey(setupServiceKey);
		String jsonResponse = executePost("http://localhost:8888/rest/user/login", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, getObjectMapper().writeValueAsString(loginRequest));
		AuthenticationResponse authResponse = getObjectMapper().readValue(jsonResponse, AuthenticationResponse.class);
		sessionKey = authResponse.getSessionKey();
		System.out.println("Session key: " + sessionKey);
		
		// TR-sync test
		Project project1 = new Project();
		project1.setName("Project1");
		project1.setComment("Comment for project 1");
		project1.setFinished(false);
		project1.setLastUpdated(new Date());
		
		Task task1 = new Task();
		task1.setName("Project1 - Task1");
		task1.setComment("Comment for task 1");
		task1.setFinished(false);
		task1.setProject(project1);
		task1.setLastUpdated(new Date());
		
		TimeRegistration timeRegistration = new TimeRegistration();
		timeRegistration.setComment("This is my first TR!");
		timeRegistration.setTask(task1);
		timeRegistration.setLastUpdated(new Date());
		timeRegistration.setStartTime(createDate(7, 1, 2013, 8, 0, 0));
		timeRegistration.setEndTime(null);
		
		List<TimeRegistration> timeRegistrations = new ArrayList<TimeRegistration>();
		timeRegistrations.add(timeRegistration);
		
		WorkTimeSyncRequest syncRequest1 = new WorkTimeSyncRequest();
		syncRequest1.setEmail(userEmail);
		syncRequest1.setServiceKey(setupServiceKey);
		syncRequest1.setSessionKey(sessionKey);
		syncRequest1.setConflictConfiguration(SyncConflictConfiguration.CLIENT);
		syncRequest1.setTimeRegistrations(timeRegistrations);
		String jsonResponse1 = executePost("http://localhost:8888/rest/sync/all", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, getObjectMapper().writeValueAsString(syncRequest1));
		WorkTimeSyncResponse syncResponse1 = getObjectMapper().readValue(jsonResponse1, WorkTimeSyncResponse.class);
		
		// Logout
		executeGet("http://localhost:8888/rest/user/logout?serviceKey=" + setupServiceKey + "&email=" + userEmail + "&sessionKey=" + sessionKey);
	}
	
	private static String executeGet(String url) {
		System.out.println("URL: " + url);
		
		try {
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.get(ClientResponse.class);
 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
 
			String output = response.getEntity(String.class);
			
			System.out.println("Response from Server: " + output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	private static String executePost(String url, String inputType, String returnType, String input) {
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
			String output = response.getEntity(String.class);
			System.out.println(output);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	private static Date createDate(int day, int month, int year, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
