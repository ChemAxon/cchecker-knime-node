/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;

import com.chemaxon.compliancechecker.knime.tabs.ConnectionSettingsTabFields;

public class ConnectionSettings implements RestConnectionDetails {

    private String host;
    private String username;
    private String password;
    private int timeout;
    
    public ConnectionSettings(NodeSettingsRO settings) throws InvalidSettingsException {
        this.host = settings.getString(ConnectionSettingsTabFields.CFGKEY_HOST);
        this.username = settings.getString(ConnectionSettingsTabFields.CFGKEY_USERNAME);
        this.password = settings.getString(ConnectionSettingsTabFields.CFGKEY_PASSWORD);
        this.timeout = settings.getInt(ConnectionSettingsTabFields.CFGKEY_TIMEOUT);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
