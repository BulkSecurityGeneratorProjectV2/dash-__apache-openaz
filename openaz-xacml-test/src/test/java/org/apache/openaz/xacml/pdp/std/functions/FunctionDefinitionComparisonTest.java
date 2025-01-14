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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.pdp.policy.FunctionArgumentAttributeValue;
import org.apache.openaz.xacml.pdp.std.StdFunctions;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionComparison;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionTimeInRange;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.ISO8601Date;
import org.apache.openaz.xacml.std.datatypes.ISO8601DateTime;
import org.apache.openaz.xacml.std.datatypes.ISO8601Time;
import org.junit.Test;

/**
 * Test FunctionDefinitionComparison TO RUN - use jUnit In Eclipse select this file or the enclosing
 * directory, right-click and select Run As/JUnit Test
 */
public class FunctionDefinitionComparisonTest {

    /*
     * variables useful in the following tests
     */
    List<FunctionArgument> arguments = new ArrayList<FunctionArgument>();

    FunctionArgumentAttributeValue stringAttr1 = null;
    FunctionArgumentAttributeValue stringAttr1a = null;
    FunctionArgumentAttributeValue stringAttr2 = null;
    FunctionArgumentAttributeValue stringAttrNeg1 = null;

    FunctionArgumentAttributeValue intAttr1 = null;
    FunctionArgumentAttributeValue intAttr1a = null;
    FunctionArgumentAttributeValue intAttr2 = null;
    FunctionArgumentAttributeValue intAttr0 = null;
    FunctionArgumentAttributeValue intAttrNeg1 = null;

    FunctionArgumentAttributeValue attr1 = null;
    FunctionArgumentAttributeValue attr1a = null;
    FunctionArgumentAttributeValue attr2 = null;
    FunctionArgumentAttributeValue attrNeg1 = null;

    FunctionArgumentAttributeValue attrDateToday = null;
    FunctionArgumentAttributeValue attrDateSameDay = null;
    FunctionArgumentAttributeValue attrDateTommorrow = null;
    FunctionArgumentAttributeValue attrDateYesterday = null;
    FunctionArgumentAttributeValue attrDateWithTimeZone = null;
    FunctionArgumentAttributeValue attrDateNoTimeZone = null;

    FunctionArgumentAttributeValue attrTimeToday = null;
    FunctionArgumentAttributeValue attrTimeSameDay = null;
    FunctionArgumentAttributeValue attrTimeTommorrow = null;
    FunctionArgumentAttributeValue attrTimeYesterday = null;
    FunctionArgumentAttributeValue attrTimeWithTimeZone = null;
    FunctionArgumentAttributeValue attrTimeNoTimeZone = null;

    FunctionArgumentAttributeValue attrDateTimeToday = null;
    FunctionArgumentAttributeValue attrDateTimeSameDay = null;
    FunctionArgumentAttributeValue attrDateTimeTommorrow = null;
    FunctionArgumentAttributeValue attrDateTimeYesterday = null;
    FunctionArgumentAttributeValue attrDateTimeWithTimeZone = null;
    FunctionArgumentAttributeValue attrDateTimeNoTimeZone = null;

