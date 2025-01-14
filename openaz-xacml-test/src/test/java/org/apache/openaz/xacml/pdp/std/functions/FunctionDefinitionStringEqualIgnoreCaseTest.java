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

package org.apache.openaz.xacml.pdp.std.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.pdp.policy.FunctionArgumentAttributeValue;
import org.apache.openaz.xacml.pdp.std.StdFunctions;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionEquality;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.junit.Test;

/**
 * Only one function to test here. Code copy/pasted from FunctionDefinitionEqualityTest TO RUN - use jUnit In
 * Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test In the first
 * implementation of XACML we had separate files for each XACML Function. This release combines multiple
 * Functions in fewer files to minimize code duplication. This file supports the following XACML codes:
 * string-equal-ignore-case
 */
public class FunctionDefinitionStringEqualIgnoreCaseTest {

    /*
     * variables useful in the following tests
     */
    List<FunctionArgument> arguments = new ArrayList<FunctionArgument>();

    FunctionArgumentAttributeValue stringAttr1 = null;
    FunctionArgumentAttributeValue stringAttr2 = null;
    FunctionArgumentAttributeValue stringAttr3 = null;
    FunctionArgumentAttributeValue stringAttr4 = null;

    FunctionArgumentAttributeValue intAttr1 = null;

    public FunctionDefinitionStringEqualIgnoreCaseTest() {
        try {
            stringAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr3 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("ABC"));
            stringAttr4 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("def"));
            intAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }
    }

    /**
     * String match even when Case is different
     */
    @Test
    public void testFunctionDefinitionStringEqualIgnoreCase() {

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_STRING_EQUAL_IGNORE_CASE;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_EQUAL_IGNORE_CASE, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check "abc" with "abc"
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check "abc" with "ABC" (should be same)
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr3);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

        // TODO - null in either first or 2nd arg => NullPointerException
    }

}
