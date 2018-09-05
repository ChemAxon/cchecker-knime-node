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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;

import com.chemaxon.compliancechecker.knime.customnodesettings.SettingsModelDateOfRegulations;

public class OptionsTabFields {
    
    public static final String CFGKEY_CHECK_MODE = "Check mode";
    
    public static final String CFGKEY_STRUCTURE = "Structure"; 
    
    public static final String CFGKEY_DATE_OF_REGULATIONS = "Date of regulations";
    
    public static final String CFGKEY_CATEGORIES = "Categories";
    
    public static final String SIMPLE_CHECK = "Simple";
    
    public static final String DETAILED_CHECK = "Detailed";
    
	public static final String CFGKEY_MOL_FORMAT = "Molecule format";
    
    private final SettingsModelString m_checkMode = new SettingsModelString(CFGKEY_CHECK_MODE, SIMPLE_CHECK);
    
    private final SettingsModelColumnName m_stucture = new SettingsModelColumnName(CFGKEY_STRUCTURE, "");
    
    private final SettingsModelDate m_date = new SettingsModelDateOfRegulations(CFGKEY_DATE_OF_REGULATIONS);
    
    private final SettingsModelString m_molFormat = new SettingsModelString(CFGKEY_MOL_FORMAT, "");

    private final SettingsModelStringArray m_categories =
            new SettingsModelStringArray(CFGKEY_CATEGORIES, new String[0]);

    public String getCheckMode() {
        return m_checkMode.getStringValue();
    }
    
    public String getStructure() {
        return m_stucture.getStringValue();
    }
    
    public Date getDateOfRegulations() {
        return m_date.getDate();
    }
    
    public String getDateOfRegulationsStr() {
        if (m_date.getSelectedFields() < 1) {
            return null;
        }
        return new SimpleDateFormat("yyyyMMdd").format(getDateOfRegulations());
    }

    public String getStructureColName() {
        return m_stucture.getColumnName();
    }
    
    public List<String> getCategories() {
        return Arrays.asList(m_categories.getStringArrayValue());
    }
    
    public String getMolFormat() {
    	return m_molFormat.getStringValue();
    }
    
    public void saveSettingsTo(NodeSettingsWO settings) {
        m_checkMode.saveSettingsTo(settings);
        m_date.saveSettingsTo(settings);
        m_stucture.saveSettingsTo(settings);
        m_categories.saveSettingsTo(settings);
        m_molFormat.saveSettingsTo(settings);
    }
    
    public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
        m_checkMode.loadSettingsFrom(settings);
        m_date.loadSettingsFrom(settings);
        m_stucture.loadSettingsFrom(settings);
        m_categories.loadSettingsFrom(settings);
        m_molFormat.loadSettingsFrom(settings);
    }
    
    public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
        m_checkMode.validateSettings(settings);
        m_date.validateSettings(settings);        
        m_stucture.validateSettings(settings);
        m_categories.validateSettings(settings);
        m_molFormat.validateSettings(settings);
    }
}
