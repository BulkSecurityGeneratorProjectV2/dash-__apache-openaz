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

package org.apache.openaz.xacml.pdp.std.combiners;

import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.policy.CombinerParameter;
import org.apache.openaz.xacml.pdp.policy.CombiningElement;

/**
 * PermitUnlessDeny implements the XACML 3.0 "permit-unless-deny" combining algorithm for both policies and
 * rules.
 *
 * @param <T> the java class for the {@link org.apache.openaz.xacml.pdp.eval.Evaluatable}
 * @param <U> the java class for the identifier
 */
public class PermitUnlessDeny<T extends org.apache.openaz.xacml.pdp.eval.Evaluatable> extends 
    CombiningAlgorithmBase<T> {

    public PermitUnlessDeny(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<T>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException {
        EvaluationResult combinedResult = new EvaluationResult(Decision.PERMIT);

        Iterator<CombiningElement<T>> iterElements = elements.iterator();
        while (iterElements.hasNext()) {
            CombiningElement<T> combiningElement = iterElements.next();
            EvaluationResult evaluationResultElement = combiningElement.evaluate(evaluationContext);

            assert evaluationResultElement != null;
            switch (evaluationResultElement.getDecision()) {
            case DENY:
                return evaluationResultElement;
            case INDETERMINATE:
            case INDETERMINATE_DENYPERMIT:
            case INDETERMINATE_DENY:
            case INDETERMINATE_PERMIT:
                break;
            case NOTAPPLICABLE:
                break;
            case PERMIT:
                combinedResult.merge(evaluationResultElement);
                break;
            default:
                throw new EvaluationException("Illegal Decision: \""
                                              + evaluationResultElement.getDecision().toString());
            }
        }

        return combinedResult;
    }

}
