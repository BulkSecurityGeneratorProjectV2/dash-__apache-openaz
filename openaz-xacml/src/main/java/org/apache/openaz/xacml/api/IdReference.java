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

package org.apache.openaz.xacml.api;

/**
 * Defines the API for objects that implement XACML reference elements PolicyIdReference and
 * PolicySetIdReference.
 */
public interface IdReference {
    /**
     * Returns the {@link Identifier} representing the XACML PolicyId or PolicySetId that is referenced by
     * this <code>IdReference</code>.
     *
     * @return the <code>Identifier</code> representing the XACML PolicyId or PolicySetId that is referenced
     *         by this <code>IdReference</code>.
     */
    Identifier getId();

    /**
     * Returns the {@link Version} of the XACML Policy or PolicySet referenced by this
     * <code>IdReference</code>.
     *
     * @return the <code>Version</code> of the XACML Policy or PolicySet referenced by this
     *         <code>IdReference</code>.
     */
    Version getVersion();

    /**
     * {@inheritDoc} Implementations of the <code>IdReference</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>IdReference</code>s (<code>i1</code>
     * and <code>i2</code>) are equal if: {@code i1.getId().equals(i2.getId())} AND
     * {@code i1.getVersion().equals(i2.getVersion())}
     */
    @Override
    boolean equals(Object obj);
}
