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
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;

/**
 * LegacyPermitOverridesPolicy extends
 * {@link org.apache.openaz.xacml.pdp.policy.combiners.CombiningAlgorithmBase} for
 * {@link org.apache.openaz.xacml.pdp.policy.PolicySetChild} elements implementing the XACML 1.0
 * permit-overrides policy combining algorithm.
 */
public class LegacyPermitOverridesPolicy extends CombiningAlgorithmBase<PolicySetChild> {

    public LegacyPermitOverridesPolicy(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext,
                                    List<CombiningElement<PolicySetChild>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException {
        boolean atLeastOneDeny = false;

        EvaluationResult evaluationResultCombined = new EvaluationResult(Decision.DENY);
        EvaluationResult evaluationResultIndeterminate = null;

        Iterator<CombiningElement<PolicySetChild>> iterElements = elements.iterator();
        while (iterElements.hasNext()) {
            CombiningElement<PolicySetChild> combiningElement = iterElements.next();
            EvaluationResult evaluationResultElement = combiningElement.evaluate(evaluationContext);

            assert evaluationResultElement != null;
            switch (evaluationResultElement.getDecision()) {
            case DENY:
                atLeastOneDeny = true;
                evaluationResultCombined.merge(evaluationResultElement);
                break;
            case INDETERMINATE:
            case INDETERMINATE_DENY:
            case INDETERMINATE_DENYPERMIT:
            case INDETERMINATE_PERMIT:
                if (evaluationResultIndeterminate == null) {
                    evaluationResultIndeterminate = evaluationResultElement;
                } else {
                    evaluationResultIndeterminate.merge(evaluationResultElement);
                }
                break;
            case NOTAPPLICABLE:
                break;
            case PERMIT:
                return evaluationResultElement;
            default:
                throw new EvaluationException("Illegal Decision: \""
                                              + evaluationResultElement.getDecision().toString());
            }

        }

        if (atLeastOneDeny) {
            return evaluationResultCombined;
        } else if (evaluationResultIndeterminate != null) {
            return evaluationResultIndeterminate;
        } else {
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }
    }

}
