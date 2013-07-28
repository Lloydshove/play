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

package org.gradle.api.internal.changedetection.state;

import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.tasks.TaskExecuter;
import org.gradle.api.internal.tasks.TaskStateInternal;

/**
 * Ensure that the task artifact cache is locked.
 * Normally this is already achieved via {@link TaskCacheLockHandlingBuildExecuter}, but for direct calls to {@link org.gradle.api.internal.TaskInternal#execute()} we need to reacquire the cache lock.
 */
public class CacheLockAcquiringTaskExecuter implements TaskExecuter {
    private final TaskExecuter executer;
    private final TaskArtifactStateCacheAccess cacheAccess;

    public CacheLockAcquiringTaskExecuter(TaskArtifactStateCacheAccess cacheAccess, TaskExecuter executer) {
        this.executer = executer;
        this.cacheAccess = cacheAccess;
    }

    public void execute(final TaskInternal task, final TaskStateInternal state) {
        cacheAccess.useCache(String.format("execute %s", task), new Runnable() {
            public void run() {
                executer.execute(task, state);
            }
        });
    }
}
