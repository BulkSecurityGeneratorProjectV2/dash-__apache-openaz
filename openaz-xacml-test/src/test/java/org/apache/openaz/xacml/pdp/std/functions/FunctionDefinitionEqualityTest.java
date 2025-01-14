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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.pdp.policy.FunctionArgumentAttributeValue;
import org.apache.openaz.xacml.pdp.std.StdFunctions;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionEquality;
import org.apache.openaz.xacml.std.datatypes.Base64Binary;
import org.apache.openaz.xacml.std.datatypes.DataTypeRFC822Name;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.HexBinary;
import org.apache.openaz.xacml.std.datatypes.RFC822Name;
import org.apache.openaz.xacml.std.datatypes.XPathDayTimeDuration;
import org.apache.openaz.xacml.std.datatypes.XPathYearMonthDuration;
import org.junit.Test;

/**
 * Test FunctionDefinitionEquality, all of its super-classes, and all XACML functions supported by that class.
 * TO RUN - use jUnit In Eclipse select this file or the enclosing directory, right-click and select Run
 * As/JUnit Test In the first implementation of XACML we had separate files for each XACML Function. This
 * release combines multiple Functions in fewer files to minimize code duplication. This file supports the
 * following XACML codes: string-equal boolean-equal integer-equal double-equal date-equal time-equal
 * dateTime-equal dayTimeDuration-equal yearMonthDuration-equal Each of these is put into a separate test
 * method just to keep things organized.
 */
public class FunctionDefinitionEqualityTest {

    /*
     * variables useful in the following tests
     */
    List<FunctionArgument> arguments = new ArrayList<FunctionArgument>();

    FunctionArgumentAttributeValue stringAttr1 = null;
    FunctionArgumentAttributeValue stringAttr2 = null;
    FunctionArgumentAttributeValue stringAttr3 = null;
    FunctionArgumentAttributeValue stringAttr4 = null;

    FunctionArgumentAttributeValue booleanAttrT1 = null;
    FunctionArgumentAttributeValue booleanAttrT2 = null;
    FunctionArgumentAttributeValue booleanAttrF1 = null;
    FunctionArgumentAttributeValue booleanAttrF2 = null;

    FunctionArgumentAttributeValue intAttr1 = null;
    FunctionArgumentAttributeValue intAttr1a = null;
    FunctionArgumentAttributeValue intAttr2 = null;
    FunctionArgumentAttributeValue intAttr0 = null;
    FunctionArgumentAttributeValue intAttrNeg1 = null;

    public FunctionDefinitionEqualityTest() {
        try {
            stringAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr3 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("ABC"));
            stringAttr4 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("def"));

