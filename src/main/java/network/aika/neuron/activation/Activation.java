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
package network.aika.neuron.activation;

import network.aika.Document;
import network.aika.Utils;
import network.aika.neuron.*;
import network.aika.neuron.activation.linker.LNode;
import network.aika.neuron.activation.linker.Linker;
import network.aika.neuron.activation.linker.LinkingPhase;

import java.util.*;
import java.util.stream.Stream;

import static network.aika.neuron.InputKey.INPUT_COMP;
import static network.aika.neuron.Synapse.State.CURRENT;
import static network.aika.neuron.activation.Direction.INPUT;
import static network.aika.neuron.activation.Fired.NOT_FIRED;

/**
 *
 * @author Lukas Molzberger
 */
public class Activation implements Comparable<Activation> {

    public static double TOLERANCE = 0.001;

    public double value;
    public double net;
    public Fired fired = NOT_FIRED;

    public double rangeCoverage;

    private int id;
    private INeuron<?> neuron;
    private Document doc;

    public double p;

    public TreeMap<Link, Link> inputLinksFiredOrder;
    public Map<InputKey, Link> inputLinks;
    public NavigableMap<Activation, Link> outputLinks;

    public LinkingPhase linkingPhase;
    public boolean isFinal;

    public LNode lNode;

    public int round; // Nur als Abbruchbedingung
    public Activation nextRound;
    public Activation lastRound;

    public Set<Activation> branches = new TreeSet<>();
    public Activation mainBranch;

    private Activation(int id, INeuron<?> n) {
        this.id = id;
        this.neuron = n;
    }

    public Activation(int id, Document doc, INeuron<?> n, boolean branch, LinkingPhase linkingPhase, Activation lastRound, int round) {
        this.id = id;
        this.doc = doc;
        this.neuron = n;
        this.round = round;
        this.linkingPhase = linkingPhase;

        this.net = n.getTotalBias(assumePosRecLinks(), CURRENT);

        if(branch) {
            lastRound.branches.add(this);
            mainBranch = lastRound;
        } else {
            this.lastRound = lastRound;
            if (lastRound != null) {
                lastRound.nextRound = this;
            }
        }

        doc.addActivation(this);

        inputLinksFiredOrder = new TreeMap<>(Comparator
                .<Link, Boolean>comparing(l -> !l.isRecurrent())
                .thenComparing(l -> l.input.getFired())
                .thenComparing(l -> l.input)
        );

        inputLinks = new TreeMap<>(INPUT_COMP);

        outputLinks = new TreeMap<>(Comparator
                .<Activation, Neuron>comparing(act -> act.getNeuron())
                .thenComparing(act -> act)
        );
    }

    public int getId() {
        return id;
    }

    public Document getDocument() {
        return doc;
    }

    public String getLabel() {
        return getINeuron().getLabel();
    }

    public boolean isInitialRound() {
        return round == 0;
    }

    public boolean assumePosRecLinks() {
        return linkingPhase == LinkingPhase.INITIAL && neuron.hasPositiveRecurrentSynapses();
    }

    public <N extends INeuron> N getINeuron() {
        return (N) neuron;
    }

    public Neuron getNeuron() {
        return neuron.getProvider();
    }

    public Fired getFired() {
        return fired;
    }

    public Collection<Link> getLinks(Direction dir) {
        return dir == INPUT ? inputLinks.values() : outputLinks.values();
    }

    public Stream<Link> getOutputLinks(Neuron n, PatternScope ps) {
        return outputLinks
                .values()
                .stream()
                .filter(l -> l.output.getNeuron().getId() == n.getId())
                .filter(l -> l.synapse.getPatternScope() == ps);
    }

    public Activation cloneAct(boolean branch) {
        Activation clonedAct = new Activation(
                branch ? doc.getNewActivationId() : id,
                doc,
                neuron,
                branch,
                linkingPhase,
                this,
                round + 1
        );

        inputLinks
                .values()
                .forEach(l -> {
                    new Link(l.synapse, l.input, clonedAct).link();
                });

        return clonedAct;
    }

