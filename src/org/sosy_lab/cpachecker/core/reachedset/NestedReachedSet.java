/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
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
package org.sosy_lab.cpachecker.core.reachedset;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import org.sosy_lab.cpachecker.cfa.model.CFANode;

public interface NestedReachedSet<R> extends Iterable<R> {

  public Collection<R> asCollection();

  @Override
  public Iterator<R> iterator();

  /**
   * Returns a subset of the reached set, which contains at least all abstract
   * states belonging to the same location as a given state. It may even
   * return an empty set if there are no such states. Note that it may return up to
   * all abstract states.
   *
   * The returned set is a view of the actual data, so it might change if nodes
   * are added to the reached set. Subsequent calls to this method with the same
   * parameter value will always return the same object.
   *
   * The returned set is unmodifiable.
   *
   * @param state An abstract state for whose location the abstract states should be retrieved.
   * @return A subset of the reached set.
   */
  public Collection<R> getReached(R state)
      throws UnsupportedOperationException;

  /**
   * Returns a subset of the reached set, which contains at least all abstract
   * states belonging to given location. It may even
   * return an empty set if there are no such states. Note that it may return up to
   * all abstract states.
   *
   * The returned set is a view of the actual data, so it might change if nodes
   * are added to the reached set. Subsequent calls to this method with the same
   * parameter value will always return the same object.
   *
   * The returned set is unmodifiable.
   *
   * @param location A location
   * @return A subset of the reached set.
   */
  public Collection<R> getReached(CFANode location);

  /**
   * Returns the first state that was added to the reached set.
   * @throws IllegalStateException If the reached set is empty.
   */
  public R getFirstState();

  /**
   * Returns the last state that was added to the reached set.
   * May be null if it is unknown, which state was added last.
   */
  public @Nullable R getLastState();

  public boolean contains(R state);

  public boolean isEmpty();

  public int size();

  void clear();

  public void printStatistics(PrintStream pOut);
}
