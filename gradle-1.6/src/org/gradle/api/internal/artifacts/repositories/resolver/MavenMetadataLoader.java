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

package org.gradle.api.internal.artifacts.repositories.resolver;

import org.apache.ivy.plugins.repository.Resource;
import org.apache.ivy.util.ContextualSAXHandler;
import org.apache.ivy.util.XMLHelper;
import org.gradle.api.internal.externalresource.ExternalResource;
import org.gradle.api.internal.externalresource.transport.ExternalResourceRepository;
import org.gradle.api.internal.resource.ResourceException;
import org.gradle.api.internal.resource.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

class MavenMetadataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenMetadataLoader.class);

    private final ExternalResourceRepository repository;

    public MavenMetadataLoader(ExternalResourceRepository repository) {
        this.repository = repository;
    }

    public MavenMetadata load(String metadataLocation) throws ResourceNotFoundException, ResourceException {
        MavenMetadata metadata = new MavenMetadata();
        try {
            parseMavenMetadataInfo(metadataLocation, metadata);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(String.format("Unable to load Maven meta-data from %s.", metadataLocation), e);
        }
        return metadata;
    }

    private void parseMavenMetadataInfo(final String metadataLocation, final MavenMetadata metadata) throws Exception {
        final ExternalResource resource = repository.getResource(metadataLocation);
        if (resource == null) {
            throw new ResourceNotFoundException(String.format("Maven meta-data not available: %s", metadataLocation));
        }
        try {
            parseMavenMetadataInto(resource, metadata);
        } finally {
            resource.close();
        }
    }

    private void parseMavenMetadataInto(Resource metadataResource, final MavenMetadata mavenMetadata) throws IOException, SAXException, ParserConfigurationException {
        LOGGER.debug("parsing maven-metadata: {}", metadataResource);
        InputStream metadataStream = metadataResource.openStream();
        XMLHelper.parse(metadataStream, null, new ContextualSAXHandler() {
            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if ("metadata/versioning/snapshot/timestamp".equals(getContext())) {
                    mavenMetadata.timestamp = getText();
                }
                if ("metadata/versioning/snapshot/buildNumber".equals(getContext())) {
                    mavenMetadata.buildNumber = getText();
                }
                if ("metadata/versioning/versions/version".equals(getContext())) {
                    mavenMetadata.versions.add(getText().trim());
                }
                super.endElement(uri, localName, qName);
            }
        }, null);
    }
}
