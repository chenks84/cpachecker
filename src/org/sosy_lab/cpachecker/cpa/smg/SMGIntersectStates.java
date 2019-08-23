/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.smg;

import com.google.common.collect.BiMap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.sosy_lab.cpachecker.cpa.smg.graphs.CLangSMG;
import org.sosy_lab.cpachecker.cpa.smg.graphs.SMGHasValueEdges;
import org.sosy_lab.cpachecker.cpa.smg.graphs.UnmodifiableCLangSMG;
import org.sosy_lab.cpachecker.cpa.smg.graphs.edge.SMGEdgeHasValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.edge.SMGEdgeHasValueFilter;
import org.sosy_lab.cpachecker.cpa.smg.graphs.edge.SMGEdgePointsTo;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.SMGNullObject;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.SMGObject;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.SMGObjectKind;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.SMGRegion;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.dll.SMGDoublyLinkedList;
import org.sosy_lab.cpachecker.cpa.smg.graphs.object.sll.SMGSingleLinkedList;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGAddressValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGExplicitValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGKnownExpValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGKnownSymbolicValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGUnknownValue;
import org.sosy_lab.cpachecker.cpa.smg.graphs.value.SMGValue;
import org.sosy_lab.cpachecker.cpa.smg.join.SMGNodeMapping;

public final class SMGIntersectStates {

  /** constant data from input */
  private final UnmodifiableSMGState smgState1;
  private final UnmodifiableSMGState smgState2;
  private final UnmodifiableCLangSMG heap1;
  private final UnmodifiableCLangSMG heap2;

  /** working data, will be modified when calling {@link #intersect}. */
  private final SMGNodeMapping mapping1 = new SMGNodeMapping();
  private final SMGNodeMapping mapping2 = new SMGNodeMapping();
  private final Set<SMGEdgeHasValue> singleHveEdge1 = new HashSet<>();
  private final Set<SMGEdgeHasValue> singleHveEdge2 = new HashSet<>();

  /** the destination SMG will be build up when calling {@link #intersect}. */
  private final CLangSMG destSMG;

  /** the destination values will be build up when calling {@link #intersect}. */
  private final BiMap<SMGKnownSymbolicValue, SMGKnownExpValue> destExplicitValues =
      HashBiMap.create();

  /** initialize the intersection-process. */
  public SMGIntersectStates(UnmodifiableSMGState pSmgState1, UnmodifiableSMGState pSmgState2) {
    smgState1 = pSmgState1;
    smgState2 = pSmgState2;
    heap1 = pSmgState1.getHeap();
    heap2 = pSmgState2.getHeap();

    mapping1.map(SMGNullObject.INSTANCE, SMGNullObject.INSTANCE);
    mapping2.map(SMGNullObject.INSTANCE, SMGNullObject.INSTANCE);

    destSMG = new CLangSMG(heap1.getMachineModel());
  }

