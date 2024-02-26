package me.khun.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
	
	private static ObjectMapper objectMapper;
	
	private static ObjectWriter ow;
	
	static {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		ow = objectMapper.writer().withDefaultPrettyPrinter();
	}
	
	public static String objectToJson(Object object) throws JsonProcessingException {
		String json = ow.writeValueAsString(object);
		return json;
	}
	
	public static <T> T JsonToObject(String json, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
		T converted = objectMapper.readValue(json, clazz);
		return converted;
	}

}