/*
 * CPAchecker is a tool for configurable software verification.
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
package org.sosy_lab.cpachecker.util.predicates.pathformula.jstoformula;

import static org.sosy_lab.cpachecker.util.predicates.pathformula.jstoformula.Types.SCOPE_TYPE;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.common.log.LogManagerWithoutDuplicates;
import org.sosy_lab.cpachecker.cfa.ast.ADeclaration;
import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.ast.js.JSAssignment;
import org.sosy_lab.cpachecker.cfa.ast.js.JSExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSExpressionStatement;
import org.sosy_lab.cpachecker.cfa.ast.js.JSFunctionCall;
import org.sosy_lab.cpachecker.cfa.ast.js.JSFunctionCallAssignmentStatement;
import org.sosy_lab.cpachecker.cfa.ast.js.JSFunctionCallExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSFunctionCallStatement;
import org.sosy_lab.cpachecker.cfa.ast.js.JSIdExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSInitializers;
import org.sosy_lab.cpachecker.cfa.ast.js.JSLeftHandSide;
import org.sosy_lab.cpachecker.cfa.ast.js.JSParameterDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.js.JSRightHandSide;
import org.sosy_lab.cpachecker.cfa.ast.js.JSRightHandSideVisitor;
import org.sosy_lab.cpachecker.cfa.ast.js.JSSimpleDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.js.JSStatement;
import org.sosy_lab.cpachecker.cfa.ast.js.JSUndefinedLiteralExpression;
import org.sosy_lab.cpachecker.cfa.ast.js.JSVariableDeclaration;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.FunctionExitNode;
import org.sosy_lab.cpachecker.cfa.model.js.JSAssumeEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSDeclarationEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSFunctionCallEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSFunctionEntryNode;
import org.sosy_lab.cpachecker.cfa.model.js.JSFunctionReturnEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSFunctionSummaryEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSReturnStatementEdge;
import org.sosy_lab.cpachecker.cfa.model.js.JSStatementEdge;
import org.sosy_lab.cpachecker.cfa.parser.eclipse.js.UnknownFunctionCallerDeclarationBuilder;
import org.sosy_lab.cpachecker.cfa.types.MachineModel;
import org.sosy_lab.cpachecker.cfa.types.js.JSAnyType;
import org.sosy_lab.cpachecker.cfa.types.js.JSType;
import org.sosy_lab.cpachecker.core.AnalysisDirection;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCFAEdgeException;
import org.sosy_lab.cpachecker.exceptions.UnrecognizedCodeException;
import org.sosy_lab.cpachecker.util.predicates.pathformula.ErrorConditions;
import org.sosy_lab.cpachecker.util.predicates.pathformula.PathFormula;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap;
import org.sosy_lab.cpachecker.util.predicates.pathformula.SSAMap.SSAMapBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.pointeraliasing.PointerTargetSet;
import org.sosy_lab.cpachecker.util.predicates.pathformula.pointeraliasing.PointerTargetSetBuilder;
import org.sosy_lab.cpachecker.util.predicates.pathformula.pointeraliasing.PointerTargetSetBuilder.DummyPointerTargetSetBuilder;
import org.sosy_lab.cpachecker.util.predicates.smt.BooleanFormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.FloatingPointFormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.FunctionFormulaManagerView;
import org.sosy_lab.cpachecker.util.predicates.smt.IntegerFormulaManagerView;
import org.sosy_lab.cpachecker.util.variableclassification.VariableClassificationBuilder;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FloatingPointFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

/** Class containing all the code that converts C code into a formula. */
@SuppressWarnings({"DeprecatedIsStillUsed", "deprecation"})
@Options(prefix = "cpa.predicate.js")
public class JSToFormulaConverter {

  //names for special variables needed to deal with functions
  @Deprecated
  private static final String RETURN_VARIABLE_NAME =
      VariableClassificationBuilder.FUNCTION_RETURN_VARIABLE;

  private static final String PARAM_VARIABLE_NAME = "__param__";

  final TypedValues typedValues;
  final TypeTags typeTags;
  final TypedValueManager tvmgr;
  private final StringIds stringIds;

  private final FormulaEncodingOptions options;
  protected final MachineModel machineModel;

  protected final FormulaManagerView fmgr;
  protected final BooleanFormulaManagerView bfmgr;
  protected final LogManagerWithoutDuplicates logger;
  protected final ShutdownNotifier shutdownNotifier;

  protected final AnalysisDirection direction;

  // Index that is used to read from variables that were not assigned yet
  private static final int VARIABLE_UNINITIALIZED = 1;

