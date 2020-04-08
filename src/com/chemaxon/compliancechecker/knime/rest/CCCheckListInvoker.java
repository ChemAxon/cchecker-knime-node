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

package com.chemaxon.compliancechecker.knime.rest;

import java.util.ArrayList;
import java.util.List;

import com.chemaxon.compliancechecker.knime.dto.CheckListRequest;
import com.chemaxon.compliancechecker.knime.dto.CheckListResponse;
import com.chemaxon.compliancechecker.knime.dto.SimpleResponse;
import com.chemaxon.compliancechecker.knime.types.CheckResultType;

public class CCCheckListInvoker {
    
    private static final String URL_PATH = "/cc-api/check-list/";
    
    private final CCRestInvoker ccRestInvoker;
    
    public CCCheckListInvoker(RestConnectionDetails connectionDetails) {
        ccRestInvoker = new CCRestInvoker(connectionDetails, URL_PATH);
    }
    
    public List<CheckResultType> getSimplifiedCheckResults(CheckListRequest request) {
        List<CheckResultType> result = new ArrayList<>();
        CheckListResponse response = ccRestInvoker.post(request, CheckListResponse.class);
        for (List<SimpleResponse> simpleResponses : response.getSimpleResponses()) {
            if (simpleResponses.isEmpty()) {
                result.add(CheckResultType.NOT_CONTROLLED);
            } else if (simpleResponses.get(0).isError()) {
                result.add(CheckResultType.ERROR);
            } else {
                result.add(CheckResultType.CONTROLLED);
            }
        }
        return result;
    }
    
    public List<List<SimpleResponse>> getDetailedCheckResults(CheckListRequest request) {
        return ccRestInvoker.post(request, CheckListResponse.class).getSimpleResponses();
    }
}
