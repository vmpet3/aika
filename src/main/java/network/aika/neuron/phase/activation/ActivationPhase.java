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

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Visitor;
import network.aika.neuron.phase.Phase;

import java.util.Comparator;

/**
 *
 * @author Lukas Molzberger
 */
public interface ActivationPhase extends Phase<Activation> {
    ActivationPhase INITIAL_LINKING = new Linking();
    ActivationPhase PREPARE_FINAL_LINKING = new PrepareFinalLinking();
    ActivationPhase FINAL_LINKING = new FinalLinking();
    ActivationPhase SOFTMAX = new Softmax();
    ActivationPhase COUNTING = new Counting();
    ActivationPhase TRAINING = new Training();
    ActivationPhase GRADIENTS = new Gradients();
    ActivationPhase UPDATE_WEIGHTS = new UpdateWeights();
    ActivationPhase INDUCTION = new Induction();
    ActivationPhase FINAL = new Final();

    void process(Activation act);

    boolean isFinal();

    void tryToLink(Activation iAct, Activation oAct, Visitor v);

    void propagate(Activation act, Visitor v);
}