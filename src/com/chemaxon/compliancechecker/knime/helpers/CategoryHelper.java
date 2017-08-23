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

package com.chemaxon.compliancechecker.knime.helpers;

import java.util.List;
import java.util.stream.Collectors;

import com.chemaxon.compliancechecker.knime.dto.Category;
import com.chemaxon.compliancechecker.knime.rest.CCCategoryInvoker;
import com.chemaxon.compliancechecker.knime.rest.RestConnectionDetails;

public class CategoryHelper {
    
    private final List<Category> categories;
    
    public CategoryHelper(RestConnectionDetails connectionDetails) {
        this.categories = new CCCategoryInvoker(connectionDetails).getCategories();
    }
    
    public List<String> convertCategoryNamesToIds(List<String> names) {
        return categories.stream()
            .filter(category -> names.contains(category.getName()))
            .map(category -> category.getId())
            .collect(Collectors.toList());
    }
}
