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
package org.gradle.api.internal.artifacts.dsl;

import groovy.lang.Closure;
import org.apache.ivy.plugins.resolver.DependencyResolver;
import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.ClosureBackedAction;
import org.gradle.api.internal.ConfigureByMapAction;
import org.gradle.api.internal.artifacts.BaseRepositoryFactory;
import org.gradle.api.internal.artifacts.DefaultArtifactRepositoryContainer;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.util.ConfigureUtil;
import org.gradle.util.DeprecationLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gradle.util.CollectionUtils.flattenToList;

/**
 * @author Hans Dockter
 */
public class DefaultRepositoryHandler extends DefaultArtifactRepositoryContainer implements RepositoryHandler {

    public static final String FLAT_DIR_DEFAULT_NAME = "flatDir";
    private static final String MAVEN_REPO_DEFAULT_NAME = "maven";
    private static final String IVY_REPO_DEFAULT_NAME = "ivy";

    private final BaseRepositoryFactory repositoryFactory;

    public DefaultRepositoryHandler(BaseRepositoryFactory repositoryFactory, Instantiator instantiator) {
        super(repositoryFactory, instantiator);
        this.repositoryFactory = repositoryFactory;
    }

    public FlatDirectoryArtifactRepository flatDir(Action<? super FlatDirectoryArtifactRepository> action) {
        return addRepository(repositoryFactory.createFlatDirRepository(), FLAT_DIR_DEFAULT_NAME, action);
    }

    public FlatDirectoryArtifactRepository flatDir(Closure configureClosure) {
        return flatDir(new ClosureBackedAction<FlatDirectoryArtifactRepository>(configureClosure));
    }

    public FlatDirectoryArtifactRepository flatDir(Map<String, ?> args) {
        Map<String, Object> modifiedArgs = new HashMap<String, Object>(args);
        if (modifiedArgs.containsKey("dirs")) {
            modifiedArgs.put("dirs", flattenToList(modifiedArgs.get("dirs")));
        }
        return flatDir(new ConfigureByMapAction<FlatDirectoryArtifactRepository>(modifiedArgs));
    }

    public MavenArtifactRepository mavenCentral() {
        return addRepository(repositoryFactory.createMavenCentralRepository(), DEFAULT_MAVEN_CENTRAL_REPO_NAME);
    }

    public MavenArtifactRepository mavenCentral(Map<String, ?> args) {
        Map<String, Object> modifiedArgs = new HashMap<String, Object>(args);
        if (modifiedArgs.containsKey("urls")) {
            DeprecationLogger.nagUserOfDeprecated(
                    "The 'urls' property of the RepositoryHandler.mavenCentral() method",
                    "You should use the 'artifactUrls' property to define additional artifact locations"
            );
            List<?> urls = flattenToList(modifiedArgs.remove("urls"));
            modifiedArgs.put("artifactUrls", urls);
        }

        return addRepository(repositoryFactory.createMavenCentralRepository(), DEFAULT_MAVEN_CENTRAL_REPO_NAME, new ConfigureByMapAction<MavenArtifactRepository>(modifiedArgs));
    }

    public MavenArtifactRepository mavenLocal() {
        return addRepository(repositoryFactory.createMavenLocalRepository(), DEFAULT_MAVEN_LOCAL_REPO_NAME);
    }

    public DependencyResolver mavenRepo(Map<String, ?> args) {
        return mavenRepo(args, null);
    }

    public DependencyResolver mavenRepo(Map<String, ?> args, Closure configClosure) {
        Map<String, Object> modifiedArgs = new HashMap<String, Object>(args);
        if (modifiedArgs.containsKey("urls")) {
            List<?> urls = flattenToList(modifiedArgs.remove("urls"));
            if (!urls.isEmpty()) {
                DeprecationLogger.nagUserOfDeprecated(
                        "The 'urls' property of the RepositoryHandler.mavenRepo() method",
                        "You should use the 'url' property to define the core Maven repository & the 'artifactUrls' property to define any additional artifact locations"
                );
                modifiedArgs.put("url", urls.get(0));
                List<?> extraUrls = urls.subList(1, urls.size());
                modifiedArgs.put("artifactUrls", extraUrls);
            }
        }

        MavenArtifactRepository repository = repositoryFactory.createMavenRepository();
        ConfigureUtil.configureByMap(modifiedArgs, repository);
        DependencyResolver resolver = repositoryFactory.toResolver(repository);
        ConfigureUtil.configure(configClosure, resolver);
        addRepository(repositoryFactory.createResolverBackedRepository(resolver), "mavenRepo");
        return resolver;
    }


    public MavenArtifactRepository maven(Action<? super MavenArtifactRepository> action) {
        return addRepository(repositoryFactory.createMavenRepository(), MAVEN_REPO_DEFAULT_NAME, action);
    }

    public MavenArtifactRepository maven(Closure closure) {
        return maven(new ClosureBackedAction<MavenArtifactRepository>(closure));
    }

    public IvyArtifactRepository ivy(Action<? super IvyArtifactRepository> action) {
        return addRepository(repositoryFactory.createIvyRepository(), IVY_REPO_DEFAULT_NAME, action);
    }

    public IvyArtifactRepository ivy(Closure closure) {
        return ivy(new ClosureBackedAction<IvyArtifactRepository>(closure));
    }


}
