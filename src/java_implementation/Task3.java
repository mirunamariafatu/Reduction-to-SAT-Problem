// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Task3
 * This being an optimization problem, the solve method's logic has to work differently.
 * You have to search for the minimum number of arrests by successively querying the oracle.
 * Hint: it might be easier to reduce the current task to a previously solved task
 */
public class Task3 extends Task {
    String task2InFilename;
    String task2OutFilename;

    // Input data
    private int nrFamilies;
    private int nrRelations;

    // Auxiliary structures
    private int[][] adjMatrix;
    private ArrayList<Integer> maxClique;

    // Final answer and results
    private String booleanAnswer;
    private ArrayList<Integer> finalResults;


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
    public void solve() throws IOException, InterruptedException {
        task2InFilename = inFilename + "_t2";
        task2OutFilename = outFilename + "_t2";
        Task2 task2Solver = new Task2();
        task2Solver.addFiles(task2InFilename, oracleInFilename,
                oracleOutFilename, task2OutFilename);
        readProblemData();
        finalResults = new ArrayList<>();

        // Starting from the maximum possible size of a clique (nr of nodes),
        // it will be searched for a valid maximal clique in
        // the given graph
        for (int i = nrFamilies; i > 0; i--) {

            // Reduce the problem to a previously solved task
            reduceToTask2(i);
            task2Solver.solve();
            extractAnswerFromTask2();

            if (booleanAnswer.equals("True")) {
                // The maximum clique was found
                for (int j = 1; j <= nrFamilies; j++) {
                    if (!maxClique.contains(j)) {

                        // Extract only the nodes that are not
                        // part of the maximal clique
                        finalResults.add(j);
                    }
                }
                break;
            }
        }
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        // Open and read from input file
        File inFile = new File(this.inFilename);
        Scanner inReader = new Scanner(inFile);

        // Extract data from input file
        this.nrFamilies = Integer.parseInt(inReader.next());
        this.nrRelations = Integer.parseInt(inReader.next());

        // Create the adjacency matrix of the graph
        adjMatrix = new int[nrFamilies][nrFamilies];
        for (int r = 0; r < nrRelations; r++) {
            int i = Integer.parseInt(inReader.next());
            int j = Integer.parseInt(inReader.next());
            addEdge(i - 1, j - 1);
        }

        inReader.close();
    }

    public void reduceToTask2(int cliqueDim) throws IOException {
        // Open the input file for task2 &  write the data to be processed
        FileWriter task2Writer = new FileWriter(this.task2InFilename);
        ArrayList<String> relations = new ArrayList<>();

        for (int i = 0; i < nrFamilies - 1; i++) {
            for (int j = 0; j < nrFamilies; j++) {
                if (adjMatrix[i][j] == 0) {

                    // Create the connections between the adjacent
                    // nodes from the complementary graph
                    relations.add((i + 1) + " " + (j + 1) + "\n");
                }
            }
        }

        task2Writer.write(nrFamilies + " " + relations.size() + " " + cliqueDim + "\n");
        for (String rel : relations) {
            task2Writer.write(rel);
        }
        task2Writer.close();
    }

    public void extractAnswerFromTask2() throws FileNotFoundException {
        // Read the oracle's answer and prediction
        File task2OutFile = new File(this.task2OutFilename);
        Scanner reader = new Scanner(task2OutFile);
        maxClique = new ArrayList<>();

        // Read the final answer
        booleanAnswer = reader.next();

        if (booleanAnswer.equals("True")) {
            while (reader.hasNext()) {
                // Extract only the nodes(families) contained
                // in the maximal clique
                maxClique.add(Integer.valueOf(reader.next()));
            }
        }
        reader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // Output file
        FileWriter outWriter = new FileWriter(this.outFilename);

        // Write the final answer
        for (Integer finalResult : finalResults) {
            outWriter.write(finalResult + " ");
        }
        outWriter.close();
    }
}
