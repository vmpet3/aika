package network;

import network.aika.Document;
import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.ExcitatoryNeuron;
import network.aika.neuron.excitatory.ExcitatorySynapse;
import network.aika.neuron.pattern.PatternNeuron;
import org.junit.Test;

public class PatternTest {


    @Test
    public void testPattern() {
        Model m = new Model();

        PatternNeuron inA = new PatternNeuron(m, "IN A");
        PatternNeuron inB = new PatternNeuron(m, "IN B");
        PatternNeuron inC = new PatternNeuron(m, "IN C");

        ExcitatoryNeuron relN = new ExcitatoryNeuron(m, "Rel");


        ExcitatoryNeuron eA = new ExcitatoryNeuron(m, "E A");
        ExcitatoryNeuron eB = new ExcitatoryNeuron(m, "E B");
        ExcitatoryNeuron eC = new ExcitatoryNeuron(m, "E C");

        PatternNeuron out = new PatternNeuron(m, "OUT");


        Neuron.init(eA.getProvider(), 1.0,
                new ExcitatorySynapse.Builder()
                        .setNeuron(inA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(out)
                        .setWeight(10.0)
                        .setRecurrent(true)
        );

        Neuron.init(eB.getProvider(), 1.0,
                new ExcitatorySynapse.Builder()
                        .setNeuron(inB)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(eA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(relN)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(out)
                        .setWeight(10.0)
                        .setRecurrent(true)
        );

        Neuron.init(eC.getProvider(), 1.0,
                new ExcitatorySynapse.Builder()
                        .setNeuron(inC)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(eB)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(relN)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(out)
                        .setWeight(10.0)
                        .setRecurrent(true)
        );

        Neuron.init(out.getProvider(), 1.0,
                new ExcitatorySynapse.Builder()
                        .setNeuron(eA)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(eB)
                        .setWeight(10.0)
                        .setRecurrent(false),
                new ExcitatorySynapse.Builder()
                        .setNeuron(eC)
                        .setWeight(10.0)
                        .setRecurrent(false)
        );


        Document doc = new Document(m, "ABC");

        Activation actA = inA.addInput(doc,
                new Activation.Builder()
                        .setValue(1.0)
                        .setInputTimestamp(0)
                        .setFired(0)
        );

        Activation actB = inB.addInput(doc,
                new Activation.Builder()
                        .setValue(1.0)
                        .setInputTimestamp(1)
                        .setFired(0)
        );

        relN.addInput(doc,
                new Activation.Builder()
                        .setValue(1.0)
                        .setInputTimestamp(1)
                        .setFired(0)
                        .addInputLink(actA)
                        .addInputLink(actB)
        );

        Activation actC = inC.addInput(doc,
                new Activation.Builder()
                        .setValue(1.0)
                        .setInputTimestamp(2)
                        .setFired(0)
        );

        relN.addInput(doc,
                new Activation.Builder()
                        .setValue(1.0)
                        .setInputTimestamp(2)
                        .setFired(0)
                        .addInputLink(actB)
                        .addInputLink(actC)
        );


        System.out.println(doc.activationsToString());
    }
}