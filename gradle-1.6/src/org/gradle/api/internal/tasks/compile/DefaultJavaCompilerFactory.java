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
package org.gradle.api.internal.tasks.compile;

import org.gradle.api.AntBuilder;
import org.gradle.api.internal.file.TemporaryFileProvider;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.compile.daemon.DaemonJavaCompiler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.internal.Factory;

public class DefaultJavaCompilerFactory implements JavaCompilerFactory {
    private final static Logger LOGGER = Logging.getLogger(DefaultJavaCompilerFactory.class);

    private final ProjectInternal project;
    private final TemporaryFileProvider tempFileProvider;
    private final Factory<AntBuilder> antBuilderFactory;
    private final JavaCompilerFactory inProcessCompilerFactory;
    private boolean jointCompilation;

    public DefaultJavaCompilerFactory(ProjectInternal project, TemporaryFileProvider tempFileProvider, Factory<AntBuilder> antBuilderFactory, JavaCompilerFactory inProcessCompilerFactory){
        this.project = project;
        this.tempFileProvider = tempFileProvider;
        this.antBuilderFactory = antBuilderFactory;
        this.inProcessCompilerFactory = inProcessCompilerFactory;
    }

    /**
     * If true, the Java compiler to be created is used for joint compilation
     * together with another language's compiler in the compiler daemon.
     * In that case, the other language's normalizing and daemon compilers should be used.
     */
    public void setJointCompilation(boolean flag) {
        jointCompilation = flag;
    }

    public Compiler<JavaCompileSpec> create(CompileOptions options) {
        fallBackToAntIfNecessary(options);

        if (options.isUseAnt()) {
            return new AntJavaCompiler(antBuilderFactory);
        }

        Compiler<JavaCompileSpec> result = createTargetCompiler(options);
        if (!jointCompilation) {
            result = new NormalizingJavaCompiler(result);
        }
        return result;
    }

    private void fallBackToAntIfNecessary(CompileOptions options) {
        if (options.isUseAnt()) { return; }

        if (options.getCompiler() != null) {
            LOGGER.warn("Falling back to Ant javac task ('CompileOptions.useAnt = true') because 'CompileOptions.compiler' is set.");
            options.setUseAnt(true);
        }
    }

    private Compiler<JavaCompileSpec> createTargetCompiler(CompileOptions options) {
        if (options.isFork() && options.getForkOptions().getExecutable() != null) {
            return new CommandLineJavaCompiler(tempFileProvider, project.getProjectDir());
        }

        Compiler<JavaCompileSpec> compiler = inProcessCompilerFactory.create(options);
        if (options.isFork() && !jointCompilation) {
            return new DaemonJavaCompiler(project, compiler);
        }

        return compiler;
    }
}
