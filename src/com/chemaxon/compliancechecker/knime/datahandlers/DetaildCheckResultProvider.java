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

package com.chemaxon.compliancechecker.knime.datahandlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.NodeLogger;

import com.chemaxon.compliancechecker.knime.dto.CheckListRequest;
import com.chemaxon.compliancechecker.knime.dto.SimpleResponse;
import com.chemaxon.compliancechecker.knime.rest.CCCheckListInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;
import com.chemaxon.compliancechecker.knime.types.CheckResultType;

public class DetaildCheckResultProvider implements CheckResultProvider {
    
    private static final NodeLogger logger = NodeLogger.getLogger(DetaildCheckResultProvider.class);

    private Map<CheckResultType, BufferedDataContainer> dataContainerMap;
    private CCCheckListInvoker checkListInvoker;
    private DataTableSpec inputDataTableSpec;
    
    public DetaildCheckResultProvider(ExecutionContext exec, DataTableSpec inputDataTableSpec,
            RestConnectionDetails connectionDetails) {
        dataContainerMap = new HashMap<>();
        this.checkListInvoker = new CCCheckListInvoker(connectionDetails);
        this.inputDataTableSpec = inputDataTableSpec;
        initDataContainerMap(exec);
    }
    
    private void initDataContainerMap(ExecutionContext exec) {
        initControlledDataContainer(exec);
        initNotControlledDataContainer(exec);
        initErrorDataContainer(exec);
    }
    
    private void initControlledDataContainer(ExecutionContext exec) {
        DataColumnSpec[] colSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator("cas", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("dea", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("example", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("category name", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("legislative links", StringCell.TYPE).createSpec(),
                new DataColumnSpecCreator("mol name", StringCell.TYPE).createSpec()
        };

        DataTableSpec dataTableSpec = new DataTableSpec(colSpecs);
        dataContainerMap.put(CheckResultType.CONTROLLED,
                exec.createDataContainer(new DataTableSpec(inputDataTableSpec, dataTableSpec)));
    }
    
    private void initNotControlledDataContainer(ExecutionContext exec) {
        dataContainerMap.put(CheckResultType.NOT_CONTROLLED, exec.createDataContainer(inputDataTableSpec));
    }
    
    private void initErrorDataContainer(ExecutionContext exec) {
        DataColumnSpec[] errorColSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator("error message", StringCell.TYPE).createSpec()
        };
        
        DataTableSpec errorDataTableSpec = new DataTableSpec(errorColSpecs);
        dataContainerMap.put(CheckResultType.ERROR,
                exec.createDataContainer(new DataTableSpec(inputDataTableSpec, errorDataTableSpec)));
    }

    @Override
    public void executeCheck(CheckListRequest request, List<DataRow> inputTableChunk) {
        List<List<SimpleResponse>> response = null;
        try {
            response = checkListInvoker.getDetailedCheckResults(request);
        } catch (Exception e) {
            for (DataRow inputRow : inputTableChunk) {
                BufferedDataContainer errorDataContainer = dataContainerMap.get(CheckResultType.ERROR);
                RowKey key = new RowKey("Row" + errorDataContainer.size());
                List<DataCell> dataCells = inputRow.stream().collect(Collectors.toList());
                dataCells.add(new StringCell(e.getMessage()));
                DataRow outputRow = new DefaultRow(key, dataCells.toArray(new DataCell[0]));
                errorDataContainer.addRowToTable(outputRow);
            }
            logger.error("Error during service call.", e);
            return;
        }
        
        int i = 0;
        for (DataRow inputRow : inputTableChunk) {
            List<SimpleResponse> simpleResponses = response.get(i++);
            if (simpleResponses.isEmpty()) {
                dataContainerMap.get(CheckResultType.NOT_CONTROLLED).addRowToTable(inputRow);
            } else if (simpleResponses.get(0).isError()) {
                SimpleResponse simpleResponse = simpleResponses.get(0);
                BufferedDataContainer errorDataContainer = dataContainerMap.get(CheckResultType.ERROR);
                
                RowKey key = new RowKey("Row" + errorDataContainer.size());
                List<DataCell> dataCells = inputRow.stream().collect(Collectors.toList());
                dataCells.add(new StringCell(simpleResponse.getErrorMessage()));
                DataRow outputRow = new DefaultRow(key, dataCells.toArray(new DataCell[0]));
                errorDataContainer.addRowToTable(outputRow);
            } else {
                BufferedDataContainer controlledDataContainer = dataContainerMap.get(CheckResultType.CONTROLLED);
                
                for (SimpleResponse simpleResponse : simpleResponses) {
                    
                    RowKey key = new RowKey("Row" + controlledDataContainer.size());
                    List<DataCell> dataCells = inputRow.stream().collect(Collectors.toList());
                    dataCells.add(
                            new StringCell(simpleResponse.getCasNumbers().stream().collect(Collectors.joining(","))));
                    dataCells.add(
                            new StringCell(simpleResponse.getDeaNumbers().stream().collect(Collectors.joining(","))));
                    dataCells.add(new StringCell(simpleResponse.getExample()));
                    dataCells.add(new StringCell(simpleResponse.getCategoryName()));
                    dataCells.add(new StringCell(simpleResponse.getLegislativeLinks()));
                    dataCells.add(new StringCell(simpleResponse.getMolName()));
                    DataRow outputRow = new DefaultRow(key, dataCells.toArray(new DataCell[0]));
                    controlledDataContainer.addRowToTable(outputRow);
                }
            }
        }
    }
    
    @Override
    public Map<CheckResultType, BufferedDataContainer> getResults() {
        return dataContainerMap;
    }

}
