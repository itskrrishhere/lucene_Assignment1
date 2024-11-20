package org.lucene.assignment;

import java.io.*;

public class qrelFormatFixer {
    //function to fix the qrel file format for trec_eval and save it in updatedQrelFilePath
    public static void convertQrelFile(String inputQrelFile, String outputQrelFile) {
        try{
            System.out.println("Started convertQrelFile");
            BufferedReader reader = new BufferedReader(new FileReader(inputQrelFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputQrelFile));
            //read the qrel file
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    // split the line into 3 parts
                    String queryId = parts[0];
                    String docId = parts[1];
                    String relevance = parts[2];
                    // change invalid records to 5
                    if (Integer.parseInt(relevance)<=0 || Integer.parseInt(relevance)>5 ) {
                        relevance = "5";
                    }
                    // add iteration as 0
                    writer.write(queryId + " 0 " + docId + " " + relevance);
                    writer.newLine();
                }
                line = reader.readLine();
            }
            System.out.println("Completed convertQrelFile successfully");
            // close writer and reader
            reader.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error in convertQrelFile"+e);
        }
    }

}
