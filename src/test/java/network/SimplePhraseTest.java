package network;

import network.aika.Config;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.excitatory.PatternNeuron;
import network.aika.neuron.excitatory.PatternPartNeuron;
import network.aika.text.Document;
import network.aika.text.TextModel;
import network.aika.text.TextReference;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class SimplePhraseTest {

    public String[] phrases = new String[]{
            "der Hund",
            "die Katze",
            "der Vogel",
            "das Pferd",
            "die Maus",
            "der Elefant",
            "der Löwe",
            "das Pony",
            "die Spinne",
            "der Jaguar"
    };

    private String trimPrefix(String l) {
        return l.substring(l.indexOf("-") + 1);
    }

    @Test
    public void simplePhraseTest() {
        TextModel model = new TextModel();
        model.setConfig(
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
                        .setEnableTraining(false)
                        .setSurprisalInductionThreshold(0.0)
                        .setGradientInductionThreshold(0.0)
        );

        Random r = new Random(1);

        for (int k = 0; k < 1000; k++) {
            model.getConfig().setEnableTraining(k > 100);

            String phrase = phrases[r.nextInt(phrases.length)];
            System.out.println("  " + phrase);

            Neuron.debugOutput = phrase.equalsIgnoreCase("der Hund");

            Document doc = new Document(phrase);

            int i = 0;
            TextReference lastRef = null;
            for(String t: doc.getContent().split(" ")) {
                int j = i + t.length();
                lastRef = doc.processToken(model, lastRef, i, j, t).getReference();

                i = j + 1;
            }

            doc.process(model);

            if (Neuron.debugOutput) {
                System.out.println(doc.toString(true));
            }
        }
    }
}
