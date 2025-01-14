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

package org.apache.openaz.xacml.std.jaxp;

import org.apache.openaz.xacml.std.StdRequestAttributesReference;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesReferenceType;

/**
 * JaxpRequestAttributesReference extends {@link org.apache.openaz.xacml.std.StdRequestAttributesReference}
 * with methods for creation using JAXP elements.
 */
public class JaxpRequestAttributesReference extends StdRequestAttributesReference {

    protected JaxpRequestAttributesReference(String referenceIdIn) {
        super(referenceIdIn);
    }

    public static JaxpRequestAttributesReference newInstances(AttributesReferenceType attributesReferenceType) {
        if (attributesReferenceType == null) {
            throw new NullPointerException("Null AttributesReferenceType");
        } else if (attributesReferenceType.getReferenceId() == null) {
            throw new IllegalArgumentException("Null referenceId for AttributesReferenceType");
        }
        return new JaxpRequestAttributesReference(attributesReferenceType.getReferenceId().toString());
    }

}
