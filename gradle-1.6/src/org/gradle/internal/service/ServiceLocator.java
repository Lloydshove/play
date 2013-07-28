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
package org.gradle.internal.service;

import org.gradle.internal.Factory;
import org.gradle.internal.reflect.DirectInstantiator;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.internal.reflect.ObjectInstantiationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Uses the Jar service resource specification to locate service implementations.
 */
public class ServiceLocator extends AbstractServiceRegistry {
    private final ClassLoader classLoader;
    private final Map<Class<?>, Object> implementations = new HashMap<Class<?>, Object>();

    public ServiceLocator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public <T> T doGet(Class<T> serviceType) throws UnknownServiceException {
        synchronized (implementations) {
            T implementation = serviceType.cast(implementations.get(serviceType));
            if (implementation == null) {
                implementation = getFactory(serviceType).create();
                implementations.put(serviceType, implementation);
            }
            return implementation;
        }
    }

    public <T> List<T> getAll(Class<T> serviceType) {
        List<ServiceFactory<T>> factories = findFactoriesForServiceType(serviceType);
        ArrayList<T> services = new ArrayList<T>();
        for (ServiceFactory<T> factory : factories) {
            services.add(factory.create());
        }
        return services;
    }

    public <T> ServiceFactory<T> getFactory(final Class<T> serviceType) throws UnknownServiceException {
        ServiceFactory<T> factory = findFactory(serviceType);
        if (factory == null) {
            throw new UnknownServiceException(serviceType, String.format("Could not find meta-data resource 'META-INF/services/%s' for service '%s'.", serviceType.getName(), serviceType.getName()));
        }
        return factory;
    }

    /**
     * Locates a factory for a given service. Returns null when no service implementation is available.
     */
    public <T> ServiceFactory<T> findFactory(Class<T> serviceType) {
        List<ServiceFactory<T>> factories = findFactoriesForServiceType(serviceType);
        if (factories.isEmpty()) {
            return null;
        }
        return factories.get(0);
    }

    private <T> List<ServiceFactory<T>> findFactoriesForServiceType(Class<T> serviceType) {
        List<Class<? extends T>> implementationClasses;
        try {
            implementationClasses = findServiceImplementations(serviceType);
        } catch (ServiceLookupException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceLookupException(String.format("Could not determine implementation classes for service '%s'.", serviceType.getName()), e);
        }
        List<ServiceFactory<T>> factories = new ArrayList<ServiceFactory<T>>();
        for (Class<? extends T> implementationClass : implementationClasses) {
            factories.add(new ServiceFactory<T>(serviceType, implementationClass));
        }
        return factories;
    }

    private <T> List<Class<? extends T>> findServiceImplementations(Class<T> serviceType) throws IOException {
        String resourceName = "META-INF/services/" + serviceType.getName();
        Enumeration<URL> resources = classLoader.getResources(resourceName);
        Set<String> implementationClassNames = new HashSet<String>();
        List<Class<? extends T>> implementations = new ArrayList<Class<? extends T>>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String implementationClassName;
            try {
                implementationClassName = extractImplementationClassName(resource);
                if (implementationClassName == null) {
                    throw new RuntimeException(String.format("No implementation class for service '%s' specified.", serviceType.getName()));
                }
            } catch (Exception e) {
                throw new ServiceLookupException(String.format("Could not determine implementation class for service '%s' specified in resource '%s'.", serviceType.getName(), resource), e);
            }
            if (!implementationClassNames.add(implementationClassName)) {
                continue;
            }
            try {
                Class<?> implClass = classLoader.loadClass(implementationClassName);
                if (!serviceType.isAssignableFrom(implClass)) {
                    throw new RuntimeException(String.format("Implementation class '%s' is not assignable to service class '%s'.", implementationClassName, serviceType.getName()));
                }
                implementations.add(implClass.asSubclass(serviceType));
            } catch (Exception e) {
                throw new ServiceLookupException(String.format("Could not load implementation class '%s' for service '%s' specified in resource '%s'.", implementationClassName, serviceType.getName(), resource), e);
            }
        }
        return implementations;
    }

    private String extractImplementationClassName(URL resource) throws IOException {
        InputStream inputStream = resource.openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll("#.*", "").trim();
                if (line.length() > 0) {
                    return line;
                }
            }
        } finally {
            inputStream.close();
        }
        return null;
    }

    public static class ServiceFactory<T> implements Factory<T> {
        private final Class<T> serviceType;
        private final Class<? extends T> implementationClass;

        public ServiceFactory(Class<T> serviceType, Class<? extends T> implementationClass) {
            this.serviceType = serviceType;
            this.implementationClass = implementationClass;
        }

        public Class<? extends T> getImplementationClass() {
            return implementationClass;
        }

        public T create() {
            return newInstance();
        }

        public T newInstance(Object... params) {
            Instantiator instantiator = new DirectInstantiator();
            try {
                return instantiator.newInstance(implementationClass, params);
            } catch (ObjectInstantiationException t) {
                throw new RuntimeException(String.format("Could not create an implementation of service '%s'.", serviceType.getName()), t);
            }
        }
    }
}
