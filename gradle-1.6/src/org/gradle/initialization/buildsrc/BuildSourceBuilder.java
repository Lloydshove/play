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

package org.gradle.initialization.buildsrc;

import org.gradle.GradleLauncher;
import org.gradle.StartParameter;
import org.gradle.cache.CacheBuilder;
import org.gradle.cache.CacheRepository;
import org.gradle.cache.PersistentCache;
import org.gradle.cache.internal.FileLockManager;
import org.gradle.initialization.ClassLoaderRegistry;
import org.gradle.initialization.GradleLauncherFactory;
import org.gradle.internal.classpath.ClassPath;
import org.gradle.internal.classpath.DefaultClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;

/**
 * @author Hans Dockter
 */
public class BuildSourceBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuildSourceBuilder.class);

    private final GradleLauncherFactory gradleLauncherFactory;
    private final ClassLoaderRegistry classLoaderRegistry;
    private final CacheRepository cacheRepository;

    public BuildSourceBuilder(GradleLauncherFactory gradleLauncherFactory, ClassLoaderRegistry classLoaderRegistry, CacheRepository cacheRepository) {
        this.gradleLauncherFactory = gradleLauncherFactory;
        this.classLoaderRegistry = classLoaderRegistry;
        this.cacheRepository = cacheRepository;
    }

    public URLClassLoader buildAndCreateClassLoader(StartParameter startParameter) {
        ClassPath classpath = createBuildSourceClasspath(startParameter);
        return new URLClassLoader(classpath.getAsURLArray(), classLoaderRegistry.getRootClassLoader());
    }

    ClassPath createBuildSourceClasspath(StartParameter startParameter) {
        assert startParameter.getCurrentDir() != null && startParameter.getBuildFile() == null;

        LOGGER.debug("Starting to build the build sources.");
        if (!startParameter.getCurrentDir().isDirectory()) {
            LOGGER.debug("Gradle source dir does not exist. We leave.");
            return new DefaultClassPath();
        }
        LOGGER.info("================================================" + " Start building buildSrc");

        // If we were not the most recent version of Gradle to build the buildSrc dir, then do a clean build
        // Otherwise, just to a regular build
        final PersistentCache buildSrcCache = createCache(startParameter);

        GradleLauncher gradleLauncher = buildGradleLauncher(startParameter);
        return buildSrcCache.useCache("rebuild buildSrc", new BuildSrcUpdateFactory(buildSrcCache, gradleLauncher, new BuildSrcBuildListenerFactory()));
    }

    PersistentCache createCache(StartParameter startParameter) {
        return cacheRepository.
                    cache("buildSrc").
                    withLockMode(FileLockManager.LockMode.None).
                    forObject(startParameter.getCurrentDir()).
                    withVersionStrategy(CacheBuilder.VersionStrategy.SharedCacheInvalidateOnVersionChange).
                    open();
    }

    private GradleLauncher buildGradleLauncher(StartParameter startParameter) {
        final StartParameter startParameterArg = startParameter.newInstance();
        startParameterArg.setProjectProperties(startParameter.getProjectProperties());
        startParameterArg.setSearchUpwards(false);
        startParameterArg.setProfile(startParameter.isProfile());
        return gradleLauncherFactory.newInstance(startParameterArg);
    }
}
