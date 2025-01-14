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


package org.apache.openaz.xacml.pdp.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.trace.StdTraceEvent;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * Policy extends {@link org.apache.openaz.xacml.pdp.policy.PolicyDef} to represent a XACML 3.0 Policy
 * element.
 */
public class Policy extends PolicyDef {
    private TargetedCombinerParameterMap<String, Rule> ruleCombinerParameters = new TargetedCombinerParameterMap<String, Rule>();
    private VariableMap variableMap = new VariableMap();
    private List<Rule> rules = new ArrayList<Rule>();
    private List<CombiningElement<Rule>> combiningRules;
    private CombiningAlgorithm<Rule> ruleCombiningAlgorithm;

    @Override
    protected boolean validateComponent() {
        if (super.validateComponent()) {
            if (this.getRuleCombiningAlgorithm() == null) {
                this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing rule combining algorithm");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Performs lazy evaluation of the combining parameters from this <code>Policy</code>.
     *
     * @return the <code>List</code> of <code>CombiningElement</code>s for all of the <code>Rule</code>s
     */
    protected List<CombiningElement<Rule>> getCombiningRules() {
        if (this.combiningRules == null) {
            this.combiningRules = new ArrayList<CombiningElement<Rule>>();
            Iterator<Rule> iterRules = this.getRules();
            while (iterRules.hasNext()) {
                Rule rule = iterRules.next();
                this.combiningRules.add(new CombiningElement<Rule>(rule, this.ruleCombinerParameters
                    .getCombinerParameters(rule)));
            }
        }
        return this.combiningRules;
    }

    public Policy(PolicySet policySetParent, StatusCode statusCodeIn, String statusMessageIn) {
        super(policySetParent, statusCodeIn, statusMessageIn);
    }

    public Policy(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Policy(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Policy(PolicySet policySetParent) {
        super(policySetParent);
    }

    public Policy() {
        super();
        ruleCombinerParameters.getTargetedCombinerParameters();
    }

    /**
     * Gets an <code>Iterator</code> over the
     * {@link org.apache.openaz.xacml.pdp.policy.TargetedCombinerParameter}s for the {@link Rule}s in this
     * <code>Policy</code>.
     *
     * @return an <code>Iterator</code> over the <code>TargetedCombinerParameter</code>s for this
     *         <code>Policy</code>.
     */
    public Iterator<TargetedCombinerParameter<String, Rule>> getRuleCombinerParameters() {
        return this.ruleCombinerParameters.getTargetedCombinerParameters();
    }

    /**
     * Sets the Rule combiner parameters for this <code>Policy</code> to the contents of the given
     * <code>Collection</code> of <code>TargetedCombinerParameter</code>s.
     *
     * @param ruleCombinerParameters the <code>Collection</code> of <code>TargetedCombinerParameter</code>s to
     *            set
     */
    public void setRuleCombinerParameters(Collection<TargetedCombinerParameter<String, Rule>> ruleCombinerParameters) {
        this.ruleCombinerParameters.setCombinerParameters(ruleCombinerParameters);
    }

    /**
     * Adds the given <code>TargetedCombinerParameter</code> to the set of Rule combiner parameters for this
     * <code>Policy</code>.
     * 
     * @param ruleCombinerParameter the <code>TargetedCombinerParameter</code> for <code>Rule</code>s to add.
     */
    public void addRuleCombinerParameter(TargetedCombinerParameter<String, Rule> ruleCombinerParameter) {
        this.ruleCombinerParameters.addCombinerParameter(ruleCombinerParameter);
    }

    /**
     * Adds the contents of the given <code>Collection</code> of <code>TargetedCombinerParameter</code>s to
     * the set of Rule combiner parameters for this <code>Policy</code>.
     *
     * @param ruleCombinerParameters the <code>Collection</code> of <code>TargetedCombinerParameter</code>s to
     *            add
     */
    public void addRuleCombinerParameters(Collection<TargetedCombinerParameter<String, Rule>> ruleCombinerParameters) {
        this.ruleCombinerParameters.addCombinerParameters(ruleCombinerParameters);
    }

    /**
     * Gets an <code>Iterator</code> over the {@link org.apache.openaz.xacml.pdp.policy.VariableDefinition}s
     * in this <code>Policy</code>.
     *
     * @return an <code>Iterator</code> over the <code>VariableDefinition</code>s in this <code>Policy</code>
     */
    public Iterator<VariableDefinition> getVariableDefinitions() {
        return this.variableMap.getVariableDefinitions();
    }

    /**
     * Gets the <code>VariableDefinition</code> for the given <code>String</code> variable identifier.
     *
     * @param variableId the <code>String</code> variable identifier
     * @return the <code>VariableDefinition</code> with the given <code>String</code> identifier or null if
     *         not found
     */
    public VariableDefinition getVariableDefinition(String variableId) {
        return this.variableMap.getVariableDefinition(variableId);
    }

    /**
     * Sets the <code>VariableDefinition</code>s in this <code>Policy</code> to the contents of the given
     * <code>Collection</code>.
     *
     * @param listVariableDefinitions the <code>Collection</code> of <code>VariableDefinition</code>s to set
     */
    public void setVariableDefinitions(Collection<VariableDefinition> listVariableDefinitions) {
        this.variableMap.setVariableDefinitions(listVariableDefinitions);
    }

    /**
     * Adds the given <code>VariableDefinition</code> to the set of <code>VariableDefinition</code>s for this
     * <code>Policy</code>.
     *
     * @param variableDefinition the <code>VariableDefinition</code> to add
     */
    public void addVariableDefinition(VariableDefinition variableDefinition) {
        this.variableMap.add(variableDefinition);
    }

    /**
     * Adds the contents of the given <code>Collection</code> of <code>VariableDefinition</code>s to this
     * <code>Policy</code>.
     *
     * @param variableDefinitions the <code>Collection</code> of <code>VariableDefinition</code>s to add.
     */
    public void addVariableDefinitions(Collection<VariableDefinition> variableDefinitions) {
        this.variableMap.addVariableDefinitions(variableDefinitions);
    }

    /**
     * Gets an <code>Iterator</code> over the <code>Rule</code>s in this <code>Policy</code> or null if there
     * are none.
     *
     * @return an <code>Iterator</code> over the <code>Rule</code>s in this <code>Policy</code> or null if
     *         there are none.
     */
    public Iterator<Rule> getRules() {
        return this.rules.iterator();
    }

    /**
     * Sets the <code>Rule</code>s for this <code>Policy</code> to the contents of the given
     * <code>Collection</code> of <code>Rule</code>s. If the <code>Collection</code> is null, the set of
     * <code>Rule</code>s for this <code>Policy</code> is set to null.
     *
     * @param listRules the <code>Collection</code> of <code>Rule</code>s or null
     */
    public void setRules(Collection<Rule> listRules) {
        this.rules.clear();
        if (listRules != null) {
            this.addRules(listRules);
        }
    }

    /**
     * Adds the given <code>Rule</code> to the set of <code>Rule</code>s for this <code>Policy</code>.
     *
     * @param rule the <code>Rule</code> to add
     */
    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    /**
     * Adds the contents of the given <code>Collection</code> of <code>Rule</code>s to the set of
     * <code>Rule</code>s for this <code>Policy</code>.
     *
     * @param listRules the <code>Collection</code> of <code>Rule</code>s to add
     */
    public void addRules(Collection<Rule> listRules) {
        this.rules.addAll(listRules);
    }

    /**
     * Gets the <code>CombiningAlgorithm</code> for <code>Rule</code>s for this <code>Policy</code>.
     *
     * @return the <code>CombiningAlgorithm</code> for <code>Rule</code>s for this <code>Policy</code>.
     */
    public CombiningAlgorithm<Rule> getRuleCombiningAlgorithm() {
        return this.ruleCombiningAlgorithm;
    }

    /**
     * Sets the <code>CombiningAlgorithm</code> for <code>Rule</code>s for this <code>Policy</code>>
     *
     * @param ruleCombiningAlgorithmIn the <code>CombiningAlgorithm</code> for <code>Rule</code>s for this
     *            <code>Policy</code>
     */
    public void setRuleCombiningAlgorithm(CombiningAlgorithm<Rule> ruleCombiningAlgorithmIn) {
        this.ruleCombiningAlgorithm = ruleCombiningAlgorithmIn;
    }

    @Override
    public EvaluationResult evaluate(EvaluationContext evaluationContext) throws EvaluationException {
        /*
         * First check to see if we are valid. If not, return an error status immediately
         */
        if (!this.validate()) {
            return new EvaluationResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * See if we match
         */
        MatchResult thisMatchResult = this.match(evaluationContext);
        assert thisMatchResult != null;
        if (evaluationContext.isTracing()) {
            evaluationContext.trace(new StdTraceEvent<MatchResult>("Match", this, thisMatchResult));
        }
        switch (thisMatchResult.getMatchCode()) {
        case INDETERMINATE:
            return new EvaluationResult(Decision.INDETERMINATE, thisMatchResult.getStatus());
        case MATCH:
            break;
        case NOMATCH:
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }

        /*
         * Get the combining elements
         */
        List<CombiningElement<Rule>> ruleCombiningElements = this.getCombiningRules();
        assert ruleCombiningElements != null;

        /*
         * Run the combining algorithm
         */
        assert this.getRuleCombiningAlgorithm() != null;
        EvaluationResult evaluationResultCombined = this.getRuleCombiningAlgorithm()
            .combine(evaluationContext, ruleCombiningElements, this.getCombinerParameterList());
        assert evaluationResultCombined != null;

        if (evaluationResultCombined.getDecision() == Decision.DENY
            || evaluationResultCombined.getDecision() == Decision.PERMIT) {
            this.updateResult(evaluationResultCombined, evaluationContext);

            /*
             * Add my id to the policy identifiers
             */
            if (evaluationContext.getRequest().getReturnPolicyIdList()) {
                evaluationResultCombined.addPolicyIdentifier(this.getIdReference());
            }
        }
        if (evaluationContext.isTracing()) {
            evaluationContext.trace(new StdTraceEvent<Result>("Result", this, evaluationResultCombined));
        }
        return evaluationResultCombined;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        String iteratorToDump;
        if ((iteratorToDump = StringUtils.toString(this.getRuleCombinerParameters())) != null) {
            stringBuilder.append(",ruleCombinerParameters=");
            stringBuilder.append(iteratorToDump);
        }
        if ((iteratorToDump = StringUtils.toString(this.getVariableDefinitions())) != null) {
            stringBuilder.append(",variableDefinitions=");
            stringBuilder.append(iteratorToDump);
        }
        if ((iteratorToDump = StringUtils.toString(this.getRules())) != null) {
            stringBuilder.append(",rules=");
            stringBuilder.append(iteratorToDump);
        }

        Object objectToDump;
        if ((objectToDump = this.getRuleCombiningAlgorithm()) != null) {
            stringBuilder.append(",ruleCombiningAlgorithm=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
