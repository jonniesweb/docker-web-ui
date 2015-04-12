package ca.jonsimpson.docker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import ca.jonsimpson.docker.model.Image;
import ca.jonsimpson.docker.model.Tag;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class DockerRepository {

	private ObjectMapper mapper = new ObjectMapper();
	
	public DockerRepository() {
		
		List<Image> images = getAllImages();
		for (Image image : images) {
			System.out.println(image);
			
			List<Tag> tags = getAllTags(image.getName());
			for (Tag tag : tags) {
				System.out.println(tag);
			}
		}
		
	}

	private List<Image> getAllImages() {
		// get list of all images
		JsonNode root = getJson("search");
		
		Iterator<JsonNode> iterator = root.get("results").elements();
		List<Image> images = new ArrayList<Image>();
		
		while (iterator.hasNext()) {
			JsonNode jsonNode = (JsonNode) iterator.next();
			String name = jsonNode.get("name").asText();
			String description = jsonNode.get("description").asText();
			
			Image image = new Image(name, description);
			images.add(image);
		}
		
		return images;
	}
	
	private List<Tag> getAllTags(String repositoryName) {
		
		JsonNode root = getJson("repositories/" + repositoryName + "/tags");
		
		List<Tag> tags = new ArrayList<Tag>();
		Iterator<Entry<String, JsonNode>> fields = root.fields();
		while (fields.hasNext()) {
			Map.Entry<java.lang.String, JsonNode> entry = (Map.Entry<java.lang.String, JsonNode>) fields.next();
			String tagName = entry.getKey();
			String hash = entry.getValue().asText();
			
			Tag tag = new Tag(repositoryName, tagName, hash);
			tags.add(tag);
		}
		
		return tags;
	}

	private JsonNode getJson(String queryApiUrl) {
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:5000/v1/" + queryApiUrl);
		
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("not 200!");
		}
		
		String json = response.getEntity(String.class);
		JsonNode root = null;
		try {
			root = mapper.readValue(json, JsonNode.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		new DockerRepository();
	}
	
}
