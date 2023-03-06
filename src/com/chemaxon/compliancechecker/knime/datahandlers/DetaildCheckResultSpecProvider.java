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

package com.chemaxon.compliancechecker.knime.datahandlers;

import java.util.HashMap;
import java.util.Map;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;

import com.chemaxon.compliancechecker.knime.types.CheckResultType;

public class DetaildCheckResultSpecProvider implements CheckResultSpecProvider {
    
    private Map<CheckResultType, DataTableSpec> dataTableSpecMap;
    private DataTableSpec inputDataTableSpec;
    
    public DetaildCheckResultSpecProvider(DataTableSpec inputDataTableSpec) {
        dataTableSpecMap = new HashMap<>();
        this.inputDataTableSpec = inputDataTableSpec;
        initDataTableSpecMap();
    }
    
    private void initDataTableSpecMap() {
        initControlledDataTableSpec();
        initNotControlledDataTableSpec();
        initErrorDataTableSpec();
    }
    
    private void initControlledDataTableSpec() {
        DataColumnSpec[] colSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator("cas", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("dea", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("example", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("category name", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("legislative links", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("mol name", StringCell.TYPE).createSpec()
        };

        DataTableSpec dataTableSpec = new DataTableSpec(colSpecs);
        dataTableSpecMap.put(CheckResultType.CONTROLLED,
                new DataTableSpec(inputDataTableSpec, dataTableSpec));
    }
    
    private void initNotControlledDataTableSpec() {
        dataTableSpecMap.put(CheckResultType.NOT_CONTROLLED, inputDataTableSpec);
    }
    
    private void initErrorDataTableSpec() {
        DataColumnSpec[] errorColSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator("error message", StringCell.TYPE).createSpec()
        };
        
        DataTableSpec errorDataTableSpec = new DataTableSpec(errorColSpecs);
        dataTableSpecMap.put(CheckResultType.ERROR,
                new DataTableSpec(inputDataTableSpec, errorDataTableSpec));
    }

    @Override
    public Map<CheckResultType, DataTableSpec> getResultSpecs() {
        return dataTableSpecMap;
    }

}
