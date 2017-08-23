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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ListSelectionModel;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentButton;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentLabel;
import org.knime.core.node.defaultnodesettings.DialogComponentStringListSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.util.DataValueColumnFilter;

import com.chemaxon.compliancechecker.knime.customnodesettings.DialogComponentDateNoTime;
import com.chemaxon.compliancechecker.knime.customnodesettings.SettingsModelDateOfRegulations;
import com.chemaxon.compliancechecker.knime.rest.CCCategoryInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;

public class OptionsTabComponent {
    
    private final DefaultNodeSettingsPane pane;

    private final RestConnectionDetails connectionDetails;
    
    private final SettingsModelString m_checkMode =
            new SettingsModelString(OptionsTabFields.CFGKEY_CHECK_MODE, OptionsTabFields.SIMPLE_CHECK);

    private final SettingsModelColumnName m_structure =
            new SettingsModelColumnName(OptionsTabFields.CFGKEY_STRUCTURE, "");
    
    private final SettingsModelDate m_date =
            new SettingsModelDateOfRegulations(OptionsTabFields.CFGKEY_DATE_OF_REGULATIONS);
    
    private final SettingsModelStringArray m_categories =
            new SettingsModelStringArray(OptionsTabFields.CFGKEY_CATEGORIES, new String[] {});
    
    private DialogComponentStringListSelection categorySelectionComponent;

    public OptionsTabComponent(DefaultNodeSettingsPane pane, RestConnectionDetails connectionDetails) {
        this.pane = pane;
        this.connectionDetails = connectionDetails;
    }
    
    public void addDialogComponents() {
        
        pane.addDialogComponent(new DialogComponentButtonGroup(m_checkMode, false, "Check mode",
                OptionsTabFields.SIMPLE_CHECK, OptionsTabFields.DETAILED_CHECK));

        @SuppressWarnings("unchecked")
        DataValueColumnFilter columnFilter = new DataValueColumnFilter(StringValue.class);
        pane.addDialogComponent(
                new DialogComponentColumnNameSelection(m_structure, "Structure column:", 0, true, columnFilter));
        
        pane.addDialogComponent(new DialogComponentDateNoTime(m_date, "Date of regulations:"));
        
        DialogComponentLabel errorMsgLabel = new DialogComponentLabel("");
        pane.addDialogComponent(errorMsgLabel);

        DialogComponentButton loadButton = new DialogComponentButton("Load/Refresh categories");
        categorySelectionComponent =
                new DialogComponentStringListSelection(m_categories, "Categories:", new ArrayList<>(),
                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, false, 10);
        
        loadButton.addActionListener(new LoadCategoriesActionListener(errorMsgLabel));
        
        pane.addDialogComponent(loadButton);
        pane.addDialogComponent(categorySelectionComponent);

    }
    
    public void initCategorySelectionComponent() {
        List<String> categoryNames = new CCCategoryInvoker(connectionDetails).getDisplayableCategoryNames();
        Collections.sort(categoryNames, String.CASE_INSENSITIVE_ORDER);
        categorySelectionComponent.replaceListItems(categoryNames, (String[])null);
    }
    
    private class LoadCategoriesActionListener implements ActionListener {
        
        private DialogComponentLabel errorMsgLabel;
        
        public LoadCategoriesActionListener(DialogComponentLabel errorMsgLabel) {
            this.errorMsgLabel = errorMsgLabel;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                initCategorySelectionComponent();
                errorMsgLabel.setText("");
            } catch (Exception e) {
                errorMsgLabel.setText("<html>Failed to connect to Compliance Checker."
                    + "<br>Please make sure connection settings are correct.<html>");
            }
        }
    }    
}
