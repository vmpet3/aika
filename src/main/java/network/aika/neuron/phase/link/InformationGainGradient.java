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
import network.aika.neuron.phase.Ranked;
import network.aika.neuron.phase.RankedImpl;
import network.aika.neuron.phase.activation.ActivationPhase;

/**
 * Computes the gradient of the information gain function for this activation.
 *
 * @see <a href="https://aika.network/training.html">Aika Training</a>
 *
 * @author Lukas Molzberger
 */
public class InformationGainGradient extends RankedImpl implements LinkPhase {

    @Override
    public Ranked getPreviousRank() {
        return ActivationPhase.ENTROPY_GRADIENT;
    }

    @Override
    public void process(Link l) {
        l.computeInformationGainGradient();
    }

    public String toString() {
        return "Link: Information-Gain Gradient";
    }

    @Override
    public int compare(Link l1, Link l2) {
        return 0;
    }
}
