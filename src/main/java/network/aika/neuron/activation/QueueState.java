package network.aika.neuron.activation;

import network.aika.Thought;
import network.aika.neuron.phase.activation.ActivationPhase;

import java.util.Comparator;
import java.util.TreeSet;

public class QueueState {

    private TreeSet<ActivationPhase> pendingPhases = new TreeSet<>(Comparator.comparing(p -> p.getRank()));;
    private Activation actToQueue;
    private Activation queuedAct;
    private boolean marked;

    private QueueState() {
    }

    public QueueState(Activation act) {
        actToQueue = act;
        pendingPhases.addAll(act.getThought().getConfig().getPhases());
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void setActToQueue(Activation actToQueue) {
        this.actToQueue = actToQueue;
    }

    public void addPhase(Activation act, ActivationPhase p) {
        actToQueue = act;
        pendingPhases.add(p);

        updateThoughtQueue();
    }

    public void updateThoughtQueue() {
        if(pendingPhases.isEmpty())
            return;

        Thought t = actToQueue.getThought();
        if(queuedAct != null) {
            t.removeActivationFromQueue(queuedAct);
        }
        actToQueue.setPhase(pendingPhases.first());
        t.addToQueue(actToQueue);
        queuedAct = actToQueue;
    }

    public void removePendingPhase() {
        queuedAct = null;
        pendingPhases.pollFirst();
    }

    public QueueState copy(Activation newAct) {
        QueueState qs = new QueueState();
        qs.pendingPhases.addAll(pendingPhases);
        qs.actToQueue = newAct;
        return qs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        pendingPhases.forEach(p -> sb.append(p.getClass().getSimpleName() + ", "));

        return sb.toString();
    }
}