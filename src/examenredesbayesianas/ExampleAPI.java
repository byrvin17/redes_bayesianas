package examenredesbayesianas;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.io.probmodel.PGMXReader;

public class ExampleAPI {

    final public static List<ProbNode> lista = new ArrayList<ProbNode>();
    final public static List<String> s = new ArrayList<String>();

    public static void main(String[] args) {
        new ExampleAPI();
    }
    // Constants
    final private String bayesNetworkName = "BN-two-diseases.pgmx";

    // Constructor
    public ExampleAPI() {
        try {
            // Open the file containing the network
//            InputStream file = new FileInputStream(new File("C:/Users/todohogar/"
//                    + "Documents/UNL/Marzo - Julio 2014/IA/X B/Redes bayesianas/Elvira/"
//                    + "elvira/D.pgmx"));
            InputStream file = new FileInputStream(new File(System.getProperty("user.dir") + "/src/archivored/exameprobar2.pgmx"));

            // Load the Bayesian network
            PGMXReader pgmxReader = new PGMXReader();
            ProbNet probNet = pgmxReader.loadProbNet(file, bayesNetworkName).getProbNet();

            System.out.println("numero nodos: " + probNet.getNumNodes());
            // Create an evidence case
            // (An evidence case is composed of a set of findings)

            List<ProbNode> listPro = probNet.getProbNodes();
            for (int i = 0; i < listPro.size(); i++) {
                System.out.println("o---- " + listPro.size());
                ProbNode probNode = listPro.get(i);
                lista.add(probNode);
                System.out.println("11---- " + probNode.getNode().toString());
                System.out.println("12---- " + probNode.getUtilityParents());
                System.out.println("13---- " + probNode.getPolicyType().name());
                //System.out.println("14---- " + probNode.getPotentials().get(probNode.getNumPotentials()).getVariables());
                //obtenemos el nombre de cada nodo
                System.out.println("1---- " + probNode.getUtilityFunction());
                System.out.println("1---- " + probNode.getName());
                System.out.println("2---- " + probNode.getProbNet());
                System.out.println("3---- " + probNode.getRelevance());
                System.out.println("4---- " + probNode.getNodeType().toString());

            }

            EvidenceCase evidence = new EvidenceCase();
//
//			// The first finding we introduce is the presence
//			// of the symptom 
            evidence.addFinding(probNet, "estado_final", "pasa");

            // Create an instance of the inference algorithm
            // In this example, we use the variable elimination algorithm
            InferenceAlgorithm variableElimination = new VariableElimination(probNet);

            // Add the evidence to the algorithm
            // The resolution of the network consists of finding the
            // optimal policies. 
            // In the case of a model that does not contain decision nodes
            // (for example, a Bayesian network), there is no difference between
            // pre-resolution and post-resolution evidence, but if the model
            // contained decision nodes (for example, an influence diagram)
            // evidence introduced before resolving the network is treated 
            // differently from that introduce afterwards.
            variableElimination.setPreResolutionEvidence(evidence);

            // We are interested in the posterior probabilities of the diseases
            //Variable disease1 = probNet.getVariable("semiestado");
            Variable disease2 = probNet.getVariable("estado_final");
            ArrayList<Variable> variablesOfInterest = new ArrayList<Variable>();
            //variablesOfInterest.add(disease1);
            variablesOfInterest.add(disease2);

            // Compute the posterior probabilities
            HashMap<Variable, TablePotential> posteriorProbabilities =
                    variableElimination.getProbsAndUtilities();

            // Print the posterior probabilities on the standard output
            //printResults(evidence, variablesOfInterest, posteriorProbabilities);

            // Add a new finding and do inference again
            // (We see that the presence of the sign confirms the presence
            // of Disease 1 with high probability and explains away Disease 2)
            evidence.addFinding(probNet, "estado_final", "pasa");
            posteriorProbabilities = variableElimination.getProbsAndUtilities(variablesOfInterest);
            printResults(evidence, variablesOfInterest, posteriorProbabilities);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Print the posterior probabilities of the variables of interest on the
     * standard output
     *
     * @param evidence. <code>EvidenceCase</code> The set of observed findings
     * @param variablesOfInterest. <code>ArrayList</code> of
     * <code>Variable</code> The variables whoseposterior probability we are
     * interested in
     * @param posteriorProbabilities. <code>HashMap</code>. Each
     * <code>Variable</code> is mapped onto a <code>TablePotential</code>
     * containing its posterior probability
     */
    public void printResults(EvidenceCase evidence, ArrayList<Variable> variablesOfInterest,
            HashMap<Variable, TablePotential> posteriorProbabilities) {
        // Print the findings
        System.out.println("Evidencia:");
        for (Finding finding : evidence.getFindings()) {
            System.out.print("1:  " + finding.getVariable() + ": ");
            s.add(String.valueOf(finding.getVariable()));
            s.add(finding.getState());
            System.out.println(finding.getState());
        }
        // Print the posterior probability of the state "present" of each variable of interest
        System.out.println("Probabilidade posteriores: ");
        for (Variable variable : variablesOfInterest) {
            double value;
            TablePotential posteriorProbabilitiesPotential = posteriorProbabilities.get(variable);
            System.out.print(" 2:  " + variable + ": ");
            int stateIndex = -1;
            try {
                stateIndex = variable.getStateIndex("pasa");
                value = posteriorProbabilitiesPotential.values[stateIndex];
                s.add(String.valueOf(Util.roundedString(value, "0.001")));
                System.out.println(" 3. " + Util.roundedString(value, "0.001"));
            } catch (InvalidStateException e) {
                System.err.println("State \"present\" not found for variable \""
                        + variable.getName() + "\".");
                e.printStackTrace();
            }
        }
        //System.out.println("sd");
    }
}
