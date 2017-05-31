/*
 * Licensed Materials - Property of Cirrus Link Solutions
 * Copyright (c) 2017 Cirrus Link Solutions LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.cirruslink.sparkplug.json;

import java.io.File;
import java.io.IOException;

import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * Validates JSON.
 */
public class JsonValidator {
	
	protected static final String JSON_SCHEMA_FILENAME = "sparkplug_b.json";
	
	private static JsonValidator instance = null;
	
	/**
	 * Constructor.
	 */
	protected JsonValidator() {
	}

	/**
	 * Returns the {@link JsonValidator} instance.
	 * 
	 * @return the {@link JsonValidator} instance.
	 */
	public static JsonValidator getInstance() {
		if (instance == null) {
			instance = new JsonValidator();
		}
		return instance;
	}
	
	/**
	 * Returns loads and returns the {@link JsonSchema} instance associated with this validator.
	 * 
	 * @return the {@link JsonSchema} instance associated with this validator.
	 * @throws IOException
	 * @throws ProcessingException
	 */
	protected JsonSchema getSchema() throws IOException, ProcessingException {	
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File schemaFile = new File(classLoader.getResource(JSON_SCHEMA_FILENAME).getFile());
        return JsonSchemaFactory.byDefault().getJsonSchema(JsonLoader.fromFile(schemaFile));
	}
	
	/**
	 * Returns true if the supplied JSON text is valid, false otherwise.
	 * 
	 * @param jsonText a {@link String} representing JSON text.
	 * @return true if the supplied JSON text is valid, false otherwise.
	 * @throws ProcessingException
	 * @throws IOException
	 */
	public boolean isJsonValid(String jsonText) throws ProcessingException, IOException {
        return getSchema().validate(JsonLoader.fromString(jsonText)).isSuccess();
    }
}