  /**
   * compute intersection of two SMGs.
   *
   * <p>Please call this method only once. Repeated calls will return the same result.
   */
  public SMGIntersectionResult intersect() {
    Map<String, SMGRegion> globals_in_smg1 = heap1.getGlobalObjects();
    Map<String, SMGRegion> globals_in_smg2 = heap2.getGlobalObjects();


    Set<String> globalVars = Sets.union(globals_in_smg1.keySet(), globals_in_smg2.keySet());

    for (String globalVar : globalVars) {
      SMGRegion globalInSMG1 = globals_in_smg1.get(globalVar);
      SMGRegion globalInSMG2 = globals_in_smg2.get(globalVar);

      SMGRegion finalObject = globalInSMG1;
      destSMG.addGlobalObject(finalObject);
      mapping1.map(globalInSMG1, finalObject);
      mapping2.map(globalInSMG2, finalObject);
    }

    Iterator<CLangStackFrame> smg1stackIterator = heap1.getStackFrames().iterator();
    Iterator<CLangStackFrame> smg2stackIterator = heap2.getStackFrames().iterator();

    while ( smg1stackIterator.hasNext() && smg2stackIterator.hasNext() ) {
      CLangStackFrame frameInSMG1 = smg1stackIterator.next();
      CLangStackFrame frameInSMG2 = smg2stackIterator.next();

      destSMG.addStackFrame(frameInSMG1.getFunctionDeclaration());

      Set<String> localVars =
          Sets.union(frameInSMG1.getVariables().keySet(), frameInSMG2.getVariables().keySet());

      for (String localVar : localVars) {
        SMGRegion localInSMG1 = frameInSMG1.getVariable(localVar);
        SMGRegion localInSMG2 = frameInSMG2.getVariable(localVar);
        SMGRegion finalObject = localInSMG1;
        destSMG.addStackObject(finalObject);
        mapping1.map(localInSMG1, finalObject);
        mapping2.map(localInSMG2, finalObject);
      }

      SMGObject returnSMG1 = frameInSMG1.getReturnObject();
      SMGObject returnSMG2 = frameInSMG2.getReturnObject();

      if (returnSMG1 == null) {
        continue;
      }

      SMGObject finalObject = destSMG.getFunctionReturnObject();
      mapping1.map(returnSMG1, finalObject);
      mapping2.map(returnSMG2, finalObject);
    }

    for (String globalVar : globalVars) {
      SMGRegion globalInSMG1 = globals_in_smg1.get(globalVar);
      SMGRegion globalInSMG2 = globals_in_smg2.get(globalVar);

      SMGObject finalObject = mapping1.get(globalInSMG1);
      boolean defined = intersectPairFields(globalInSMG1, globalInSMG2, finalObject);

      if (!defined) {
        return SMGIntersectionResult.getNotDefinedInstance();
      }
    }

    smg1stackIterator = heap1.getStackFrames().iterator();
    smg2stackIterator = heap2.getStackFrames().iterator();

    while ( smg1stackIterator.hasNext() && smg2stackIterator.hasNext() ) {
      CLangStackFrame frameInSMG1 = smg1stackIterator.next();
      CLangStackFrame frameInSMG2 = smg2stackIterator.next();

      Set<String> localVars =
          Sets.union(frameInSMG1.getVariables().keySet(), frameInSMG2.getVariables().keySet());

      for (String localVar : localVars) {
        SMGRegion localInSMG1 = frameInSMG1.getVariable(localVar);
        SMGRegion localInSMG2 = frameInSMG2.getVariable(localVar);
        SMGObject finalObject = mapping1.get(localInSMG1);

        boolean defined = intersectPairFields(localInSMG1, localInSMG2, finalObject);

        if (!defined) {
          return SMGIntersectionResult.getNotDefinedInstance();
        }
      }

      SMGObject returnSMG1 = frameInSMG1.getReturnObject();
      SMGObject returnSMG2 = frameInSMG2.getReturnObject();

      if (returnSMG1 == null) {
        continue;
      }

      SMGObject finalObject = destSMG.getFunctionReturnObject();

      boolean defined = intersectPairFields(returnSMG1, returnSMG2, finalObject);

      if (!defined) {
        return SMGIntersectionResult.getNotDefinedInstance();
      }
    }

    for(SMGEdgeHasValue hve1 : singleHveEdge1) {
      intersectHveEdgeWithTop(hve1, heap1, smgState1, mapping1);
    }

    for(SMGEdgeHasValue hve2 : singleHveEdge2) {
      intersectHveEdgeWithTop(hve2, heap2, smgState2, mapping2);
    }

    UnmodifiableSMGState pIntersectResult = smgState1.copyWith(destSMG, destExplicitValues);

    return new SMGIntersectionResult(smgState1, smgState2, pIntersectResult, true);
  }

  private void intersectHveEdgeWithTop(
      SMGEdgeHasValue pHve,
      UnmodifiableCLangSMG pSmg,
      UnmodifiableSMGState pSmgState,
      SMGNodeMapping pMapping) {

    SMGObject destObject = pMapping.get(pHve.getObject());
    SMGValue value = pHve.getValue();

    intersectValueWithTop(value, pSmg, pSmgState, pMapping);

    SMGValue destValue = pMapping.get(value);
    SMGEdgeHasValue destHve = new SMGEdgeHasValue(pSmg.getMachineModel(), pHve.getType(), pHve.getOffset(), destObject, destValue);
    destSMG.addHasValueEdge(destHve);
  }

