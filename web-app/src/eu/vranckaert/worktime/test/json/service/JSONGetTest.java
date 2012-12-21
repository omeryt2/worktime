package eu.vranckaert.worktime.test.json.service;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JSONGetTest {
	private static final String HOST = "http://localhost:8888/";
	private static final String REST = "rest/";
	
	public static void main(String[] args) {
		try {
			String url = HOST + REST + "user/resetPassword?email=dirkvranckaert@gmail.com";
			String returnType = MediaType.APPLICATION_JSON;
	 
			System.out.println("URL: " + url + " and return type: " + returnType);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.get(ClientResponse.class);
 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
 
			String output = response.getEntity(String.class);
 
			System.out.println("Response from Server:\n");
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
