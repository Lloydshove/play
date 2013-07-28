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

package org.gradle.tooling.internal.consumer;

import org.gradle.StartParameter;
import org.gradle.internal.service.SynchronizedServiceRegistry;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.internal.service.DefaultServiceRegistry;
import org.gradle.tooling.internal.consumer.loader.CachingToolingImplementationLoader;
import org.gradle.tooling.internal.consumer.loader.DefaultToolingImplementationLoader;
import org.gradle.tooling.internal.consumer.loader.SynchronizedToolingImplementationLoader;
import org.gradle.tooling.internal.consumer.loader.ToolingImplementationLoader;

/**
 * by Szczepan Faber, created at: 12/6/11
 */
public class ConnectorServices {

    private static ServiceRegistry singletonRegistry = new SynchronizedServiceRegistry(new ConnectorServiceRegistry());

    public DefaultGradleConnector createConnector() {
        ConnectionFactory connectionFactory = new ConnectionFactory(singletonRegistry.get(ToolingImplementationLoader.class));
        return new DefaultGradleConnector(connectionFactory, new DistributionFactory(StartParameter.DEFAULT_GRADLE_USER_HOME));
    }

    /**
     * Resets the state of connector services. Meant to be used only for testing!
     */
    public void reset() {
        singletonRegistry = new SynchronizedServiceRegistry(new ConnectorServiceRegistry());
    }

    private static class ConnectorServiceRegistry extends DefaultServiceRegistry {
        protected ToolingImplementationLoader createToolingImplementationLoader() {
            return new SynchronizedToolingImplementationLoader(new CachingToolingImplementationLoader(new DefaultToolingImplementationLoader()));
        }
    }
}
