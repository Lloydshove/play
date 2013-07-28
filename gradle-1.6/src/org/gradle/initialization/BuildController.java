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

package org.gradle.initialization;

import org.gradle.GradleLauncher;
import org.gradle.StartParameter;

public interface BuildController {
    /**
     * Specifies the start parameter to use to run the build. Cannot be used after the launcher has been created.
     */
    void setStartParameter(StartParameter startParameter);

    /**
     * Returns the launcher to use to run the build.
     */
    GradleLauncher getLauncher();

    /**
     * Runs the build.
     */
    void run();

    /**
     * Configures the build but does not run any tasks.
     */
    void configure();
}
