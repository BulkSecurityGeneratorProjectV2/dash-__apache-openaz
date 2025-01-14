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

package org.apache.openaz.xacml.std.pip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdAttribute;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.pip.PIPResponse} interface.
 */
public class StdPIPResponse extends Wrapper<PIPResponse> implements PIPResponse {
    public static final PIPResponse PIP_RESPONSE_EMPTY = new StdPIPResponse(StdStatus.STATUS_OK);

    /**
     * Creates a new immutable <code>StdPIPResponse</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.pip.PIPResponse}.
     *
     * @param wrappedObjectIn the <code>PIPResponse</code> to wrap.
     */
    public StdPIPResponse(PIPResponse wrappedObjectIn) {
        super(wrappedObjectIn);
    }

    /**
     * Creates a new <code>StdPIPResponse</code> with the given {@link org.apache.openaz.xacml.api.Status}.
     *
     * @param status the <code>Status</code> for the new <code>StdPIPResponse</code>
     */
    public StdPIPResponse(Status status) {
        this(new StdMutablePIPResponse(status));
    }

    /**
     * Creates a new <code>StdPIPResponse</code> with an OK {@link org.apache.openaz.xacml.api.Status} and a
     * single {@link org.apache.openaz.xacml.api.Attribute}.
     *
     * @param attribute the <code>Attribute</code> for the new <code>StdPIPResponse</code>.
     */
    public StdPIPResponse(Attribute attribute) {
        this(new StdMutablePIPResponse(attribute));
    }

    /**
     * Creates a new <code>StdPIPResponse</code> with an OK {@link org.apache.openaz.xacml.api.Status} and a
     * copy of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s.
     *
     * @param attributes the <code>Attribute</code>s for the new <code>StdPIPResponse</code>.
     */
    public StdPIPResponse(Collection<Attribute> attributes) {
        this(new StdMutablePIPResponse(attributes));
    }

    @Override
    public Status getStatus() {
        return this.getWrappedObject().getStatus();
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return this.getWrappedObject().getAttributes();
    }

    /**
     * Determines if the given {@link org.apache.openaz.xacml.api.pip.PIPRequest} matches the given
     * {@link org.apache.openaz.xacml.api.Attribute} by comparing the category, attribute id, and if not null
     * in the <code>PIPRequest</code>, the issuer.
     *
     * @param pipRequest the <code>PIPRequest</code> to compare against
     * @param attribute the <code>Attribute</code> to compare to
     * @return true if the <code>Attribute</code> matches the <code>PIPRequest</code> else false
     */
    public static boolean matches(PIPRequest pipRequest, Attribute attribute) {
        if (!pipRequest.getCategory().equals(attribute.getCategory())) {
            return false;
        }
        if (!pipRequest.getAttributeId().equals(attribute.getAttributeId())) {
            return false;
        }
        if (pipRequest.getIssuer() != null && !pipRequest.getIssuer().equals(attribute.getIssuer())) {
            return false;
        }
        return true;
    }

    /**
     * Gets the subset of the {@link org.apache.openaz.xacml.api.AttributeValue}s from the given
     * <code>Collection</code> whose data type matches the data type in the given
     * {@link org.apache.openaz.xacml.api.pip.PIPRequest}.
     *
     * @param pipRequest the <code>PIPRequest</code> to compare against
     * @param listAttributeValues the <code>Collection</code> of <code>AttributeValue</code>s to select from
     * @return a <code>Collection</code> of matching <code>AttributeValue</code>s or null if there are no
     *         matches
     */
    public static Collection<AttributeValue<?>> matchingValues(PIPRequest pipRequest,
                                                               Collection<AttributeValue<?>> listAttributeValues) {
        if (listAttributeValues.size() == 0) {
            return listAttributeValues;
        }

        /*
         * See if all of the values match the requested data type
         */
        boolean allMatch = true;
        for (Iterator<AttributeValue<?>> iterAttributeValues = listAttributeValues.iterator(); allMatch
                                                                                               && iterAttributeValues
                                                                                                   .hasNext();) {
            AttributeValue<?> attributeValue = iterAttributeValues.next();
            allMatch = attributeValue.getDataTypeId().equals(pipRequest.getDataTypeId());
        }
        if (allMatch) {
            return listAttributeValues;
        }

        /*
         * If there was only one, return a null list
         */
        if (listAttributeValues.size() == 1) {
            return null;
        }

        List<AttributeValue<?>> listAttributeValuesMatching = new ArrayList<AttributeValue<?>>();
        for (Iterator<AttributeValue<?>> iterAttributeValues = listAttributeValues.iterator(); iterAttributeValues
            .hasNext();) {
            AttributeValue<?> attributeValue = iterAttributeValues.next();
            if (attributeValue.getDataTypeId().equals(pipRequest.getDataTypeId())) {
                listAttributeValuesMatching.add(attributeValue);
            }
        }
        if (listAttributeValuesMatching.size() == 0) {
            return null;
        } else {
            return listAttributeValuesMatching;
        }
    }

