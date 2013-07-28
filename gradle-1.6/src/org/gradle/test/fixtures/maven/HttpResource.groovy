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

package org.gradle.test.fixtures.maven

import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.server.http.HttpServer

abstract class HttpResource {

    protected HttpServer server

    public HttpResource(HttpServer server) {
        this.server = server
    }

    void expectGet() {
        server.expectGet(getPath(), file)
    }

    void expectGetBroken() {
        server.expectGetBroken(getPath())
    }

    void expectHead() {
        server.expectHead(getPath(), file)
    }

    void expectPut(PasswordCredentials credentials) {
        expectPut(200, credentials)
    }

    void expectPut(Integer statusCode = 200, PasswordCredentials credentials = null) {
        server.expectPut(getPath(), getFile(), statusCode, credentials)
    }

    abstract void expectGetMissing();

    abstract TestFile getFile();

    abstract protected String getPath();
}
