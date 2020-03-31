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


import network.aika.neuron.INeuron;
import network.aika.neuron.Neuron;
import network.aika.neuron.Synapse;
import network.aika.neuron.TNeuron;
import network.aika.neuron.activation.Link;
import network.aika.neuron.activation.Linker;


/**
 *
 * @author Lukas Molzberger
 */
public class PrimaryInhibitorySynapse extends InhibitorySynapse {

    public static byte type;

    public PrimaryInhibitorySynapse() {
        super();
    }

    public PrimaryInhibitorySynapse(Neuron input, Neuron output) {
        super(input, output);
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public boolean isRecurrent() {
        return false;
    }

    @Override
    public boolean isNegative() {
        return false;
    }

    @Override
    public boolean isPropagate() {
        return true;
    }

    protected void addLinkInternal(INeuron in, INeuron out) {
        in.addOutputSynapse(this);
    }

    protected void removeLinkInternal(INeuron in, INeuron out) {
        in.removeOutputSynapse(this);
    }

    public static class Builder extends Synapse.Builder {
        protected SynapseFactory getSynapseFactory() {
            return (input, output) -> new PrimaryInhibitorySynapse(input, output);
        }
    }
}