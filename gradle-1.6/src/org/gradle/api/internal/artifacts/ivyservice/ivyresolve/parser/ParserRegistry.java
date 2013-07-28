/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser;

import org.apache.ivy.plugins.parser.ModuleDescriptorParser;
import org.apache.ivy.plugins.repository.Resource;

import java.util.ArrayList;
import java.util.List;

public class ParserRegistry {
    private List<ModuleDescriptorParser> parsers = new ArrayList<ModuleDescriptorParser>();

    public ParserRegistry() {
        parsers.add(new GradlePomModuleDescriptorParser());
        parsers.add(new DownloadedIvyModuleDescriptorParser());
    }

    public ModuleDescriptorParser forResource(Resource resource) {
        for (ModuleDescriptorParser parser : parsers) {
            if (parser.accept(resource)) {
                return parser;
            }
        }
        throw new IllegalStateException("No registered parser for resource: " + resource.getName());
    }
}
