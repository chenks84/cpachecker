/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2010  Dirk Beyer
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
package org.sosy_lab.cpachecker.fllesh.cpa.edgevisit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.sosy_lab.cpachecker.cfa.objectmodel.CFAEdge;
import org.sosy_lab.cpachecker.cfa.objectmodel.CFAFunctionDefinitionNode;
import org.sosy_lab.cpachecker.core.defaults.AbstractCPAFactory;
import org.sosy_lab.cpachecker.core.defaults.FlatLatticeDomain;
import org.sosy_lab.cpachecker.core.defaults.MergeJoinOperator;
import org.sosy_lab.cpachecker.core.defaults.SingletonPrecision;
import org.sosy_lab.cpachecker.core.defaults.StaticPrecisionAdjustment;
import org.sosy_lab.cpachecker.core.interfaces.AbstractElement;
import org.sosy_lab.cpachecker.core.interfaces.AbstractQueryableElement;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.core.interfaces.MergeOperator;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustment;
import org.sosy_lab.cpachecker.core.interfaces.StopOperator;
import org.sosy_lab.cpachecker.core.interfaces.TransferRelation;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.exceptions.InvalidQueryException;

/**
 * @author holzera
 *
 * This CPA tracks which CFA edge was taken.
 * CAUTION: The used stop operator violates the over-approximation
 * criterion: It always returns true because we do not want to
 * interfere with the stop operators of other CPAs. This CPA should
 * only serve as a simple oracle for queries of the observer
 * automaton CPA.
 *
 */
public class EdgeVisitCPA implements ConfigurableProgramAnalysis {

