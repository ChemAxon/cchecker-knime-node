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

package com.chemaxon.compliancechecker.knime.tabs;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;

public class ConnectionSettingsTabFields implements RestConnectionDetails {
    
    public static final String CFGKEY_HOST = "Host";
    
    public static final String CFGKEY_TIMEOUT = "Timeout";
    
    public static final int DEFAULT_TIMEOUT = 15000;
    
    public static final String CFGKEY_USERNAME = "Username";
    
    public static final String CFGKEY_PASSWORD = "Password";   

    private final SettingsModelString m_host =
            new SettingsModelString(CFGKEY_HOST, "");
    
    private final SettingsModelIntegerBounded m_timeout =
            new SettingsModelIntegerBounded(CFGKEY_TIMEOUT, DEFAULT_TIMEOUT, 0, Integer.MAX_VALUE);

    private final SettingsModelString m_username =
            new SettingsModelString(CFGKEY_USERNAME, "");
    
    private final SettingsModelString m_password =
            new SettingsModelString(CFGKEY_PASSWORD, "");

    @Override
    public String getHost() {
        return m_host.getStringValue();
    }
    
    @Override
    public int getTimeout() {
        return m_timeout.getIntValue();
    }
    
    @Override
    public String getUsername() {
        return m_username.getStringValue();
    }
    
    @Override
    public String getPassword() {
        return m_password.getStringValue();
    }
    
    public void saveSettingsTo(NodeSettingsWO settings) {
        m_password.saveSettingsTo(settings);
        m_timeout.saveSettingsTo(settings);
        m_host.saveSettingsTo(settings);
        m_username.saveSettingsTo(settings);
    }
    
    public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
        m_password.loadSettingsFrom(settings);
        m_timeout.loadSettingsFrom(settings);
        m_host.loadSettingsFrom(settings);
        m_username.loadSettingsFrom(settings);
    }
    
    public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
        m_password.validateSettings(settings);
        m_timeout.validateSettings(settings);
        m_host.validateSettings(settings);
        m_username.validateSettings(settings);
    }
}
