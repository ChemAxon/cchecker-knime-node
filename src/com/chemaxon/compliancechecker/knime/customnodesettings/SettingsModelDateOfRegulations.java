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

package com.chemaxon.compliancechecker.knime.customnodesettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;

import com.chemaxon.compliancechecker.knime.rest.CCDbInitializationDateInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;

public class SettingsModelDateOfRegulations extends SettingsModelDate {

    private RestConnectionDetails connectionDetails;

    public SettingsModelDateOfRegulations(String configName, RestConnectionDetails connectionDetails) {
        super(configName);
        this.connectionDetails = connectionDetails;
    }

    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        SettingsModelDate settingsModelDate = this.createCloneWithValidatedValue(settings);
        // date is not selected
        if (settingsModelDate.getSelectedFields() < 1) {
            return;
        }
        String minDateStr = null;
        try {
            minDateStr = new CCDbInitializationDateInvoker(connectionDetails).getDbInitializationDate();
        } catch (Exception e) {
            throw new InvalidSettingsException("Failed to connect to Compliance Checker. "
                    + "Please check if connection settings are correct.");
        }

        Date minDate = null;
        try {
            minDate = new SimpleDateFormat("yyyyMMdd").parse(minDateStr);
        } catch (ParseException e) {
            throw new InvalidSettingsException("Invalid date format received form server.");
        }

        Date dateOfRegulation = settingsModelDate.getDate();

        minDateStr = new StringBuilder(minDateStr).insert(4, "-").insert(7, "-").toString();
        if (dateOfRegulation.before(minDate)) {
            throw new InvalidSettingsException("Date of regulation should not be before " + minDateStr);
        } else if (new Date().before(dateOfRegulation)) {
            throw new InvalidSettingsException("Date of regulation should not be a future date.");
        }
    }
}
