/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.api.internal.project;

import org.gradle.api.internal.TaskInternal;
import org.gradle.api.internal.TaskOutputsInternal;
import org.gradle.api.internal.tasks.DefaultTaskInputs;
import org.gradle.api.internal.tasks.DefaultTaskOutputs;
import org.gradle.api.internal.tasks.TaskStatusNagger;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.logging.LoggingManagerInternal;

/**
 * Contains the services for a given task.
 */
public class TaskInternalServiceRegistry extends DefaultServiceRegistry implements ServiceRegistryFactory {
    private final ProjectInternal project;
    private final TaskInternal taskInternal;

    public TaskInternalServiceRegistry(ServiceRegistry parent, final ProjectInternal project, TaskInternal taskInternal) {
        super(parent);
        this.project = project;
        this.taskInternal = taskInternal;
    }

    protected TaskInputs createTaskInputs() {
        return new DefaultTaskInputs(project.getFileResolver(), taskInternal, get(TaskStatusNagger.class));
    }

    protected TaskOutputsInternal createTaskOutputs() {
        return new DefaultTaskOutputs(project.getFileResolver(), taskInternal, get(TaskStatusNagger.class));
    }

    protected TaskStatusNagger createTaskStatusNagger() {
        return new TaskStatusNagger(taskInternal);
    }

    protected LoggingManagerInternal createLoggingManager() {
        return getFactory(LoggingManagerInternal.class).create();
    }

    public ServiceRegistryFactory createFor(Object domainObject) {
        throw new UnsupportedOperationException();
    }
}
