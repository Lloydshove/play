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

package org.gradle.api.artifacts.result;

import org.gradle.api.Incubating;
import org.gradle.api.artifacts.ModuleVersionSelector;

/**
 * An edge in the dependency graph. Provides information about the origin of the dependency and the requested module version.
 *
 * @see ResolutionResult
 */
@Incubating
public interface DependencyResult {
    /**
     * Returns the requested module version.
     *
     * @return the requested module version
     */
    ModuleVersionSelector getRequested();

    /**
     * Returns the origin of the dependency.
     *
     * @return the origin of the dependency
     */
    ResolvedModuleVersionResult getFrom();
}
