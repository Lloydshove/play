/*
 * Copyright 2009 the original author or authors.
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

package org.gradle.configuration;

import org.gradle.api.Project;
import org.gradle.api.ProjectEvaluationListener;
import org.gradle.api.ProjectState;
import org.gradle.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hans Dockter
 */
public class ProjectDependencies2TaskResolver implements ProjectEvaluationListener {
    private static Logger logger = LoggerFactory.getLogger(ProjectDependencies2TaskResolver.class);

    public void beforeEvaluate(Project project) {}

    public void afterEvaluate(Project project, ProjectState state) {
        for (Project dependsOnProject : project.getDependsOnProjects()) {
            logger.debug("Checking task dependencies for project: {} dependsOn: {}", project, dependsOnProject);
            for (Task task : project.getTasks()) {
                String taskName = task.getName();
                Task dependentTask = dependsOnProject.getTasks().findByName(taskName);
                if (dependentTask != null) {
                    logger.debug("Setting task dependencies for task: {}", taskName);
                    task.dependsOn(dependentTask);
                }
            }
        }
    }
}