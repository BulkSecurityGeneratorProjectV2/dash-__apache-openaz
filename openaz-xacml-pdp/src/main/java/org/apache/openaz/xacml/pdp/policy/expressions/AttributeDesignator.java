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

package org.apache.openaz.xacml.pdp.policy.expressions;

import java.util.Collection;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.StdStatusDetail;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;

/**
 * AttributeDesignator extends {@link org.apache.openaz.xacml.pdp.policy.expression.AttributeRetrievalBase}
 * to represent the XACML AttributeDesignator element.
 */
public class AttributeDesignator extends AttributeRetrievalBase {
    private Identifier attributeId;
    private String issuer;
    private PIPRequest pipRequestCached;
    private MissingAttributeDetail missingAttributeDetail;
    private StatusDetail statusDetail;

    protected PIPRequest getPIPRequest() {
        if (this.pipRequestCached == null) {
            this.pipRequestCached = new StdPIPRequest(this.getCategory(), this.getAttributeId(),
                                                      this.getDataTypeId(), this.getIssuer());
        }
        return this.pipRequestCached;
    }

    protected MissingAttributeDetail getMissingAttributeDetail() {
        if (this.missingAttributeDetail == null) {
            this.missingAttributeDetail = new StdMutableMissingAttributeDetail(this.getCategory(),
                                                                               this.getAttributeId(),
                                                                               this.getDataTypeId(),
                                                                               this.getIssuer());
        }
        return this.missingAttributeDetail;
    }

    protected StatusDetail getStatusDetail() {
        if (this.statusDetail == null) {
            this.statusDetail = new StdStatusDetail(this.getMissingAttributeDetail());
        }
        return this.statusDetail;
    }

    public AttributeDesignator(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AttributeDesignator(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AttributeDesignator() {
    }

    public Identifier getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(Identifier identifierAttributeId) {
        this.attributeId = identifierAttributeId;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuerIn) {
        this.issuer = issuerIn;
    }

    @Override
    protected boolean validateComponent() {
        if (!super.validateComponent()) {
            return false;
        } else if (this.getAttributeId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AttributeId");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the given <code>Attribute</code> has the same category, attribute id, and issuer as this
     * <code>AttributeDesignator</code>.
     *
     * @param attribute the <code>Attribute</code> to test
     * @return true if the <code>Attribute</code> matches, else false
     */
    protected boolean match(Attribute attribute) {
        if (!this.getCategory().equals(attribute.getCategory())) {
            return false;
        } else if (!this.getAttributeId().equals(attribute.getAttributeId())) {
            return false;
        } else if (this.getIssuer() != null && !this.getIssuer().equals(attribute.getIssuer())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determines if the given <code>AttributeValue</code> has the same data type id as this
     * <code>AttributeDesignator</code>.
     *
     * @param attributeValue the <code>AttributeValue</code> to test
     * @return true if the <code>AttributeValue</code> maches, else false
     */
    protected boolean match(AttributeValue<?> attributeValue) {
        if (!this.getDataTypeId().equals(attributeValue.getDataTypeId())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newInstance(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * Set up the PIPRequest representing this
         */
        PIPRequest pipRequest = this.getPIPRequest();
        assert pipRequest != null;

        /*
         * Query the evaluation context for results
         */
        PIPResponse pipResponse = null;
        try {
            pipResponse = evaluationContext.getAttributes(pipRequest);
        } catch (PIPException ex) {
            throw new EvaluationException("PIPException getting Attributes", ex);
        }
        assert pipResponse != null;

        /*
         * See if the request was successful
         */
        Status pipStatus = pipResponse.getStatus();
        if (pipStatus != null && !pipStatus.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newInstance(pipStatus);
        }

        /*
         * See if there were any results
         */
        Bag bagAttributeValues = new Bag();
        Collection<Attribute> listAttributes = pipResponse.getAttributes();
        for (Attribute attribute : listAttributes) {
            if (this.match(attribute)) {
                for (AttributeValue<?> attributeValue : attribute.getValues()) {
                    if (this.match(attributeValue)) {
                        bagAttributeValues.add(attributeValue);
                    }
                }
            }
        }
        if (this.getMustBePresent() && bagAttributeValues.size() == 0) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE,
                                                           "Missing required attribute",
                                                           new StdStatusDetail(this
                                                               .getMissingAttributeDetail())));
        } else {
            return ExpressionResult.newBag(bagAttributeValues);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getAttributeId()) != null) {
            stringBuilder.append(",attributeId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getIssuer()) != null) {
            stringBuilder.append(",issuer=");
            stringBuilder.append((String)objectToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
