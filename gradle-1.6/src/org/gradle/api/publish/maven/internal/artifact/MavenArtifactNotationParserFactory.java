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

package org.gradle.api.publish.maven.internal.artifact;

import org.apache.commons.lang.StringUtils;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.notations.NotationParserBuilder;
import org.gradle.api.internal.notations.api.NotationParser;
import org.gradle.api.internal.notations.api.UnsupportedNotationException;
import org.gradle.api.internal.notations.parsers.MapKey;
import org.gradle.api.internal.notations.parsers.MapNotationParser;
import org.gradle.api.internal.notations.parsers.TypedNotationParser;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.internal.Factory;
import org.gradle.internal.reflect.Instantiator;

import java.io.File;
import java.util.Collection;

public class MavenArtifactNotationParserFactory implements Factory<NotationParser<MavenArtifact>> {
    private final Instantiator instantiator;
    private final FileResolver fileResolver;

    public MavenArtifactNotationParserFactory(Instantiator instantiator, FileResolver fileResolver) {
        this.instantiator = instantiator;
        this.fileResolver = fileResolver;
    }

    public NotationParser<MavenArtifact> create() {
        FileNotationParser fileNotationParser = new FileNotationParser(fileResolver);
        ArchiveTaskNotationParser archiveTaskNotationParser = new ArchiveTaskNotationParser();
        PublishArtifactNotationParser publishArtifactNotationParser = new PublishArtifactNotationParser();

        NotationParser<MavenArtifact> sourceNotationParser = new NotationParserBuilder<MavenArtifact>()
                .resultingType(MavenArtifact.class)
                .parser(archiveTaskNotationParser)
                .parser(publishArtifactNotationParser)
                .parser(fileNotationParser)
                .toComposite();

        MavenArtifactMapNotationParser mavenArtifactMapNotationParser = new MavenArtifactMapNotationParser(sourceNotationParser);

        NotationParserBuilder<MavenArtifact> parserBuilder = new NotationParserBuilder<MavenArtifact>()
                .resultingType(MavenArtifact.class)
                .parser(archiveTaskNotationParser)
                .parser(publishArtifactNotationParser)
                .parser(mavenArtifactMapNotationParser)
                .parser(fileNotationParser);

        return parserBuilder.toComposite();
    }

    private class ArchiveTaskNotationParser extends TypedNotationParser<AbstractArchiveTask, MavenArtifact> {
        private ArchiveTaskNotationParser() {
            super(AbstractArchiveTask.class);
        }

        @Override
        protected MavenArtifact parseType(AbstractArchiveTask archiveTask) {
            DefaultMavenArtifact artifact = instantiator.newInstance(
                    DefaultMavenArtifact.class,
                    archiveTask.getArchivePath(), archiveTask.getExtension(), archiveTask.getClassifier());
            artifact.builtBy(archiveTask);
            return artifact;
        }
    }

    private class PublishArtifactNotationParser extends TypedNotationParser<PublishArtifact, MavenArtifact> {
        private PublishArtifactNotationParser() {
            super(PublishArtifact.class);
        }

        @Override
        protected MavenArtifact parseType(PublishArtifact publishArtifact) {
            DefaultMavenArtifact artifact = instantiator.newInstance(
                    DefaultMavenArtifact.class,
                    publishArtifact.getFile(), publishArtifact.getExtension(), publishArtifact.getClassifier());
            artifact.builtBy(publishArtifact.getBuildDependencies());
            return artifact;
        }
    }

    private class FileNotationParser implements NotationParser<MavenArtifact> {
        private final NotationParser<File> fileResolverNotationParser;

        private FileNotationParser(FileResolver fileResolver) {
            this.fileResolverNotationParser = fileResolver.asNotationParser();
        }

        public MavenArtifact parseNotation(Object notation) throws UnsupportedNotationException {
            File file = fileResolverNotationParser.parseNotation(notation);
            return parseFile(file);
        }

        protected MavenArtifact parseFile(File file) {
            String extension = StringUtils.substringAfterLast(file.getName(), ".");
            return instantiator.newInstance(DefaultMavenArtifact.class, file, extension, null);
        }

        public void describe(Collection<String> candidateFormats) {
            fileResolverNotationParser.describe(candidateFormats);
        }
    }

    private class MavenArtifactMapNotationParser extends MapNotationParser<MavenArtifact> {
        private final NotationParser<MavenArtifact> sourceNotationParser;

        private MavenArtifactMapNotationParser(NotationParser<MavenArtifact> sourceNotationParser) {
            this.sourceNotationParser = sourceNotationParser;
        }

        protected MavenArtifact parseMap(@MapKey("source") Object source) {
            return sourceNotationParser.parseNotation(source);
        }

        @Override
        public void describe(Collection<String> candidateFormats) {
            candidateFormats.add("Maps containing a 'source' entry, e.g. [source: '/path/to/file', extension: 'zip'].");
        }
    }
}
