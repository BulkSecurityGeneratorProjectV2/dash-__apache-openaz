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

package org.apache.openaz.xacml.api.pap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.openaz.xacml.std.pap.StdPDPPolicy;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * The following allows us to use Jackson to convert sub-types of this type into JSON and back to objects.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "PDPPolicyType")
@JsonSubTypes({
    @Type(value = StdPDPPolicy.class, name = "StdPDPPolicy")
})
public interface PDPPolicy {

    String getId();

    String getName();

    String getPolicyId();

    String getDescription();

    String getVersion();

    int[] getVersionInts();

    boolean isRoot();

    boolean isValid();

    InputStream getStream() throws PAPException, IOException;

    URI getLocation() throws PAPException, IOException;
}
