/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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

import org.sosy_lab.cpachecker.core.algorithm.DefaultWaitlistElement;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.waitlist.Waitlist.WaitlistFactory;

/** Basic implementation of ReachedSet. It does not group states by location or any other key. */
class DefaultReachedSet extends AbstractReachedSet {

  DefaultReachedSet(WaitlistFactory waitlistFactory, MainNestedReachedSet rSet) {
    super(waitlistFactory, rSet);
  }

  @Override
  public void reAddToWaitlist(AbstractState pState, Precision pPrecision) {
    DefaultWaitlistElement element = new DefaultWaitlistElement(pState, pPrecision);
    waitlist.add(element);
  }

  @Override
  public void removeOnlyFromWaitlist(AbstractState pState, Precision pPrecision) {
    DefaultWaitlistElement element = new DefaultWaitlistElement(pState, pPrecision);
    waitlist.remove(element);
  }

}