  private void intersectValueWithTop(
      SMGValue pValue,
      UnmodifiableCLangSMG pSmg,
      UnmodifiableSMGState pSmgState,
      SMGNodeMapping pMapping) {

    if(pMapping.containsKey(pValue)) {
      return;
    }

    pMapping.map(pValue, pValue);

    SMGKnownSymbolicValue symVal = (SMGKnownSymbolicValue) pValue;
    if (pSmgState.isExplicit(symVal)) {
      destExplicitValues.put(symVal, (SMGKnownExpValue) pSmgState.getExplicit(symVal));
    }

    if (pSmg.isPointer(pValue)) {
      intersectPointerWithTop(pSmg.getPointer(pValue), pSmg, pSmgState, pMapping);
    }
  }

  private void intersectPointerWithTop(
      SMGEdgePointsTo pPte,
      UnmodifiableCLangSMG pSmg,
      UnmodifiableSMGState pSmgState,
      SMGNodeMapping pMapping) {
    intersectObjectWithTop(pPte.getObject(), pSmg, pSmgState, pMapping);
    destSMG.addPointsToEdge(pPte);
  }

  private void intersectObjectWithTop(
      SMGObject pObject,
      UnmodifiableCLangSMG pSmg,
      UnmodifiableSMGState pSmgState,
      SMGNodeMapping pMapping) {

    if(pMapping.containsKey(pObject)) {
      return;
    }

    destSMG.addHeapObject(pObject);
    pMapping.map(pObject, pObject);

    for (SMGEdgeHasValue hve : pSmg.getHVEdges(SMGEdgeHasValueFilter.objectFilter(pObject))) {
      intersectHveEdgeWithTop(hve, pSmg, pSmgState, pMapping);
    }
  }

  private boolean intersectPairFields(
      SMGObject pObject1, SMGObject pObject2, SMGObject pDestObject) {

    SMGHasValueEdges hves1 = heap1.getHVEdges(SMGEdgeHasValueFilter.objectFilter(pObject1));
    SMGHasValueEdges hves2 = heap2.getHVEdges(SMGEdgeHasValueFilter.objectFilter(pObject2));

    Map<Long, SMGEdgeHasValue> offsetToHve1Map =
        FluentIterable.from(hves1).uniqueIndex(SMGEdgeHasValue::getOffset);

    Map<Long, SMGEdgeHasValue> offsetToHve2Map =
        FluentIterable.from(hves2).uniqueIndex(SMGEdgeHasValue::getOffset);

    Set<Long> offsetSet = Sets.union(offsetToHve1Map.keySet(), offsetToHve2Map.keySet());

    for (long offset : offsetSet) {
      if (offsetToHve1Map.containsKey(offset)) {
        if (offsetToHve2Map.containsKey(offset)) {
          SMGEdgeHasValue hve1 = offsetToHve1Map.get(offset);
          SMGEdgeHasValue hve2 = offsetToHve2Map.get(offset);
          boolean defined = intersectPairHveEdges(hve1, hve2, pDestObject);

          if (!defined) {
            return false;
          }
        } else {
          SMGEdgeHasValue hve1 = offsetToHve1Map.get(offset);
          singleHveEdge1.add(hve1);
        }
      } else {
        SMGEdgeHasValue hve2 = offsetToHve2Map.get(offset);
        singleHveEdge2.add(hve2);
      }
    }

    return true;
  }

  private boolean intersectPairHveEdges(
      SMGEdgeHasValue pHve1, SMGEdgeHasValue pHve2, SMGObject pDestObject) {

    SMGValue value1 = pHve1.getValue();
    SMGValue value2 = pHve2.getValue();

    boolean defined = intersectValues(value1, value2);

    if (!defined) {
      return false;
    }

    SMGValue destValue = mapping1.get(value1);

    SMGEdgeHasValue destHveEdge =
        new SMGEdgeHasValue(destSMG.getMachineModel(), pHve1.getType(), pHve1.getOffset(), pDestObject, destValue);
    destSMG.addHasValueEdge(destHveEdge);

    return true;
  }

