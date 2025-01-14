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

package org.apache.openaz.xacml.std;

import java.text.ParseException;
import java.util.Arrays;

import org.apache.openaz.xacml.api.Version;

/**
 * StdVersion implements the {@link org.apache.openaz.xacml.api.Version} interface to represent XACML version
 * identifiers.
 */
public class StdVersion implements Version {
    private int[] versionDigits;
    private String cachedVersionString;

    /**
     * Creates a new <code>StdVersion</code> from the given array of integer digits.
     *
     * @param versionDigitsIn the array of integer digits representing the version
     */
    public StdVersion(int[] versionDigitsIn) {
        this.versionDigits = versionDigitsIn;
    }

    /**
     * Creates a new <code>StdVersion</code> by parsing a <code>String</code> of the form
     * Number"."Number"."Number"..."Number"
     *
     * @param versionString the <code>String</code> representation of the version
     * @return a new <code>StdVersion</code> parsed from the <code>String</code>
     * @throws java.text.ParseException if the <code>String</code> is not a valid <code>Version</code>
     */
    public static StdVersion newInstance(String versionString) throws ParseException {
        if (versionString == null) {
            throw new NullPointerException("Null version string");
        }
        String[] versionParts = versionString.split("[.]", -1);
        if (versionParts == null) {
            throw new ParseException("Invalid version string \"" + versionString + "\"", 0);
        }
        int[] versionNumberParts = new int[versionParts.length];
        for (int i = 0; i < versionParts.length; i++) {
            try {
                versionNumberParts[i] = Integer.parseInt(versionParts[i]);
            } catch (NumberFormatException ex) {
                throw new ParseException("Invalid version number \"" + versionParts[i] + "\"", i);
            }
            if (versionNumberParts[i] < 0) {
                throw new ParseException("Invalid version number \"" + versionParts[i] + "\"", i);
            }
        }
        return new StdVersion(versionNumberParts);
    }

    @Override
    public String getVersion() {
        if (this.cachedVersionString == null) {
            StringBuilder stringBuilder = new StringBuilder();
            int[] versionDigitsHere = this.getVersionDigits();
            if (versionDigitsHere != null && versionDigitsHere.length > 0) {
                stringBuilder.append(versionDigitsHere[0]);
                for (int i = 1; i < versionDigitsHere.length; i++) {
                    stringBuilder.append('.');
                    stringBuilder.append(versionDigitsHere[i]);
                }
            }
            this.cachedVersionString = stringBuilder.toString();
        }
        return this.cachedVersionString;
    }

    @Override
    public int[] getVersionDigits() {
        return this.versionDigits;
    }

    @Override
    public String toString() {
        return this.getVersion();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Version)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            int[] objDigits = ((Version)obj).getVersionDigits();
            int[] thisDigits = this.getVersionDigits();
            if (thisDigits == null || thisDigits.length == 0) {
                if (objDigits == null || objDigits.length == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (objDigits == null || objDigits.length == 0) {
                    return false;
                } else {
                    return Arrays.equals(thisDigits, objDigits);
                }
            }
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getVersionDigits() != null) {
            result = 31 * result + Arrays.hashCode(getVersionDigits());
        }
        return result;
    }

    @Override
    public int compareTo(Version o) {
        /*
         * Comparing two equivalent objects should generate a compare value of 0
         */
        if (o == this || this.equals(o)) {
            return 0;
        } else {
            int[] thisDigits = this.getVersionDigits();
            int[] oDigits = o.getVersionDigits();
            if (thisDigits == null || thisDigits.length == 0) {
                if (oDigits == null || oDigits.length == 0) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (oDigits == null || oDigits.length == 0) {
                    return 1;
                } else {
                    int maxDigits = (thisDigits.length > oDigits.length ? thisDigits.length : oDigits.length);
                    for (int i = 0; i < maxDigits; i++) {
                        if (i < thisDigits.length) {
                            if (i < oDigits.length) {
                                int diff = thisDigits[i] - oDigits[i];
                                if (diff != 0) {
                                    return diff;
                                }
                            } else if (thisDigits[i] > 0) {
                                return 1;
                            }
                        } else {
                            if (oDigits[i] > 0) {
                                return -1;
                            }
                        }
                    }
                    return 0;
                }
            }
        }
    }

    @Override
    public String stringValue() {
        return this.getVersion();
    }
}
