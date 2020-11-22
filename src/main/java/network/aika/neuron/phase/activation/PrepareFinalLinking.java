package network.aika.neuron.phase.activation;

import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Visitor;

public class PrepareFinalLinking implements ActivationPhase {

    @Override
    public void process(Activation act) {
        act.updateForFinalPhase();
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void tryToLink(Activation iAct, Activation oAct, Visitor v) {

    }

    @Override
    public void propagate(Activation act, Visitor v) {

    }

    @Override
    public int getRank() {
        return 1;
    }

    @Override
    public int compare(Activation o1, Activation o2) {
        return 0;
    }
}