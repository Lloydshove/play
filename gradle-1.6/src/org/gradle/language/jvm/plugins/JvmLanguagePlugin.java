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
package org.gradle.language.jvm.plugins;

import org.gradle.api.*;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.tasks.*;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.language.base.BinariesContainer;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.language.jvm.ResourceSet;
import org.gradle.language.base.plugins.LanguageBasePlugin;
import org.gradle.language.jvm.ClassDirectoryBinary;
import org.gradle.language.jvm.JvmBinaryContainer;
import org.gradle.language.jvm.internal.DefaultClassDirectoryBinary;
import org.gradle.language.jvm.internal.DefaultJvmBinaryContainer;
import org.gradle.language.jvm.internal.DefaultResourceSet;
import org.gradle.language.jvm.tasks.ProcessResources;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Base plugin for JVM language support. Applies the {@link org.gradle.language.base.plugins.LanguageBasePlugin}.
 * Adds a {@link org.gradle.language.jvm.JvmBinaryContainer} named {@code jvm} to the project's {@link org.gradle.language.base.BinariesContainer}.
 * Registers the {@link org.gradle.language.jvm.ClassDirectoryBinary} element type for that container.
 * Adds a lifecycle task named {@code classes} for each {@link org.gradle.language.jvm.ClassDirectoryBinary}.
 * Registers the {@link org.gradle.language.jvm.ResourceSet} element type for each {@link org.gradle.language.base.FunctionalSourceSet} added to {@link org.gradle.language.base.ProjectSourceSet}.
 * Adds a {@link Copy} task named {@code processXYZResources} for each {@link org.gradle.language.jvm.ResourceSet} added to a {@link org.gradle.language.jvm.ClassDirectoryBinary}.
 */
@Incubating
public class JvmLanguagePlugin implements Plugin<Project> {
    private final Instantiator instantiator;
    private final FileResolver fileResolver;

    @Inject
    public JvmLanguagePlugin(Instantiator instantiator, FileResolver fileResolver) {
        this.instantiator = instantiator;
        this.fileResolver = fileResolver;
    }

    public void apply(final Project target) {
        target.getPlugins().apply(LanguageBasePlugin.class);

        ProjectSourceSet projectSourceSet = target.getExtensions().getByType(ProjectSourceSet.class);
        projectSourceSet.all(new Action<FunctionalSourceSet>() {
            public void execute(final FunctionalSourceSet functionalSourceSet) {
                functionalSourceSet.registerFactory(ResourceSet.class, new NamedDomainObjectFactory<ResourceSet>() {
                    public ResourceSet create(String name) {
                        return instantiator.newInstance(DefaultResourceSet.class, name,
                                instantiator.newInstance(DefaultSourceDirectorySet.class, name, fileResolver), functionalSourceSet);
                    }
                });
            }
        });

        BinariesContainer binariesContainer = target.getExtensions().getByType(BinariesContainer.class);
        JvmBinaryContainer jvmBinaryContainer = instantiator.newInstance(DefaultJvmBinaryContainer.class, instantiator);
        binariesContainer.add(jvmBinaryContainer);

        jvmBinaryContainer.registerFactory(ClassDirectoryBinary.class, new NamedDomainObjectFactory<ClassDirectoryBinary>() {
            public ClassDirectoryBinary create(String name) {
                return instantiator.newInstance(DefaultClassDirectoryBinary.class, name);
            };
        });

        jvmBinaryContainer.all(new Action<ClassDirectoryBinary>() {
            public void execute(final ClassDirectoryBinary binary) {
                ConventionMapping conventionMapping = new DslObject(binary).getConventionMapping();
                conventionMapping.map("classesDir", new Callable<File>() {
                    public File call() throws Exception {
                        return new File(new File(target.getBuildDir(), "classes"), binary.getName());
                    }
                });
                final Task classesTask = target.getTasks().create(binary.getTaskName(null, "classes"));
                classesTask.setDescription(String.format("Assembles %s.", binary));
                binary.setClassesTask(classesTask);
                binary.getSource().withType(ResourceSet.class).all(new Action<ResourceSet>() {
                    public void execute(ResourceSet resourceSet) {
                        // TODO: handle case where binary has multiple ResourceSet's
                        Copy resourcesTask = target.getTasks().create(binary.getTaskName("process", "resources"), ProcessResources.class);
                        resourcesTask.setDescription(String.format("Processes %s.", resourceSet));
                        new DslObject(resourcesTask).getConventionMapping().map("destinationDir", new Callable<File>() {
                            public File call() throws Exception {
                                return binary.getResourcesDir();
                            }
                        });
                        classesTask.dependsOn(resourcesTask);
                        resourcesTask.from(resourceSet.getSource());
                    }
                });
            }
        });
    }
}
