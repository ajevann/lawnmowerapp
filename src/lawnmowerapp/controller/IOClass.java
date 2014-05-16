/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lawnmowerapp.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Class in charge of reading in and writing out necessary files.
 * 
 * Alexandre Vann, 2013
 */
public class IOClass {

    private static ArrayList<String> output = new ArrayList<String>();

    public IOClass() {
        //
    }

    public static void exportMonthlyBill(java.sql.Date date, String lastName, String firstName, double owed, ArrayList<String> jobs) {
        prepareOutput(date, lastName, firstName, owed, jobs);
        exportFile(output, "bills/", date + " " + lastName + " " + firstName + ".txt");
    }

    public static void exportWeeklyJobs(java.sql.Date date, String lastName, String firstName, ArrayList<String> jobs) {
        prepareOutput(date, lastName, firstName, jobs);
        exportFile(output, "weeklyjobs/", date + " " + lastName + " " + firstName + ".txt");
    }

    private static void prepareOutput(java.sql.Date date, String lastName, String firstName, ArrayList<String> jobs) {
        output.clear();
        output.add("                                                        LAWN MOWERS INC.");
        output.add("                                                        123 SOMEWHERE ST.,");
        output.add("                                                        ANYWEHRE, USA");
        output.add("\n");
        output.add("DATE : " + date.toString() + "\n");
        output.add("NAME : " + lastName + ", " + firstName + "\n");
        output.add("\n");
        output.add("------------------------------------------------------------------------\n");
        output.add("\n");
        output.add("\n");

        for (String job : jobs) {
            output.add(job);
        }

    }

    private static void prepareOutput(java.sql.Date date, String lastName, String firstName, double owed, ArrayList<String> jobs) {
        output.clear();
        output.add("                                                        LAWN MOWERS INC.");
        output.add("                                                        123 SOMEWHERE ST.,");
        output.add("                                                        ANYWEHRE, USA");
        output.add("\n");
        output.add(lastName + ", " + firstName + "\n");
        output.add("\n");
        output.add("------------------------------------------------------------------------\n");
        output.add("AS OF  : " + date + "\n");
        output.add("DUE    : $ " + owed + "\n");
        output.add("------------------------------------------------------------------------\n");
        output.add("HISTORY OF JOBS");
        output.add("---------------\n");

        for (String job : jobs) {
            output.add(job);
        }
    }

    public static void exportFile(ArrayList<String> fileContents, String filePath, String fileName) {

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(new File(filePath, fileName)));

            for (String s : fileContents) {
                bw.write(s + "\n");
            }

            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
