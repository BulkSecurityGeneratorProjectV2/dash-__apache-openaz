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

import java.net.URI;
import java.util.Collection;

import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;

/**
 * PDPEngine is the interface that applications use to make policy queries against a XACML 3.0 policy engine.
 */
public interface PDPEngine {
    /**
     * Evaluates the given {@link org.apache.openaz.xacml.api.Request} using this <code>PDPEngine</code>'s
     * Policy Sets to determine if the given <code>Request</code> is allowed.
     *
     * @param pepRequest the <code>Request</code> to evaluate
     * @return a {@link org.apache.openaz.xacml.api.Response} indicating the decision
     */
    Response decide(Request pepRequest) throws PDPException;

    /**
     * Gets the <code>Collection</code> of <code>URI</code>s that represent the profiles supported by this
     * <code>PDPEngine</code>.
     *
     * @return an <code>Collection</code> over the <code>URI</code>s that represent the profiles supported by
     *         this <code>PDPEngine</code>.
     */
    Collection<URI> getProfiles();

    /**
     * Determines if this <code>PDPEngine</code> supports the given <code>URI</code> profile.
     *
     * @param uriProfile the <code>URI</code> representing the profile feature requested.
     * @return true if the profile is supported, else false
     */
    boolean hasProfile(URI uriProfile);
}