  private class TransferException extends CPATransferException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TransferException(CFAEdge pCfaEdge) {
      super("EdgeVisitCPA has not found a corresponding EdgeElement: " + pCfaEdge.toString());
    }
  }
  
  private final StopOperator mStopOperator = new StopOperator() {

    @Override
    public boolean stop(AbstractElement pElement,
        Collection<AbstractElement> pReached, Precision pPrecision)
        throws CPAException {
      // In order to not interfere with the stop operators of other
      // CPAs we always are willing to stop.
      return true;
    }

    @Override
    public boolean stop(AbstractElement pElement,
        AbstractElement pReachedElement) throws CPAException {
      // In order to not interfere with the stop operators of other
      // CPAs we always are willing to stop.
      return true;
    }

  };

  private final TransferRelation mTransferRelation = new TransferRelation() {

    @Override
    public Collection<? extends AbstractElement> getAbstractSuccessors(
        AbstractElement pElement, Precision pPrecision, CFAEdge pCfaEdge)
        throws CPATransferException {

      if (pElement.equals(mDomain.getBottomElement())) {
        return Collections.emptySet();
      }
      
      EdgeElement lEdgeElement = mElements.get(pCfaEdge);
      
      if (lEdgeElement == null) {
        throw new TransferException(pCfaEdge);
      }
      
      return Collections.singleton(mElements.get(pCfaEdge));
    }

    @Override
    public Collection<? extends AbstractElement> strengthen(
        AbstractElement pElement, List<AbstractElement> pOtherElements,
        CFAEdge pCfaEdge, Precision pPrecision) throws CPATransferException {
      // We do no strengthening.
      return null;
    }

  };

  public static abstract class EdgeVisitElement implements AbstractQueryableElement {

    @Override
    public String getCPAName() {
      return "edgevisit";
    }

    @Override
    public boolean isError() {
      return false;
    }
    
    @Override
    public void modifyProperty(String pModification)
        throws InvalidQueryException {
      throw new InvalidQueryException("The EdgeVisit CPA does not (yet) support modification.");
      
    }

  }

  public static class TopElement extends EdgeVisitElement {

    private final static TopElement mInstance = new TopElement();

    private TopElement() {

    }

    public static TopElement getInstance() {
      return mInstance;
    }

    @Override
    public boolean checkProperty(String pProperty) throws InvalidQueryException {
      // We do not know which edge we have taken, so we return false.
      return false;
    }

    @Override
    public String toString() {
      return "<EdgeVisit - Top-Element>";
    }

  }

  public static class BottomElement extends EdgeVisitElement {

    private final static BottomElement mInstance = new BottomElement();

    private BottomElement() {

    }

    public static BottomElement getInstance() {
      return mInstance;
    }

    @Override
    public boolean checkProperty(String pProperty) throws InvalidQueryException {
      // We do not know which edge we have taken, so we return false.
      return false;
    }

    @Override
    public String toString() {
      return "<EdgeVisit - Bottom-Element>";
    }

  }

  public static class NoEdgeElement extends EdgeVisitElement {

    private final static NoEdgeElement mInstance = new NoEdgeElement();

    private NoEdgeElement() {

    }

    public static NoEdgeElement getInstance() {
      return mInstance;
    }

    @Override
    public boolean checkProperty(String pProperty) throws InvalidQueryException {
      // We have not visited a CFA edge, therefore we return false.
      return false;
    }

    @Override
    public String toString() {
      return "<VisitEdge - No Edge>";
    }

  }

  public static class EdgeElement extends EdgeVisitElement {

    private Set<String> mAnnotations;

    private final String mId;

    public EdgeElement(String pId) {
      mId = pId;
      mAnnotations = Collections.emptySet();
    }

    public EdgeElement(String pId, Set<String> pAnnotations) {
      mId = pId;
      mAnnotations = pAnnotations;
    }

    @Override
    public boolean checkProperty(String pProperty) throws InvalidQueryException {
      if (pProperty.startsWith("E")) {
        return mId.equals(pProperty);
      }
      else {
        return mAnnotations.contains(pProperty);
      }
    }

    @Override
    public String toString() {
      return "<VisitEdge - " + mId + ">";
    }
  }

  public static class Factory extends AbstractCPAFactory {

    Annotations mAnnotations;

    public Factory(Annotations pAnnotations) {
      mAnnotations = pAnnotations;
    }

    public String getId(CFAEdge pEdge) {
      return mAnnotations.getId(pEdge);
    }

    @Override
    public ConfigurableProgramAnalysis createInstance() throws CPAException {
      return new EdgeVisitCPA(mAnnotations);
    }

  }

  private final FlatLatticeDomain mDomain;
  private final SingletonPrecision mPrecision;
  private final PrecisionAdjustment mPrecisionAdjustment;
  private final MergeJoinOperator mMergeOperator;

  private final HashMap<CFAEdge, EdgeElement> mElements;

  public EdgeVisitCPA(Annotations pAnnotations) {
    mDomain = new FlatLatticeDomain(TopElement.getInstance(), BottomElement.getInstance());
    mPrecision = SingletonPrecision.getInstance();
    mPrecisionAdjustment = StaticPrecisionAdjustment.getInstance();
    mMergeOperator = new MergeJoinOperator(mDomain.getJoinOperator());

    mElements = new HashMap<CFAEdge, EdgeElement>();

    for (CFAEdge lEdge : pAnnotations.getCFAEdges()) {
      mElements.put(lEdge, new EdgeElement(pAnnotations.getId(lEdge), pAnnotations.getAnnotations(lEdge)));
    }
  }

  @Override
  /*
   * We want to use our always-stop-operator.
   */
  public StopOperator getStopOperator() {
    return mStopOperator;
  }

  @Override
  public NoEdgeElement getInitialElement(CFAFunctionDefinitionNode pNode) {
    // Initially, we have not visited any CFA edge.
    return NoEdgeElement.getInstance();
  }

  @Override
  public FlatLatticeDomain getAbstractDomain() {
    return mDomain;
  }

  @Override
  public Precision getInitialPrecision(CFAFunctionDefinitionNode pNode) {
    return mPrecision;
  }

  @Override
  public PrecisionAdjustment getPrecisionAdjustment() {
    return mPrecisionAdjustment;
  }

  @Override
  public MergeOperator getMergeOperator() {
    return mMergeOperator;
  }

  @Override
  public TransferRelation getTransferRelation() {
    return mTransferRelation;
  }

}