            booleanAttrT1 = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_BOOLEAN
                                                                   .createAttributeValue(true));
            booleanAttrT2 = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_BOOLEAN
                                                                   .createAttributeValue(true));
            booleanAttrF1 = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_BOOLEAN
                                                                   .createAttributeValue(false));
            booleanAttrF2 = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_BOOLEAN
                                                                   .createAttributeValue(false));

            intAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
            intAttr1a = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
            intAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(2));
            intAttr0 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(0));
            intAttrNeg1 = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(-1));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }
    }

    /**
     * String (matching case)
     */
    @Test
    public void testString_Equal() {

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_STRING_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check "abc" with "abc" - separate string objects with same value
        arguments.add(stringAttr1);
        arguments.add(stringAttr2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check "abc" with "ABC" (not same)
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(stringAttr3);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * Boolean
     */
    @Test
    public void testBoolean_Equal() {

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_BOOLEAN_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_BOOLEAN_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with same value
        arguments.add(booleanAttrT1);
        arguments.add(booleanAttrT2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check different values
        arguments.clear();
        arguments.add(booleanAttrT1);
        arguments.add(booleanAttrF1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * Integer
     */
    @Test
    public void testInteger_Equal() {

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_INTEGER_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_INTEGER_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_INTEGER.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with same value
        arguments.add(intAttr1);
        arguments.add(intAttr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        arguments.clear();
        arguments.add(intAttr1);
        arguments.add(intAttrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * Double
     */
    @Test
    public void testDouble_Equal() {
        FunctionArgumentAttributeValue attr1 = null;
        FunctionArgumentAttributeValue attr1a = null;
        FunctionArgumentAttributeValue attr2 = null;
        FunctionArgumentAttributeValue attrNeg1 = null;
        try {
            attr1 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(1.0));
            attr1a = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(1.0));
            attr2 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(2.4));
            attrNeg1 = new FunctionArgumentAttributeValue(DataTypes.DT_DOUBLE.createAttributeValue(-1.0));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_DOUBLE_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DOUBLE_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DOUBLE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attr1);
        arguments.add(attr1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attr1);
        arguments.add(attr2);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        arguments.clear();
        arguments.add(attr1);
        arguments.add(attrNeg1);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * Date
     */
    @Test
    public void testDate_Equal() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date longAgo = new Date(1234);
        // create a date that is different than "today" but within the same day (i.e. has a different hour)
        if (calendar.get(Calendar.HOUR_OF_DAY) > 3) {
            calendar.set(Calendar.HOUR_OF_DAY, 3);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 5);
        }
        Date todayPlus = calendar.getTime();

        FunctionArgumentAttributeValue attrToday = null;
        FunctionArgumentAttributeValue attrToday2 = null;
        FunctionArgumentAttributeValue attrLaterToday = null;
        FunctionArgumentAttributeValue attrYesterday = null;
        try {
            attrToday = new FunctionArgumentAttributeValue(DataTypes.DT_DATE.createAttributeValue(today));
            attrToday2 = new FunctionArgumentAttributeValue(DataTypes.DT_DATE.createAttributeValue(today));
            attrLaterToday = new FunctionArgumentAttributeValue(
                                                                DataTypes.DT_DATE
                                                                    .createAttributeValue(todayPlus));
            attrYesterday = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_DATE
                                                                   .createAttributeValue(longAgo));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_DATE_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATE_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATE.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrToday);
        arguments.add(attrToday2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrToday);
        arguments.add(attrYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // Date objects with different times but within the same day should match
        arguments.clear();
        arguments.add(attrToday);
        arguments.add(attrLaterToday);
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

    /**
     * Time
     */
    @Test
    public void testTime_Equal() {

        Date now = new Date();
        Date now2 = new Date(now.getTime());
        Date notNow = new Date(now.getTime() - 100000);

        FunctionArgumentAttributeValue attrNow = null;
        FunctionArgumentAttributeValue attrNow2 = null;
        FunctionArgumentAttributeValue attrNotNow = null;
        try {
            attrNow = new FunctionArgumentAttributeValue(DataTypes.DT_TIME.createAttributeValue(now));
            attrNow2 = new FunctionArgumentAttributeValue(DataTypes.DT_TIME.createAttributeValue(now2));
            attrNotNow = new FunctionArgumentAttributeValue(DataTypes.DT_TIME.createAttributeValue(notNow));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_TIME_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_TIME_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_TIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrNow);
        arguments.add(attrNow2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrNow);
        arguments.add(attrNotNow);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * DateTime
     */
    @Test
    public void testDateTime_Equal() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        Date longAgo = new Date(1234);
        // create a dateTime that is different than "today" changing only the Timezone
        if (calendar.get(Calendar.ZONE_OFFSET) > 3) {
            calendar.set(Calendar.ZONE_OFFSET, 3);
        } else {
            calendar.set(Calendar.ZONE_OFFSET, 5);
        }
        Date todayPlus = calendar.getTime();

        FunctionArgumentAttributeValue attrToday = null;
        FunctionArgumentAttributeValue attrToday2 = null;
        FunctionArgumentAttributeValue attrLaterToday = null;
        FunctionArgumentAttributeValue attrYesterday = null;
        try {
            attrToday = new FunctionArgumentAttributeValue(DataTypes.DT_DATETIME.createAttributeValue(today));
            attrToday2 = new FunctionArgumentAttributeValue(DataTypes.DT_DATETIME.createAttributeValue(today));
            attrLaterToday = new FunctionArgumentAttributeValue(
                                                                DataTypes.DT_DATETIME
                                                                    .createAttributeValue(todayPlus));
            attrYesterday = new FunctionArgumentAttributeValue(
                                                               DataTypes.DT_DATETIME
                                                                   .createAttributeValue(longAgo));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        // String exact match
        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_DATETIME_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DATETIME_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DATETIME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrToday);
        arguments.add(attrToday2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrToday);
        arguments.add(attrYesterday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // DateTime with different Zones should not match
        arguments.clear();
        arguments.add(attrToday);
        arguments.add(attrLaterToday);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * dayTimeDuration - Version1
     */
    @Test
    public void testDayTimeDuration_Equal_V1() {

        XPathDayTimeDuration dur1 = new XPathDayTimeDuration(1, 3, 5, 12, 38);
        XPathDayTimeDuration dur2 = new XPathDayTimeDuration(1, 3, 5, 12, 38);
        XPathDayTimeDuration differentDur = new XPathDayTimeDuration(-1, 4, 7, 5, 33);

        FunctionArgumentAttributeValue attrDur1 = null;
        FunctionArgumentAttributeValue attrDur2 = null;
        FunctionArgumentAttributeValue attrDifferentDur = null;
        try {
            attrDur1 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_DAYTIMEDURATION
                                                              .createAttributeValue(dur1));
            attrDur2 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_DAYTIMEDURATION
                                                              .createAttributeValue(dur2));
            attrDifferentDur = new FunctionArgumentAttributeValue(
                                                                  DataTypes.DT_DAYTIMEDURATION
                                                                      .createAttributeValue(differentDur));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_DAYTIMEDURATION_EQUAL_VERSION1;

        // check identity and type of the thing created
        assertEquals(XACML1.ID_FUNCTION_DAYTIMEDURATION_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DAYTIMEDURATION.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrDur1);
        arguments.add(attrDur2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrDur1);
        arguments.add(attrDifferentDur);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * dayTimeDuration - Current version
     */
    @Test
    public void testDayTimeDuration_Equal() {

        XPathDayTimeDuration dur1 = new XPathDayTimeDuration(1, 3, 5, 12, 38);
        XPathDayTimeDuration dur2 = new XPathDayTimeDuration(1, 3, 5, 12, 38);
        XPathDayTimeDuration differentDur = new XPathDayTimeDuration(-1, 4, 7, 5, 33);

        FunctionArgumentAttributeValue attrDur1 = null;
        FunctionArgumentAttributeValue attrDur2 = null;
        FunctionArgumentAttributeValue attrDifferentDur = null;
        try {
            attrDur1 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_DAYTIMEDURATION
                                                              .createAttributeValue(dur1));
            attrDur2 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_DAYTIMEDURATION
                                                              .createAttributeValue(dur2));
            attrDifferentDur = new FunctionArgumentAttributeValue(
                                                                  DataTypes.DT_DAYTIMEDURATION
                                                                      .createAttributeValue(differentDur));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_DAYTIMEDURATION_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_DAYTIMEDURATION_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_DAYTIMEDURATION.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrDur1);
        arguments.add(attrDur2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrDur1);
        arguments.add(attrDifferentDur);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * dayTimeDuration - Version1
     */
    @Test
    public void testYearMonthDuration_Equal_V1() {

        XPathYearMonthDuration dur1 = new XPathYearMonthDuration(1, 3, 5);
        XPathYearMonthDuration dur2 = new XPathYearMonthDuration(1, 3, 5);
        XPathYearMonthDuration differentDur = new XPathYearMonthDuration(-1, 4, 7);

        FunctionArgumentAttributeValue attrDur1 = null;
        FunctionArgumentAttributeValue attrDur2 = null;
        FunctionArgumentAttributeValue attrDifferentDur = null;
        try {
            attrDur1 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_YEARMONTHDURATION
                                                              .createAttributeValue(dur1));
            attrDur2 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_YEARMONTHDURATION
                                                              .createAttributeValue(dur2));
            attrDifferentDur = new FunctionArgumentAttributeValue(
                                                                  DataTypes.DT_YEARMONTHDURATION
                                                                      .createAttributeValue(differentDur));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_YEARMONTHDURATION_EQUAL_VERSION1;

        // check identity and type of the thing created
        assertEquals(XACML1.ID_FUNCTION_YEARMONTHDURATION_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_YEARMONTHDURATION.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrDur1);
        arguments.add(attrDur2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrDur1);
        arguments.add(attrDifferentDur);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * dayTimeDuration - Current version
     */
    @Test
    public void testYearMonthDuration_Equal() {

        XPathYearMonthDuration dur1 = new XPathYearMonthDuration(1, 3, 5);
        XPathYearMonthDuration dur2 = new XPathYearMonthDuration(1, 3, 5);
        XPathYearMonthDuration differentDur = new XPathYearMonthDuration(-1, 4, 7);

        FunctionArgumentAttributeValue attrDur1 = null;
        FunctionArgumentAttributeValue attrDur2 = null;
        FunctionArgumentAttributeValue attrDifferentDur = null;
        try {
            attrDur1 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_YEARMONTHDURATION
                                                              .createAttributeValue(dur1));
            attrDur2 = new FunctionArgumentAttributeValue(
                                                          DataTypes.DT_YEARMONTHDURATION
                                                              .createAttributeValue(dur2));
            attrDifferentDur = new FunctionArgumentAttributeValue(
                                                                  DataTypes.DT_YEARMONTHDURATION
                                                                      .createAttributeValue(differentDur));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_YEARMONTHDURATION_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_YEARMONTHDURATION_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_YEARMONTHDURATION.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrDur1);
        arguments.add(attrDur2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrDur1);
        arguments.add(attrDifferentDur);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * URI
     */
    @Test
    public void testAnyURI_Equal() {

        URI uri1 = null;
        URI uri2 = null;
        URI uriNotThere = null;
        try {
            uri1 = new URI("http://someplace.com/gothere");
            uri2 = new URI("http://someplace.com/gothere");
            uriNotThere = new URI("http://someplace.com/notGoingThere");
        } catch (Exception e) {
            fail(e.toString());
        }

        FunctionArgumentAttributeValue attrUri1 = null;
        FunctionArgumentAttributeValue attrUri2 = null;
        FunctionArgumentAttributeValue attrUriNotThere = null;
        try {
            attrUri1 = new FunctionArgumentAttributeValue(DataTypes.DT_ANYURI.createAttributeValue(uri1));
            attrUri2 = new FunctionArgumentAttributeValue(DataTypes.DT_ANYURI.createAttributeValue(uri2));
            attrUriNotThere = new FunctionArgumentAttributeValue(
                                                                 DataTypes.DT_ANYURI
                                                                     .createAttributeValue(uriNotThere));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_ANYURI_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_ANYURI_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_ANYURI.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrUri1);
        arguments.add(attrUri2);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrUri1);
        arguments.add(attrUriNotThere);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * X500Name
     */
    @Test
    public void testX500Name_Equal() {

        X500Principal name1 = new X500Principal("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US");
        X500Principal name2 = new X500Principal("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US");
        X500Principal name3 = new X500Principal("CN=NotDuke, OU=NotThere, O=Oracle, C=US");

        FunctionArgumentAttributeValue attrName1 = null;
        FunctionArgumentAttributeValue attrName1a = null;
        FunctionArgumentAttributeValue attrNotSameName = null;
        try {
            attrName1 = new FunctionArgumentAttributeValue(DataTypes.DT_X500NAME.createAttributeValue(name1));
            attrName1a = new FunctionArgumentAttributeValue(DataTypes.DT_X500NAME.createAttributeValue(name2));
            attrNotSameName = new FunctionArgumentAttributeValue(
                                                                 DataTypes.DT_X500NAME
                                                                     .createAttributeValue(name3));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_X500NAME_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_X500NAME_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_X500NAME.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrName1);
        arguments.add(attrName1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrName1);
        arguments.add(attrNotSameName);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * RFC822Name
     */
    @Test
    public void testRfc822Name_Equal() {

        RFC822Name name1 = null;
        RFC822Name name1a = null;
        RFC822Name differentLocalName = null;
        RFC822Name differentDomainName = null;
        RFC822Name localCaseName = null;
        RFC822Name domainCaseName = null;
        @SuppressWarnings("unused")
        RFC822Name noAtName = null;

        try {
            name1 = RFC822Name.newInstance("localPart@DomainPart");
            name1a = RFC822Name.newInstance("localPart@DomainPart");
            differentLocalName = RFC822Name.newInstance("differentlocalPart@DomainPart");
            differentDomainName = RFC822Name.newInstance("localPart@differentDomainPart");
            localCaseName = RFC822Name.newInstance("LOCALPart@DomainPart");
            domainCaseName = RFC822Name.newInstance("localPart@DOMAINPart");

        } catch (Exception e) {
            fail(e.toString());
        }

        // should not be able to create a name without an @. If you try, newInstance returns null
        Exception exSeen = null;
        try {
            noAtName = RFC822Name.newInstance("nameWithoutAnAtSign");
        } catch (Exception e) {
            exSeen = e;
        }
        assertNotNull(exSeen);

        FunctionArgumentAttributeValue attrName1 = null;
        FunctionArgumentAttributeValue attrName1a = null;
        FunctionArgumentAttributeValue attrDifferentLocalName = null;
        FunctionArgumentAttributeValue attrDifferentDomainName = null;
        FunctionArgumentAttributeValue attrLocalCaseName = null;
        FunctionArgumentAttributeValue attrDomainCaseName = null;
        try {
            attrName1 = new FunctionArgumentAttributeValue(
                                                           DataTypes.DT_RFC822NAME
                                                               .createAttributeValue(name1));
            attrName1a = new FunctionArgumentAttributeValue(
                                                            DataTypes.DT_RFC822NAME
                                                                .createAttributeValue(name1a));
            attrDifferentLocalName = new FunctionArgumentAttributeValue(
                                                                        DataTypes.DT_RFC822NAME
                                                                            .createAttributeValue(differentLocalName));
            attrDifferentDomainName = new FunctionArgumentAttributeValue(
                                                                         DataTypes.DT_RFC822NAME
                                                                             .createAttributeValue(differentDomainName));
            attrLocalCaseName = new FunctionArgumentAttributeValue(
                                                                   DataTypes.DT_RFC822NAME
                                                                       .createAttributeValue(localCaseName));
            attrDomainCaseName = new FunctionArgumentAttributeValue(
                                                                    DataTypes.DT_RFC822NAME
                                                                        .createAttributeValue(domainCaseName));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_RFC822NAME_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_RFC822NAME_EQUAL, fd.getId());
        assertEquals(DataTypeRFC822Name.newInstance().getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrName1);
        arguments.add(attrName1a);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same Local
        arguments.clear();
        arguments.add(attrName1);
        arguments.add(attrDifferentLocalName);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // check not same Domain
        arguments.clear();
        arguments.add(attrName1);
        arguments.add(attrDifferentDomainName);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test case-sensitivity in local part
        arguments.clear();
        arguments.add(attrName1);
        arguments.add(attrLocalCaseName);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test non-case-sensitivity in Domain part
        arguments.clear();
        arguments.add(attrName1);
        arguments.add(attrDomainCaseName);
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

    /**
     * Hex Binary
     */
    @Test
    public void testHexBinary_Equal() {
        HexBinary binary = null;
        HexBinary sameBinary = null;
        HexBinary differentBinary = null;
        try {
            binary = HexBinary.newInstance("e04fd020ea3a6910a2d808002b30309d");
            sameBinary = HexBinary.newInstance("e04fd020ea3a6910a2d808002b30309d");
            differentBinary = HexBinary.newInstance("f123a890ee3d");
        } catch (Exception e) {
            fail(e.toString());
        }

        FunctionArgumentAttributeValue attrBinary = null;
        FunctionArgumentAttributeValue attrSameBinary = null;
        FunctionArgumentAttributeValue attrDifferentBinary = null;
        ;
        try {
            attrBinary = new FunctionArgumentAttributeValue(
                                                            DataTypes.DT_HEXBINARY
                                                                .createAttributeValue(binary));
            attrSameBinary = new FunctionArgumentAttributeValue(
                                                                DataTypes.DT_HEXBINARY
                                                                    .createAttributeValue(sameBinary));
            attrDifferentBinary = new FunctionArgumentAttributeValue(
                                                                     DataTypes.DT_HEXBINARY
                                                                         .createAttributeValue(differentBinary));

        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_HEXBINARY_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_HEXBINARY_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_HEXBINARY.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrBinary);
        arguments.add(attrSameBinary);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrBinary);
        arguments.add(attrDifferentBinary);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }

    /**
     * Base64 Binary
     */
    @Test
    public void testBase64Binary_Equal() {
        Base64Binary binary = null;
        Base64Binary sameBinary = null;
        Base64Binary differentBinary = null;
        try {
            binary = Base64Binary
                .newInstance("TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz");
            sameBinary = Base64Binary
                .newInstance("TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz");
            differentBinary = Base64Binary.newInstance("f123a890ee3d");
        } catch (Exception e) {
            fail(e.toString());
        }

        FunctionArgumentAttributeValue attrBinary = null;
        FunctionArgumentAttributeValue attrSameBinary = null;
        FunctionArgumentAttributeValue attrDifferentBinary = null;
        try {
            attrBinary = new FunctionArgumentAttributeValue(
                                                            DataTypes.DT_BASE64BINARY
                                                                .createAttributeValue(binary));
            attrSameBinary = new FunctionArgumentAttributeValue(
                                                                DataTypes.DT_BASE64BINARY
                                                                    .createAttributeValue(sameBinary));
            attrDifferentBinary = new FunctionArgumentAttributeValue(
                                                                     DataTypes.DT_BASE64BINARY
                                                                         .createAttributeValue(differentBinary));

        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<?> fd = (FunctionDefinitionEquality<?>)StdFunctions.FD_BASE64BINARY_EQUAL;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_BASE64BINARY_EQUAL, fd.getId());
        assertEquals(DataTypes.DT_BASE64BINARY.getId(), fd.getDataTypeArgs().getId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());
        assertFalse(fd.returnsBag());
        assertEquals(new Integer(2), fd.getNumArgs());

        // test normal equals and non-equals
        // check separate objects with the same value
        arguments.add(attrBinary);
        arguments.add(attrSameBinary);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertTrue(resValue);

        // check not same
        arguments.clear();
        arguments.add(attrBinary);
        arguments.add(attrDifferentBinary);
        res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        resValue = (Boolean)res.getValue().getValue();
        assertFalse(resValue);

        // test bad args data types? Not needed?
        arguments.clear();
        arguments.add(stringAttr1);
        arguments.add(intAttr1);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());

    }
}
