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

package org.gradle.execution.taskgraph;

import com.google.common.collect.Iterables;
import org.gradle.api.internal.TaskInternal;

import java.util.TreeSet;

class TaskInfo implements Comparable<TaskInfo> {

    private enum TaskExecutionState {
        NOT_REQUIRED, READY, EXECUTING, EXECUTED, SKIPPED
    }

    private final TaskInternal task;
    private TaskExecutionState state;
    private Throwable executionFailure;
    private final TreeSet<TaskInfo> hardSuccessors = new TreeSet<TaskInfo>();
    private final TreeSet<TaskInfo> softSuccessors = new TreeSet<TaskInfo>();

    public TaskInfo(TaskInternal task) {
        this.task = task;
        this.state = TaskExecutionState.NOT_REQUIRED;
    }

    public TaskInternal getTask() {
        return task;
    }

    public boolean isReady() {
        return state == TaskExecutionState.READY;
    }

    public boolean isComplete() {
        return state == TaskExecutionState.EXECUTED || state == TaskExecutionState.SKIPPED || state == TaskExecutionState.NOT_REQUIRED;
    }

    public boolean isSuccessful() {
        return (state == TaskExecutionState.EXECUTED && !isFailed()) || state == TaskExecutionState.NOT_REQUIRED;
    }

    public boolean isFailed() {
        return getTaskFailure() != null || getExecutionFailure() != null;
    }

    public void startExecution() {
        assert state == TaskExecutionState.READY;
        state = TaskExecutionState.EXECUTING;
    }

    public void finishExecution() {
        assert state == TaskExecutionState.EXECUTING;
        state = TaskExecutionState.EXECUTED;
    }

    public void skipExecution() {
        assert state == TaskExecutionState.READY;
        state = TaskExecutionState.SKIPPED;
    }

    public void setExecutionFailure(Throwable failure) {
        assert state == TaskExecutionState.EXECUTING;
        this.executionFailure = failure;
    }

    public Throwable getExecutionFailure() {
        return this.executionFailure;
    }

    public Throwable getTaskFailure() {
        return this.getTask().getState().getFailure();
    }

    public boolean allDependenciesComplete() {
        for (TaskInfo dependency : Iterables.concat(softSuccessors, hardSuccessors)) {
            if (!dependency.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public boolean allDependenciesSuccessful() {
        for (TaskInfo dependency : hardSuccessors) {
            if (!dependency.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    public TreeSet<TaskInfo> getHardSuccessors() {
        return hardSuccessors;
    }

    public TreeSet<TaskInfo> getSoftSuccessors() {
        return softSuccessors;
    }

    public boolean getRequired() {
        return state != TaskExecutionState.NOT_REQUIRED;
    }

    public void setRequired(boolean required) {
        state = required ? TaskExecutionState.READY : TaskExecutionState.NOT_REQUIRED;
    }

    public void addHardSuccessor(TaskInfo toNode) {
        hardSuccessors.add(toNode);
    }

    public void addSoftSuccessor(TaskInfo toNode) {
        softSuccessors.add(toNode);
    }

    public int compareTo(TaskInfo otherInfo) {
        return task.compareTo(otherInfo.getTask());
    }
}
