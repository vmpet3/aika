package network.aika.neuron.activation.linker;

import network.aika.neuron.INeuron;
import network.aika.neuron.activation.Activation;


public class LMatchingNode<N extends INeuron> extends LNode<N> {

    public LMatchingNode(PatternType patternType, Class<N> neuronClass, String label) {
        super(patternType, neuronClass, label);
    }

    public Activation follow(Mode m, INeuron n, Activation act, LLink from, Activation startAct) {

        if(neuronClass != null && act.getINeuron().getClass().equals(neuronClass)) {
            return null;
        }

        if(act.isConflicting()) return null;

        act.lNode = this;

        links.stream()
                .filter(nl -> nl != from)
                .forEach(nl -> nl.follow(m, act, this, startAct));

        act.lNode = null;

        return act;
    }
}
