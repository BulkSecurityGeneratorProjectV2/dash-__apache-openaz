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

import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinition;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.datatypes.DataTypeBoolean;

/**
 * /** FunctionDefinitionBase provides a common base for
 * {@link org.apache.openaz.xacml.pdp.policy.FunctionDefinition}s. The child classes derived from this are
 * of two types:
 * <UL>
 * <LI>Functions returning a single simple value of a type defined in
 * {@link org.apache.openaz.xacml.std.datatypes.DataTypes}. These functions will all derive from
 * {@link org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionSimple}.
 * <LI>Functions returning a single bag with elements of a single type.
 * <UL>
 * <P>
 * This base class contains the following components:
 * <UL>
 * <LI>The Identity for this function.
 * <LI>The DataType of the data returned from this function. For Bags this means the DataType of the elements
 * in the bag, or null if that is ambiguous.
 * <LI>Commonly-used values.
 * </UL>
 *
 * @param <O> the java class for the Output data type returned by the <code>FunctionDefinition</code>
 * @param <I> the java class for the Input data type expected in the arguments to the <code>FunctionDefinition
 *            </code>. Some functions have non-homogeneous arguments but may still have a main 'type'.
 */
public abstract class FunctionDefinitionBase<O, I> implements FunctionDefinition {

    // The XACML identifier string for this particular function
    private Identifier id;

    // each function derived from this returns a single non-bag data value of the following type, or a Bag
    // containing elements of this type
    private DataType<O> dataTypeReturn;

    // All functions have input arguments and expect them to be of a given type.
    // In some instances the argument gets multiple values of different types, but when the function has a
    // 'type' associated with it's name
    // specific ones of the input must be of this type.
    // When an argument Input to the function is a Bag, the elements in that bag will be of this type.
    // This corresponds most closely to the 'type' in the function name (as in 'type'-bag or 'type'-equals).
    private DataType<I> dataTypeArgs;

    // true = the return value from this function is a bag; false = return value is a single-value DataType
    // object
    private boolean returnsBag;

    /*
     * For functions that return a Boolean result we create a single instance of the True/False return values
     * that they can share
     */
    protected static final ExpressionResult ER_TRUE = ExpressionResult.newSingle(DataTypeBoolean.AV_TRUE);
    protected static final ExpressionResult ER_FALSE = ExpressionResult.newSingle(DataTypeBoolean.AV_FALSE);

    /**
     * Creates a new <code>FunctionDefinitionBase</code> with the
     * {@link org.apache.openaz.xacml.api.Identifier} <code>idIn</code> as the function id.
     *
     * @param idIn the <code>Identifier</code> for this <code>FunctionDefinitionBase</code>
     */
    protected FunctionDefinitionBase(Identifier idIn, DataType<O> returnDataTypeIn,
                                     DataType<I> argumentDataTypeIn, boolean returnsBagIn) {
        this.id = idIn;
        this.dataTypeReturn = returnDataTypeIn;
        this.dataTypeArgs = argumentDataTypeIn;
        this.returnsBag = returnsBagIn;
    }

    /**
     * Returns a shortened version of the Id for this function, primarilly for use with error messages to
     * prevent them from becoming too long. This is a simple convenience method to reduce code bloat.
     *
     * @return
     */
    public String getShortFunctionId() {
        return this.getId().getUri().toString()
            .substring(this.getId().getUri().toString().indexOf("function:"));
    }

    /**
     * Returns a shortened version of the given DataType Id, primarily for use with error messages to prevent
     * them from becoming too long. This is a simple convenience method to reduce code bloat.
     *
     * @param identifier expected to have '#' in it, and if no '#' should have ":data-type:"
     * @return
     */
    public String getShortDataTypeId(Identifier identifier) {
        String idString = identifier.stringValue();
        int index = idString.indexOf("#");
        if (index < 0) {
            index = idString.indexOf(":data-type:");
            if (index < 0) {
                return idString;
            } else {
                return idString.substring(index + 11);
            }
        } else {
            return idString.substring(index + 1);
        }
    }

    /**
     * Return a new Status that includes the name of this function in front of the original status' message.
     * This is a convenience method to reduce code bloat.
     *
     * @param originalStatu
     * @return
     */
    public Status getFunctionStatus(Status originalStatus) {
        return new StdStatus(originalStatus.getStatusCode(), getShortFunctionId() + " "
                                                             + originalStatus.getStatusMessage());
    }

    //
    // Getters for the internal variables
    //

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Identifier getDataTypeId() {
        if (this.dataTypeReturn == null) {
            return null;
        } else {
            return this.dataTypeReturn.getId();
        }
    }

    public DataType<O> getDataType() {
        return this.dataTypeReturn;
    }

    /**
     * Return the Identifier for the Input Argument(s) DataType.
     *
     * @return
     */
    public DataType<I> getDataTypeArgs() {
        return this.dataTypeArgs;
    }

    @Override
    public boolean returnsBag() {
        return returnsBag;
    }

}
