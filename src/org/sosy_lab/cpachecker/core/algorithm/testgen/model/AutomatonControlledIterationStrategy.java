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
package org.sosy_lab.cpachecker.core.algorithm.testgen.model;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sosy_lab.common.LogManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.io.Files;
import org.sosy_lab.common.io.Path;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.CPABuilder;
import org.sosy_lab.cpachecker.core.CounterexampleInfo;
import org.sosy_lab.cpachecker.core.ShutdownNotifier;
import org.sosy_lab.cpachecker.core.algorithm.Algorithm;
import org.sosy_lab.cpachecker.core.algorithm.CPAAlgorithm;
import org.sosy_lab.cpachecker.core.algorithm.testgen.StartupConfig;
import org.sosy_lab.cpachecker.core.algorithm.testgen.TestGenStatistics;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.StatisticsProvider;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSetFactory;
import org.sosy_lab.cpachecker.cpa.arg.ARGUtils;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.exceptions.PredicatedAnalysisPropertyViolationException;
import org.sosy_lab.cpachecker.util.AbstractStates;

import com.google.common.collect.Lists;

public class AutomatonControlledIterationStrategy implements TestGenIterationStrategy {

  private Configuration config;
  private LogManager logger;
  private ShutdownNotifier shutdownNotifier;
  private CFA cfa;
  private ConfigurableProgramAnalysis currentCPA;
  private final TestGenIterationStrategy.IterationModel model;
  private ReachedSetFactory reachedSetFactory;
  private CPABuilder cpaBuilder;

  private int automatonCounter = 0;
  private TestGenStatistics stats;
  private final List<Statistics> subAlgorithmStats = new ArrayList<>();

  public AutomatonControlledIterationStrategy(StartupConfig startupConfig, CFA pCfa, IterationModel model,
      ReachedSetFactory pReachedSetFactory, CPABuilder pCpaBuilder, TestGenStatistics pStats) {
    super();
    this.reachedSetFactory = pReachedSetFactory;
    cpaBuilder = pCpaBuilder;
    stats = pStats;
    config = startupConfig.getConfig();
    logger = startupConfig.getLog();
    shutdownNotifier = startupConfig.getNotifier();
    cfa = pCfa;
    this.model = model;
  }

  @Override
  public void updateIterationModelForNextIteration(PredicatePathAnalysisResult pResult) {
    // TODO might have to reinit the reached-sets
    fetchSubAlgorithmStats();
    model.setAlgorithm(createAlgorithmForNextIteration(pResult));
    ReachedSet newReached = reachedSetFactory.create();
    AbstractState initialState = model.getGlobalReached().getFirstState();
    CFANode initialLoc = AbstractStates.extractLocation(initialState);
    initialState = currentCPA.getInitialState(initialLoc);
    newReached.add(initialState, currentCPA.getInitialPrecision(initialLoc));
    model.setLocalReached(newReached);
  }

  @Override
  public IterationModel getModel() {
    return model;
  }

  @Override
  public boolean runAlgorithm() throws PredicatedAnalysisPropertyViolationException, CPAException, InterruptedException {
    stats.beforeCpaAlgortihm();
    boolean ret = model.getAlgorithm().run(model.getLocalReached());
    stats.afterCpaAlgortihm();
    return ret;
  }

