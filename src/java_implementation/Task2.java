// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Task2
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task2 extends Task {
    // Input data
    private int nrFamilies;
    private int nrRelations;
    private int cliqueDim;

    // Auxiliary structures
    private int[][] adjMatrix;
    private HashMap<Integer, ArrayList<Integer>> variablesDict;
    private ArrayList<String> clausesList;

    // Final answer and results
    private String booleanAnswer;
    private ArrayList<Integer> finalResults;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    /**
     * Method that inserts a new edge between two nodes.
     *
     * @param i first node
     * @param j second node
     */
    private void addEdge(int i, int j) {
        adjMatrix[i][j] = 1;
        adjMatrix[j][i] = 1;
    }

    @Override
    public void readProblemData() throws IOException {
        // Open and read from input file
        File inFile = new File(this.inFilename);
        Scanner inReader = new Scanner(inFile);

        // Extract data from input file
        this.nrFamilies = Integer.parseInt(inReader.next());
        this.nrRelations = Integer.parseInt(inReader.next());
        this.cliqueDim = Integer.parseInt(inReader.next());

        // Create the adjacency matrix of the graph
        adjMatrix = new int[nrFamilies][nrFamilies];
        for (int r = 0; r < nrRelations; r++) {
            int i = Integer.parseInt(inReader.next());
            int j = Integer.parseInt(inReader.next());
            addEdge(i - 1, j - 1);
        }

        inReader.close();

        // Structure that stores all variables
        variablesDict = new HashMap<>();
        for (int i = 0; i < nrFamilies * cliqueDim; i++) {
            variablesDict.put(i + 1, new ArrayList<>());
        }

        // Auxiliary structure that stores all clauses to be created
        clausesList = new ArrayList<>();
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // Open oracle input file & write the data to be processed
        int var = 1;
        FileWriter oracleWriter = new FileWriter(this.oracleInFilename);

        // Create clauses that require that there must be exactly one
        // node in the clique for each i between 1 and cliqueDim (k)
        for (int i = 1; i <= cliqueDim; i++) {
            StringBuilder clause = new StringBuilder();
            for (int v = 0; v < nrFamilies; v++) {
                (variablesDict.get(var)).add(i);
                (variablesDict.get(var)).add(v);
                clause.append(var).append(" ");
                var++;
            }
            clause.append("0");
            clausesList.add(clause.toString());
        }

        for (Map.Entry<Integer, ArrayList<Integer>> var1 : variablesDict.entrySet()) {
            int i = (var1.getValue()).get(0);
            int v = (var1.getValue()).get(1);

            for (Map.Entry<Integer, ArrayList<Integer>> var2 : variablesDict.entrySet()) {
                int j = (var2.getValue()).get(0);
                int w = (var2.getValue()).get(1);

                // Create clauses that emphasize as if here's no edge from
                // v to w then nodes v and w cannot both be in the clique
                if ((adjMatrix[v][w] == 0) && (v != w) &&
                        (!(var1.getKey()).equals(var2.getKey()))) {
                    String clause = "-" + var1.getKey() + " ";
                    clause += "-" + var2.getKey() + " " + "0";
                    clausesList.add(clause);
                }

                // Create clauses stating that no node is both the ith
                // and jth node of the clique
                if (((v == w && i != j) || (v != w && i == j)) &&
                        !var1.getKey().equals(var2.getKey())) {
                    String clause = "-" + var1.getKey() + " ";
                    clause += "-" + var2.getKey() + " " + "0";
                    clausesList.add(clause);
                }
            }
        }

        oracleWriter.write("p cnf" + " " + variablesDict.size() +
                " " + clausesList.size() + "\n");

        // Write all newly created clauses in the oracle input file
        for (String str : clausesList) {
            oracleWriter.write(str + "\n");
        }
        oracleWriter.close();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        // Read the oracle's answer and prediction
        File oracleOutFile = new File(this.oracleOutFilename);
        Scanner reader = new Scanner(oracleOutFile);
        finalResults = new ArrayList<>();

        // Read the final answer
        booleanAnswer = reader.next();

        if (booleanAnswer.equals("True")) {
            reader.next();
            while (reader.hasNext()) {
                int number = Integer.parseInt(reader.next());
                // Check if the variable is true (>0)
                // or false (<0)
                if (number > 0) {

                    // Pin out the family that belongs to
                    // the extended family (maximal clique)
                    int familyNumber = number % nrFamilies;
                    if (familyNumber != 0) {
                        finalResults.add(familyNumber);
                    } else {
                        finalResults.add(nrFamilies);
                    }
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        // Output file
        FileWriter outWriter = new FileWriter(this.outFilename);

        // Write the final answer
        outWriter.write(booleanAnswer + "\n");
        if (booleanAnswer.equals("True")) {

            // Write the number of the family that is
            // part of the extended family we are looking for
            for (Integer finalResult : finalResults) {
                outWriter.write(finalResult + " ");
            }
        }

        outWriter.close();
    }
}
