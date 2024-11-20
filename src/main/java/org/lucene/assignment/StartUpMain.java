package org.lucene.assignment;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StartUpMain {
    //Map that contains objects of Different Analyzers
    public static Map<String, Analyzer> analyzerMap = new HashMap<>();
    //Map that contains objects of Different similarities
    public static Map<String, Similarity> similarityMap = new HashMap<>();

    static {
        //Initialize the analyzer map with respective analyzer object
        analyzerMap.put("standard", new StandardAnalyzer());
        analyzerMap.put("simple", new SimpleAnalyzer());
        analyzerMap.put("whitespace", new WhitespaceAnalyzer());
        analyzerMap.put("english", new EnglishAnalyzer());
        //Initialize the similarity map with respective similarity object
        similarityMap.put("BM25", new BM25Similarity());
        similarityMap.put("Classic", new ClassicSimilarity());
        similarityMap.put("Boolean", new BooleanSimilarity());
        similarityMap.put("LMDirichlet", new LMDirichletSimilarity());
    }
    //startup main
    public static void main(String[] args) throws Exception {
        String cranPath = "cran";
        String outputPath ="output";
        String cranIndexFilePath = Paths.get(cranPath, "cran.all.1400").toString();

        //Iterate and check indexing for different analyzers
        for (Map.Entry<String, Analyzer> analyzerEntry : analyzerMap.entrySet()) {
            String analyzerName = analyzerEntry.getKey();
            Analyzer analyzer = analyzerEntry.getValue();
            System.out.println("Using Analyzer: " + analyzerName);

            String outputDir = Paths.get(outputPath,analyzerName).toString();
            String cranIndexDir = Paths.get(outputDir, "cranIndexed").toString();
            //Index the document and save it in outputIndexDir using specified analyzer
            Indexer.indexDocuments(cranIndexFilePath,cranIndexDir,analyzer);
            //Iterate and check querying for different similarities
            for (Map.Entry<String, Similarity> similarlityEntry : similarityMap.entrySet()) {
                String similarlityName = similarlityEntry.getKey();
                Similarity similarlity = similarlityEntry.getValue();
                System.out.println("Using similarlity: " + similarlityName);

                String queryFilePath = Paths.get(cranPath, "cran.qry").toString();
                String outputQueryFilePath = Paths.get(outputDir, similarlityName+"results.txt").toString();
                //search the document and save it in outputQueryFilePath using specified similarity
                Searcher.searchDocuments(queryFilePath, outputQueryFilePath, cranIndexDir, analyzer,similarlity);
            }
        }
        String qrelFilePath = Paths.get(cranPath, "cranqrel").toString();
        String updatedQrelFilePath = Paths.get(outputPath, "updated_cranqrel").toString();
        //fix the qrel file format for trec_eval and save it in updatedQrelFilePath
        qrelFormatFixer.convertQrelFile(qrelFilePath,updatedQrelFilePath);
    }
}