  // Index to be used for first assignment to a variable (must be higher than VARIABLE_UNINITIALIZED!)
  private static final int VARIABLE_FIRST_ASSIGNMENT = 2;

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
  private final Set<JSVariableDeclaration> globalDeclarations = new HashSet<>();

  FloatingPointFormulaManagerView fpfmgr;

  // TODO this option should be removed as soon as NaN and float interpolation can be used together
  @Option(secure = true, description = "Do not check for NaN in operations")
  private boolean useNaN = true;

  private final IntegerFormula mainScope;

  public JSToFormulaConverter(
      FormulaEncodingOptions pOptions,
      final Configuration pConfig,
      FormulaManagerView pFmgr,
      MachineModel pMachineModel,
      LogManager pLogger,
      ShutdownNotifier pShutdownNotifier,
      AnalysisDirection pDirection)
      throws InvalidConfigurationException {

    pConfig.inject(this, JSToFormulaConverter.class);

    this.fmgr = pFmgr;
    this.options = pOptions;
    this.machineModel = pMachineModel;

    this.bfmgr = pFmgr.getBooleanFormulaManager();
    final IntegerFormulaManagerView nfmgr = pFmgr.getIntegerFormulaManager();
    final FunctionFormulaManagerView ffmgr = pFmgr.getFunctionFormulaManager();
    this.fpfmgr = pFmgr.getFloatingPointFormulaManager();
    this.logger = new LogManagerWithoutDuplicates(pLogger);
    this.shutdownNotifier = pShutdownNotifier;

    this.direction = pDirection;

    typedValues = new TypedValues(ffmgr);
    typeTags = new TypeTags(nfmgr);
    tvmgr = new TypedValueManager(typedValues, typeTags);
    stringIds = new StringIds();
    mainScope = fmgr.makeNumber(SCOPE_TYPE, 0);
  }

  @SuppressWarnings("SameParameterValue")
  private void logfOnce(Level level, CFAEdge edge, String msg, Object... args) {
    if (logger.wouldBeLogged(level)) {
      logger.logfOnce(level, "%s: %s: %s",
          edge.getFileLocation(),
          String.format(msg, args),
          edge.getDescription());
    }
  }

  /** Produces a fresh new SSA index for an assignment and updates the SSA map. */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private int makeFreshIndex(String name, JSType type, SSAMapBuilder ssa) {
    int idx = getFreshIndex(name, ssa);
    ssa.setIndex(name, type, idx);
    return idx;
  }

  /**
   * Produces a fresh new SSA index for an assignment, but does _not_ update the SSA map. Usually
   * you should use {@link #makeFreshIndex(String, JSType, SSAMapBuilder)} instead, because using
   * variables with indices that are not stored in the SSAMap is not a good idea (c.f. the comment
   * inside getIndex()). If you use this method, you need to make sure to update the SSAMap
   * correctly.
   */
  private int getFreshIndex(String name, SSAMapBuilder ssa) {
//    checkSsaSavedType(name, type, ssa.getType(name));
    int idx = ssa.getFreshIndex(name);
    if (idx <= 0) {
      idx = VARIABLE_FIRST_ASSIGNMENT;
    }
    return idx;
  }

  /**
   * This method returns the index of the given variable in the ssa map, if there is none, it
   * creates one with the value 1.
   *
   * @return the index of the variable
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  protected int getIndex(String name, JSType type, SSAMapBuilder ssa) {
//    checkSsaSavedType(name, type, ssa.getType(name));
    int idx = ssa.getIndex(name);
    if (idx <= 0) {
      logger.log(Level.ALL, "WARNING: Auto-instantiating variable:", name);
      idx = VARIABLE_UNINITIALIZED;

      // It is important to store the index in the variable here.
      // If getIndex() was called with a specific name,
      // this means that name@idx will appear in formulas.
      // Thus we need to make sure that calls to FormulaManagerView.instantiate()
      // will also add indices for this name,
      // which it does exactly if the name is in the SSAMap.
      ssa.setIndex(name, type, idx);
    }

    return idx;
  }

  /**
   * Create a formula for a given variable. This method does not handle scoping and the
   * NON_DET_VARIABLE!
   *
   * <p>This method does not update the index of the variable.
   */
  protected IntegerFormula makeVariable(String name, SSAMapBuilder ssa) {
    int useIndex = getIndex(name, JSAnyType.ANY, ssa);
    return fmgr.makeVariable(Types.VARIABLE_TYPE, name, useIndex);
  }

