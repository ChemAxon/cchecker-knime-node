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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.chemaxon.compliancechecker.knime.datahandlers.CheckResultProvider;
import com.chemaxon.compliancechecker.knime.datahandlers.CheckResultProviderFactory;
import com.chemaxon.compliancechecker.knime.datahandlers.CheckResultSpecProvider;
import com.chemaxon.compliancechecker.knime.datahandlers.CheckResultSpecProviderFactory;
import com.chemaxon.compliancechecker.knime.dto.CheckListRequest;
import com.chemaxon.compliancechecker.knime.helpers.CategoryHelper;
import com.chemaxon.compliancechecker.knime.rest.CCDbInitializationDateInvoker;
import com.chemaxon.compliancechecker.knime.tabs.ConnectionSettingsTabFields;
import com.chemaxon.compliancechecker.knime.tabs.OptionsTabFields;
import com.chemaxon.compliancechecker.knime.types.CheckResultType;
import com.google.common.collect.Iterators;


/**
 * This is the model implementation of ComplianceChecker.
 */
public class ComplianceCheckerNodeModel extends NodeModel {

    private static final int DATA_CHUNK_SIZE = 20;

    private final OptionsTabFields optionFields;

    private final ConnectionSettingsTabFields connectionFields;

    /**
     * Constructor for the node model.
     */
    protected ComplianceCheckerNodeModel() {
        super(1, 3);
        this.connectionFields = new ConnectionSettingsTabFields();
        this.optionFields = new OptionsTabFields(connectionFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        BufferedDataTable inputTable = inData[0];

        Iterable<List<DataRow>> inputTableChunks = partitionInputTable(inputTable);

        int structureColIndex = inputTable.getDataTableSpec().findColumnIndex(optionFields.getStructureColName());
        CategoryHelper categoryHelper = new CategoryHelper(connectionFields);

        CheckResultProvider checkResultProvider =
                new CheckResultProviderFactory(exec, inputTable.getDataTableSpec(), connectionFields)
                        .getProvider(optionFields.getCheckMode());

        int count = 0;
        for (List<DataRow> inputTableChunk : inputTableChunks) {
            CheckListRequest checkListRequest =
                    createCheckListRequest(inputTableChunk, structureColIndex, categoryHelper);
            checkResultProvider.executeCheck(checkListRequest, inputTableChunk);

            // check if the execution monitor was canceled
            exec.checkCanceled();

            double checkedStructNum = DATA_CHUNK_SIZE * count++;
            double progress = checkedStructNum / inputTable.size();
            exec.setProgress(progress, "Number of checked structures: " + checkedStructNum);
        }
        Map<CheckResultType, BufferedDataContainer> outputDataContainerMap = checkResultProvider.getResults();
        outputDataContainerMap.values().forEach(BufferedDataContainer::close);
        return new BufferedDataTable[]{
                outputDataContainerMap.get(CheckResultType.CONTROLLED).getTable(),
                outputDataContainerMap.get(CheckResultType.NOT_CONTROLLED).getTable(),
                outputDataContainerMap.get(CheckResultType.ERROR).getTable()};
    }

    // creates iterable chunks from the inputTable
    private Iterable<List<DataRow>> partitionInputTable(BufferedDataTable inputTable) {
        CloseableRowIterator inputTableIterator = inputTable.iterator();
        return () -> Iterators.partition(inputTableIterator, DATA_CHUNK_SIZE);
    }

    private CheckListRequest createCheckListRequest(List<DataRow> inputTableChunk, int structureColIndex,
            CategoryHelper categoryHelper) {
        CheckListRequest checkListRequest = new CheckListRequest();
        checkListRequest.setDateOfRegulations(optionFields.getDateOfRegulationsStr());
        checkListRequest.setCategoryGroupIds(categoryHelper.convertCategoryNamesToIds(optionFields.getCategories()));
        checkListRequest.setMolFormat(optionFields.getMolFormat());
        inputTableChunk.stream()
            .map(row -> row.getCell(structureColIndex).toString())
            .collect(checkListRequest::getInputs, Collection::add, Collection::addAll);
        return checkListRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        DataTableSpec inputTableSpec = inSpecs[0];

        validateConnectionDetails();

        CheckResultSpecProvider checkResultSpecProvider =
                new CheckResultSpecProviderFactory(inputTableSpec)
                        .getProvider(optionFields.getCheckMode());

        Map<CheckResultType, DataTableSpec> outputDataTableSpecMap = checkResultSpecProvider.getResultSpecs();
        return new DataTableSpec[]{
                outputDataTableSpecMap.get(CheckResultType.CONTROLLED),
                outputDataTableSpecMap.get(CheckResultType.NOT_CONTROLLED),
                outputDataTableSpecMap.get(CheckResultType.ERROR)};
    }

    private void validateConnectionDetails() throws InvalidSettingsException {

        if (connectionFields.getHost().isEmpty()) {
            throw new InvalidSettingsException("No connection settings are provided.");
        }

        try {
            // validate connection settings by invoking a service
            new CCDbInitializationDateInvoker(connectionFields).getDbInitializationDate();
        } catch (Exception e) {
            setWarningMessage("Failed to connect to Compliance Checker. "
                    + "Please check if connection settings are correct.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        optionFields.saveSettingsTo(settings);
        connectionFields.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        optionFields.loadSettingsFrom(settings);
        connectionFields.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        connectionFields.validateSettings(settings);
        optionFields.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {

        // TODO load internal data.
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {

        // TODO save internal models.
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }
}

