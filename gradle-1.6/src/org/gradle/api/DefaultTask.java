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

package org.gradle.api;

import groovy.lang.GroovyObject;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.internal.NoConventionMapping;

/**
 * {@code DefaultTask} is the standard {@link Task} implementation. You can extend this to implement your own task types.
 *
 * @author Hans Dockter
 */
@NoConventionMapping
public class DefaultTask extends AbstractTask {
    public DefaultTask() {
        if (this instanceof GroovyObject) {
            GroovyObject groovyObject = (GroovyObject) this;
            if (groovyObject.getMetaClass() == null) {
                groovyObject.setMetaClass(InvokerHelper.getMetaClass(getClass()));
            }
        }
    }
}
