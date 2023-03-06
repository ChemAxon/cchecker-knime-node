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
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import com.chemaxon.compliancechecker.knime.dto.CheckListRequest;
import com.chemaxon.compliancechecker.knime.rest.CCCheckListInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;
import com.chemaxon.compliancechecker.knime.types.CheckResultType;

public class SimpleCheckResultProvider implements CheckResultProvider {
    
    private static final NodeLogger logger = NodeLogger.getLogger(SimpleCheckResultProvider.class);

    private Map<CheckResultType, BufferedDataContainer> dataContainerMap;
    private CCCheckListInvoker checkListInvoker;
    
    public SimpleCheckResultProvider(ExecutionContext exec, DataTableSpec dataTableSpec,
            RestConnectionDetails connectionDetails) {
        this.checkListInvoker = new CCCheckListInvoker(connectionDetails);
        initDataContainerMap(exec, dataTableSpec);
    }
    
    private void initDataContainerMap(ExecutionContext exec, DataTableSpec dataTableSpec) {
        Map<CheckResultType, BufferedDataContainer> dataContainerMap = new HashMap<>();
        dataContainerMap.put(CheckResultType.CONTROLLED, exec.createDataContainer(dataTableSpec));
        dataContainerMap.put(CheckResultType.NOT_CONTROLLED, exec.createDataContainer(dataTableSpec));
        dataContainerMap.put(CheckResultType.ERROR, exec.createDataContainer(dataTableSpec));
        this.dataContainerMap = dataContainerMap;
    }
    
    @Override
    public void executeCheck(CheckListRequest request, List<DataRow> inputTableChunk) {
        List<CheckResultType> response = null;
        try {
            response = checkListInvoker.getSimplifiedCheckResults(request);
        } catch (Exception e) {
            for (DataRow inputRow : inputTableChunk) {
                dataContainerMap.get(CheckResultType.ERROR).addRowToTable(inputRow);    
            }
            logger.error("Error during service call.", e);
            return;
        }
        int i = 0;
        for (DataRow inputRow : inputTableChunk) {
            dataContainerMap.get(response.get(i++)).addRowToTable(inputRow);    
        }
    }
    
    @Override
    public Map<CheckResultType, BufferedDataContainer> getResults() {
        return dataContainerMap;
    }
}
