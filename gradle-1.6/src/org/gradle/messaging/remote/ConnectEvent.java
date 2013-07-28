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

package org.gradle.messaging.remote;

public class ConnectEvent<T> implements Addressable {
    private final T connection;
    private final Address localAddress;
    private final Address remoteAddress;

    public ConnectEvent(T connection, Address localAddress, Address remoteAddress) {
        this.connection = connection;
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public T getConnection() {
        return connection;
    }

    public Address getLocalAddress() {
        return localAddress;
    }

    public Address getRemoteAddress() {
        return remoteAddress;
    }
}
