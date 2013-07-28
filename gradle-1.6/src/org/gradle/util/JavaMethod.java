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
package org.gradle.util;

import org.gradle.api.GradleException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class JavaMethod<T, R> {
    private final Method method;
    private final Class<R> returnType;

    private JavaMethod(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        this.returnType = returnType;
        method = findMethod(target, name, paramTypes);
        method.setAccessible(true);
    }

    private JavaMethod(Class<T> target, Class<R> returnType, Method method) {
        this.returnType = returnType;
        this.method = method;
        method.setAccessible(true);
    }

    private Method findMethod(Class target, String name, Class<?>[] paramTypes) {
        for (Method method : target.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), paramTypes)) {
                return method;
            }
        }
        throw new GradleException(String.format("Could not find method %s(%s) on %s", name, Arrays.toString(paramTypes),
                target));
    }

    public R invoke(T target, Object... args) {
        try {
            return returnType.cast(method.invoke(target, args));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new GradleException(String.format("Could not call %s.%s() on %s", method.getDeclaringClass().getSimpleName(), method.getName(), target), cause);
        } catch (Exception e) {
            throw new GradleException(String.format("Could not call %s.%s() on %s", method.getDeclaringClass().getSimpleName(), method.getName(), target), e);
        }
    }

    public static <T, R> JavaMethod<T, R> create(Class<T> target, Class<R> returnType, String name, Class<?>... paramTypes) {
        return new JavaMethod<T, R>(target, returnType, name, paramTypes);
    }

    public static <T, R> JavaMethod<T, R> create(Class<T> target, Class<R> returnType, Method method) {
        return new JavaMethod<T, R>(target, returnType, method);
    }
}
