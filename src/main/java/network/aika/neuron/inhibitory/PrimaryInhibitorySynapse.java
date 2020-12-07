/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.neuron.inhibitory;


import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Direction;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Visitor;

public class PrimaryInhibitorySynapse extends InhibitorySynapse {


    public PrimaryInhibitorySynapse() {
        super();
    }

    public PrimaryInhibitorySynapse(Neuron<?> input, InhibitoryNeuron output, Synapse template) {
        super(input, output, template);
    }

    @Override
    public boolean checkTemplate(Activation iAct, Activation oAct, Visitor v) {
        return v.scope == Direction.SAME;
    }

    @Override
    public boolean checkInduction(Link l) {
        return true;
    }

    @Override
    public PrimaryInhibitorySynapse instantiateTemplate(Neuron<?> input, InhibitoryNeuron output) {
        if(!input.getTemplates().contains(getInput())) {
            return null;
        }

        return new PrimaryInhibitorySynapse(input, output, this);
    }

    public void transition(Visitor v, Activation nextAct, boolean create) {
        Visitor nv = v.prepareNextStep();
        nv.incrementPathLength();

        nv.scope = v.scope.getNext(v.downUpDir);

        nv.follow(nextAct);
    }
}
