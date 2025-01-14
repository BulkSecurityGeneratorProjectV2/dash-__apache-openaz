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

package org.apache.openaz.xacml.std.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableResult;
import org.apache.openaz.xacml.std.StdResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMResult extends {@link org.apache.openaz.xacml.std.StdMutableResult} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMResult {
    private static final Log logger = LogFactory.getLog(DOMResult.class);

    protected DOMResult() {
    }

    /**
     * Creates a new <code>DOMResult</code> by parsing the given <code>Node</code> representing a XACML Result
     * element.
     *
     * @param nodeResult the <code>Node</code> representing the XACML Result element
     * @return a new <code>DOMResult</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static Result newInstance(Node nodeResult) throws DOMStructureException {
        Element elementResult = DOMUtil.getElement(nodeResult);
        boolean bLenient = DOMProperties.isLenient();

        StdMutableResult mutableResult = new StdMutableResult();

        NodeList children = elementResult.getChildNodes();
        int numChildren;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_DECISION.equals(childName)) {
                            Decision decision = Decision.get(child.getTextContent());
                            if (decision == null) {
                                if (!bLenient) {
                                    throw new DOMStructureException(child, "Unknown Decision \""
                                                                           + child.getTextContent()
                                                                           + "\" in \""
                                                                           + DOMUtil.getNodeLabel(child)
                                                                           + "\"");
                                }
                            } else {
                                mutableResult.setDecision(decision);
                            }
                        } else if (XACML3.ELEMENT_STATUS.equals(childName)) {
                            mutableResult.setStatus(DOMStatus.newInstance(child));
                        } else if (XACML3.ELEMENT_OBLIGATIONS.equals(childName)) {
                            mutableResult.addObligations(DOMObligation.newList(child));
                        } else if (XACML3.ELEMENT_ASSOCIATEDADVICE.equals(childName)) {
                            mutableResult.addAdvice(DOMAdvice.newList(child));
                        } else if (XACML3.ELEMENT_ATTRIBUTES.equals(childName)) {
                            mutableResult.addAttributeCategory(DOMAttributeCategory.newInstance(child));
                        } else if (XACML3.ELEMENT_POLICYIDENTIFIERLIST.equals(childName)) {
                            NodeList grandchildren = child.getChildNodes();
                            int numGrandchildren;
                            if (grandchildren != null && (numGrandchildren = grandchildren.getLength()) > 0) {
                                for (int j = 0; j < numGrandchildren; j++) {
                                    Node grandchild = grandchildren.item(j);
                                    if (DOMUtil.isElement(grandchild)) {
                                        String grandchildName = grandchild.getLocalName();
                                        if (DOMUtil.isInNamespace(grandchild, XACML3.XMLNS)) {
                                            if (XACML3.ELEMENT_POLICYIDREFERENCE.equals(grandchildName)) {
                                                mutableResult.addPolicyIdentifier(DOMIdReference
                                                    .newInstance(grandchild));
                                            } else if (XACML3.ELEMENT_POLICYSETIDREFERENCE
                                                .equals(grandchildName)) {
                                                mutableResult.addPolicySetIdentifier(DOMIdReference
                                                    .newInstance(grandchild));
                                            } else {
                                                if (!bLenient) {
                                                    throw DOMUtil.newUnexpectedElementException(grandchild,
                                                                                                child);
                                                }
                                            }
                                        } else {
                                            if (!bLenient) {
                                                throw DOMUtil
                                                    .newUnexpectedElementException(grandchild, child);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeResult);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeResult);
                        }
                    }
                }
            }
        }

        if (mutableResult.getDecision() == null && !bLenient) {
            throw DOMUtil.newMissingElementException(nodeResult, XACML3.XMLNS, XACML3.ELEMENT_DECISION);
        }
        return new StdResult(mutableResult);
    }

    public static boolean repair(Node nodeResult) throws DOMStructureException {
        Element elementResult = DOMUtil.getElement(nodeResult);
        boolean result = false;

        NodeList children = elementResult.getChildNodes();
        int numChildren;
        boolean sawDecision = false;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_DECISION.equals(childName)) {
                            Decision decision = Decision.get(child.getTextContent());
                            if (decision == null) {
                                throw new DOMStructureException(child, "Unknown Decision \""
                                                                       + child.getTextContent() + "\" in \""
                                                                       + DOMUtil.getNodeLabel(child) + "\"");
                            }
                            sawDecision = true;
                        } else if (XACML3.ELEMENT_STATUS.equals(childName)) {
                            result = DOMStatus.repair(child) || result;
                        } else if (XACML3.ELEMENT_OBLIGATIONS.equals(childName)) {
                            result = DOMObligation.repairList(child) || result;
                        } else if (XACML3.ELEMENT_ASSOCIATEDADVICE.equals(childName)) {
                            result = DOMAdvice.repairList(child) || result;
                        } else if (XACML3.ELEMENT_ATTRIBUTES.equals(childName)) {
                            result = DOMAttributeCategory.repair(child) || result;
                        } else if (XACML3.ELEMENT_POLICYIDENTIFIERLIST.equals(childName)) {
                            NodeList grandchildren = child.getChildNodes();
                            int numGrandchildren;
                            if (grandchildren != null && (numGrandchildren = grandchildren.getLength()) > 0) {
                                for (int j = 0; j < numGrandchildren; j++) {
                                    Node grandchild = grandchildren.item(j);
                                    if (DOMUtil.isElement(grandchild)) {
                                        String grandchildName = grandchild.getLocalName();
                                        if (DOMUtil.isInNamespace(grandchild, XACML3.XMLNS)) {
                                            if (XACML3.ELEMENT_POLICYIDREFERENCE.equals(grandchildName)) {
                                                result = DOMIdReference.repair(grandchild) || result;
                                            } else if (XACML3.ELEMENT_POLICYSETIDREFERENCE
                                                .equals(grandchildName)) {
                                                result = DOMIdReference.repair(grandchild) || result;
                                            } else {
                                                logger.warn("Unexpected element " + grandchild.getNodeName());
                                                child.removeChild(grandchild);
                                                result = true;
                                            }
                                        } else {
                                            logger.warn("Unexpected element " + grandchild.getNodeName());
                                            child.removeChild(grandchild);
                                            result = true;
                                        }
                                    }
                                }
                            }
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementResult.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementResult.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        if (!sawDecision) {
            throw DOMUtil.newMissingElementException(nodeResult, XACML3.XMLNS, XACML3.ELEMENT_DECISION);
        }
        return result;
    }
}
