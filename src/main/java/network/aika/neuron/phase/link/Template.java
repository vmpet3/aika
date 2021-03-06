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
package network.aika.neuron.phase.link;

import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.direction.Direction;
import network.aika.neuron.phase.Phase;
import network.aika.neuron.phase.Ranked;
import network.aika.neuron.phase.RankedImpl;

import network.aika.neuron.phase.activation.ActivationPhase;

import static network.aika.neuron.phase.activation.ActivationPhase.TEMPLATE_INPUT;
import static network.aika.neuron.phase.activation.ActivationPhase.UPDATE_SYNAPSE_INPUT_LINKS;

/**
 * Uses the Template Network defined in the {@link network.aika.neuron.Templates} to induce new template
 * activations and links.
 *
 * @author Lukas Molzberger
 */
public class Template extends RankedImpl implements LinkPhase {

    @Override
    public Ranked getPreviousRank() {
        return UPDATE_SYNAPSE_INPUT_LINKS;
    }

    @Override
    public void process(Link l) {
        l.follow(TEMPLATE_INPUT);
    }

    public String toString() {
        return "Link: Template";
    }

    @Override
    public int compare(Link l1, Link l2) {
        return 0;
    }
}
