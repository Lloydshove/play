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
package org.gradle.plugins.cpp;

import org.gradle.plugins.binaries.model.Library;
import org.gradle.plugins.binaries.model.HeaderExportingSourceSet;
import org.gradle.plugins.binaries.model.NativeDependencyCapableSourceSet;

import org.gradle.api.Named;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.file.SourceDirectorySet;

import groovy.lang.Closure;

import java.util.Map;

/**
 * A representation of a unit of cpp source
 */
public interface CppSourceSet extends HeaderExportingSourceSet, NativeDependencyCapableSourceSet, Named {

    /**
     * The headers.
     */
    SourceDirectorySet getExportedHeaders();

    /**
     * The headers.
     */
    CppSourceSet exportedHeaders(Closure closure);

    /**
     * The source.
     */
    SourceDirectorySet getSource();

    /**
     * The source.
     */
    CppSourceSet source(Closure closure);

    /**
     * Libs this source set requires
     */
    DomainObjectSet<Library> getLibs();
    
    /**
     * Add a dependency to this source set
     */
    void dependency(Map<?, ?> dep);

}