    /**
     * Returns a {@link org.apache.openaz.xacml.api.pip.PIPResponse} that only contains the
     * {@link org.apache.openaz.xacml.api.Attribute}s that match the given
     * {@link org.apache.openaz.xacml.api.pip.PIPRequest} with
     * {@link org.apache.openaz.xacml.api.AttributeValue}s that match the requested data type.
     *
     * @param pipRequest
     * @param pipResponse
     * @return
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    public static PIPResponse getMatchingResponse(PIPRequest pipRequest, PIPResponse pipResponse)
        throws PIPException {
        /*
         * Error responses should not contain any attributes, so just return the error response as is.
         * Likewise for empty responses
         */
        if (!pipResponse.getStatus().isOk() || pipResponse.getAttributes().size() == 0) {
            return pipResponse;
        }

        /*
         * See if the response is simple
         */
        if (pipResponse.isSimple()) {
            /*
             * Get the one Attribute and verify that it matches the request, and that the data type of its
             * values matches the request
             */
            Attribute attributeResponse = pipResponse.getAttributes().iterator().next();
            if (matches(pipRequest, attributeResponse)) {
                Collection<AttributeValue<?>> attributeValues = attributeResponse.getValues();
                if (attributeValues == null || attributeValues.size() == 0) {
                    return pipResponse;
                } else {
                    AttributeValue<?> attributeValueResponse = attributeResponse.getValues().iterator()
                        .next();
                    if (attributeValueResponse.getDataTypeId().equals(pipRequest.getDataTypeId())) {
                        return pipResponse;
                    } else {
                        return PIP_RESPONSE_EMPTY;
                    }
                }
            } else {
                return PIP_RESPONSE_EMPTY;
            }
        } else {
            /*
             * Iterate over the Attributes and just get the ones that match and collapse any matching
             * AttributeValues
             */
            StdMutableAttribute attributeMatch = null;
            Iterator<Attribute> iterAttributesResponse = pipResponse.getAttributes().iterator();
            while (iterAttributesResponse.hasNext()) {
                Attribute attributeResponse = iterAttributesResponse.next();
                if (matches(pipRequest, attributeResponse)) {
                    /*
                     * Get subset of the matching attribute values
                     */
                    Collection<AttributeValue<?>> listAttributeValuesMatch = matchingValues(pipRequest,
                                                                                            attributeResponse
                                                                                                .getValues());
                    if (listAttributeValuesMatch != null && listAttributeValuesMatch.size() > 0) {
                        if (attributeMatch == null) {
                            attributeMatch = new StdMutableAttribute(pipRequest.getCategory(),
                                                                     pipRequest.getAttributeId(),
                                                                     listAttributeValuesMatch,
                                                                     pipRequest.getIssuer(), false);
                        } else {
                            attributeMatch.addValues(listAttributeValuesMatch);
                        }
                    }
                }
            }
            if (attributeMatch == null) {
                return PIP_RESPONSE_EMPTY;
            } else {
                return new StdPIPResponse(attributeMatch);
            }
        }
    }

    /**
     * Splits an Attribute that may contain multiple data types into a list of Attributes, each with only one
     * data type
     * 
     * @param attribute
     * @return
     */
    private static List<Attribute> simplifyAttribute(Attribute attribute) {
        List<Attribute> listAttributes = new ArrayList<Attribute>();
        if (attribute.getValues() == null || attribute.getValues().size() <= 1) {
            listAttributes.add(attribute);
        } else {
            for (AttributeValue<?> attributeValue : attribute.getValues()) {
                listAttributes.add(new StdAttribute(attribute.getCategory(), attribute.getAttributeId(),
                                                    attributeValue, attribute.getIssuer(), attribute
                                                        .getIncludeInResults()));
            }
        }
        return listAttributes;
    }

    /**
     * Takes a list of simple Attributes and collapses attributes with the same category, id, value data type,
     * and issuer into a single Attribute and returns the list of collapsed Attributes.
     *
     * @param listAttributes
     * @return
     */
    private static Collection<? extends Attribute> collapseAttributes(List<Attribute> listAttributes) {
        if (listAttributes.size() <= 0) {
            return listAttributes;
        }
        Map<PIPRequest, StdMutableAttribute> map = new HashMap<PIPRequest, StdMutableAttribute>();
        for (Attribute attribute : listAttributes) {
            PIPRequest pipRequest = new StdPIPRequest(attribute);
            if (map.containsKey(pipRequest)) {
                StdMutableAttribute mutableAttribute = map.get(pipRequest);
                mutableAttribute.addValues(attribute.getValues());
            } else {
                map.put(pipRequest, new StdMutableAttribute(attribute));
            }
        }

        if (map.size() == 0) {
            return null;
        } else {
            return map.values();
        }
    }

    /**
     * Takes a {@link org.apache.openaz.xacml.api.pip.PIPResponse} that may contain
     * {@link org.apache.openaz.xacml.api.Attribute}s, with multiple identifiers, each of which may contain
     * multiple {@link org.apache.openaz.xacml.api.AttributeValue}s with different data types and creates a
     * collection of simple <code>PIPResponse</code>s that contain a single <code>Attribute</code> with
     * <code>AttributeValue</code>s of one data type.
     *
     * @param pipResponse the <code>PIPResponse</code> to split
     * @return a <code>Collection</code> of simple <code>PIPResponse</code>s
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error splitting the response
     */
    public static Map<PIPRequest, PIPResponse> splitResponse(PIPResponse pipResponse) throws PIPException {
        Map<PIPRequest, PIPResponse> map = new HashMap<PIPRequest, PIPResponse>();
        if (!pipResponse.getStatus().isOk() || pipResponse.isSimple()) {
            map.put(new StdPIPRequest(pipResponse.getAttributes().iterator().next()), pipResponse);
        } else {
            List<Attribute> listAllAttributesSimple = new ArrayList<Attribute>();
            for (Iterator<Attribute> iterAttributes = pipResponse.getAttributes().iterator(); iterAttributes
                .hasNext();) {
                Attribute attribute = iterAttributes.next();
                List<Attribute> listAttributesSplit = simplifyAttribute(attribute);
                if (listAttributesSplit != null && listAttributesSplit.size() > 0) {
                    listAllAttributesSimple.addAll(listAttributesSplit);
                }
            }
            if (listAllAttributesSimple.size() > 0) {
                Collection<? extends Attribute> listAttributesCollapsed = collapseAttributes(listAllAttributesSimple);
                for (Attribute attributeCollapsed : listAttributesCollapsed) {
                    map.put(new StdPIPRequest(attributeCollapsed), new StdPIPResponse(attributeCollapsed));
                }
            }
        }
        return map;
    }

    @Override
    public boolean isSimple() {
        return this.getWrappedObject().isSimple();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        boolean needsComma = false;

        if (this.getStatus() != null) {
            stringBuilder.append(this.getStatus().toString());
            needsComma = true;
        }
        if (!this.getAttributes().isEmpty()) {
            if (needsComma) {
                stringBuilder.append(",");
            }
            needsComma = false;
        }

        stringBuilder.append("[");
        for (Attribute attribute : this.getAttributes()) {
            if (needsComma) {
                stringBuilder.append(",");
            }
            stringBuilder.append(attribute.toString());
            needsComma = true;
        }
        stringBuilder.append("]");

        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
