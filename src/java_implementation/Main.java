// Copyright 2020
// Author: Matei Simtinică

import java.io.IOException;

class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Task task = null;

        if (args.length < 5) {
            System.err.println("Usage: ./main <task> <input_file> <SAT_input_file> <SAT_output_file> <output_file>\n");
            System.exit(-1);
        }

        String taskNumber = args[0];
        String input = args[1];
        String satInput = args[2];
        String satOutput = args[3];
        String output = args[4];

        switch (taskNumber) {
            case "task1":
                task = new Task1(); break;
            case "task2":
                task = new Task2(); break;
            case "task3":
                task = new Task3(); break;
            case "bonus":
                task = new BonusTask(); break;
            default :
                System.err.println("Not a valid task");
                System.exit(-1);
        }

        task.addFiles(input, satInput, satOutput, output);
        task.solve();
    }
}
