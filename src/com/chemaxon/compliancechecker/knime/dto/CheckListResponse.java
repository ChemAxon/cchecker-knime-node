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

package com.chemaxon.compliancechecker.knime.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckListResponse {
    
    private String categoryLabel;
    private Instant checkTime;
    private String date;
    private String checkId;
    private List<List<SimpleResponse>> simpleResponses;

    public String getCategoryLabel() {
        return categoryLabel;
    }
    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }
    public Instant getCheckTime() {
        return checkTime;
    }
    public void setCheckTime(Instant checkTime) {
        this.checkTime = checkTime;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getCheckId() {
        return checkId;
    }
    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }
    public List<List<SimpleResponse>> getSimpleResponses() {
        return simpleResponses;
    }
    public void setSimpleResponses(List<List<SimpleResponse>> simpleResponses) {
        this.simpleResponses = simpleResponses;
    }
}
