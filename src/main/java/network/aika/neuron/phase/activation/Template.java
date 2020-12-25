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
package network.aika.neuron.phase.activation;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Visitor;
import network.aika.neuron.activation.direction.Direction;
import network.aika.neuron.phase.RankedImpl;
import network.aika.neuron.phase.VisitorPhase;
import network.aika.neuron.phase.link.LinkPhase;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Lukas Molzberger
 */
public class Template extends RankedImpl implements VisitorPhase, ActivationPhase {

    private Direction direction;

    public Template(int rank, Direction dir) {
        super(rank);
        direction = dir;
    }

    @Override
    public ActivationPhase[] getNextActivationPhases(Config c) {
        return new ActivationPhase[] {
                PREPARE_FINAL_LINKING,
                SOFTMAX,
                COUNTING,
//                TEMPLATE,
                INDUCTION,
                PROPAGATE_GRADIENT,
                UPDATE_SYNAPSE_INPUT_LINKS
        };
    }

    @Override
    public LinkPhase[] getNextLinkPhases(Config c) {
        return new LinkPhase[] {
                LinkPhase.SELF_GRADIENT,
                LinkPhase.SHADOW_FACTOR,
                LinkPhase.INDUCTION,
                LinkPhase.UPDATE_WEIGHTS
        };
    }

    @Override
    public void process(Activation act) {
        act.followLinks( // Sollte durch die Link phase erfolgen
                new Visitor(
                        this,
                        act,
                        direction
                )
        );

        propagate(act,
                new Visitor(
                        this,
                        act,
                        direction
                )
        );
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void tryToLink(Activation fromAct, Visitor v) {
        Direction dir = v.startDir;
        Activation iAct = dir.getCycleInput(fromAct, v.getOriginAct());
        Activation oAct = dir.getCycleOutput(fromAct, v.getOriginAct());

        Neuron<?> n = fromAct.getNeuron();

        if(!iAct.isActive() || n.isInputNeuron())
            return;

        if (n.getInputSynapse(iAct.getNeuronProvider()) != null)
            return;

        oAct.getNeuron()
                .getTemplates()
                .stream()
                .flatMap(tn -> tn.getInputSynapses())
                .filter(ts -> ts.checkTemplate(iAct, oAct, v))
                .filter(s -> iAct.getNeuron().getTemplates().contains(s.getInput()))
                .forEach(s ->
                        s.closeCycle(fromAct, v, iAct, oAct)
                );
    }

    private void propagate(Activation act, Visitor v) {
        if (act.gradientSumIsZero())
            return;

        if (!act.getNeuron().checkTemplate(act)) {
            return;
        }

        Set<Synapse> templateSynapses = act
                .getNeuron()
                .getTemplates()
                .stream()
                .flatMap(tn -> v.startDir.getSynapses(tn))
                .filter(ts -> ts.checkTemplatePropagate(v, act))
                .collect(Collectors.toSet());

        v.startDir.getLinks(act)
                .forEach(l ->
                        templateSynapses.remove(l.getSynapse().getTemplate())
                );

        templateSynapses.forEach(s ->
                s.propagate(act, v)
        );
    }

    @Override
    public int compare(Activation act1, Activation act2) {
        return 0;
    }
}
