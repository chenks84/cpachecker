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
package org.sosy_lab.cpachecker.cfa.types.c;
import javax.annotation.Nullable;
import org.sosy_lab.cpachecker.cfa.types.Type;

@SuppressWarnings("serial")
public interface CType extends Type {

  public boolean isConst();

  @Override
  public abstract String toString();

  public boolean isVolatile();

  /**
   * Check whether the current type is *incomplete* as defined by the C standard in § 6.2.5 (1).
   * Incomplete types miss some information (e.g., <code>struct s;</code>),
   * and for example their size cannot be computed.
   */
  public boolean isIncomplete();

  /**
   * Will throw a UnsupportedOperationException
   */
  @Override
  public int hashCode();

  /**
   * Be careful, this method compares the CType as it is to the given object,
   * typedefs won't be resolved. If you want to compare the type without having
   * typedefs in it use #getCanonicalType().equals()
   */
  @Override
  public boolean equals(@Nullable Object obj);

  public abstract <R, X extends Exception> R accept(CTypeVisitor<R, X> visitor) throws X;

  public CType getCanonicalType();

  public CType getCanonicalType(boolean forceConst, boolean forceVolatile);

  /**
   * Implements assignment compatibility for simple assignments (=)
   * as described in the constraints of C-Standard §6.5.16.1 (1).
   * <p>
   * Currently the fifth of those constraints is not considered,
   * since a {@link CType} does not expose if it is a null pointer
   * constant.
   * <p>
   * Do not override this method. If you find some condition that
   * is not met by this implementation yet but required for
   * compliance with the standard, just add the necessary condition
   * to this code.
   *
   * @param pType the {@link CType} to check, if it can be assigned to <b><code>this</code></b>
   * @return if pType can be assigned to <b><code>this</code></b>
   */
  default boolean canBeAssignedFrom(CType pType) {
    CType leftHandSide = this.getCanonicalType();
    CType rightHandSide = pType.getCanonicalType();

    // Cf. C-Standard §6.5.16.1 (1), first and last constraint of the list.
    // The {@link CSimpleType}s are corresponding to the arithmetic types
    // described in the standard (§6.2.5).
    if (leftHandSide instanceof CSimpleType) {
      if ((((CSimpleType) leftHandSide).getType().equals(CBasicType.BOOL) && rightHandSide instanceof CPointerType)
          || rightHandSide instanceof CSimpleType) {
        return true;
      }
    }

    // Cf. C-Standard §6.5.16.1 (1), second constraint.
    if (leftHandSide instanceof CCompositeType && rightHandSide instanceof CCompositeType) {
      CType plainCompositeLeft = CTypes.copyDequalified(leftHandSide);
      CType plainCompositeRight = CTypes.copyDequalified(rightHandSide);

      return CTypes.areTypesCompatible(plainCompositeLeft, plainCompositeRight);
    }

    if (rightHandSide instanceof CPointerType && leftHandSide instanceof CPointerType) {
      CPointerType pointerLeft = (CPointerType) leftHandSide;
      CPointerType pointerRight = (CPointerType) rightHandSide;
      CType leftPointedToType = pointerLeft.getType();
      CType rightPointedToType = pointerRight.getType();

      // Cf. C-Standard §6.5.16.1 (1), third and forth constraint.
      return pointerLeft.isConst() == pointerRight.isConst() && pointerLeft.isVolatile() == pointerRight.isVolatile()
          && ((leftPointedToType instanceof CVoidType && !(rightPointedToType instanceof CVoidType))
              || (rightPointedToType instanceof CVoidType && !(leftPointedToType instanceof CVoidType))
              || CTypes.areTypesCompatible(leftHandSide, rightHandSide));
    }

    // default case
    return false;
  }
}
