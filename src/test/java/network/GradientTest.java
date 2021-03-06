package network;

import network.aika.Config;
import network.aika.Thought;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Reference;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.PatternPartNeuron;
import network.aika.neuron.excitatory.PatternPartSynapse;
import network.aika.neuron.phase.Ranked;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

public class GradientTest {


    private String trimPrefix(String l) {
        return l.substring(l.indexOf("-") + 1);
    }

    @Test
    public void gradientAndInduction() throws InterruptedException {
        System.out.println(Ranked.dumpPhaseRanks());

        TextModel m = new TextModel();
        m.setConfig(
                new Config() {
                    public String getLabel(Activation act) {
                        Neuron n = act.getNeuron();
                        Activation iAct = act.getInputLinks()
                                .findFirst()
                                .map(l -> l.getInput())
                                .orElse(null);

                        if(n instanceof PatternPartNeuron) {
                            return "PP-" + trimPrefix(iAct.getLabel());
                        } else if (n instanceof PatternNeuron) {
                            return "P-" + ((Document)act.getThought()).getContent();
                        } else {
                            return "I-" + trimPrefix(iAct.getLabel());
                        }
                    }
                }
                        .setAlpha(0.99)
                        .setLearnRate(-0.1)
                        .setEnableTraining(true)
                        .setSurprisalInductionThreshold(0.0)
                        .setGradientInductionThreshold(0.0)
        );

        m.setN(912);

        Document doc = new Document("A B ");

        int i = 0;
        TextReference lastRef = null;
        for(String t: doc.getContent().split(" ")) {
            int j = i + t.length();
            lastRef = doc.processToken(m, lastRef, i, j, t).getReference();

            i = j + 1;
        }

        Neuron nA = m.getNeuron("A");
        nA.setFrequency(53.0);
        nA.getSampleSpace().setN(299);
        nA.getSampleSpace().setLastPos(899);

        Neuron nB = m.getNeuron("B");
        nB.setFrequency(10.0);
        nB.getSampleSpace().setN(121);
        nB.getSampleSpace().setLastPos(739);

        doc.process(m);

        System.out.println();
    }
}
