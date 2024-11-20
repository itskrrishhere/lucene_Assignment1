package org.lucene.assignment;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Searcher {
    //function to search the document and save it in outputQueryFilePath using specified similarity
    public static void searchDocuments(String queryFilePath, String outputFilePath, String outputIndexDir, Analyzer analyzer, Similarity similarity) throws Exception {
        System.out.println("Started searchDocuments");
        Directory directory = FSDirectory.open(Paths.get(outputIndexDir));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        indexSearcher.setSimilarity(similarity);

        //create weightage for heading
        Map<String, Float> ScoreMap = new HashMap<>();
        ScoreMap.put("Title", 0.7f);
        ScoreMap.put("Author", 0.13f);
        ScoreMap.put("Bibliography", 0.2f);
        ScoreMap.put("Words", 0.6f);

        //create a multifield query parser
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] {"Title", "Author", "Bibliography", "Words"}, analyzer, ScoreMap
        );

        //read the file to be queried
        BufferedReader reader = Files.newBufferedReader(Paths.get(queryFilePath));
        FileWriter writer = new FileWriter(outputFilePath);
        String line = reader.readLine();
        int id = 1;
        String queryID,queryContent;

        while(line != null){
            if(line.matches("(\\.I)( )(\\d)*")){ // check for queryId
                StringBuilder stringBuilder;
                line = reader.readLine();
                queryID =String.valueOf(id);
                while(line != null && !line.matches("(\\.I)( )(\\d)*")){
                    if(line.matches("(\\.W)")){ // check for content
                        stringBuilder = new StringBuilder();
                        line = reader.readLine();
                        while(line != null && !line.matches("(\\.I)( )(\\d)*")){
                            stringBuilder.append(line).append(" ");
                            line = reader.readLine();
                        }
                        queryContent=stringBuilder.toString();
                        //remove unwanted characters
                        String escapedQueryContent= QueryParser.escape(queryContent.trim());

                        // Create query and search and get top 50 doc scores
                        Query query = queryParser.parse(escapedQueryContent);
                        TopDocs results = indexSearcher.search(query, 50);

                        // Store results in Trec_eval format: queryID, iteration , documentID, rank, score, runID
                        int rank = 1;
                        String runID = "dummy";
                        for (ScoreDoc scoreDoc : results.scoreDocs) {
                            Document resultDoc = indexSearcher.doc(scoreDoc.doc);
                            writer.write(queryID + " 0 " + resultDoc.get("Id") + " " + rank + " " + scoreDoc.score + " " + runID + "\n");
                            rank++;
                        }
                    }
                }
                id++;
            }
        }
        // close writer and reader
        writer.close();
        reader.close();
        System.out.println("Completed searchDocuments Successfully");
    }

}
