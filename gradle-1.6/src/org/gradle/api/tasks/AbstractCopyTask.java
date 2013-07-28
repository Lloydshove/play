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
package org.gradle.api.tasks;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.*;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.internal.file.copy.CopyActionImpl;
import org.gradle.api.internal.file.copy.CopySpecSource;
import org.gradle.api.internal.file.copy.ReadableCopySpec;
import org.gradle.api.specs.Spec;
import org.gradle.internal.Factory;
import org.gradle.util.DeprecationLogger;

import java.io.FilterReader;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@code AbstractCopyTask} is the base class for all copy tasks.
 */
public abstract class AbstractCopyTask extends ConventionTask implements CopySpec, CopySpecSource {

    @TaskAction
    protected void copy() {
        configureRootSpec();
        getCopyAction().execute();
        setDidWork(getCopyAction().getDidWork());
    }

    protected void configureRootSpec() {
        if (!getCopyAction().hasSource()) {
            Object srcDirs = getDefaultSource();
            if (srcDirs != null) {
                from(srcDirs);
            }
        }
    }

    /**
     * Returns the default source for this task.
     * @deprecated Use getSource() instead.
     */
    @Deprecated
    public FileCollection getDefaultSource() {
        DeprecationLogger.nagUserOfReplacedMethod("AbstractCopyTask.getDefaultSource()", "getSource()");
        return null;
    }

    /**
     * Returns the source files for this task.
     * @return The source files. Never returns null.
     */
    @InputFiles @SkipWhenEmpty @Optional
    public FileCollection getSource() {
        if(getCopyAction().hasSource()){
            return getCopyAction().getAllSource();
        }else{
            return DeprecationLogger.whileDisabled(new Factory<FileCollection>() {
                public FileCollection create() {
                    return getDefaultSource();
                }
            });
        }
    }
    
    protected abstract CopyActionImpl getCopyAction();

    public ReadableCopySpec getRootSpec() {
        return getCopyAction().getRootSpec();
    }

    // -----------------------------------------------
    // ---- Delegate CopySpec methods to rootSpec ----
    // -----------------------------------------------

    protected CopySpec getMainSpec() {
        return getCopyAction();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCaseSensitive() {
        return getMainSpec().isCaseSensitive();
    }

    /**
     * {@inheritDoc}
     */
    public void setCaseSensitive(boolean caseSensitive) {
        getMainSpec().setCaseSensitive(caseSensitive);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getIncludeEmptyDirs() {
        return getMainSpec().getIncludeEmptyDirs();
    }

    /**
     * {@inheritDoc}
     */
    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        getMainSpec().setIncludeEmptyDirs(includeEmptyDirs);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask from(Object... sourcePaths) {
        getMainSpec().from(sourcePaths);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask from(Object sourcePath, Closure c) {
        getMainSpec().from(sourcePath, c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public CopySpec with(CopySpec... sourceSpecs) {
        getMainSpec().with(sourceSpecs);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask into(Object destDir) {
        getMainSpec().into(destDir);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask into(Object destPath, Closure configureClosure) {
        getMainSpec().into(destPath, configureClosure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask include(String... includes) {
        getMainSpec().include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask include(Iterable<String> includes) {
        getMainSpec().include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask include(Spec<FileTreeElement> includeSpec) {
        getMainSpec().include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask include(Closure includeSpec) {
        getMainSpec().include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask exclude(String... excludes) {
        getMainSpec().exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask exclude(Iterable<String> excludes) {
        getMainSpec().exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask exclude(Spec<FileTreeElement> excludeSpec) {
        getMainSpec().exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask exclude(Closure excludeSpec) {
        getMainSpec().exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask setIncludes(Iterable<String> includes) {
        getMainSpec().setIncludes(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getIncludes() {
        return getMainSpec().getIncludes();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask setExcludes(Iterable<String> excludes) {
        getMainSpec().setExcludes(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExcludes() {
        return getMainSpec().getExcludes();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask rename(Closure closure) {
        getMainSpec().rename(closure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask rename(String sourceRegEx, String replaceWith) {
        getMainSpec().rename(sourceRegEx, replaceWith);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask rename(Pattern sourceRegEx, String replaceWith) {
        getMainSpec().rename(sourceRegEx, replaceWith);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        getMainSpec().filter(properties, filterType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask filter(Class<? extends FilterReader> filterType) {
        getMainSpec().filter(filterType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask filter(Closure closure) {
        getMainSpec().filter(closure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask expand(Map<String, ?> properties) {
        getMainSpec().expand(properties);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getDirMode() {
        return getMainSpec().getDirMode();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getFileMode() {
        return getMainSpec().getFileMode();
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask setDirMode(Integer mode) {
        getMainSpec().setDirMode(mode);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask setFileMode(Integer mode) {
        getMainSpec().setFileMode(mode);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask eachFile(Action<? super FileCopyDetails> action) {
        getMainSpec().eachFile(action);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCopyTask eachFile(Closure closure) {
        getMainSpec().eachFile(closure);
        return this;
    }
}
