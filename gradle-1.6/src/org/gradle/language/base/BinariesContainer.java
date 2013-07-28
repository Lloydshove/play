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
package org.gradle.language.base;

import org.gradle.api.*;

/**
 * A container for binaries that in turn contains more specialized containers.
 * Added to a project by the {@link org.gradle.language.base.plugins.LanguageBasePlugin}.
 */
// TODO: ideally this would be a container where each element type is only allowed once and elements can be looked up by type
// for now I solved the lookup (usability) problem with a JvmLanguagePlugin.getJvmBinariesContainer() method; maybe that's good enough
@Incubating
public interface BinariesContainer extends NamedDomainObjectSet<Named> {}
