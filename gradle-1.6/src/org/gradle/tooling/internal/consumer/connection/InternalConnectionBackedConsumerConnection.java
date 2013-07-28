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

package org.gradle.tooling.internal.consumer.connection;

import org.gradle.tooling.internal.adapter.ProtocolToModelAdapter;
import org.gradle.tooling.internal.consumer.parameters.ConsumerOperationParameters;
import org.gradle.tooling.internal.consumer.versioning.ModelMapping;
import org.gradle.tooling.internal.consumer.versioning.VersionDetails;
import org.gradle.tooling.internal.protocol.ConnectionVersion4;
import org.gradle.tooling.internal.protocol.InternalConnection;
import org.gradle.tooling.model.GradleProject;
import org.gradle.tooling.model.build.BuildEnvironment;
import org.gradle.tooling.model.eclipse.EclipseProject;
import org.gradle.tooling.model.eclipse.HierarchicalEclipseProject;
import org.gradle.tooling.model.idea.BasicIdeaProject;
import org.gradle.tooling.model.idea.IdeaProject;

public class InternalConnectionBackedConsumerConnection extends AbstractPre12ConsumerConnection {
    private final InternalConnection connection;

    public InternalConnectionBackedConsumerConnection(ConnectionVersion4 delegate, ModelMapping modelMapping, ProtocolToModelAdapter adapter) {
        super(delegate, new R10M8VersionDetails(delegate.getMetaData().getVersion()), modelMapping, adapter);
        connection = (InternalConnection) delegate;
    }

    @Override
    protected Object doGetModel(Class<?> protocolType, ConsumerOperationParameters operationParameters) {
        return connection.getTheModel(protocolType, operationParameters);
    }

    private static class R10M8VersionDetails extends VersionDetails {
        public R10M8VersionDetails(String version) {
            super(version);
        }

        @Override
        public boolean supportsConfiguringJavaHome() {
            return true;
        }

        @Override
        public boolean supportsConfiguringJvmArguments() {
            return true;
        }

        @Override
        public boolean supportsConfiguringStandardInput() {
            return true;
        }

        @Override
        public boolean supportsGradleProjectModel() {
            return true;
        }

        @Override
        public boolean isModelSupported(Class<?> modelType) {
            return modelType.equals(Void.class)
                    || modelType.equals(HierarchicalEclipseProject.class)
                    || modelType.equals(EclipseProject.class)
                    || modelType.equals(IdeaProject.class)
                    || modelType.equals(BasicIdeaProject.class)
                    || modelType.equals(GradleProject.class)
                    || modelType.equals(BuildEnvironment.class);
        }
    }
}
