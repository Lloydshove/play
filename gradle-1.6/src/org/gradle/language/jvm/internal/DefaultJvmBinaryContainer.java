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
package org.gradle.language.jvm.internal;

import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer;
import org.gradle.language.jvm.ClassDirectoryBinary;
import org.gradle.language.jvm.JvmBinaryContainer;
import org.gradle.internal.reflect.Instantiator;

public class DefaultJvmBinaryContainer extends DefaultPolymorphicDomainObjectContainer<ClassDirectoryBinary> implements JvmBinaryContainer {
    public DefaultJvmBinaryContainer(Instantiator instantiator) {
        super(ClassDirectoryBinary.class, instantiator);
    }

    public String getName() {
        return "jvm";
    }

    @Override
    protected ClassDirectoryBinary doCreate(String name) {
        return getInstantiator().newInstance(DefaultClassDirectoryBinary.class, name);
    }
}
