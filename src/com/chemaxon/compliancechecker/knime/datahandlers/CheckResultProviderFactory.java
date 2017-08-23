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

package com.chemaxon.compliancechecker.knime.datahandlers;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.ExecutionContext;

import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;
import com.chemaxon.compliancechecker.knime.tabs.OptionsTabFields;

public class CheckResultProviderFactory {

    private ExecutionContext exec;
    private DataTableSpec inputDataTableSpec;
    private RestConnectionDetails connectionDetails;

    public CheckResultProviderFactory(ExecutionContext exec, DataTableSpec inputDataTableSpec,
            RestConnectionDetails connectionDetails) {
            this.exec = exec;
            this.inputDataTableSpec = inputDataTableSpec;
            this.connectionDetails = connectionDetails;
    }

    public CheckResultProvider getProvider(String mode) {
        
        if (OptionsTabFields.SIMPLE_CHECK.equals(mode)) {
            return new SimpleCheckResultProvider(exec, inputDataTableSpec, connectionDetails);
        } else if (OptionsTabFields.DETAILED_CHECK.equals(mode)) {
            return new DetaildCheckResultProvider(exec, inputDataTableSpec, connectionDetails);
        }
        return null;
    }
}
