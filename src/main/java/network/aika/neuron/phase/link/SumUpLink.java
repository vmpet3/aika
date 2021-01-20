package network.aika.neuron.phase.link;

import network.aika.neuron.activation.Link;
import network.aika.neuron.phase.RankedImpl;

public class SumUpLink extends RankedImpl implements LinkPhase {

    private double delta;

    public SumUpLink(int rank, double delta) {
        super(rank);

        this.delta = delta;
    }

    @Override
    public void process(Link l) {
        l.sumUpLink(delta);
        l.getOutput().checkIfFired();
    }

    @Override
    public int compare(Link l1, Link l2) {
        return 0;
    }
}