  /**
   * Takes a variable name and its type and create the corresponding formula out of it. The <code>
   * pContextSSA</code> is used to supply this method with the necessary {@link SSAMap} and (if
   * necessary) the {@link PointerTargetSet} can be supplied via <code>pContextPTS</code>.
   *
   * @param pContextSSA the SSAMap indices from which the variable should be created
   * @param pContextPTS the PointerTargetSet which should be used for formula generation
   * @param pVarName the name of the variable
   * @param pType the type of the variable
   * @param forcePointerDereference (only used in CToFormulaConverterWithPointerAliasing)
   * @return the created formula
   */
  @SuppressWarnings("unused")
  public Formula makeFormulaForVariable(
      SSAMap pContextSSA,
      PointerTargetSet pContextPTS,
      String pVarName,
      JSType pType,
      boolean forcePointerDereference) {
//    Preconditions.checkArgument(!(pType instanceof CEnumType));

    SSAMapBuilder ssa = pContextSSA.builder();
    Formula formula = makeVariable(pVarName, ssa);

    if (!ssa.build().equals(pContextSSA)) {
      throw new IllegalArgumentException(
          "we cannot apply the SSAMap changes to the point where the"
              + " information would be needed possible problems: uninitialized variables could be"
              + " in more formulas which get conjuncted and then we get unsatisfiable formulas as a result");
    }

    return formula;
  }

  /**
   * Create a formula for a given variable with a fresh index for the left-hand side of an
   * assignment. This method does not handle scoping and the NON_DET_VARIABLE!
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  Formula makeFreshVariable(String name, JSType type, SSAMapBuilder ssa) {
    int useIndex;

    if (direction == AnalysisDirection.BACKWARD) {
      useIndex = getIndex(name, type, ssa);
    } else {
      useIndex = makeFreshIndex(name, type, ssa);
    }

    Formula result = fmgr.makeVariable(Types.VARIABLE_TYPE, name, useIndex);

    if (direction == AnalysisDirection.BACKWARD) {
      makeFreshIndex(name, type, ssa);
    }

    return result;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private IntegerFormula makeFreshVariable(final String name, final SSAMapBuilder ssa) {
    int useIndex;

    if (direction == AnalysisDirection.BACKWARD) {
      useIndex = getIndex(name, JSAnyType.ANY, ssa);
    } else {
      useIndex = makeFreshIndex(name, JSAnyType.ANY, ssa);
    }

    IntegerFormula result = fmgr.makeVariable(Types.VARIABLE_TYPE, name, useIndex);

    if (direction == AnalysisDirection.BACKWARD) {
      makeFreshIndex(name, JSAnyType.ANY, ssa);
    }

    return result;
  }

  //  @Override
  public PathFormula makeAnd(PathFormula oldFormula, CFAEdge edge, ErrorConditions errorConditions)
      throws UnrecognizedCFAEdgeException, UnrecognizedCodeException {

    String function = (edge.getPredecessor() != null)
                          ? edge.getPredecessor().getFunctionName() : null;
    assert function != null;

    SSAMapBuilder ssa = oldFormula.getSsa().builder();
    Constraints constraints = new Constraints(bfmgr);
    PointerTargetSetBuilder pts = createPointerTargetSetBuilder(oldFormula.getPointerTargetSet());

    // handle the edge
    BooleanFormula edgeFormula =
        createFormulaForEdge(edge, function, ssa, constraints, errorConditions);

    // result-constraints must be added _after_ handling the edge (some lines above),
    // because this edge could write a global value.
    if (edge.getSuccessor() instanceof FunctionExitNode) {
      addGlobalAssignmentConstraints(
          edge, function, ssa, constraints, errorConditions, RETURN_VARIABLE_NAME, true);
    }

    edgeFormula = bfmgr.and(edgeFormula, constraints.get());

    SSAMap newSsa = ssa.build();
    PointerTargetSet newPts = pts.build();

    if (bfmgr.isTrue(edgeFormula)
        && (newSsa == oldFormula.getSsa())
        && newPts.equals(oldFormula.getPointerTargetSet())) {
      // formula is just "true" and rest is equal
      // i.e. no writes to SSAMap, no branching and length should stay the same
      return oldFormula;
    }

    BooleanFormula newFormula = bfmgr.and(oldFormula.getFormula(), edgeFormula);
    int newLength = oldFormula.getLength() + 1;
    return new PathFormula(newFormula, newSsa, newPts, newLength);
  }

  /**
   * this function is only executed, if the option useParameterVariablesForGlobals is used,
   * otherwise it does nothing. create and add constraints about a global variable:
   * tmp_1_f==global1, tmp_2_f==global2, ...
   *
   * @param tmpAsLHS if tmpAsLHS: tmp_result1_f := global1 else global1 := tmp_result1_f
   */
  @SuppressWarnings("LoopStatementThatDoesntLoop")
  private void addGlobalAssignmentConstraints(
      final CFAEdge edge,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions,
      final String tmpNamePart,
      final boolean tmpAsLHS)
      throws UnrecognizedCodeException {

    if (options.useParameterVariablesForGlobals()) {

      // make assignments: tmp_param1_f==global1, tmp_param2_f==global2, ...
      // function-name is important, because otherwise the name is not unique over several
      // function-calls.
      for (final JSVariableDeclaration decl : globalDeclarations) {
        final JSParameterDeclaration tmpParameter =
            new JSParameterDeclaration(
                decl.getFileLocation(), decl.getName() + tmpNamePart + function);
        tmpParameter.setQualifiedName(decl.getQualifiedName() + tmpNamePart + function);

        final JSIdExpression tmp = new JSIdExpression(decl.getFileLocation(), tmpParameter);
        final JSIdExpression glob = new JSIdExpression(decl.getFileLocation(), decl);

        final BooleanFormula eq;
        if (tmpAsLHS) {
          eq = makeAssignment(tmp, glob, edge, function, ssa, constraints, errorConditions);
        } else {
          eq = makeAssignment(glob, tmp, edge, function, ssa, constraints, errorConditions);
        }
        constraints.addConstraint(eq);
        throw new RuntimeException("Not implemented");
      }

    }
  }

