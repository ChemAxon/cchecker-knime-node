/*
 * Licensed to the Chemaxon Ltd. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Chemaxon licenses this file
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

package com.chemaxon.compliancechecker.knime;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import com.chemaxon.compliancechecker.knime.tabs.ConnectionSettingsTabComponent;
import com.chemaxon.compliancechecker.knime.tabs.OptionsTabComponent;

/**
 * <code>NodeDialog</code> for the "Compliance Checker" Node.
 */
public class ComplianceCheckerNodeDialog extends DefaultNodeSettingsPane {

    private static final NodeLogger logger = NodeLogger.getLogger(ComplianceCheckerNodeDialog.class);

    private ConnectionSettingsTabComponent connectionSettingsTab;
    private OptionsTabComponent optionsTab;

    protected ComplianceCheckerNodeDialog() {
        connectionSettingsTab = new ConnectionSettingsTabComponent(this);
        optionsTab = new OptionsTabComponent(this, connectionSettingsTab);

        optionsTab.addDialogComponents();
        connectionSettingsTab.addDialogComponents();
    }

    @Override
    public void onClose() {
        connectionSettingsTab.clearConnectionStatusLabel();
    }

    @Override
    public void onOpen() {
        try {
            optionsTab.initCategorySelectionComponent();
        } catch (Exception e) {
            logger.info("Failed to initialize categories on opening configuration dialog. "
                    + "Probably connections settings are not yet configured, or incorrect.");
        }
    }
}

