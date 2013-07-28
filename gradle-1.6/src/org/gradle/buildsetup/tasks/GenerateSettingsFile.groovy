/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.buildsetup.tasks

import org.gradle.api.internal.DocumentationRegistry
import org.gradle.api.tasks.OutputFile

import javax.inject.Inject

class GenerateSettingsFile extends TextFileGenerationTask {
    private final DocumentationRegistry documentationRegistry

    @OutputFile
    File settingsFile

    @Inject
    public GenerateSettingsFile(DocumentationRegistry documentationRegistry) {
        this.documentationRegistry = documentationRegistry;
        templateURL = GenerateSettingsFile.class.getResource("/org/gradle/buildsetup/tasks/templates/settings.gradle.template")
    }

    @Override
    protected File getOutputFile() {
        return settingsFile;
    }

    @Override
    protected Map getTemplateBindings(){
        return [ref_userguide_multiproject:documentationRegistry.getDocumentationFor("multi_project_builds")]
    }
}
