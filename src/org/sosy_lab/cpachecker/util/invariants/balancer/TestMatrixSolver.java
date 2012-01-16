/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2012  Dirk Beyer
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
package org.sosy_lab.cpachecker.util.invariants.balancer;

import org.sosy_lab.common.LogManager;
import org.sosy_lab.common.configuration.Configuration;


public class TestMatrixSolver {

  static Matrix A;
  static LogManager logger;

  public static void main(String[] args) {

    try {
      logger = new LogManager(Configuration.defaultConfiguration());
    } catch (Exception e) {}

    Variable p1 = new Variable("p1");
    Variable p2 = new Variable("p2");
    Variable p3 = new Variable("p3");
    Variable p4 = new Variable("p4");

    RationalFunction a11 = new RationalFunction(-1);
    RationalFunction a12 = RationalFunction.multiply(new RationalFunction(-1), new RationalFunction(p4));
    RationalFunction a13 = new RationalFunction(p4);
    RationalFunction a14 = new RationalFunction(0);
    RationalFunction a15 = new RationalFunction(1);

    RationalFunction a21 = new RationalFunction(0);
    RationalFunction a22 = RationalFunction.multiply(new RationalFunction(-3), new RationalFunction(p1));
    RationalFunction a23 = RationalFunction.multiply(new RationalFunction(3), new RationalFunction(p1));
    RationalFunction a24 = new RationalFunction(-1);
    RationalFunction a25 = new RationalFunction(-3);

    RationalFunction a31 = new RationalFunction(0);
    RationalFunction a32 = new RationalFunction(p2);
    RationalFunction a33 = RationalFunction.multiply(new RationalFunction(-1), new RationalFunction(p2));
    RationalFunction a34 = new RationalFunction(0);
    RationalFunction a35 = new RationalFunction(1);

    RationalFunction a41 = new RationalFunction(0);
    RationalFunction a42 = new RationalFunction(p3);
    RationalFunction a43 = RationalFunction.multiply(new RationalFunction(-1), new RationalFunction(p3));
    RationalFunction a44 = new RationalFunction(0);
    RationalFunction a45 = new RationalFunction(1);

    RationalFunction a51 = new RationalFunction(0);
    RationalFunction a52 = new RationalFunction(0);
    RationalFunction a53 = new RationalFunction(0);
    RationalFunction a54 = new RationalFunction(1);
    RationalFunction a55 = new RationalFunction(0);

    RationalFunction a16 = new RationalFunction(1);
    RationalFunction a26 = new RationalFunction(0);
    RationalFunction a36 = new RationalFunction(0);
    RationalFunction a46 = new RationalFunction(0);
    RationalFunction a56 = new RationalFunction(0);

    RationalFunction[][] a = {
        {a11, a12, a13, a14, a15},
        {a21, a22, a23, a24, a25},
        {a31, a32, a33, a34, a35},
        {a41, a42, a43, a44, a45},
        {a51, a52, a53, a54, a55},
    };
    A = new Matrix(a);
    RationalFunction[][] c = {
        {a16}, {a26}, {a36}, {a46}, {a56}
    };
    Matrix C = new Matrix(c);
    A = Matrix.augment(A, C);

    test1();
  }

  private static void test1() {
    System.out.println(A);
    PivotRowHandler ms = new PivotRowHandler(A,logger);
    System.out.println(ms);
  }

}
