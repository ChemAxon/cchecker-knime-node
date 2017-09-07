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

import java.util.ArrayList;
import java.util.List;

public class CheckListRequest {
    private List<String> input;
    private String date;
    private List<String> categories;

    public List<String> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return input;
    }
    public void setInput(List<String> input) {
        this.input = input;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public List<String> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "CheckListRequest [input=" + input + ", date=" + date + ", categories=" + categories + "]";
    }
}


