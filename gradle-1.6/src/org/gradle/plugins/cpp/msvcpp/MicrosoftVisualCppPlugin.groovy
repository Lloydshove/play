/*
 * Copyright 2012 the original author or authors.
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



package org.gradle.plugins.cpp.msvcpp

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.Factory
import org.gradle.plugins.binaries.BinariesPlugin
import org.gradle.plugins.binaries.model.CompilerRegistry
import org.gradle.plugins.cpp.msvcpp.internal.VisualCppCompilerAdapter
import org.gradle.process.internal.ExecAction
import org.gradle.process.internal.DefaultExecAction
import org.gradle.internal.os.OperatingSystem

import javax.inject.Inject

/**
 * A {@link Plugin} which makes the Microsoft Visual C++ compiler available to compile C/C++ code.
 */
class MicrosoftVisualCppPlugin implements Plugin<Project> {
    private final FileResolver fileResolver;

    @Inject
    MicrosoftVisualCppPlugin(FileResolver fileResolver) {
        this.fileResolver = fileResolver
    }

    void apply(Project project) {
        project.plugins.apply(BinariesPlugin)

        if (!OperatingSystem.current().windows) {
            return
        }

        project.extensions.getByType(CompilerRegistry).add(new VisualCppCompilerAdapter(
                OperatingSystem.current(),
                new Factory<ExecAction>() {
                    ExecAction create() {
                        new DefaultExecAction(fileResolver)
                    }
                }
        ))
    }
}
