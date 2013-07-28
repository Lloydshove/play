/*
 * Copyright 2007 the original author or authors.
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

package org.gradle.wrapper;

import org.gradle.cli.CommandLineParser;
import org.gradle.cli.SystemPropertiesCommandLineConverter;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Hans Dockter
 */
public class GradleWrapperMain {
    public static final String DEFAULT_GRADLE_USER_HOME = System.getProperty("user.home") + "/.gradle";
    public static final String GRADLE_USER_HOME_PROPERTY_KEY = "gradle.user.home";
    public static final String GRADLE_USER_HOME_ENV_KEY = "GRADLE_USER_HOME";

    public static void main(String[] args) throws Exception {
        File wrapperJar = wrapperJar();
        File propertiesFile = wrapperProperties(wrapperJar);
        File rootDir = rootDir(wrapperJar);

        Properties systemProperties = System.getProperties();
        systemProperties.putAll(parseSystemPropertiesFromArgs(args));

        addSystemProperties(rootDir);

        WrapperExecutor wrapperExecutor = WrapperExecutor.forWrapperPropertiesFile(propertiesFile, System.out);
        wrapperExecutor.execute(
                args,
                new Install(new Download("gradlew", wrapperVersion()), new PathAssembler(gradleUserHome())),
                new BootstrapMainStarter());
    }

    private static Map<String, String> parseSystemPropertiesFromArgs(String[] args) {
        SystemPropertiesCommandLineConverter converter = new SystemPropertiesCommandLineConverter();
        CommandLineParser commandLineParser = new CommandLineParser();
        converter.configure(commandLineParser);
        commandLineParser.allowUnknownOptions();
        return converter.convert(commandLineParser.parse(args));
    }

    private static void addSystemProperties(File rootDir) {
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(gradleUserHome(), "gradle.properties")));
        System.getProperties().putAll(SystemPropertiesHandler.getSystemProperties(new File(rootDir, "gradle.properties")));
    }

    private static File rootDir(File wrapperJar) {
        return wrapperJar.getParentFile().getParentFile().getParentFile();
    }

    private static File wrapperProperties(File wrapperJar) {
        return new File(wrapperJar.getParent(), wrapperJar.getName().replaceFirst("\\.jar$", ".properties"));
    }

    private static File wrapperJar() {
        URI location;
        try {
            location = GradleWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (!location.getScheme().equals("file")) {
            throw new RuntimeException(String.format("Cannot determine classpath for wrapper Jar from codebase '%s'.", location));
        }
        return new File(location.getPath());
    }

    static String wrapperVersion() {
        try {
            InputStream resourceAsStream = GradleWrapperMain.class.getResourceAsStream("/build-receipt.properties");
            if (resourceAsStream == null) {
                throw new RuntimeException("No build receipt resource found.");
            }
            Properties buildReceipt = new Properties();
            try {
                buildReceipt.load(resourceAsStream);
                String versionNumber = buildReceipt.getProperty("versionNumber");
                if (versionNumber == null) {
                    throw new RuntimeException("No version number specified in build receipt resource.");
                }
                return versionNumber;
            } finally {
                resourceAsStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not determine wrapper version.", e);
        }
    }

    private static File gradleUserHome() {
        String gradleUserHome = System.getProperty(GRADLE_USER_HOME_PROPERTY_KEY);
        if (gradleUserHome != null) {
            return new File(gradleUserHome);
        } else if ((gradleUserHome = System.getenv(GRADLE_USER_HOME_ENV_KEY)) != null) {
            return new File(gradleUserHome);
        } else {
            return new File(DEFAULT_GRADLE_USER_HOME);
        }
    }
}
