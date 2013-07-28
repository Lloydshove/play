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
package org.gradle.cache.internal;

import java.io.File;

public interface FileLockManager {
    /**
     * Creates a locks for the given file with the given mode. Acquires a lock with the given mode, which is held until the lock is
     * released by calling {@link org.gradle.cache.internal.FileLock#close()}. This method blocks until the lock can be acquired.
     *
     * @param target The file to be locked.
     * @param mode The lock mode.
     * @param targetDisplayName A display name for the target file. This is used in log and error messages.
     */
    FileLock lock(File target, LockMode mode, String targetDisplayName) throws LockTimeoutException;

    /**
     * Creates a locks for the given file with the given mode. Acquires a lock with the given mode, which is held until the lock is
     * released by calling {@link org.gradle.cache.internal.FileLock#close()}. This method blocks until the lock can be acquired.
     *
     * @param target The file to be locked.
     * @param mode The lock mode.
     * @param targetDisplayName A display name for the target file. This is used in log and error messages.
     * @param operationDisplayName A display name for the operation being performed on the target file. This is used in log and error messages.
     */
    FileLock lock(File target, LockMode mode, String targetDisplayName, String operationDisplayName) throws LockTimeoutException;

    enum LockMode {
        /**
         * No synchronisation is done.
         */
        None,
        /**
         * Multiple readers, no writers.
         */
        Shared,
        /**
         * Single writer, no readers.
         */
        Exclusive,
    }
}
