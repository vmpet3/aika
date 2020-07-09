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

import network.aika.ActivationFunction;
import network.aika.Model;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Sign;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Fired;
import network.aika.neuron.Neuron;

import java.util.List;


/**
 *
 * @author Lukas Molzberger
 */
public class InhibitoryNeuron extends Neuron<InhibitorySynapse> {

    public static byte type;

    protected InhibitoryNeuron() {
        super();
    }

    public InhibitoryNeuron(NeuronProvider p) {
        super(p);
    }

    public InhibitoryNeuron(Model model, String descriptionLabel, Boolean isInputNeuron) {
        super(model, descriptionLabel, isInputNeuron);
    }

    public void tryToLink(Activation iAct, Activation oAct) {
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public double getCost(Sign s) {
        return 0;
    }

    @Override
    public Synapse getInputSynapse(NeuronProvider n) {
        throw new UnsupportedOperationException();
    }

    public double propagateRangeCoverage(Activation iAct) {
        return iAct.rangeCoverage;
    }

    public List<Neuron> induceNeuron(Activation act) {
        return null;
    }

    public Synapse induceSynapse(Activation iAct, Activation oAct) {
        return new InhibitorySynapse(iAct.getNeuron(), (InhibitoryNeuron) oAct.getNeuron());
    }

    @Override
    public Fired incrementFired(Fired f) {
        return f;
    }

    /*
    public boolean isWeak(Synapse s, Synapse.State state) {
        return s.getWeight(state) < -getBias();
    }
*/

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
    }

    @Override
    public void addInputSynapse(InhibitorySynapse s) {
    }

    @Override
    public void addOutputSynapse(Synapse s) {
    }

    @Override
    public void removeInputSynapse(InhibitorySynapse s) {
    }

    @Override
    public void removeOutputSynapse(Synapse s) {
    }
}
