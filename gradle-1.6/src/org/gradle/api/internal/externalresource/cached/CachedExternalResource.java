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

package org.gradle.api.internal.externalresource.cached;

import org.gradle.api.Nullable;
import org.gradle.api.internal.externalresource.metadata.ExternalResourceMetaData;
import org.gradle.util.hash.HashValue;

import java.util.Date;

/**
 * A record of some kind of external resource that has been cached locally (typically into the filestore).
 *
 * Note that this is not a kind of {@link org.gradle.api.internal.externalresource.ExternalResource}. There is an adapter that can represent a cached resource as an external resource as {@link
 * CachedExternalResourceAdapter}.
 */
public interface CachedExternalResource extends CachedItem {

    /**
     * Always the actual content length of the cached file, not the external source.
     *
     * @return The content length of the cached file.
     */
    long getContentLength();

    /**
     * Always the actual checksum of the cached file, not the external source.
     *
     * @return The hash of the cached file.
     */
    HashValue getSha1();

    @Nullable
    ExternalResourceMetaData getExternalResourceMetaData();

    /**
     * Null safe shortcut for getExternalResourceMetaData().getLastModified();
     *
     * @return The external last modified, or null if unavailable.
     */
    Date getExternalLastModified();

    /**
     * Null safe shortcut for getExternalResourceMetaData().getLastModified() as a timestamp.
     *
     * @return The external last modified as a timestamp, or < 0 if unavailable.
     */
    long getExternalLastModifiedAsTimestamp();

}
