/*
 * CPAchecker is a tool for configurable software verification.
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
package org.sosy_lab.cpachecker.cfa.ast.js;

import org.sosy_lab.cpachecker.cfa.ast.AExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpressionVisitor;
import org.sosy_lab.cpachecker.cfa.ast.java.JExpressionVisitor;
import org.sosy_lab.cpachecker.cfa.types.js.JSType;

/** Interface for side-effect free expressions. */
public interface JSExpression extends JSRightHandSide, AExpression {

  long serialVersionUID = -5392226590169684927L;

  public <R, X extends Exception> R accept(JSExpressionVisitor<R, X> v) throws X;

  @Override
  JSType getExpressionType();

  @Override
  default <R, X extends Exception> R accept(JSRightHandSideVisitor<R, X> pV) throws X {
    return accept((JSExpressionVisitor<R, X>) pV);
  }

  @Deprecated // Call accept() directly
  @SuppressWarnings("unchecked") // should not be necessary, but javac complains otherwise
  @Override
  default <
          R,
          R1 extends R,
          R2 extends R,
          X1 extends Exception,
          X2 extends Exception,
          V extends CExpressionVisitor<R1, X1> & JExpressionVisitor<R2, X2>>
      R accept_(V pV) throws X1, X2 {
    return accept((JSExpressionVisitor<R1, X1>) pV);
  }
}