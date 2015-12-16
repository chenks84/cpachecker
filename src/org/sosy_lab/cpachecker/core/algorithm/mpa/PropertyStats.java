/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2015  Dirk Beyer
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
package org.sosy_lab.cpachecker.core.algorithm.mpa;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.sosy_lab.cpachecker.core.CPAcheckerResult.Result;
import org.sosy_lab.cpachecker.core.interfaces.Property;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.util.statistics.StatCounter;
import org.sosy_lab.cpachecker.util.statistics.StatCpuTime;
import org.sosy_lab.cpachecker.util.statistics.StatCpuTime.StatCpuTimer;
import org.sosy_lab.cpachecker.util.statistics.StatisticsUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public enum PropertyStats implements Statistics {
  INSTANCE;

  private Map<Property, StatCpuTime> refinementTime = Maps.newHashMap();
  private Map<Property, StatCounter> refinementCount = Maps.newHashMap();
  private Map<Property, StatCounter> coverageCount = Maps.newHashMap();
  private Map<Property, StatCounter> noCoverageCount = Maps.newHashMap();

  public synchronized void clear() {
    refinementCount.clear();
    refinementTime.clear();
    coverageCount.clear();
    noCoverageCount.clear();
  }

  public interface StatHandle extends AutoCloseable{
    @Override
    public void close();
  }

  public synchronized Optional<StatCpuTime> getRefinementTime(Property pProperty) {
    StatCpuTime result = refinementTime.get(pProperty);
    if (result == null) {
      return Optional.absent();
    }
    return Optional.of(result);
  }

  public synchronized Optional<StatCounter> getRefinementCount(Property pProperty) {
    StatCounter result = refinementCount.get(pProperty);
    if (result == null) {
      return Optional.absent();
    }
    return Optional.of(result);
  }

  public synchronized double getExplosionFactor(Property pProperty) {
    final StatCounter covCount = coverageCount.get(pProperty);
    final StatCounter noCovCount = noCoverageCount.get(pProperty);

    if (covCount == null || noCovCount == null) {
      return 0;
    }

    int divBy = covCount.getValue() + noCovCount.getValue();
    if (divBy == 0) {
      return 0;
    }

    return (double) noCovCount.getValue() / (double) divBy;
  }

  public synchronized void signalStopOperatorResult(final Set<? extends Property> pProperties, boolean pCoverage) {
    for (Property p: pProperties) {
      StatCounter counter;
      if (pCoverage) {
        counter = coverageCount.get(p);
        if (counter == null) {
          counter = new StatCounter(p.toString());
          coverageCount.put(p, counter);
        }
      } else {
        counter = noCoverageCount.get(p);
        if (counter == null) {
          counter = new StatCounter(p.toString());
          noCoverageCount.put(p, counter);
        }
      }
      counter.inc();
    }
  }

  public synchronized StatHandle startRefinement(final Set<Property> pProperties) {

    final List<StatCpuTimer> timers = Lists.newArrayList();

    for (Property p: pProperties) {
      StatCounter counter = refinementCount.get(p);
      if (counter == null) {
        counter = new StatCounter(p.toString());
        refinementCount.put(p, counter);
      }
      counter.inc();

      StatCpuTime time = refinementTime.get(p);
      if (time == null) {
        time = new StatCpuTime();
        refinementTime.put(p, time);
      }
      timers.add(time.start());
    }

    return new StatHandle() {
      @Override
      public void close() {
        for (StatCpuTimer t: timers) {
          t.stop();
        }
      }
    };
  }

  @Override
  public void printStatistics(PrintStream pOut, Result pResult, ReachedSet pReached) {
    TreeSet<Property> properties = Sets.newTreeSet(new Comparator<Property>() {
      @Override
      public int compare(Property pO1, Property pO2) {
        return pO1.toString().compareTo(pO2.toString());
      }
    });

    for (Property p: properties) {
      StatisticsUtils.write(pOut, 1, 50, p.toString(), "");

      StatisticsUtils.write(pOut, 2, 50, "Coverage Ratio", getExplosionFactor(p));
    }
  }

  @Override
  public String getName() {
    return "Property Statistics";
  }

}
