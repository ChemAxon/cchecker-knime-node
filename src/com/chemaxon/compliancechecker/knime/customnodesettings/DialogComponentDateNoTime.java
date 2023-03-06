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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponent;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.DateInputDialog;

public class DialogComponentDateNoTime extends DialogComponent {

    private DateInputDialog m_dialogcomp;

    private SettingsModelDate m_datemodel;

    public DialogComponentDateNoTime(final SettingsModelDate model, final String label) {
        this(model, label, true);
    }

    public DialogComponentDateNoTime(final SettingsModelDate model, final String label, final boolean optional) {
        super(model);
        m_datemodel = model;
        m_dialogcomp = new DateInputDialog(DateInputDialog.Mode.NOTIME, optional);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label));
        panel.add(m_dialogcomp);
        getComponentPanel().add(panel);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateComponent() {
        m_dialogcomp.setDateAndMode(m_datemodel.getTimeInMillis(),
                                    DateInputDialog.getModeForStatus(m_datemodel.getSelectedFields()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsBeforeSave() throws InvalidSettingsException {
        m_datemodel.setTimeInMillis(m_dialogcomp.getSelectedDate().getTime());
        m_datemodel.setSelectedFields(m_dialogcomp.getIntForStatus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkConfigurabilityBeforeLoad(final PortObjectSpec[] specs) throws NotConfigurableException {
        //nothing todo here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setEnabledComponents(final boolean enabled) {
        m_dialogcomp.setEnabled(enabled);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(final String text) {
    }
}
