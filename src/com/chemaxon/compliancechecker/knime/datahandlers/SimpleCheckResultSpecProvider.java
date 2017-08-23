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

import java.util.HashMap;
import java.util.Map;

import org.knime.core.data.DataTableSpec;

import com.chemaxon.compliancechecker.knime.types.CheckResultType;

public class SimpleCheckResultSpecProvider implements CheckResultSpecProvider {
    
    private Map<CheckResultType, DataTableSpec> dataTableSpecMap;
    
    public SimpleCheckResultSpecProvider(DataTableSpec dataTableSpec) {
        initDataTableSpecMap(dataTableSpec);
    }
    
    private void initDataTableSpecMap(DataTableSpec dataTableSpec) {
        Map<CheckResultType, DataTableSpec> dataTableSpecMap = new HashMap<>();
        dataTableSpecMap.put(CheckResultType.CONTROLLED, dataTableSpec);
        dataTableSpecMap.put(CheckResultType.NOT_CONTROLLED, dataTableSpec);
        dataTableSpecMap.put(CheckResultType.ERROR, dataTableSpec);
        this.dataTableSpecMap = dataTableSpecMap;
    }
    
    @Override
    public Map<CheckResultType, DataTableSpec> getResultSpecs() {
        return dataTableSpecMap;
    }
}
