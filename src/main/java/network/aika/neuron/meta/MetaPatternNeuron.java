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
package network.aika.neuron.meta;

import network.aika.Model;
import network.aika.neuron.Neuron;


/**
 *
 * @author Lukas Molzberger
 */
public class MetaPatternNeuron extends MetaNeuron {

    public static final String TYPE_STR = Model.register("NMP", MetaPatternNeuron.class);


    public MetaPatternNeuron(Neuron p) {
        super(p);
    }

    public MetaPatternNeuron(Model model, String label) {
        super(model, label);
    }

    @Override
    public String getType() {
        return TYPE_STR;
    }

}
