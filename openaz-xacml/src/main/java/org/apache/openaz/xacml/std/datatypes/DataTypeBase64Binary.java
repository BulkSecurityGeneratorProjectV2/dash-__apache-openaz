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

package org.apache.openaz.xacml.std.datatypes;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML;

/**
 * DataTypeBase64Binary extends {@link DataTypeBase} for the XACML base64Binary data type.
 */
public class DataTypeBase64Binary extends DataTypeBase<Base64Binary> {
    private static final DataTypeBase64Binary singleInstance = new DataTypeBase64Binary();

    private DataTypeBase64Binary() {
        super(XACML.ID_DATATYPE_BASE64BINARY, Base64Binary.class);
    }

    public static DataTypeBase64Binary newInstance() {
        return singleInstance;
    }

    @Override
    public Base64Binary convert(Object source) throws DataTypeException {
        if (source == null || source instanceof Base64Binary) {
            return (Base64Binary)source;
        } else if (source instanceof byte[]) {
            return new Base64Binary((byte[])source);
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            }
            Base64Binary base64Binary = null;
            try {
                base64Binary = Base64Binary.newInstance(stringValue);
            } catch (Exception ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to Base64Binary", ex);
            }
            return base64Binary;
        }
    }

    @Override
    public String toStringValue(Base64Binary source) throws DataTypeException {
        return (source == null ? null : source.stringValue());
    }

}
