// Copyright 2020
// Author: Matei Simtinică

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Task1
 * You have to implement 4 methods:
 * readProblemData         - read the problem input and store it however you see fit
 * formulateOracleQuestion - transform the current problem instance into a SAT instance and write the oracle input
 * decipherOracleAnswer    - transform the SAT answer back to the current problem's answer
 * writeAnswer             - write the current problem's answer
 */
public class Task1 extends Task {

    // HashMap in which all families and their correspondent
    // friends (adjacent nodes) will be stored
    public HashMap<Integer, ArrayList<Integer>> myMap;

    // Input data
    public int nrFamilies;
    public int nrRel;
    public int nrSpies;

    // Final answer and results
    String booleanAnswer;
    public ArrayList<Integer> finalResults;

    @Override
    public void solve() throws IOException, InterruptedException {
        readProblemData();
        formulateOracleQuestion();
        askOracle();
        decipherOracleAnswer();
        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        File inFile = new File(this.inFilename);
        Scanner reader = new Scanner(inFile);

        // Extract the data from input file
        this.nrFamilies = Integer.parseInt(reader.next());
        this.nrRel = Integer.parseInt(reader.next());
        this.nrSpies = Integer.parseInt(reader.next());

        // Initialize the hashmap & set the families as keys
        myMap = new HashMap<>();
        for (int i = 0; i < nrFamilies; i++) {
            myMap.put(i + 1, new ArrayList<>());
        }

        while (reader.hasNext()) {
            // Extract the current family
            int family1 = Integer.parseInt(reader.next());
            ArrayList<Integer> value = myMap.get(family1);

            // Add a new family "friend" to the current family
            value.add(Integer.valueOf(reader.next()));
            myMap.put(family1, value);
        }

        reader.close();
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        // Open oracle input file & write the data to be processed
        FileWriter myWriter = new FileWriter(this.oracleInFilename);

        // Create an array that stores all variables used in order to
        // reduce the current problem to SAT
        ArrayList<Integer> varArray = new ArrayList<>();
        for (int i = 0; i < nrFamilies * nrSpies; i++) {
            varArray.add(i + 1);
        }

        int nrOfVariables = nrFamilies * nrSpies;
        int nrOfClauses = nrSpies * nrRel + nrFamilies + 1 + ((nrSpies - 1) * nrSpies / 2);
        myWriter.write("p cnf" + " " + nrOfVariables + " " +
                nrOfClauses + "\n");

        int idx = 0;
        for (int i = 0; i < nrFamilies; i++) {
            // Clauses stating that every family must have
            // an assigned spy
            for (int j = 0; j < nrSpies; j++) {
                myWriter.write(varArray.get(idx + j) + " ");
            }
            myWriter.write("0" + "\n");

            // Create clauses for which a family cannot
            // have more than one spy
            for (int m = 0; m < nrSpies - 1; m++) {
                for (int n = m + 1; n < nrSpies; n++) {
                    myWriter.write("-" + varArray.get(idx + m) + " ");
                    myWriter.write("-" + varArray.get(idx + n) + " ");
                    myWriter.write("0" + "\n");
                }
            }
            idx += nrSpies;
        }

        for (int i = 0; i < nrFamilies; i++) {
            ArrayList<Integer> friends = myMap.get(i + 1);
            if (!friends.isEmpty()) {
                for (Integer friend : friends) {
                    for (int m = 0; m < nrSpies; m++) {

                        // Clauses that introduce the limitation of families to
                        // the fact that they cannot have the same spy as their
                        // friend-families (adjacent nodes)
                        myWriter.write("-" + varArray.get(nrSpies * i + m) + " ");
                        myWriter.write("-" + varArray.get((friend - 1) * nrSpies + m) + " ");
                        myWriter.write("0" + "\n");
                    }
                }
            }
        }

        myWriter.close();
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

                    // Identify the family to which the
                    // spy belongs to
                    int spyNumber = number % nrSpies;
                    if (spyNumber != 0) {
                        finalResults.add(spyNumber);
                    } else {
                        finalResults.add(nrSpies);
                    }
                }
            }
        }
        reader.close();
    }

    @Override
    public void writeAnswer() throws IOException {
        // Output file
        FileWriter outWriter = new FileWriter(this.outFilename);

        // Write the final answer
        outWriter.write(booleanAnswer + "\n");
        if (booleanAnswer.equals("True")) {
            // Write the spy number assigned to each family
            for (Integer finalResult : finalResults) {
                outWriter.write(finalResult + " ");
            }
        }

        outWriter.close();
    }
}
