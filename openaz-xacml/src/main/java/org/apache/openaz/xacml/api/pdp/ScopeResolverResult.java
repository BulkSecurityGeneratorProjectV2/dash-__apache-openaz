/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.openaz.xacml.api.pdp;

import java.util.Iterator;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Status;

/**
 * ScopeResolverResult is the interface for objects returned by the {@link ScopeResolver}'s
 * <code>resolveScope</code> method.
 */
public interface ScopeResolverResult {
    /*
     * Gets the {@link org.apache.openaz.xacml.api.Status} for the scope resolution request.
     * @return the <code>Status</code> of the scope resolution request
     */
    Status getStatus();

    /*
     * Gets an <code>Iterator</code> over {@link org.apache.openaz.xacml.api.Attribute}s resolved from a scope
     * resolution request.
     * @return an <code>Iterator</code> over the <code>Attribute</code>s resolved from a scope resolution
     * request.
     */
    Iterator<Attribute> getAttributes();
}
