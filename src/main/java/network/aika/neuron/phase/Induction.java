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
package network.aika.neuron.phase;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Visitor;

/**
 *
 * @author Lukas Molzberger
 */
public class Induction implements Phase {


    @Override
    public void process(Activation act) {
        act.updateValueAndPropagate();
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void tryToLink(Activation iAct, Activation oAct, Visitor v) {
        Neuron n = oAct.getNeuron();

        if(!iAct.isActive() ||
                n.isInputNeuron()) return;

        Synapse s = n.getInputSynapse(iAct.getNeuronProvider());
        if (s != null) return;

        n.induceSynapse(iAct, oAct, v);
    }

    @Override
    public void propagate(Activation act) {
        if(act.isActive()) {
            act.getNeuron().induceNeuron(act);
        }
    }

    @Override
    public int getRank() {
        return 8;
    }

    @Override
    public int compare(Activation act1, Activation act2) {
        return 0;
    }
}
