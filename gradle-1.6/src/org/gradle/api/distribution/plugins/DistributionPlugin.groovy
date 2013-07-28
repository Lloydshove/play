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

package org.gradle.api.distribution.plugins

import org.gradle.api.GradleException
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.distribution.Distribution
import org.gradle.api.distribution.DistributionContainer
import org.gradle.api.distribution.internal.DefaultDistributionContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.internal.reflect.Instantiator

import javax.inject.Inject
import org.gradle.api.plugins.BasePlugin

/**
 * <p>A {@link Plugin} to package project as a distribution.</p>
 *
 * @author scogneau
 *
 */
@Incubating
class DistributionPlugin implements Plugin<Project> {
    /**
     * Name of the main distribution
     */
    static final String MAIN_DISTRIBUTION_NAME = "main"

    static final String DISTRIBUTION_GROUP = "distribution"
    static final String TASK_DIST_ZIP_NAME = "distZip"
    static final String TASK_DIST_TAR_NAME = "distTar"
    static final String TASK_INSTALL_NAME = "installDist"

    private DistributionContainer extension
    private Project project
    private Instantiator instantiator

    @Inject
    public DistributionPlugin(Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    public void apply(Project project) {
        this.project = project
        project.plugins.apply(BasePlugin)
        addPluginExtension()
    }

    void addPluginExtension() {
        extension = project.extensions.create("distributions", DefaultDistributionContainer.class, Distribution.class, instantiator, project.fileResolver)
        extension.all { dist ->
            dist.baseName = dist.name == MAIN_DISTRIBUTION_NAME ? project.name : String.format("%s-%s", project.name, dist.name)
            dist.contents.from("src/${dist.name}/dist")
            addZipTask(dist)
            addTarTask(dist)
            addInstallTask(dist)
        }
        extension.create(DistributionPlugin.MAIN_DISTRIBUTION_NAME)
    }

    void addZipTask(Distribution distribution) {
        def taskName = TASK_DIST_ZIP_NAME
        if (!MAIN_DISTRIBUTION_NAME.equals(distribution.name)) {
            taskName = distribution.name + "DistZip"
        }
        configureArchiveTask(taskName, distribution, Zip)
    }

    void addTarTask(Distribution distribution) {
        def taskName = TASK_DIST_TAR_NAME
        if (!MAIN_DISTRIBUTION_NAME.equals(distribution.name)) {
            taskName = distribution.name + "DistTar"
        }
        configureArchiveTask(taskName, distribution, Tar)
    }

    private <T extends AbstractArchiveTask> void configureArchiveTask(String taskName, Distribution distribution, Class<T> type) {
        def archiveTask = project.tasks.create(taskName, type)
        archiveTask.description = "Bundles the project as a distribution."
        archiveTask.group = DISTRIBUTION_GROUP
        archiveTask.conventionMapping.baseName = {
            if (distribution.baseName == null || distribution.baseName.equals("")) {
                throw new GradleException("Distribution baseName must not be null or empty! Check your configuration of the distribution plugin.")
            }
            distribution.baseName
        }
        def baseDir = { archiveTask.archiveName - ".${archiveTask.extension}" }
        archiveTask.into(baseDir) {
            with(distribution.contents)
        }
    }

    private void addInstallTask(Distribution distribution) {
        def taskName = TASK_INSTALL_NAME
        if (!MAIN_DISTRIBUTION_NAME.equals(distribution.name)) {
            taskName = "install"+ distribution.name.capitalize() + "Dist"
        }
        def installTask = project.tasks.create(taskName, Sync)
        installTask.description = "Installs the project as a JVM application along with libs and OS specific scripts."
        installTask.group = DISTRIBUTION_GROUP
        installTask.with distribution.contents
        installTask.into { project.file("${project.buildDir}/install/${distribution.baseName}") }
        installTask.doFirst {
            if (destinationDir.directory) {
                if (!new File(destinationDir, 'lib').directory || !new File(destinationDir, 'bin').directory) {
                    throw new GradleException("The specified installation directory '${destinationDir}' is neither empty nor does it contain an installation for '${distribution.name}'.\n" +
                            "If you really want to install to this directory, delete it and run the install task again.\n" +
                            "Alternatively, choose a different installation directory."
                    )
                }
            }
        }
    }
}