    /**
     * Set up some common variables on startup
     */
    public FunctionDefinitionComparisonTest() {
        try {
            stringAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr1a = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("def"));
            stringAttrNeg1 = new FunctionArgumentAttributeValue(
                                                                DataTypes.DT_STRING
                                                                    .createAttributeValue("AAA"));

            intAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
            intAttr1a = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
            intAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(2));
            intAttr0 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(0));
            intAttrNeg1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(-1));

            attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(1.0));
            attr1a = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(1.0));
            attr2 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(2.4));
            attrNeg1 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(-1.0));

            // create dates
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            Date longAgo = new Date(1234);
            // create a date that is different than "today" but within the same day (i.e. has a different
            // hour)
            if (calendar.get(Calendar.HOUR_OF_DAY) > 3) {
                calendar.set(Calendar.HOUR_OF_DAY, 3);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, 5);
            }
            Date todayPlus = calendar.getTime();
            calendar.add(Calendar.DATE, 1);
            Date tommorrow = calendar.getTime();
            attrDateToday = new FunctionArgumentAttributeValue(DataTypes.DT_DATE.createAttributeValue(today));
            attrDateSameDay = new FunctionArgumentAttributeValue(
                                                                 DataTypes.DT_DATE
                                                                     .createAttributeValue(todayPlus));
            attrDateTommorrow = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_DATE
                                                                       .createAttributeValue(tommorrow));
            attrDateYesterday = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_DATE
                                                                       .createAttributeValue(longAgo));
            ISO8601Date isoDate = new ISO8601Date(1920, 5, 8);
            attrDateNoTimeZone = new FunctionArgumentAttributeValue(
                                                                    DataTypes.DT_DATE
                                                                        .createAttributeValue(isoDate));
            isoDate = new ISO8601Date("GMT+00:02", 1920, 5, 8);
            attrDateWithTimeZone = new FunctionArgumentAttributeValue(
                                                                      DataTypes.DT_DATE
                                                                          .createAttributeValue(isoDate));

            // create Times
            ISO8601Time isoTime = new ISO8601Time(14, 43, 12, 145);
            attrTimeToday = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_TIME
                                                                   .createAttributeValue(isoTime));
            attrTimeSameDay = new FunctionArgumentAttributeValue(
                                                                 DataTypes.DT_TIME
                                                                     .createAttributeValue(isoTime));
            isoTime = new ISO8601Time(18, 53, 34, 423);
            attrTimeTommorrow = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_TIME
                                                                       .createAttributeValue(isoTime));
            isoTime = new ISO8601Time(7, 34, 6, 543);
            attrTimeYesterday = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_TIME
                                                                       .createAttributeValue(isoTime));
            isoTime = new ISO8601Time(12, 12, 12, 12);
            attrTimeNoTimeZone = new FunctionArgumentAttributeValue(
                                                                    DataTypes.DT_TIME
                                                                        .createAttributeValue(isoTime));
            isoTime = new ISO8601Time("GMT:+00:03", 12, 12, 12, 12);
            attrTimeWithTimeZone = new FunctionArgumentAttributeValue(
                                                                      DataTypes.DT_TIME
                                                                          .createAttributeValue(isoTime));

            // create DateTimes
            isoDate = new ISO8601Date(1920, 5, 8);
            isoTime = new ISO8601Time(18, 53, 34, 423);
            ISO8601DateTime isoDateTime = new ISO8601DateTime((String)null, 1920, 5, 8, 18, 53, 34, 423);
            attrDateTimeToday = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_DATETIME
                                                                       .createAttributeValue(isoDateTime));
            attrDateTimeSameDay = new FunctionArgumentAttributeValue(
                                                                     DataTypes.DT_DATETIME
                                                                         .createAttributeValue(isoDateTime));
            isoTime = new ISO8601Time(20, 53, 34, 423);
            isoDateTime = new ISO8601DateTime((String)null, 1920, 5, 8, 20, 53, 34, 423);
            attrDateTimeTommorrow = new FunctionArgumentAttributeValue(
                                                                       DataTypes.DT_DATETIME
                                                                           .createAttributeValue(isoDateTime));
            isoTime = new ISO8601Time(7, 34, 6, 543);
            isoDateTime = new ISO8601DateTime((String)null, 1920, 5, 8, 7, 34, 6, 543);
            attrDateTimeYesterday = new FunctionArgumentAttributeValue(
                                                                       DataTypes.DT_DATETIME
                                                                           .createAttributeValue(isoDateTime));
            isoTime = new ISO8601Time(12, 12, 12, 12);
            isoDateTime = new ISO8601DateTime((String)null, 1920, 5, 8, 12, 12, 12, 12);
            attrDateTimeNoTimeZone = new FunctionArgumentAttributeValue(
                                                                        DataTypes.DT_DATETIME
                                                                            .createAttributeValue(isoDateTime));
            isoTime = new ISO8601Time("GMT:+00:03", 12, 12, 12, 12);
            isoDate = new ISO8601Date("GMT:+00:03", 1920, 5, 8);
            isoDateTime = new ISO8601DateTime("GMT:+00:03", 1920, 5, 8, 12, 12, 12, 12);
            attrDateTimeWithTimeZone = new FunctionArgumentAttributeValue(
                                                                          DataTypes.DT_DATETIME
                                                                              .createAttributeValue(isoDateTime));

        } catch (Exception e) {
            fail("Error creating values e=" + e);
        }
    }

    /**
     * String
     */
    @Test
    public void testString_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_STRING_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(stringAttr1);
        arguments.add(stringAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // check first < second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(stringAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
    }

    @Test
    public void testString_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_STRING_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(stringAttr1);
        arguments.add(stringAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check first < second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testString_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_STRING_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(stringAttr1);
        arguments.add(stringAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // check first < second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testString_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_STRING_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(stringAttr1);
        arguments.add(stringAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check first < second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    /**
     * Integer
     */
    @Test
    public void testInteger_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_INTEGER_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_INTEGER_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(intAttr1);
        arguments.add(intAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // check first < second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttrNeg1);
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
    }

    @Test
    public void testInteger_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_INTEGER_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_INTEGER_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(intAttr1);
        arguments.add(intAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check first < second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testInteger_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_INTEGER_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_INTEGER_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(intAttr1);
        arguments.add(intAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // check first < second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testInteger_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_INTEGER_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_INTEGER_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(intAttr1);
        arguments.add(intAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check first < second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    /**
     * Double
     */
    @Test
    public void testDouble_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DOUBLE_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DOUBLE_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attr1);
        arguments.add(attr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attrNeg1);
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

    }

    @Test
    public void testDouble_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DOUBLE_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DOUBLE_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attr1);
        arguments.add(attr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testDouble_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DOUBLE_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DOUBLE_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attr1);
        arguments.add(attr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testDouble_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DOUBLE_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DOUBLE_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attr1);
        arguments.add(attr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    /**
     * Date
     */

    @Test
    public void testDate_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATE_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATE_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_DATE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateToday);
        arguments.add(attrDateSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // test bad args data types? One with TimeZone and one without
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

        // test with TimeZone vs without
        arguments.clear();
        arguments.add(attrDateWithTimeZone);
        arguments.add(attrDateNoTimeZone);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:date-greater-than Cannot compare this ISO8601DateTime with non-time-zoned ISO8601DateTime",
                     res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

    }

    @Test
    public void testDate_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATE_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATE_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateToday);
        arguments.add(attrDateSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testDate_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATE_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATE_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_DATE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateToday);
        arguments.add(attrDateSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testDate_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATE_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATE_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateToday);
        arguments.add(attrDateSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateToday);
        arguments.add(attrDateYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    /**
     * Time
     */

    @Test
    public void testTime_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_TIME_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrTimeToday);
        arguments.add(attrTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // test bad args data types? One with TimeZone and one without
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

        // test with TimeZone vs without
        arguments.clear();
        arguments.add(attrTimeWithTimeZone);
        arguments.add(attrTimeNoTimeZone);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:time-greater-than Cannot compare this ISO8601DateTime with non-time-zoned ISO8601DateTime",
                     res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

    }

    @Test
    public void testTime_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_TIME_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrTimeToday);
        arguments.add(attrTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testTime_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_TIME_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrTimeToday);
        arguments.add(attrTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testTime_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_TIME_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrTimeToday);
        arguments.add(attrTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    /**
     * Time-in-range
     */
    @Test
    public void testTime_in_range() {

        FunctionDefinitionTimeInRange<?> fd = (FunctionDefinitionTimeInRange<?>)StdFunctions.FD_TIME_IN_RANGE;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_IN_RANGE, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(3), fd.getNumArgs());

        // arg 0 in range of others
        arguments.add(attrTimeToday);
        arguments.add(attrTimeYesterday);
        arguments.add(attrTimeTommorrow);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // below range
        arguments.clear();
        arguments.add(attrTimeYesterday);
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // above range
        arguments.clear();
        arguments.add(attrTimeTommorrow);
        arguments.add(attrTimeYesterday);
        arguments.add(attrTimeToday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // range bad
        arguments.clear();
        arguments.add(attrTimeToday);
        arguments.add(attrTimeTommorrow);
        arguments.add(attrTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // bad types
        arguments.clear();
        arguments.add(attrDateTimeWithTimeZone);
        arguments.add(attrDateTimeNoTimeZone);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:time-in-range Expected 3 arguments, got 2", res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        arguments.clear();
        arguments.add(attrDateTimeWithTimeZone);
        arguments.add(attrDateTimeNoTimeZone);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:time-in-range Expected data type 'time' saw 'dateTime' at arg index 0", res
            .getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

    }

    /**
     * DateTime
     */

    @Test
    public void testDateTime_GT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATETIME_GREATER_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATETIME_GREATER_THAN, fd.getId());
        assertEquals(DataTypes.DT_DATETIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // test bad args data types? One with TimeZone and one without
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

        // test with TimeZone vs without
        arguments.clear();
        arguments.add(attrDateTimeWithTimeZone);
        arguments.add(attrDateTimeNoTimeZone);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:dateTime-greater-than Cannot compare this ISO8601DateTime with non-time-zoned ISO8601DateTime",
                     res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

    }

    @Test
    public void testDateTime_GTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATETIME_GREATER_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATETIME_GREATER_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATETIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);
    }

    @Test
    public void testDateTime_LT() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATETIME_LESS_THAN;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATETIME_LESS_THAN, fd.getId());
        assertEquals(DataTypes.DT_DATETIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

    @Test
    public void testDateTime_LTE() {

        FunctionDefinitionComparison<?> fd = (FunctionDefinitionComparison<?>)StdFunctions.FD_DATETIME_LESS_THAN_OR_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATETIME_LESS_THAN_OR_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATETIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // first == second
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeSameDay);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first < second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeTommorrow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // first > second
        arguments.clear();
        arguments.add(attrDateTimeToday);
        arguments.add(attrDateTimeYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);
    }

}
