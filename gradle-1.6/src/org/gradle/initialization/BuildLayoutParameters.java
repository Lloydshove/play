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

package org.gradle.initialization;

import org.gradle.StartParameter;
import org.gradle.internal.SystemProperties;

import java.io.File;

import static org.gradle.util.GFileUtils.canonicalise;

/**
 * by Szczepan Faber, created at: 2/18/13
 */
public class BuildLayoutParameters {

    private boolean searchUpwards = true;
    private File projectDir = canonicalise(SystemProperties.getCurrentDir());
    private File gradleUserHomeDir;

    public BuildLayoutParameters() {
        String gradleUserHome = System.getProperty(StartParameter.GRADLE_USER_HOME_PROPERTY_KEY);
        if (gradleUserHome == null) {
            gradleUserHome = System.getenv("GRADLE_USER_HOME");
            if (gradleUserHome == null) {
                gradleUserHome = StartParameter.DEFAULT_GRADLE_USER_HOME.getAbsolutePath();
            }
        }
        gradleUserHomeDir = canonicalise(new File(gradleUserHome));
    }

    public BuildLayoutParameters setSearchUpwards(boolean searchUpwards) {
        this.searchUpwards = searchUpwards;
        return this;
    }

    public BuildLayoutParameters setProjectDir(File projectDir) {
        this.projectDir = projectDir;
        return this;
    }

    public BuildLayoutParameters setGradleUserHomeDir(File gradleUserHomeDir) {
        this.gradleUserHomeDir = gradleUserHomeDir;
        return this;
    }

    public File getProjectDir() {
        return projectDir;
    }

    public File getGradleUserHomeDir() {
        return gradleUserHomeDir;
    }

    public boolean getSearchUpwards() {
        return searchUpwards;
    }
}
