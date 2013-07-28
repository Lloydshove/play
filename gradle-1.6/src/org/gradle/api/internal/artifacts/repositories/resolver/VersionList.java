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

package org.gradle.api.internal.artifacts.repositories.resolver;

import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.plugins.latest.LatestStrategy;
import org.gradle.api.internal.resource.ResourceException;
import org.gradle.api.internal.resource.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

public interface VersionList {
    /**
     * <p>Adds those versions available for the given pattern.</p>
     *
     * @throws ResourceNotFoundException If information for versions cannot be found.
     * @throws ResourceException If information for versions cannot be loaded.
     */
    void visit(ResourcePattern pattern, Artifact artifact) throws ResourceNotFoundException, ResourceException;

    Set<String> getVersionStrings();

    boolean isEmpty();

    List<String> sortLatestFirst(LatestStrategy latestStrategy);
}