  private Algorithm createAlgorithmForNextIteration(PredicatePathAnalysisResult pResult) {
    Path path = org.sosy_lab.common.io.Paths.get("output/automaton/next_automaton" + automatonCounter++ + ".spc");
    try (Writer w = Files.openOutputFile(path, Charset.forName("UTF8"))) {
      //    try (DeleteOnCloseFile automatonFile = Files.createTempFile("next_automaton", ".txt")) {

      //      try (Writer w = Files.openOutputFile(automatonFile.toPath())) {
      CounterexampleInfo ci = CounterexampleInfo.feasible(pResult.getPath(), pResult.getTrace().getModel());
      //      ARGUtils.producePathAutomaton(w, "nextPathAutomaton", pNewPath);
      //      ARGUtils.producePathAutomaton(w, pResult.getPath().getFirst().getFirst(), pResult.getPath().getStateSet(), "nextPathAutomaton", ci);
      ARGUtils.produceTestGenPathAutomaton(w, pResult.getPath().getFirst().getFirst(), pResult.getPath().getStateSet(),
          "nextPathAutomaton", ci,true);
      //      }

    } catch (IOException e) {
      throw new IllegalStateException("Unable to create the Algorithm for next Iteration", e);
    }
    try {
      Configuration lConfig =
          Configuration.builder().copyFrom(config).clearOption("analysis.algorithm.testGen").build();
      CPABuilder localBuilder =
          new CPABuilder(lConfig, logger, ShutdownNotifier.createWithParent(shutdownNotifier), reachedSetFactory);
      currentCPA = localBuilder.buildCPAs(cfa, Lists.newArrayList(path));
      //      CoreComponentsFactory factory = new CoreComponentsFactory(lConfig, logger, ShutdownNotifier.createWithParent(shutdownNotifier));
      //      return factory.createAlgorithm(nextCpa, "", cfa, null);

      if (model.getAlgorithm() instanceof CPAAlgorithm) {
        return CPAAlgorithm.create(currentCPA, logger, lConfig, shutdownNotifier);
      } else {
        throw new InvalidConfigurationException("Generating a new Algorithm here only Works if the "
            + "Algorithm is a CPAAlgorithm");
      }

    } catch (InvalidConfigurationException | CPAException e) {
      // TODO: use another exception?
      throw new IllegalStateException("Unable to create the Algorithm for next Iteration", e);
    }
  }


  private Algorithm createAlgorithmForNextIteration2(PredicatePathAnalysisResult pResult) {
    // This temp file will be automatically deleted when the try block terminates.
    Path path = org.sosy_lab.common.io.Paths.get("config/specification/testgen_next_automaton.spc");
    try (Writer w = Files.openOutputFile(path, Charset.forName("UTF8"))) {
      //    try (DeleteOnCloseFile automatonFile = Files.createTempFile("next_automaton", ".txt")) {

      //      try (Writer w = Files.openOutputFile(automatonFile.toPath())) {
      CounterexampleInfo ci = CounterexampleInfo.feasible(pResult.getPath(), pResult.getTrace().getModel());
      //      ARGUtils.producePathAutomaton(w, "nextPathAutomaton", pNewPath);
      //      ARGUtils.producePathAutomaton(w, pResult.getPath().getFirst().getFirst(), pResult.getPath().getStateSet(), "nextPathAutomaton", ci);
      ARGUtils.produceTestGenPathAutomaton(w, pResult.getPath().getFirst().getFirst(), pResult.getPath().getStateSet(),
          "nextPathAutomaton", ci,true);
      //      }

    } catch (IOException e) {
      throw new IllegalStateException("Unable to create the Algorithm for next Iteration", e);
    }
    try {
      Configuration lConfig =
          Configuration.builder().copyFrom(config).clearOption("analysis.algorithm.testGen")
              .setOption("specification", "config/specification/defaulttestgen.spc").build();
      CPABuilder localBuilder =
          new CPABuilder(lConfig, logger, ShutdownNotifier.createWithParent(shutdownNotifier), reachedSetFactory);
      currentCPA = localBuilder.buildCPAs(cfa);//, Lists.newArrayList(path));
      //      CoreComponentsFactory factory = new CoreComponentsFactory(lConfig, logger, ShutdownNotifier.createWithParent(shutdownNotifier));
      //      return factory.createAlgorithm(nextCpa, "", cfa, null);

      if (model.getAlgorithm() instanceof CPAAlgorithm) {
        return CPAAlgorithm.create(currentCPA, logger, lConfig, shutdownNotifier);
      } else {
        throw new InvalidConfigurationException("Generating a new Algorithm here only Works if the "
            + "Algorithm is a CPAAlgorithm");
      }

    } catch (InvalidConfigurationException | CPAException e) {
      // TODO: use another exception?
      throw new IllegalStateException("Unable to create the Algorithm for next Iteration", e);
    }
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    // Fetch statistics for last used algorithm instance
    fetchSubAlgorithmStats();
    pStatsCollection.addAll(subAlgorithmStats);

  }


  private void fetchSubAlgorithmStats() {
    if(getModel().getAlgorithm() instanceof StatisticsProvider) {
      StatisticsProvider sProvider = (StatisticsProvider) getModel().getAlgorithm();
      sProvider.collectStatistics(subAlgorithmStats);
    }

  }

}
