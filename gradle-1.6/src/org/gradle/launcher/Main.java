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
package org.gradle.launcher;

import org.gradle.launcher.cli.CommandLineActionFactory;
import org.gradle.launcher.bootstrap.EntryPoint;
import org.gradle.launcher.bootstrap.ExecutionListener;

import java.util.Arrays;

/**
 * The main command-line entry-point for Gradle.
 *
 * @author Hans Dockter
 */
public class Main extends EntryPoint {

    private final String[] args;
    
    public Main(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) {
        try {
            new Main(args).run();
            System.exit(0);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(1);
        }
    }

    protected void doAction(ExecutionListener listener) {
        createActionFactory().convert(Arrays.asList(args)).execute(listener);
    }

    CommandLineActionFactory createActionFactory() {
        return new CommandLineActionFactory();
    }
}
