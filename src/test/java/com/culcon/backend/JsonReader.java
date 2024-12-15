package com.culcon.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.io.File;
import java.io.IOException;


@Getter
public class JsonReader {
	final static ObjectMapper mapper = new ObjectMapper();

	private final JsonNode jsonData;

	public JsonReader(String fileName) {
		this.jsonData = loadJson(fileName);
	}

	public JsonNode loadJson(String fileName) {
		JsonNode jsonObject;

		try {
			jsonObject = mapper.readTree(new File(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return jsonObject;
	}

	public ObjectNode getTestCase(String testCase) {
		return (ObjectNode) this.jsonData.get(testCase);
	}
}