    public void setValue(double v) {
        this.value = v;
    }

    public void setFired(Fired fired) {
        this.fired = fired;
    }

    public void setRangeCoverage(double rangeCoverage) {
        this.rangeCoverage = rangeCoverage;
    }

    public boolean isActive() {
        return value > 0.0;
    }

    public double getP() {
        return 1.0;
    }

    public boolean isConflicting() {
        if(isInitialRound()) {
            return false;
        }

        return inputLinks.values().stream()
                .filter(l -> l.isConflict())
                .flatMap(l -> l.input.inputLinks.values().stream())  // Hangle dich durch die inhib. Activation.
                .anyMatch(l -> l.input.lNode == null);
    }

    public void addLink(Link l) {
        boolean firedInOrder = inputLinks.isEmpty() || l.input.fired.compareTo(inputLinksFiredOrder.lastKey().input.fired) >= 0;

        l.output = this;
        l.link();

        if(isFinal) return;

        if(firedInOrder) {
            sumUpLink(l);
        } else {
            compute();
        }
    }

    public void sumUpLink(Link l) {
        double w = l.synapse.getWeight();
        net += l.input.value * w;
        rangeCoverage += getINeuron().propagateRangeCoverage(l.input);

        checkIfFired(l);
    }

    public void compute() {
        fired = NOT_FIRED;
        net = neuron.getTotalBias(assumePosRecLinks(), CURRENT);
        for (Link l: inputLinksFiredOrder.values()) {
            sumUpLink(l);
        }
    }

    public void checkIfFired(Link l) {
        if(fired == NOT_FIRED && net > 0.0) {
            fired = neuron.incrementFired(l.input.fired);
            doc.getQueue().add(this);
        }
    }

    public void process(boolean processMode) {
        value = neuron.getActivationFunction().f(net);
        isFinal = true;
        if(lastRound == null || !equals(lastRound)) {
            Linker.linkForward(this, processMode);
        }
    }

    public void unlink() {
        inputLinks
                .values()
                .forEach(l -> l.unlink());
    }

    public boolean equals(Activation act) {
        return Math.abs(value - act.value) <= TOLERANCE;
    }

    public String toString() {
        return getId() + " " +
                getINeuron().getClass().getSimpleName() + ":" + getLabel() +
                " value:" + Utils.round(value) +
                " net:" + Utils.round(net) +
                " p:" + Utils.round(p) +
                " round:" + round;
    }

    @Override
    public int compareTo(Activation act) {
        return Integer.compare(id, act.id);
    }

    public static class Builder {
        public double value = 1.0;
        public int inputTimestamp;
        public int fired;
        public Map<InputKey, Activation> inputLinks = new TreeMap<>(INPUT_COMP);

        public double rangeCoverage;


        public Builder setValue(double value) {
            this.value = value;
            return this;
        }

        public Builder setInputTimestamp(int inputTimestamp) {
            this.inputTimestamp = inputTimestamp;
            return this;
        }

        public Builder setFired(int fired) {
            this.fired = fired;
            return this;
        }

        public Map<InputKey, Activation> getInputLinks() {
            return this.inputLinks;
        }

        public Builder addInputLink(PatternScope ps, Activation iAct) {
            InputKey ik = new InputKey() {
                @Override
                public Neuron getPInput() {
                    return iAct.getNeuron();
                }

                @Override
                public PatternScope getPatternScope() {
                    return ps;
                }
            };

            inputLinks.put(ik, iAct);
            return this;
        }

        public Builder setRangeCoverage(double rangeCoverage) {
            this.rangeCoverage = rangeCoverage;
            return this;
        }
    }

    public static class OscillatingActivationsException extends RuntimeException {

        private String activationsDump;

        public OscillatingActivationsException(String activationsDump) {
            super("Maximum number of rounds reached. The network might be oscillating.");

            this.activationsDump = activationsDump;
        }

        public String getActivationsDump() {
            return activationsDump;
        }
    }
}
