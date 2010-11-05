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
package org.sosy_lab.cpachecker.cpa.defuse;

import java.util.Iterator;
import java.util.Set;

import org.sosy_lab.cpachecker.cfa.objectmodel.CFAEdge;

import org.sosy_lab.cpachecker.core.interfaces.AbstractElement;
import org.sosy_lab.cpachecker.cpa.defuse.DefUseDefinition;
import org.sosy_lab.cpachecker.cpa.defuse.DefUseElement;

import com.google.common.collect.ImmutableSet;

public class DefUseElement implements AbstractElement, Iterable<DefUseDefinition>
{
    private final Set<DefUseDefinition> definitions;

    public DefUseElement (Set<DefUseDefinition> definitions)
    {
      this.definitions = ImmutableSet.copyOf(definitions);
    }
    
    public DefUseElement(DefUseElement definitions, DefUseDefinition newDefinition) {
      ImmutableSet.Builder<DefUseDefinition> builder = ImmutableSet.builder();
      builder.add(newDefinition);
      for(DefUseDefinition def : definitions.definitions) {
        if (!def.getVariableName().equals(newDefinition.getVariableName())) {
          builder.add(def);
        }
      }
      this.definitions = builder.build();
    }

    @Override
    public Iterator<DefUseDefinition> iterator()
    {
        return definitions.iterator ();
    }
    
    public boolean containsAllOf(DefUseElement other) {
      return definitions.containsAll(other.definitions);
    }

    @Override
    public boolean equals (Object other)
    {
        if (this == other)
            return true;

        if (!(other instanceof DefUseElement))
            return false;

        DefUseElement otherDefUse = (DefUseElement) other;
        return otherDefUse.definitions.equals(this.definitions);
    }
    
    @Override
    public int hashCode() {
      return definitions.hashCode();
    }

    @Override
    public String toString ()
    {
        StringBuilder builder = new StringBuilder ();
        builder.append ('{');

        boolean hasAny = false;
        for (DefUseDefinition def : definitions)
        {
            CFAEdge assigningEdge = def.getAssigningEdge ();
            builder.append ('(').append (def.getVariableName ()).append(", ");

            if (assigningEdge != null)
                builder.append(assigningEdge.getPredecessor ().getNodeNumber ());
            else
                builder.append (0);

            builder.append (", ");

            if (assigningEdge != null)
                builder.append (assigningEdge.getSuccessor ().getNodeNumber ());
            else
                builder.append (0);

            builder.append("), ");
            hasAny = true;
        }

        if (hasAny)
            builder.replace (builder.length () - 2, builder.length (), "}");
        else
            builder.append ('}');

        return builder.toString ();
    }
}
