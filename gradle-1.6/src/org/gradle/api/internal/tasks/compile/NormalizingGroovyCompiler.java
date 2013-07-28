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
package org.gradle.api.internal.tasks.compile;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.SimpleFileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.WorkResult;
import org.gradle.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * A Groovy {@link org.gradle.api.internal.tasks.compile.Compiler} which does some normalization of the compile configuration and behaviour before delegating to some other compiler.
 */
public class NormalizingGroovyCompiler implements Compiler<GroovyJavaJointCompileSpec> {
    private static final Logger LOGGER = Logging.getLogger(NormalizingGroovyCompiler.class);
    private final Compiler<GroovyJavaJointCompileSpec> delegate;

    public NormalizingGroovyCompiler(Compiler<GroovyJavaJointCompileSpec> delegate) {
        this.delegate = delegate;
    }

    public WorkResult execute(GroovyJavaJointCompileSpec spec) {
        resolveAndFilterSourceFiles(spec);
        resolveClasspath(spec);
        resolveNonStringsInCompilerArgs(spec);
        logSourceFiles(spec);
        logCompilerArguments(spec);
        return delegateAndHandleErrors(spec);
    }

    private void resolveAndFilterSourceFiles(final GroovyJavaJointCompileSpec spec) {
        FileCollection filtered = spec.getSource().filter(new Spec<File>() {
            public boolean isSatisfiedBy(File element) {
                for (String fileExtension : spec.getGroovyCompileOptions().getFileExtensions()) {
                    if (element.getName().endsWith("." + fileExtension)) {
                        return true;
                    }
                }
                return false;
            }
        });

        spec.setSource(new SimpleFileCollection(filtered.getFiles()));
    }

    private void resolveClasspath(GroovyJavaJointCompileSpec spec) {
        spec.setClasspath(new SimpleFileCollection(Lists.newArrayList(spec.getClasspath())));
        spec.setGroovyClasspath(new SimpleFileCollection(Lists.newArrayList(spec.getGroovyClasspath())));
    }

    private void resolveNonStringsInCompilerArgs(GroovyJavaJointCompileSpec spec) {
        // in particular, this is about GStrings
        spec.getCompileOptions().setCompilerArgs(CollectionUtils.toStringList(spec.getCompileOptions().getCompilerArgs()));
    }

    private void logSourceFiles(GroovyJavaJointCompileSpec spec) {
        if (!spec.getGroovyCompileOptions().isListFiles()) { return; }

        StringBuilder builder = new StringBuilder();
        builder.append("Source files to be compiled:");
        for (File file : spec.getSource()) {
            builder.append('\n');
            builder.append(file);
        }

        LOGGER.quiet(builder.toString());
    }

    private void logCompilerArguments(GroovyJavaJointCompileSpec spec) {
        if (!LOGGER.isDebugEnabled()) { return; }

        List<String> compilerArgs = new JavaCompilerArgumentsBuilder(spec).includeLauncherOptions(true).includeSourceFiles(true).build();
        String joinedArgs = Joiner.on(' ').join(compilerArgs);
        LOGGER.debug("Java compiler arguments: {}", joinedArgs);
    }

    private WorkResult delegateAndHandleErrors(GroovyJavaJointCompileSpec spec) {
        try {
            return delegate.execute(spec);
        } catch (CompilationFailedException e) {
            if (spec.getCompileOptions().isFailOnError()) {
                throw e;
            }
            LOGGER.debug("Ignoring compilation failure.");
            return new SimpleWorkResult(false);
        }
    }
}
