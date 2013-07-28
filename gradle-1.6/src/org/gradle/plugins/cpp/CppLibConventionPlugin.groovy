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
package org.gradle.plugins.cpp

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.bundling.Zip

import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet

class CppLibConventionPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply(CppPlugin)

        project.with {
            cpp {
                sourceSets {
                    main {}
                }
            }
            libraries {
                main {
                    sourceSets << project.cpp.sourceSets.main
                    spec.baseName = project.name
                }
            }

            def libArtifact = new DefaultPublishArtifact(
                archivesBaseName, // name
                "so", // ext
                "so", // type
                "so", // classifier
                null, // date

                // needs to be more general and not peer into the spec
                libraries.main.spec.outputFile,
                libraries.main
            )

            task("assembleHeaders", type: Zip) {
                from libraries.main.headers
                classifier = "headers"
            }

            def headerArtifact = new ArchivePublishArtifact(assembleHeaders)

            extensions.getByType(DefaultArtifactPublicationSet).addCandidate(libArtifact)
            extensions.getByType(DefaultArtifactPublicationSet).addCandidate(headerArtifact)
        }
    }

}