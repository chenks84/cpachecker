/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.thread;

import static com.google.common.collect.FluentIterable.from;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.sosy_lab.cpachecker.cfa.ast.c.CThreadOperationStatement.CThreadCreateStatement;
import org.sosy_lab.cpachecker.cfa.ast.c.CThreadOperationStatement.CThreadJoinStatement;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.LatticeAbstractState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractStateWithLocation;
import org.sosy_lab.cpachecker.core.interfaces.AbstractWrapperState;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.cpa.callstack.CallstackState;
import org.sosy_lab.cpachecker.cpa.location.LocationState;
import org.sosy_lab.cpachecker.cpa.thread.ThreadLabel.LabelStatus;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.util.Pair;


public class ThreadState implements LatticeAbstractState<ThreadState>, AbstractStateWithLocation, Partitionable,
    AbstractWrapperState, Comparable<ThreadState> {

  public static class ThreadStateBuilder {
    private List<ThreadLabel> tSet;
    private List<ThreadLabel> rSet;

    private ThreadStateBuilder(ThreadState state) {
      tSet = new LinkedList<>(state.threadSet);
      rSet = new LinkedList<>(state.removedSet);
    }

    public void handleParentThread(CThreadCreateStatement tCall) throws CPATransferException {
      createThread(tCall, LabelStatus.PARENT_THREAD);
    }

    public void handleChildThread(CThreadCreateStatement tCall) throws CPATransferException {
      createThread(tCall, tCall.isSelfParallel() ? LabelStatus.SELF_PARALLEL_THREAD : LabelStatus.CREATED_THREAD);
    }

    private void createThread(CThreadCreateStatement tCall, LabelStatus pParentThread) throws CPATransferException {
      final String pVarName = tCall.getVariableName();
      //Just to info
      final String pFunctionName = tCall.getFunctionCallExpression().getFunctionNameExpression().toASTString();

      if (from(tSet)
          .anyMatch(l -> l.getName().equals(pFunctionName) && l.getVarName().equals(pVarName))) {
        throw new CPATransferException("Can not create thread " + pFunctionName + ", it was already created");
      }

      ThreadLabel label = new ThreadLabel(pFunctionName, pVarName, pParentThread);
      if (!tSet.isEmpty() && tSet.get(tSet.size() - 1).isSelfParallel()) {
        //Can add only the same status
        label = label.toSelfParallelLabel();
      }
      tSet.add(label);
    }

    public ThreadState build(LocationState l, CallstackState c) {
      //May be called several times per one builder
      return new ThreadState(l, c, tSet, rSet);
    }

    public boolean joinThread(CThreadJoinStatement jCall) {
      String pVarName = jCall.getVariableName();
      ThreadLabel result = null;
      for (ThreadLabel tmpLabel : tSet) {
        if (tmpLabel.getVarName().equals(pVarName)) {
          assert result == null : "Found several threads with the same variable";
          assert tmpLabel.isParentThread() : "Try to self-join";
          result = tmpLabel;
        }
      }
      if (result == null) {
        return false;
      } else {
        return tSet.remove(result);
      }
    }

    public int getThreadSize() {
      //Only for statistics
      return tSet.size();
    }
  }

  private final LocationState location;
  private final CallstackState callstack;
  private final ImmutableList<ThreadLabel> threadSet;
  private final ImmutableList<ThreadLabel> removedSet;

  private ThreadState(LocationState l, CallstackState c, List<ThreadLabel> Tset, List<ThreadLabel> Rset) {
    location = l;
    callstack = c;
    threadSet = ImmutableList.copyOf(Tset);
    removedSet = Rset == null ? null : ImmutableList.copyOf(Rset);
  }

  public String getCurrentThreadName() {
    //Info method, in difficult cases may be wrong
    Optional<ThreadLabel> createdThread =
        from(threadSet)
        .firstMatch(ThreadLabel::isCreatedThread);

    if (createdThread.isPresent()) {
      return createdThread.get().getName();
    } else {
      return "";
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Objects.hashCode(callstack);
    result = prime * result + Objects.hashCode(location);
    result = prime * result + Objects.hashCode(removedSet);
    result = prime * result + Objects.hashCode(threadSet);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null ||
        getClass() != obj.getClass()) {
      return false;
    }
    ThreadState other = (ThreadState) obj;
    return Objects.equals(callstack, other.callstack)
        && Objects.equals(location, other.location)
        && Objects.equals(removedSet, other.removedSet)
        && Objects.equals(threadSet, other.threadSet);
  }

  @Override
  public Object getPartitionKey() {
    List<Object> keys = new ArrayList<>(2);
    keys.add(location.getPartitionKey());
    keys.add(callstack.getPartitionKey());
    return keys;
  }

  @Override
  public CFANode getLocationNode() {
    return location.getLocationNode();
  }

  @Override
  public Iterable<CFANode> getLocationNodes() {
    return location.getLocationNodes();
  }

  @Override
  public Iterable<CFAEdge> getOutgoingEdges() {
    return location.getOutgoingEdges();
  }

  @Override
  public Iterable<CFAEdge> getIngoingEdges() {
    return location.getIngoingEdges();
  }

  @Override
  public Iterable<AbstractState> getWrappedStates() {
    List<AbstractState> states = new ArrayList<>(2);
    states.add(location);
    states.add(callstack);
    return states;
  }

  public List<ThreadLabel> getThreadSet() {
    return threadSet;
  }

  public List<ThreadLabel> getRemovedSet() {
    return removedSet;
  }

  public LocationState getLocationState() {
    return location;
  }

  public CallstackState getCallstackState() {
    return callstack;
  }

  @Override
  public int compareTo(ThreadState other) {
    int result = 0;

    result = other.threadSet.size() - this.threadSet.size(); //decreasing queue

    if (result != 0) {
      return result;
    }

    //Sizes are equal
    for (Pair<ThreadLabel, ThreadLabel> pair : Pair.zipList(threadSet, other.threadSet)) {
      result = pair.getFirst().compareTo(pair.getSecond());
      if (result != 0) {
        return result;
      }
    }
    //Use compare only for StoredThreadState
    Preconditions.checkArgument(location == null && callstack == null);
    return 0;
  }

  public boolean isCompatibleWith(ThreadState other) {
    for (ThreadLabel label : threadSet) {
      if (from(other.threadSet)
            .anyMatch(l -> label.isCompatibleWith(l))) {
        return true;
      }
    }
    return false;
  }

  public ThreadState prepareToStore() {
    return new StoredThreadState(this);
  }

  public ThreadStateBuilder getBuilder() {
    return new ThreadStateBuilder(this);
  }

  public static ThreadState emptyState(LocationState l, CallstackState c) {
    List<ThreadLabel> emptySet = new LinkedList<>();
    return new ThreadState(l, c, emptySet, emptySet);
  }

  @Override
  public String toString() {
    return getCurrentThreadName();
  }

  public static class StoredThreadState extends ThreadState {
    StoredThreadState(ThreadState origin) {
      super(null, null, origin.threadSet, null);
    }
  }

  @Override
  public ThreadState join(ThreadState pOther) {
    Preconditions.checkArgument(false, "Join of Thread states is not supported");
    return null;
  }

  @Override
  public boolean isLessOrEqual(ThreadState pOther) throws CPAException, InterruptedException {
    return Objects.equals(removedSet, pOther.removedSet)
        && pOther.threadSet.containsAll(threadSet)
        && location.equals(pOther.location)
        && callstack.equals(pOther.callstack);
  }
}