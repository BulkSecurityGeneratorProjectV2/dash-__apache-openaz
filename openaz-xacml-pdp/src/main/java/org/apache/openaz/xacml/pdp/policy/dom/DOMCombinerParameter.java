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

package org.apache.openaz.xacml.pdp.policy.dom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.CombinerParameter;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMAttributeValue;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMCombinerParameter extends {@link org.apache.openaz.xacml.pdp.policy.CombinerParameter} with methods
 * for creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMCombinerParameter extends CombinerParameter {
    private static final Log logger = LogFactory.getLog(DOMCombinerParameter.class);

    protected DOMCombinerParameter() {

    }

    /**
     * Creates a new <code>CombinerParameter</code> by parsing the given <code>Node</code> representing a
     * XACML CombinerParameter element.
     *
     * @param nodeCombinerParameter the <code>Node</code> representing the XACML CombinerParameter element
     * @return a new <code>CombinerParameter</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static CombinerParameter newInstance(Node nodeCombinerParameter) throws DOMStructureException {
        Element elementCombinerParameter = DOMUtil.getElement(nodeCombinerParameter);
        boolean bLenient = DOMProperties.isLenient();

        DOMCombinerParameter combinerParameter = new DOMCombinerParameter();

        try {
            NodeList children = elementCombinerParameter.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            if (combinerParameter.getAttributeValue() != null && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, elementCombinerParameter);
                            } else {
                                combinerParameter.setAttributeValue(DOMAttributeValue
                                    .newInstance(child, null));
                            }
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, elementCombinerParameter);
                        }
                    }
                }
            }

            if (combinerParameter.getAttributeValue() == null && !bLenient) {
                throw DOMUtil.newMissingElementException(elementCombinerParameter, XACML3.XMLNS,
                                                         XACML3.ELEMENT_ATTRIBUTEVALUE);
            }
            combinerParameter.setName(DOMUtil.getStringAttribute(elementCombinerParameter,
                                                                 XACML3.ATTRIBUTE_PARAMETERNAME, !bLenient));
        } catch (DOMStructureException ex) {
            combinerParameter.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return combinerParameter;
    }

    public static boolean repair(Node nodeCombinerParameter) throws DOMStructureException {
        Element elementCombinerParameter = DOMUtil.getElement(nodeCombinerParameter);
        boolean result = false;

        NodeList children = elementCombinerParameter.getChildNodes();
        int numChildren;
        boolean sawAttributeValue = false;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                        if (sawAttributeValue) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementCombinerParameter.removeChild(child);
                            result = true;
                        } else {
                            result = DOMAttributeValue.repair(child) || result;
                            sawAttributeValue = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementCombinerParameter.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        if (!sawAttributeValue) {
            throw DOMUtil.newMissingElementException(elementCombinerParameter, XACML3.XMLNS,
                                                     XACML3.ELEMENT_ATTRIBUTEVALUE);
        }

        result = DOMUtil.repairStringAttribute(elementCombinerParameter, XACML3.ATTRIBUTE_PARAMETERNAME,
                                               "parameter", logger) || result;

        return result;
    }

    /**
     * Creates a <code>List</code> of <code>CombinerParameter</code>s by parsing the given <code>Node</code>
     * representing a XACML CombinerParameters element.
     *
     * @param nodeCombinerParameters the <code>Node</code> representing the XACML CombinerParameters element
     * @return a <code>List</code> of <code>CombinerParameter</code>s parsed from the given <code>Node</code>.
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static List<CombinerParameter> newList(Node nodeCombinerParameters) throws DOMStructureException {
        Element elementCombinerParameters = DOMUtil.getElement(nodeCombinerParameters);
        boolean bLenient = DOMProperties.isLenient();

        List<CombinerParameter> listCombinerParameters = new ArrayList<CombinerParameter>();

        NodeList children = elementCombinerParameters.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_COMBINERPARAMETER.equals(child.getLocalName())) {
                        listCombinerParameters.add(DOMCombinerParameter.newInstance(child));
                    } else if (!bLenient) {
                        throw DOMUtil.newUnexpectedElementException(child, elementCombinerParameters);
                    }
                }
            }
        }
        return listCombinerParameters;
    }

    public static boolean repairList(Node nodeCombinerParameters) throws DOMStructureException {
        Element elementCombinerParameters = DOMUtil.getElement(nodeCombinerParameters);
        boolean result = false;

        NodeList children = elementCombinerParameters.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_COMBINERPARAMETER.equals(child.getLocalName())) {
                        result = DOMCombinerParameter.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementCombinerParameters.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}
