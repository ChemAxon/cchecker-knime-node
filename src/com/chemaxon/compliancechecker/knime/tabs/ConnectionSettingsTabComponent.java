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

package com.chemaxon.compliancechecker.knime.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.chemaxon.compliancechecker.knime.rest.CCDbInitializationDateInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;

public class ConnectionSettingsTabComponent implements RestConnectionDetails {
    
    private final DefaultNodeSettingsPane pane;
    
    private final SettingsModelString m_host = new SettingsModelString(ConnectionSettingsTabFields.CFGKEY_HOST, "");
    
    private final SettingsModelIntegerBounded m_timeout =
            new SettingsModelIntegerBounded(ConnectionSettingsTabFields.CFGKEY_TIMEOUT, ConnectionSettingsTabFields.DEFAULT_TIMEOUT, 0,
                    Integer.MAX_VALUE);
    
    private final SettingsModelString m_username = new SettingsModelString(ConnectionSettingsTabFields.CFGKEY_USERNAME, "");
    
    private final SettingsModelString m_password = new SettingsModelString(ConnectionSettingsTabFields.CFGKEY_PASSWORD, "");

    private final DialogComponentLabel connectionStatusLabel = new DialogComponentLabel("");
    
    public ConnectionSettingsTabComponent(DefaultNodeSettingsPane pane) {
        this.pane = pane;
    }
    
    public void addDialogComponents() {
        pane.createNewTab("Connection settings");
        
        pane.createNewGroup("Settings");
        pane.addDialogComponent(new DialogComponentString(m_host, "CC host:", true, 30));
        pane.addDialogComponent(new DialogComponentNumber(m_timeout, "Timeout:", 1000, 5));
        
        pane.createNewGroup("Authentication");
        pane.addDialogComponent(new DialogComponentString(m_username, "Username:", true, 30));
        pane.addDialogComponent(new DialogComponentPasswordField(m_password, "Password:", 30));
        
        pane.closeCurrentGroup();
        
        pane.addDialogComponent(connectionStatusLabel);
        DialogComponentButton testConnectionButton = new DialogComponentButton("Test connection");
        testConnectionButton.addActionListener(new ConnectionCheckActionListener());
        pane.addDialogComponent(testConnectionButton);
    }
    
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
    
    public void clearConnectionStatusLabel() {
        connectionStatusLabel.setText("");
    }

    private class ConnectionCheckActionListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                new CCDbInitializationDateInvoker(ConnectionSettingsTabComponent.this).getDbInitializationDate();
                connectionStatusLabel.setText("Success.");
            } catch (Exception e) {
                connectionStatusLabel.setText("Failed to connect.");
            }
        }
    }
}
