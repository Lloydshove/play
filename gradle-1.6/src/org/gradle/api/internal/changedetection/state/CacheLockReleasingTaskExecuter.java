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
import org.gradle.api.internal.tasks.ContextualTaskExecuter;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.TaskStateInternal;

public class CacheLockReleasingTaskExecuter implements ContextualTaskExecuter {
    private final ContextualTaskExecuter executer;
    private final TaskArtifactStateCacheAccess cacheAccess;

    public CacheLockReleasingTaskExecuter(TaskArtifactStateCacheAccess cacheAccess, ContextualTaskExecuter executer) {
        this.executer = executer;
        this.cacheAccess = cacheAccess;
    }

    public void execute(final TaskInternal task, final TaskStateInternal state, final TaskExecutionContext context) {
        cacheAccess.longRunningOperation(String.format("execute %s", task), new Runnable() {
            public void run() {
                executer.execute(task, state, context);
            }
        });
    }
}
