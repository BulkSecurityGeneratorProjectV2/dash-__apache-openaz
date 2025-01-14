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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinition;
import org.apache.openaz.xacml.pdp.std.StdFunctions;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionBag;
import org.junit.Test;

/**
 * Test functions in the abstract FunctionDefinitionSimpleTest class. Functions are tested by creating
 * instances of other classes that should have appropriate properties to verify all variations of the
 * responses expected. Note: we do not test getDataTypeId() because all it does is get the String out of the
 * Identity object and we assume that the Data Type Identity objects are tested enough in everything else that
 * any errors in them will be found and fixed quickly. TO RUN - use jUnit In Eclipse select this file or the
 * enclosing directory, right-click and select Run As/JUnit Test
 */
public class FunctionDefinitionBaseTest {
    /**
     * getId() is pretty trivial, so verifying one should be enough to check that the mechanism is working ok
     */
    @Test
    public void testGetId() {
        FunctionDefinition fd = StdFunctions.FD_STRING_EQUAL;
        Identifier id = fd.getId();
        assertTrue(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue().equals(id.stringValue()));
    }

    /**
     * check an instance of every result type that we can deal with
     */
    @Test
    public void testGetDataType() {

        // ?? Need functions that return each of these data types except for Boolean which is returned by any
        // of the EQUAL functions
        FunctionDefinition fdstring = StdFunctions.FD_STRING_NORMALIZE_SPACE;
        assertEquals(XACML3.ID_DATATYPE_STRING, fdstring.getDataTypeId());

        FunctionDefinition fdboolean = StdFunctions.FD_STRING_EQUAL;
        assertEquals(XACML3.ID_DATATYPE_BOOLEAN, fdboolean.getDataTypeId());

        FunctionDefinition fdinteger = StdFunctions.FD_INTEGER_ADD;
        assertEquals(XACML3.ID_DATATYPE_INTEGER, fdinteger.getDataTypeId());

        FunctionDefinition fddouble = StdFunctions.FD_DOUBLE_ADD;
        assertEquals(XACML3.ID_DATATYPE_DOUBLE, fddouble.getDataTypeId());

        FunctionDefinition fddate = StdFunctions.FD_DATE_BAG;
        assertEquals(XACML3.ID_DATATYPE_DATE, fddate.getDataTypeId());

        FunctionDefinition fdtime = StdFunctions.FD_TIME_BAG;
        assertEquals(XACML3.ID_DATATYPE_TIME, fdtime.getDataTypeId());

        FunctionDefinition fddateTime = StdFunctions.FD_DATETIME_BAG;
        assertEquals(XACML3.ID_DATATYPE_DATETIME, fddateTime.getDataTypeId());

        FunctionDefinition fddayTimeDuration = StdFunctions.FD_DAYTIMEDURATION_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_DAYTIMEDURATION, fddayTimeDuration.getDataTypeId());

        FunctionDefinition fdyearMonthDuration = StdFunctions.FD_YEARMONTHDURATION_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_YEARMONTHDURATION, fdyearMonthDuration.getDataTypeId());

        FunctionDefinition fdanyURI = StdFunctions.FD_ANYURI_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_ANYURI, fdanyURI.getDataTypeId());

        FunctionDefinition fdhexBinary = StdFunctions.FD_HEXBINARY_UNION;
        assertEquals(XACML3.ID_DATATYPE_HEXBINARY, fdhexBinary.getDataTypeId());

        FunctionDefinition fdbase64Binary = StdFunctions.FD_BASE64BINARY_UNION;
        assertEquals(XACML3.ID_DATATYPE_BASE64BINARY, fdbase64Binary.getDataTypeId());

        FunctionDefinition fdrfc822Name = StdFunctions.FD_RFC822NAME_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_RFC822NAME, fdrfc822Name.getDataTypeId());

        FunctionDefinition fdx500Name = StdFunctions.FD_X500NAME_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_X500NAME, fdx500Name.getDataTypeId());

        // TODO - There are currently no functions that return XPathExpression objects
        // FunctionDefinition fdxpathExpression = StdFunctions.FD_XPATHEXPRESSION_FROM_STRING;
        // assertEquals(XACML3.ID_DATATYPE_XPATHEXPRESSION, fdxpathExpression.getDataTypeId());

        FunctionDefinition fdipAddress = StdFunctions.FD_IPADDRESS_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_IPADDRESS, fdipAddress.getDataTypeId());

        FunctionDefinition fddnsName = StdFunctions.FD_DNSNAME_FROM_STRING;
        assertEquals(XACML3.ID_DATATYPE_DNSNAME, fddnsName.getDataTypeId());
    }

    /**
     * check the type of return, single vs multiple values
     */
    @Test
    public void testReturnsBag() {
        FunctionDefinition fdNotBag = StdFunctions.FD_BOOLEAN_EQUAL;
        assertFalse(fdNotBag.returnsBag());

        FunctionDefinitionBag<?> fdBag = (FunctionDefinitionBag<?>)StdFunctions.FD_STRING_BAG;
        assertTrue(fdBag.returnsBag());
    }

}