  /**
   * This helper method creates a formula for an CFA edge, given the current function, SSA map and
   * constraints.
   *
   * @param edge the edge for which to create the formula
   * @param function the current scope
   * @param ssa the current SSA map
   * @param constraints the current constraints
   * @return the formula for the edge
   */
  private BooleanFormula createFormulaForEdge(
      final CFAEdge edge,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCFAEdgeException, UnrecognizedCodeException {
    switch (edge.getEdgeType()) {
    case StatementEdge: {
          return makeStatement((JSStatementEdge) edge, function, ssa, constraints, errorConditions);
    }

    case ReturnStatementEdge: {
          final JSReturnStatementEdge returnEdge = (JSReturnStatementEdge) edge;
          assert returnEdge.asAssignment().isPresent()
              : "There are no void functions in JavaScript";
          return makeReturn(
              returnEdge.asAssignment().get(),
              returnEdge,
              function,
              ssa,
              constraints,
              errorConditions);
    }

    case DeclarationEdge: {
          return makeDeclaration(
              (JSDeclarationEdge) edge, function, ssa, constraints, errorConditions);
    }

    case AssumeEdge: {
      JSAssumeEdge assumeEdge = (JSAssumeEdge)edge;
          return makePredicate(
              assumeEdge.getExpression(),
              assumeEdge.getTruthAssumption(),
              assumeEdge,
              function,
              ssa,
              constraints,
              errorConditions);
    }

    case BlankEdge: {
      return bfmgr.makeTrue();
    }

    case FunctionCallEdge: {
          return makeFunctionCall(
              (JSFunctionCallEdge) edge, function, ssa, constraints, errorConditions);
    }

    case FunctionReturnEdge: {
          // get the expression from the summary edge
          JSFunctionSummaryEdge ce = ((JSFunctionReturnEdge) edge).getSummaryEdge();
          return makeExitFunction(ce, function, ssa, constraints, errorConditions);
    }

    case CallToReturnEdge:
        JSFunctionSummaryEdge ce = (JSFunctionSummaryEdge) edge;
        return makeExitFunction(ce, function, ssa, constraints, errorConditions);

    default:
      throw new UnrecognizedCFAEdgeException(edge);
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  private BooleanFormula makeStatement(
      final JSStatementEdge statement,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {

    JSStatement stmt = statement.getStatement();
    if (stmt instanceof JSAssignment) {
      JSAssignment assignment = (JSAssignment)stmt;
      return makeAssignment(
          assignment.getLeftHandSide(),
          assignment.getRightHandSide(),
          statement,
          function,
          ssa,
          constraints,
          errorConditions);

    } else {
      if (!(stmt instanceof JSExpressionStatement)) {
        throw new UnrecognizedCodeException("Unknown statement", statement, stmt);
      }

      // side-effect free statement, ignore
      return bfmgr.makeTrue();
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private BooleanFormula makeDeclaration(
      final JSDeclarationEdge edge,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {

    if (!(edge.getDeclaration() instanceof JSVariableDeclaration)) {
      // struct prototype, function declaration, typedef etc.
      logfOnce(Level.FINEST, edge, "Ignoring declaration");
      return bfmgr.makeTrue();
    }

    JSVariableDeclaration decl = (JSVariableDeclaration)edge.getDeclaration();
    final String varName = decl.getQualifiedName();

//    if (!isRelevantVariable(decl)) {
//      logger.logfOnce(Level.FINEST, "%s: Ignoring declaration of unused variable: %s",
//          decl.getFileLocation(), decl.toASTString());
//      return bfmgr.makeTrue();
//    }
//
//    checkForLargeArray(edge, decl.getType().getCanonicalType());
//
//    if (options.useParameterVariablesForGlobals() && decl.isGlobal()) {
//      globalDeclarations.add(decl);
//    }

    // if the var is unsigned, add the constraint that it should
    // be > 0
    //    if (((CSimpleType)spec).isUnsigned()) {
    //    long z = mathsat.api.msat_make_number(msatEnv, "0");
    //    long mvar = buildMsatVariable(var, idx);
    //    long t = mathsat.api.msat_make_gt(msatEnv, mvar, z);
    //    t = mathsat.api.msat_make_and(msatEnv, m1.getTerm(), t);
    //    m1 = new MathsatFormula(t);
    //    }

    // just increment index of variable in SSAMap
    // (a declaration contains an implicit assignment, even without initializer)
    // In case of an existing initializer, we increment the index twice
    // (here and below) so that the index 2 only occurs for uninitialized variables.
    // DO NOT OMIT THIS CALL, even without an initializer!
    if (direction == AnalysisDirection.FORWARD) {
      makeFreshIndex(varName, decl.getType(), ssa);
    }

    // if there is an initializer associated to this variable,
    // take it into account
    BooleanFormula result = bfmgr.makeTrue();

//    if (decl.getInitializer() instanceof CInitializerList) {
//      // If there is an initializer, all fields/elements not mentioned
//      // in the initializer are set to 0 (C standard § 6.7.9 (21)
//
//      int size = machineModel.getSizeof(decl.getType());
//      if (size > 0) {
//        Formula var = makeVariable(varName, decl.getType(), ssa);
//        CType elementCType = decl.getType();
//        FormulaType<?> elementFormulaType = getFormulaTypeFromCType(elementCType);
//        Formula zero = fmgr.makeNumber(elementFormulaType, 0L);
//        result = bfmgr.and(result, fmgr.assignment(var, zero));
//      }
//    }

    for (JSAssignment assignment : JSInitializers.convertToAssignments(decl, edge)) {
      result = bfmgr.and(result,
          makeAssignment(
              assignment.getLeftHandSide(),
              assignment.getRightHandSide(),
              edge,
              function,
              ssa,
              constraints,
              errorConditions));
    }

    return result;
  }

  private BooleanFormula makeExitFunction(
      final JSFunctionSummaryEdge ce,
      final String calledFunction,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {

    addGlobalAssignmentConstraints(
        ce, calledFunction, ssa, constraints, errorConditions, RETURN_VARIABLE_NAME, false);

    JSFunctionCall retExp = ce.getExpression();
    if (retExp instanceof JSFunctionCallStatement) {
      // this should be a void return, just do nothing...
      return bfmgr.makeTrue();

    } else if (retExp instanceof JSFunctionCallAssignmentStatement) {
      JSFunctionCallAssignmentStatement exp = (JSFunctionCallAssignmentStatement) retExp;
      JSFunctionCallExpression funcCallExp = exp.getRightHandSide();

      String callerFunction = ce.getSuccessor().getFunctionName();
      final com.google.common.base.Optional<JSVariableDeclaration> returnVariableDeclaration =
          ce.getFunctionEntry().getReturnVariable();
      if (!returnVariableDeclaration.isPresent()) {
        throw new UnrecognizedCodeException("Void function used in assignment", ce, retExp);
      }
      final JSIdExpression rhs =
          new JSIdExpression(funcCallExp.getFileLocation(), returnVariableDeclaration.get());

      return makeAssignment(
          exp.getLeftHandSide(), rhs, ce, callerFunction, ssa, constraints, errorConditions);
    } else {
      throw new UnrecognizedCodeException("Unknown function exit expression", ce, retExp);
    }
  }

  private BooleanFormula makeFunctionCall(
      final JSFunctionCallEdge edge,
      final String callerFunction,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {

    final Iterator<JSExpression> actualParams = edge.getArguments().iterator();

    final JSFunctionEntryNode fn = edge.getSuccessor();
    final List<JSParameterDeclaration> formalParams = fn.getFunctionParameters();

    if (UnknownFunctionCallerDeclarationBuilder.maxParameterCount < edge.getArguments().size()) {
      throw new UnrecognizedCodeException(
          "Cannot handle more function arguments than "
              + UnknownFunctionCallerDeclarationBuilder.maxParameterCount
              + " (configured in UnknownFunctionCallerDeclarationBuilder.maxParameterCount)",
          edge);
    }
    BooleanFormula result = bfmgr.makeTrue();
    for (JSParameterDeclaration formalParam : formalParams) {
      JSExpression paramExpression =
          actualParams.hasNext()
              ? actualParams.next()
              : new JSUndefinedLiteralExpression(FileLocation.DUMMY);
      JSIdExpression lhs = new JSIdExpression(paramExpression.getFileLocation(), formalParam);
      final JSIdExpression paramLHS;
      if (options.useParameterVariables()) {
        // make assignments: tmp_param1==arg1, tmp_param2==arg2, ...
        JSParameterDeclaration tmpParameter =
            new JSParameterDeclaration(
                formalParam.getFileLocation(), formalParam.getName() + PARAM_VARIABLE_NAME);
        tmpParameter.setQualifiedName(formalParam.getQualifiedName() + PARAM_VARIABLE_NAME);
        paramLHS = new JSIdExpression(paramExpression.getFileLocation(), tmpParameter);
      } else {
        paramLHS = lhs;
      }

      result =
          bfmgr.and(
              result,
              makeAssignment(
                  paramLHS,
                  paramExpression,
                  edge,
                  callerFunction,
                  ssa,
                  constraints,
                  errorConditions));
    }

    addGlobalAssignmentConstraints(
        edge, fn.getFunctionName(), ssa, constraints, errorConditions, PARAM_VARIABLE_NAME, true);

    return result;
  }

  private BooleanFormula makeReturn(
      final JSAssignment assignment,
      final JSReturnStatementEdge edge,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {
    return makeAssignment(
        assignment.getLeftHandSide(),
        assignment.getRightHandSide(),
        edge,
        function,
        ssa,
        constraints,
        errorConditions);
  }

  IntegerFormula scopedVariable(
      final JSSimpleDeclaration pDeclaration,
      final SSAMapBuilder pSsa) {
    final boolean isGlobal =
        pDeclaration instanceof ADeclaration && ((ADeclaration) pDeclaration).isGlobal();
    return typedValues.var(
        isGlobal ? mainScope : fmgr.makeNumber(SCOPE_TYPE, 1),
        makeVariable(pDeclaration.getQualifiedName(), pSsa));
  }

  /**
   * Creates formula for the given assignment.
   *
   * @param lhs the left-hand-side of the assignment
   * @param rhs the right-hand-side of the assignment
   * @return the assignment formula
   */
  private BooleanFormula makeAssignment(
      final JSLeftHandSide lhs,
      JSRightHandSide rhs,
      final CFAEdge edge,
      final String function,
      final SSAMapBuilder ssa,
      final Constraints constraints,
      final ErrorConditions errorConditions)
      throws UnrecognizedCodeException {
    final TypedValue r = buildTerm(rhs, edge, function, ssa, constraints, errorConditions);
    assert lhs instanceof JSIdExpression
        : "Only assignment to variable is implemented yet";
    final JSSimpleDeclaration declaration = ((JSIdExpression) lhs).getDeclaration();
    assert declaration != null;
    final IntegerFormula l = buildLvalueTerm(declaration, ssa);
    final IntegerFormula rType = r.getType();
    if (rType.equals(typeTags.BOOLEAN)) {
      return bfmgr.and(
          fmgr.assignment(typedValues.typeof(l), typeTags.BOOLEAN),
          fmgr.makeEqual(typedValues.booleanValue(l), r.getValue()));
    }
    if (rType.equals(typeTags.FUNCTION)) {
      return bfmgr.and(
          fmgr.assignment(typedValues.typeof(l), typeTags.FUNCTION),
          fmgr.makeEqual(typedValues.functionValue(l), r.getValue()));
    }
    if (rType.equals(typeTags.NUMBER)) {
      return bfmgr.and(
          fmgr.assignment(typedValues.typeof(l), typeTags.NUMBER),
          fmgr.makeEqual(typedValues.numberValue(l), r.getValue()));
    }
    if (rType.equals(typeTags.STRING)) {
      return bfmgr.and(
          fmgr.assignment(typedValues.typeof(l), typeTags.STRING),
          fmgr.makeEqual(typedValues.stringValue(l), r.getValue()));
    }
    return fmgr.makeAnd(
        fmgr.assignment(typedValues.typeof(l), r.getType()),
        bfmgr.or(
            fmgr.makeAnd(
                fmgr.makeEqual(typedValues.typeof(l), typeTags.BOOLEAN),
                fmgr.makeEqual(typedValues.booleanValue(l), toBoolean(r))),
            fmgr.makeAnd(
                fmgr.makeEqual(typedValues.typeof(l), typeTags.FUNCTION),
                fmgr.makeEqual(typedValues.functionValue(l), toFunction(r))),
            fmgr.makeAnd(
                fmgr.makeEqual(typedValues.typeof(l), typeTags.NUMBER),
                fmgr.makeEqual(typedValues.numberValue(l), toNumber(r))),
            fmgr.makeAnd(
                fmgr.makeEqual(typedValues.typeof(l), typeTags.STRING),
                fmgr.makeEqual(typedValues.stringValue(l), toStringFormula(r))),
            fmgr.makeEqual(typedValues.typeof(l), typeTags.OBJECT),
            fmgr.makeEqual(typedValues.typeof(l), typeTags.UNDEFINED)));
  }

  private IntegerFormula toFunction(final TypedValue pValue) {
    final IntegerFormula type = pValue.getType();
    final IntegerFormula notAFunction = fmgr.makeNumber(Types.FUNCTION_TYPE, 0);
    if (Lists.newArrayList(
            typeTags.BOOLEAN, typeTags.NUMBER, typeTags.OBJECT, typeTags.STRING, typeTags.UNDEFINED)
        .contains(type)) {
      return notAFunction;
    } else if (type.equals(typeTags.FUNCTION)) {
      return typedValues.functionValue((IntegerFormula) pValue.getValue());
    }
    final IntegerFormula variable = (IntegerFormula) pValue.getValue();
    return bfmgr.ifThenElse(
        fmgr.makeEqual(type, typeTags.FUNCTION), typedValues.functionValue(variable), notAFunction);
  }

  IntegerFormula toStringFormula(final TypedValue pValue) {
    final IntegerFormula type = pValue.getType();
    final IntegerFormula unknownStringValue = fmgr.makeNumber(Types.STRING_TYPE, 0);
    if (Lists.newArrayList(
            typeTags.BOOLEAN,
            typeTags.NUMBER,
            typeTags.OBJECT,
            typeTags.FUNCTION,
            typeTags.UNDEFINED)
        .contains(type)) {
      return unknownStringValue;
    } else if (type.equals(typeTags.STRING)) {
      return (IntegerFormula) pValue.getValue();
    }
    final IntegerFormula variable = (IntegerFormula) pValue.getValue();
    return bfmgr.ifThenElse(
        fmgr.makeEqual(type, typeTags.STRING),
        typedValues.stringValue(variable),
        unknownStringValue);
  }

  IntegerFormula getStringFormula(final String pValue) {
    return fmgr.makeNumber(Types.STRING_TYPE, stringIds.get(pValue));
  }

  BooleanFormula toBoolean(final TypedValue pValue) {
    final IntegerFormula type = pValue.getType();
    if (type.equals(typeTags.BOOLEAN)) {
      return (BooleanFormula) pValue.getValue();
    } else if (type.equals(typeTags.FUNCTION)) {
      return bfmgr.makeTrue();
    } else if (type.equals(typeTags.NUMBER)) {
      return numberToBoolean((FloatingPointFormula) pValue.getValue());
    } else if (type.equals(typeTags.STRING)) {
      return stringToBoolean((IntegerFormula) pValue.getValue());
    } else if (type.equals(typeTags.UNDEFINED)) {
      return bfmgr.makeFalse();
    } else if (type.equals(typeTags.OBJECT)) {
      return bfmgr.makeFalse(); // TODO handle non null objects
    } else {
      // variable
      final IntegerFormula variable = (IntegerFormula) pValue.getValue();
      return bfmgr.ifThenElse(
          fmgr.makeEqual(type, typeTags.BOOLEAN),
          typedValues.booleanValue(variable),
          bfmgr.ifThenElse(
              fmgr.makeEqual(type, typeTags.NUMBER),
              numberToBoolean(typedValues.numberValue(variable)),
              bfmgr.ifThenElse(
                  fmgr.makeEqual(type, typeTags.STRING),
                  stringToBoolean(typedValues.stringValue(variable)),
                  fmgr.makeEqual(type, typeTags.FUNCTION))));
    }
  }

  @Nonnull
  private BooleanFormula stringToBoolean(final IntegerFormula pValue) {
    return bfmgr.not(fmgr.makeEqual(pValue, getStringFormula("")));
  }

  private BooleanFormula numberToBoolean(final FloatingPointFormula pValue) {
    return bfmgr.ifThenElse(
        bfmgr.or(fpfmgr.isZero(pValue), fpfmgr.isNaN(pValue)), bfmgr.makeFalse(), bfmgr.makeTrue());
  }

  FloatingPointFormula toNumber(final TypedValue pValue) {
    final IntegerFormula type = pValue.getType();
    if (type.equals(typeTags.BOOLEAN)) {
      return booleanToNumber((BooleanFormula) pValue.getValue());
    } else if (type.equals(typeTags.FUNCTION)) {
      return fpfmgr.makeNaN(Types.NUMBER_TYPE);
    } else if (type.equals(typeTags.NUMBER)) {
      return (FloatingPointFormula) pValue.getValue();
    } else if (type.equals(typeTags.STRING)) {
      // TODO string to number conversion of string constants should be possible
      // For now, assume that every string is not a StringNumericLiteral, see
      // https://www.ecma-international.org/ecma-262/5.1/#sec-9.3
      return fpfmgr.makeNaN(Types.NUMBER_TYPE);
    } else if (type.equals(typeTags.UNDEFINED)) {
      return fpfmgr.makeNaN(Types.NUMBER_TYPE);
    } else if (type.equals(typeTags.OBJECT)) {
      return fmgr.makeNumber(Types.NUMBER_TYPE, 0); // TODO handle non null objects
    } else {
      // variable
      final IntegerFormula variable = (IntegerFormula) pValue.getValue();
      return bfmgr.ifThenElse(
          fmgr.makeEqual(type, typeTags.BOOLEAN),
          booleanToNumber(typedValues.booleanValue(variable)),
          bfmgr.ifThenElse(
              fmgr.makeEqual(type, typeTags.NUMBER),
              typedValues.numberValue(variable),
              bfmgr.ifThenElse(
                  fmgr.makeEqual(type, typeTags.OBJECT),
                  fmgr.makeNumber(Types.NUMBER_TYPE, 0), // TODO handle non null objects
                  fpfmgr.makeNaN(Types.NUMBER_TYPE))));
    }
  }

  private FloatingPointFormula booleanToNumber(final BooleanFormula pValue) {
    return bfmgr.ifThenElse(
        pValue, fmgr.makeNumber(Types.NUMBER_TYPE, 1), fmgr.makeNumber(Types.NUMBER_TYPE, 0));
  }

  private IntegerFormula buildLvalueTerm(
      final JSSimpleDeclaration pDeclaration,
      final SSAMapBuilder pSsa) {
    final boolean isGlobal =
        pDeclaration instanceof ADeclaration && ((ADeclaration) pDeclaration).isGlobal();
    return typedValues.var(
        isGlobal ? mainScope : fmgr.makeNumber(SCOPE_TYPE, 1),
        makeFreshVariable(pDeclaration.getQualifiedName(), pSsa));
  }

  private TypedValue buildTerm(
      JSRightHandSide exp,
      CFAEdge edge,
      String function,
      SSAMapBuilder ssa,
      Constraints constraints,
      ErrorConditions errorConditions)
      throws UnrecognizedCodeException {
    return exp.accept(
        createJSRightHandSideVisitor(edge, function, ssa, constraints, errorConditions));
  }

  private BooleanFormula makePredicate(
      JSExpression exp,
      boolean isTrue,
      CFAEdge edge,
      String function,
      SSAMapBuilder ssa,
      Constraints constraints,
      ErrorConditions errorConditions)
      throws UnrecognizedCodeException {

    final TypedValue condition =
        exp.accept(createJSRightHandSideVisitor(edge, function, ssa, constraints, errorConditions));
    BooleanFormula result = toBoolean(condition);

    if (!isTrue) {
      result = bfmgr.not(result);
    }
    return result;
  }

  /**
   * Parameters not used in {@link JSToFormulaConverter}, may be in subclasses they are.
   *
   * @param pts the pointer target set to use initially
   */
  @SuppressWarnings("unused")
  private PointerTargetSetBuilder createPointerTargetSetBuilder(PointerTargetSet pts) {
    return DummyPointerTargetSetBuilder.INSTANCE;
  }

  /**
   * Parameters not used in {@link JSToFormulaConverter}, may be in subclasses they are.
   *
   * @param pEdge the edge to be visited
   * @param pFunction the current function name
   * @param ssa the current SSAMapBuilder
   * @param constraints the constraints needed during visiting
   * @param errorConditions the error conditions
   */
  @SuppressWarnings("unused")
  private JSRightHandSideVisitor<TypedValue, UnrecognizedCodeException>
      createJSRightHandSideVisitor(
          CFAEdge pEdge,
          String pFunction,
          SSAMapBuilder ssa,
          Constraints constraints,
          ErrorConditions errorConditions) {
    return new ExpressionToFormulaVisitor(this, useNaN, fmgr, pEdge, pFunction, ssa, constraints);
  }

}
