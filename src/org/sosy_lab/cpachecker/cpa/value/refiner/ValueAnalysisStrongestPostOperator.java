/*
 * CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2015  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.cpa.value.refiner;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.core.defaults.precision.VariableTrackingPrecision;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.cpa.arg.ARGPath;
import org.sosy_lab.cpachecker.cpa.conditions.path.AssignmentsInPathCondition.UniqueAssignmentsInPathConditionState;
import org.sosy_lab.cpachecker.cpa.interval.UnifyAnalysisState;
import org.sosy_lab.cpachecker.cpa.value.ValueAnalysisTransferRelation;
import org.sosy_lab.cpachecker.cpa.value.symbolic.ConstraintsStrengthenOperator;
import org.sosy_lab.cpachecker.cpa.value.symbolic.SymbolicValueAssigner;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.refinement.StrongestPostOperator;
import org.sosy_lab.cpachecker.util.states.MemoryLocation;

/**
 * Strongest post-operator using {@link ValueAnalysisTransferRelation}.
 */
public class ValueAnalysisStrongestPostOperator implements StrongestPostOperator<UnifyAnalysisState> {

  private final ValueAnalysisTransferRelation transfer;

  public ValueAnalysisStrongestPostOperator(
      final LogManager pLogger,
      final Configuration pConfig,
      final CFA pCfa
  ) throws InvalidConfigurationException {

    transfer =
        new ValueAnalysisTransferRelation(
            pLogger,
            pCfa,
            new ValueAnalysisTransferRelation.ValueTransferOptions(pConfig),
            new SymbolicValueAssigner(pConfig),
            new ConstraintsStrengthenOperator(pConfig),
            null);
  }

  @Override
  public Optional<UnifyAnalysisState> getStrongestPost(
      final UnifyAnalysisState pOrigin,
      final Precision pPrecision,
      final CFAEdge pOperation
  ) throws CPAException {

    final Collection<UnifyAnalysisState> successors =
        transfer.getAbstractSuccessorsForEdge(pOrigin, pPrecision, pOperation);

    if (successors.isEmpty()) {
      return Optional.empty();

    } else {
      return Optional.of(Iterables.getOnlyElement(successors));
    }
  }

  @Override
  public UnifyAnalysisState handleFunctionCall(UnifyAnalysisState state, CFAEdge edge,
      Deque<UnifyAnalysisState> callstack) {
    callstack.push(state);
    return state;
  }

  @Override
  public UnifyAnalysisState handleFunctionReturn(UnifyAnalysisState next, CFAEdge edge,
      Deque<UnifyAnalysisState> callstack) {

    final UnifyAnalysisState callState = callstack.pop();
    return next.rebuildStateAfterFunctionCall(callState, (FunctionExitNode)edge.getPredecessor());
  }

  @Override
  public UnifyAnalysisState performAbstraction(
      final UnifyAnalysisState pNext,
      final CFANode pCurrNode,
      final ARGPath pErrorPath,
      final Precision pPrecision
  ) {

    assert pPrecision instanceof VariableTrackingPrecision;

    VariableTrackingPrecision precision = (VariableTrackingPrecision)pPrecision;

    final boolean performAbstraction = precision.allowsAbstraction();
    final Collection<MemoryLocation> exceedingMemoryLocations =
        obtainExceedingMemoryLocations(pErrorPath);

    if (performAbstraction) {
      for (MemoryLocation memoryLocation : pNext.getTrackedMemoryLocations()) {
        if (!precision.isTracking(memoryLocation,
            pNext.getTypeForMemoryLocation(memoryLocation),
            pCurrNode)) {
          pNext.forget(memoryLocation);
        }
      }
    }

    for (MemoryLocation exceedingMemoryLocation : exceedingMemoryLocations) {
      pNext.forget(exceedingMemoryLocation);
    }

    return pNext;
  }

  protected Set<MemoryLocation> obtainExceedingMemoryLocations(final ARGPath pPath) {
    UniqueAssignmentsInPathConditionState assignments =
        AbstractStates.extractStateByType(pPath.getLastState(),
            UniqueAssignmentsInPathConditionState.class);

    if (assignments == null) {
      return Collections.emptySet();
    }

    return assignments.getMemoryLocationsExceedingThreshold();
  }
}