  private boolean intersectValues(SMGValue pValue1, SMGValue pValue2) {

    boolean containsValue1 = mapping1.containsKey(pValue1);
    boolean containsValue2 = mapping2.containsKey(pValue2);

    /*Already intersected*/
    if (containsValue1 && containsValue2) {
      return mapping1.get(pValue1).equals(mapping2.get(pValue2));
    }

    /*Intesect is null due to different values.*/
    if (containsValue1 || containsValue2) {
      return false;
    }

    SMGValue destValue = pValue1;

    mapping1.map(pValue1, destValue);
    mapping2.map(pValue2, destValue);
    destSMG.addValue(destValue);

    boolean isPointer1 = heap1.isPointer(pValue1);
    boolean isPointer2 = heap2.isPointer(pValue2);

    if ((isPointer1 && !isPointer2) || (!isPointer1 && isPointer2)) {
      return false;
    }

    if (isPointer1 && isPointer2) {
      SMGEdgePointsTo pte1 = heap1.getPointer(pValue1);
      SMGEdgePointsTo pte2 = heap2.getPointer(pValue2);

      boolean defined = intersectPairPointsToEdges(pte1, pte2, pte1.getValue());

      if (!defined) {
        return false;
      }
    }

    SMGKnownSymbolicValue symVal1 = (SMGKnownSymbolicValue) pValue1;
    SMGKnownSymbolicValue symVal2 = (SMGKnownSymbolicValue) pValue2;
    SMGKnownSymbolicValue symDestVal = symVal1;

    SMGExplicitValue expVal1 = SMGUnknownValue.INSTANCE;
    SMGExplicitValue expVal2 = SMGUnknownValue.INSTANCE;

    if (smgState1.isExplicit(symVal1)) {
      expVal1 = smgState1.getExplicit(symVal1);
    }

    if (smgState1.isExplicit(symVal2)) {
      expVal2 = smgState1.getExplicit(symVal2);
    }

    if (!expVal1.isUnknown() && !expVal2.isUnknown()) {
      if (expVal1.equals(expVal2)) {
        destExplicitValues.put(symDestVal, (SMGKnownExpValue) expVal1);
      } else {
        return false;
      }
    } else if (!expVal1.isUnknown()) {
      destExplicitValues.put(symDestVal, (SMGKnownExpValue) expVal1);
    } else if (!expVal2.isUnknown()) {
      destExplicitValues.put(symDestVal, (SMGKnownExpValue) expVal2);
    }

    return true;
  }

  private boolean intersectPairPointsToEdges(
      SMGEdgePointsTo pPte1, SMGEdgePointsTo pPte2, SMGAddressValue destValue) {

    long offset1 = pPte1.getOffset();
    long offset2 = pPte2.getOffset();

    if (offset1 != offset2) {
      return false;
    }

    SMGTargetSpecifier tg1 = pPte1.getTargetSpecifier();
    SMGTargetSpecifier tg2 = pPte2.getTargetSpecifier();

    // TODO Imprecise intersect, handle abstraction correctly
    if (tg1 != tg2) {
      return false;
    }

    SMGObject obj1 = pPte1.getObject();
    SMGObject obj2 = pPte2.getObject();

    boolean defined = intersectObjectPair(obj1, obj2);

    if (!defined) {
      return false;
    }

    SMGObject destObject = mapping1.get(obj1);

    SMGEdgePointsTo destPte = new SMGEdgePointsTo(destValue, destObject, offset1, tg1);
    destSMG.addPointsToEdge(destPte);

    return true;
  }

