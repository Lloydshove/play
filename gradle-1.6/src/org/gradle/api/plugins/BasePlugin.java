/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.plugins.BuildConfigurationRule;
import org.gradle.api.internal.plugins.CleanRule;
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet;
import org.gradle.api.internal.plugins.UploadRule;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Jar;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * <p>A  {@link org.gradle.api.Plugin}  which defines a basic project lifecycle and some common convention properties.</p>
 */
public class BasePlugin implements Plugin<Project> {
    public static final String CLEAN_TASK_NAME = "clean";
    public static final String ASSEMBLE_TASK_NAME = "assemble";
    public static final String BUILD_GROUP = "build";
    public static final String UPLOAD_GROUP = "upload";

    public void apply(Project project) {
        BasePluginConvention convention = new BasePluginConvention(project);
        project.getConvention().getPlugins().put("base", convention);

        configureBuildConfigurationRule(project);
        configureUploadRules(project);
        configureArchiveDefaults(project, convention);
        configureConfigurations(project);

        addClean(project);
        addCleanRule(project);
        addAssemble(project);
    }

    private void addAssemble(Project project) {
        Task assembleTask = project.getTasks().create(ASSEMBLE_TASK_NAME);
        assembleTask.setDescription("Assembles the outputs of this project.");
        assembleTask.setGroup(BUILD_GROUP);
        assembleTask.dependsOn(project.getConfigurations().getByName(Dependency.ARCHIVES_CONFIGURATION).getAllArtifacts().getBuildDependencies());
    }

    private void configureArchiveDefaults(final Project project, final BasePluginConvention pluginConvention) {
        project.getTasks().withType(AbstractArchiveTask.class, new Action<AbstractArchiveTask>() {
            public void execute(AbstractArchiveTask task) {
                ConventionMapping taskConventionMapping = task.getConventionMapping();

                Callable<File> destinationDir;
                if (task instanceof Jar) {
                    destinationDir = new Callable<File>() {
                        public File call() throws Exception {
                            return pluginConvention.getLibsDir();
                        }
                    };
                } else {
                    destinationDir = new Callable<File>() {
                        public File call() throws Exception {
                            return pluginConvention.getDistsDir();
                        }
                    };
                }
                taskConventionMapping.map("destinationDir", destinationDir);

                taskConventionMapping.map("version", new Callable<String>() {
                    public String call() throws Exception {
                        return project.getVersion() == Project.DEFAULT_VERSION ? null : project.getVersion().toString();
                    }
                });

                taskConventionMapping.map("baseName", new Callable<String>() {
                    public String call() throws Exception {
                        return pluginConvention.getArchivesBaseName();
                    }
                });
            }
        });
    }

    private void addClean(final Project project) {
        Delete clean = project.getTasks().create(CLEAN_TASK_NAME, Delete.class);
        clean.setDescription("Deletes the build directory.");
        clean.setGroup(BUILD_GROUP);
        clean.delete(new Callable<File>() {
            public File call() throws Exception {
                return project.getBuildDir();
            }
        });
    }

    private void addCleanRule(Project project) {
        project.getTasks().addRule(new CleanRule(project.getTasks()));
    }

    private void configureBuildConfigurationRule(Project project) {
        project.getTasks().addRule(new BuildConfigurationRule(project.getConfigurations(), project.getTasks()));
    }

    private void configureUploadRules(final Project project) {
        project.getTasks().addRule(new UploadRule(project));
    }

    private void configureConfigurations(final Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        project.setProperty("status", "integration");

        Configuration archivesConfiguration = configurations.create(Dependency.ARCHIVES_CONFIGURATION).
                setDescription("Configuration for archive artifacts.");

        configurations.create(Dependency.DEFAULT_CONFIGURATION).
                setDescription("Configuration for default artifacts.");

        final DefaultArtifactPublicationSet defaultArtifacts = project.getExtensions().create(
                "defaultArtifacts", DefaultArtifactPublicationSet.class, archivesConfiguration.getArtifacts()
        );

        configurations.all(new Action<Configuration>() {
            public void execute(Configuration configuration) {
                configuration.getArtifacts().all(new Action<PublishArtifact>() {
                    public void execute(PublishArtifact artifact) {
                        defaultArtifacts.addCandidate(artifact);
                    }
                });
            }
        });
    }
}
