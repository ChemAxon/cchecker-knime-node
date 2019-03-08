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
import java.net.Authenticator;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.StatusType;

import com.chemaxon.compliancechecker.knime.ssl.CcTrustManager;
import com.chemaxon.compliancechecker.knime.ssl.SSLContextException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
 
public class CCRestInvoker {
    
    private static final ObjectMapper mapper;
    
    static {
        mapper = new ObjectMapper();
        // needed to be able to parse java8 objects (e.g: Instant)
        mapper.findAndRegisterModules();
    }

    private final RestConnectionDetails connectionDetails;
    private final String urlPath;
    
    public CCRestInvoker(RestConnectionDetails connectionDetails, String urlPath) {
        this.connectionDetails = connectionDetails;
        this.urlPath = urlPath;
    }
    
    public <T> T get(Class<T> responseClass) {
        WebResource webResource = createWebResource();
        
        ClientResponse clientResponse = webResource
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        
        if (clientResponse.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                 + clientResponse.getStatus());
        }
        String responseJson = clientResponse.getEntity(String.class);
        
        return mapResponse(responseJson, responseClass);
    }
    
    public <T> T get(TypeReference<T> type) {
        WebResource webResource = createWebResource();
        
        ClientResponse clientResponse = webResource
                .accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .get(ClientResponse.class);
        
        if (clientResponse.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                 + clientResponse.getStatus());
        }
        String responseJson = clientResponse.getEntity(String.class);
        
        return mapResponse(responseJson, type);
    }

    public <T> T post(Object request, Class<T> responseClass) {

        String requestJson = null;
        try {
            requestJson = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write object to json: " + request, e);
        }

        WebResource webResource = createWebResource();
        
        ClientResponse clientResponse = webResource
                .type(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, requestJson);
        
        if (clientResponse.getStatus() != 200) {
        	StatusType status = clientResponse.getStatusInfo();
            throw new RuntimeException("Failed: HTTP error code: "
                 + status.getStatusCode() + " Reason: " + status.getReasonPhrase());
        }
        
        String responseJson = clientResponse.getEntity(String.class);
        
        return mapResponse(responseJson, responseClass);
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

    private WebResource createWebResource() {
        Authenticator.setDefault(null);
		Client client = getClient();
        int timeout = connectionDetails.getTimeout();
        client.addFilter(new HTTPBasicAuthFilter(connectionDetails.getUsername(), connectionDetails.getPassword()));
        client.setConnectTimeout(timeout);
        client.setReadTimeout(timeout);
        return client.resource(connectionDetails.getHost() + urlPath);
    }
    
    private Client getClient() {
    	if (connectionDetails.getHost().startsWith("https://")) {
    		return Client.create(getClientConfig());
    	}
    	return Client.create();
    }
    
    private ClientConfig getClientConfig() {
        ClientConfig config = new DefaultClientConfig();
        try {
			SSLContext ctx = SSLContext.getInstance("SSL");
			ctx.init(null, new TrustManager[]{new CcTrustManager()}, null);
	        HostnameVerifier hv = (urlHostname, sslSession) -> true;
	        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(hv, ctx));
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new SSLContextException("Failed to create SSL Context", e);
		}
        return config;
    }
}
