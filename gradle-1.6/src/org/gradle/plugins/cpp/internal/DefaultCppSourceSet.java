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
package org.gradle.plugins.cpp.internal;

import org.gradle.plugins.cpp.CppSourceSet;

import org.gradle.plugins.binaries.model.Library;
import org.gradle.plugins.binaries.model.NativeDependencySet;
import org.gradle.plugins.binaries.model.internal.ConfigurationBasedNativeDependencySet;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.SourceDirectorySet;

import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;
import java.util.Map;

public class DefaultCppSourceSet implements CppSourceSet {

    private final String name;

    private final DefaultSourceDirectorySet exportedHeaders;
    private final DefaultSourceDirectorySet source;
    private final DefaultDomainObjectSet<Library> libs;
    private final DefaultDomainObjectSet<NativeDependencySet> nativeDependencySets;
    private final ConfigurationBasedNativeDependencySet configurationDependencySet;

    public DefaultCppSourceSet(String name, ProjectInternal project) {
        this.name = name;

        this.exportedHeaders = new DefaultSourceDirectorySet("exported headers", project.getFileResolver());
        this.source = new DefaultSourceDirectorySet("source", project.getFileResolver());
        this.libs = new DefaultDomainObjectSet<Library>(Library.class);
        this.nativeDependencySets = new DefaultDomainObjectSet<NativeDependencySet>(NativeDependencySet.class);
        this.configurationDependencySet = new ConfigurationBasedNativeDependencySet(project, name);
        
        nativeDependencySets.add(configurationDependencySet);
    }

    public String getName() {
        return name;
    }

    public SourceDirectorySet getExportedHeaders() {
        return exportedHeaders;
    }

    public DefaultCppSourceSet exportedHeaders(Closure closure) {
        ConfigureUtil.configure(closure, exportedHeaders);
        return this;
    }

    public SourceDirectorySet getSource() {
        return source;
    }

    public DefaultCppSourceSet source(Closure closure) {
        ConfigureUtil.configure(closure, source);
        return this;
    }

    public DomainObjectSet<Library> getLibs() {
        return libs;
    }

    public DomainObjectSet<NativeDependencySet> getNativeDependencySets() {
        return nativeDependencySets;
    }

    public void dependency(Map<?, ?> dep) {
        configurationDependencySet.add(dep);
    }
}