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
package org.gradle.util

import org.gradle.internal.os.OperatingSystem

enum TestPrecondition {
    SWING({
        !UNKNOWN_OS.fulfilled
    }),
    JNA({
        !UNKNOWN_OS.fulfilled
    }),
    NO_JNA({
        UNKNOWN_OS.fulfilled
    }),
    SYMLINKS({
        MAC_OS_X.fulfilled || LINUX.fulfilled
    }),
    NO_SYMLINKS({
        !SYMLINKS.fulfilled
    }),
    CASE_INSENSITIVE_FS({
        MAC_OS_X.fulfilled || WINDOWS.fulfilled
    }),
    FILE_PERMISSIONS({
        MAC_OS_X.fulfilled || LINUX.fulfilled
    }),
    NO_FILE_PERMISSIONS({
        !FILE_PERMISSIONS.fulfilled
    }),
    SET_ENV_VARIABLE({
        !UNKNOWN_OS.fulfilled
    }),
    WORKING_DIR({
        !UNKNOWN_OS.fulfilled
    }),
    PROCESS_ID({
        !UNKNOWN_OS.fulfilled
    }),
    NO_FILE_LOCK_ON_OPEN({
        MAC_OS_X.fulfilled || LINUX.fulfilled
    }),
    MANDATORY_FILE_LOCKING({
        OperatingSystem.current().windows
    }),
    WINDOWS({
        OperatingSystem.current().windows
    }),
    NOT_WINDOWS({
        !OperatingSystem.current().windows
    }),
    MAC_OS_X({
        OperatingSystem.current().macOsX
    }),
    LINUX({
        OperatingSystem.current().linux
    }),
    UNIX({
        OperatingSystem.current().unix
    }),
    UNKNOWN_OS({
        OperatingSystem.current().name == "unknown operating system"
    }),
    NOT_UNKNOWN_OS({
        !UNKNOWN_OS.fulfilled
    }),
    JDK5({
        System.getProperty("java.version").startsWith("1.5")
    }),
    JDK6({
        System.getProperty("java.version").startsWith("1.6")
    }),
    JDK7({
        System.getProperty("java.version").startsWith("1.7")
    }),
    NOT_JDK5({
        !JDK5.fulfilled
    }),
    NOT_JDK7({
        !JDK7.fulfilled
    }),
    JDK7_POSIX({
        JDK7.fulfilled && NOT_WINDOWS.fulfilled
    });

    /**
     * A predicate for testing whether the precondition is fulfilled.
     */
    private Closure predicate

    TestPrecondition(Closure predicate) {
        this.predicate = predicate
    }

    /**
     * Tells if the precondition is fulfilled.
     */
    boolean isFulfilled() {
        predicate()
    }
}