  private boolean intersectObjectPair(SMGObject pObj1, SMGObject pObj2) {

    boolean containsObject1 = mapping1.containsKey(pObj1);
    boolean containsObject2 = mapping2.containsKey(pObj2);

    /*Already intersected*/
    if (containsObject1 && containsObject2) {
      return mapping1.get(pObj1).equals(mapping2.get(pObj2));
    }

    /*Intesect is null due to different pointer.*/
    if (containsObject1 || containsObject2) {
      return false;
    }

    boolean match = matchObject(pObj1, pObj2);

    if (!match) {
      return false;
    }

    SMGObject destObject = getConcretestObject(pObj1, pObj2);

    mapping1.map(pObj1, destObject);
    mapping2.map(pObj2, destObject);

    /*Global and stack objects already mapped */
    destSMG.addHeapObject(destObject);

    boolean defined = intersectPairFields(pObj1, pObj2, destObject);

    return defined;
  }

  private static boolean matchObject(SMGObject pObj1, SMGObject pObj2) {

    if (pObj1.getSize() != pObj2.getSize()) {
      return false;
    }

    if (pObj1.getLevel() != pObj2.getLevel()) {
      return false;
    }

    if (pObj1.getKind() != pObj2.getKind()) {

      switch (pObj1.getKind()) {
        case OPTIONAL:
          switch (pObj2.getKind()) {
            case REG:
            case DLL:
            case SLL:
              return true;
            default:
              return false;
          }
        case REG:
        case DLL:
        case SLL:
          return pObj2.getKind() == SMGObjectKind.OPTIONAL;
        default:
          return false;
      }
    }

    switch (pObj1.getKind()) {
      case DLL:
        return ((SMGDoublyLinkedList) pObj1).matchSpecificShape((SMGDoublyLinkedList) pObj2);
      case SLL:
        return ((SMGSingleLinkedList) pObj1).matchSpecificShape((SMGSingleLinkedList) pObj2);
      case GENERIC:
        //TODO match generic
        return pObj1.equals(pObj2);
      default:
        return true;
    }
  }

  private static SMGObject getConcretestObject(SMGObject pObj1, SMGObject pObj2) {

    /*Determine which object results in the least amount of concrete states
     * if included in a smg state.*/

    if (!pObj1.isAbstract()) {
      return pObj1;
    }

    if (!pObj2.isAbstract()) {
      return pObj2;
    }

    SMGObjectKind kind1 = pObj1.getKind();
    SMGObjectKind kind2 = pObj2.getKind();

    if (kind1 == SMGObjectKind.OPTIONAL) {
      return pObj2;
    }

    if (kind2 == SMGObjectKind.OPTIONAL) {
      return pObj1;
    }

    if (kind1 == kind2) {
      switch (kind1) {
        case DLL:
          int length1 = ((SMGDoublyLinkedList) pObj1).getMinimumLength();
          int length2 = ((SMGDoublyLinkedList) pObj2).getMinimumLength();
          return length1 < length2 ? pObj2 : pObj1;
        case SLL:
          length1 = ((SMGSingleLinkedList) pObj1).getMinimumLength();
          length2 = ((SMGSingleLinkedList) pObj2).getMinimumLength();
          return length1 < length2 ? pObj2 : pObj1;
        default:
          return pObj1;
      }
    }

    return pObj1;
  }

  public static class SMGIntersectionResult {
    private static final SMGIntersectionResult NOT_DEFINED = new SMGIntersectionResult(null, null, null, false);
    private final UnmodifiableSMGState smg1;
    private final UnmodifiableSMGState smg2;
    private final UnmodifiableSMGState combinationResult;
    private final boolean defined;

    public SMGIntersectionResult(
        UnmodifiableSMGState pSmg1,
        UnmodifiableSMGState pSmg2,
        UnmodifiableSMGState pJoinResult,
        boolean pDefined) {
      super();
      smg1 = pSmg1;
      smg2 = pSmg2;
      combinationResult = pJoinResult;
      defined = pDefined;
    }

    public UnmodifiableSMGState getSmg1() {
      return smg1;
    }

    public UnmodifiableSMGState getSmg2() {
      return smg2;
    }

    public boolean isDefined() {
      return defined;
    }

    public UnmodifiableSMGState getCombinationResult() {
      return combinationResult;
    }

    public static SMGIntersectionResult getNotDefinedInstance() {
      return NOT_DEFINED;
    }
  }
}