/*
 * Licensed to the ChemAxon Ltd. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  ChemAxon licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.chemaxon.compliancechecker.knime.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;
 
public class CCRestInvoker {
    
    private static final ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        // needed to be able to parse java8 objects (e.g: Instant)
        mapper.findAndRegisterModules();
    }

    private final RestConnectionDetails connectionDetails;
    private final String urlStr;
    
    public CCRestInvoker(RestConnectionDetails connectionDetails, String urlPath) {
        this.connectionDetails = connectionDetails;
        this.urlStr = connectionDetails.getHost() + urlPath;
    }
    
	public <T> T get(Class<T> responseClass) {
		HttpURLConnection connection = null;
		try {
			connection = getConnection();
			String responseJson = getJsonResponse(connection);
			return mapResponse(responseJson, responseClass);
		} catch (IOException e) {
			throw new CcInvocationException("Request failed. URL: " + urlStr, e);
		} finally {
			if (connection != null) {
				connection.disconnect();	
			}
		}
    }
    
    public <T> T get(TypeReference<T> type) {
		HttpURLConnection connection = null;
		try {
			connection = getConnection();
			String responseJson = getJsonResponse(connection);
		    return mapResponse(responseJson, type);
		} catch (IOException e) {
			throw new CcInvocationException("Request failed. URL: " + urlStr, e);
		} finally {
			if (connection != null) {
				connection.disconnect();	
			}
		}
    }

    public <T> T post(Object request, Class<T> responseClass) {

        String requestJson = null;
        try {
            requestJson = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write object to json: " + request, e);
        }

        HttpURLConnection connection = null;
        try {
			connection = getConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			
			try (OutputStream os = connection.getOutputStream()) {
				os.write(requestJson.getBytes());
				os.flush();
			}
		    String responseJson = getJsonResponse(connection);
			return mapResponse(responseJson, responseClass);			
		} catch (IOException e) {
			throw new CcInvocationException("Request failed. URL: " + urlStr, e);
		} finally {
			if (connection != null) {
				connection.disconnect();	
			}
		}
    }
    
    private HttpURLConnection getConnection() {
    	HttpURLConnection connection;
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/json,text/plain");
			int timeout = connectionDetails.getTimeout();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			String auth = connectionDetails.getUsername() + ":" + connectionDetails.getPassword();
			byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
			String authHeaderValue = "Basic " + new String(encodedAuth);
			connection.setRequestProperty("Authorization", authHeaderValue);
		} catch (IOException e) {
			throw new CcInvocationException("Failed to set up connection. URL: " + urlStr, e);
		}
		return connection;
    }
    
    private String getJsonResponse(HttpURLConnection connection) throws IOException {
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new CcInvocationException("Request failed. HTTP error code : " + responseCode +  " URL: " + urlStr);
		}
		try (InputStream inputStream = connection.getInputStream()) {
		    ByteSource byteSource = new ByteSource() {
		        @Override
		        public InputStream openStream() throws IOException {
		            return inputStream;
		        }
		    };
		    return byteSource.asCharSource(StandardCharsets.UTF_8).read();
		}
    }
    
    private <T> T mapResponse(String responseJson, Class<T> responseClass) {
        T response = null;
        try {
            response = mapper.readValue(responseJson, responseClass);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json: " + responseJson, e);
        }
        return response;
    }
    
    private <T> T mapResponse(String responseJson, TypeReference<T> type) {
        T response = null;
        try {
            response = mapper.readValue(responseJson, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse json: " + responseJson, e);
        }
        return response;
    }
}
