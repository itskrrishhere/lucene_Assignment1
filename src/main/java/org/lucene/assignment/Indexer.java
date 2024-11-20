package org.lucene.assignment;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Indexer {
    //function for Index the document and save it in outputIndexDir using specified analyzer
    public static void indexDocuments(String filePath, String outputIndexDir, Analyzer analyzer) throws Exception {
        System.out.println("Started indexDocuments");
        Directory outputDir = FSDirectory.open(Paths.get(outputIndexDir));
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(outputDir, indexWriterConfig);
        //read the file to be indexed
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
        String line = reader.readLine();
        while(line != null) {
            //create a new document
            Document document = new Document();
            // check for id
            if (line.matches("(\\.I)( )(\\d)*")) {
                StringBuilder stringBuilder;
                document.add(new StringField("Id", line.substring(3), Field.Store.YES));
                line = reader.readLine();
                while (line != null && !line.matches("(\\.I)( )(\\d)*")) {
                    // check for Title
                    if (line.matches("\\.T")) {
                        stringBuilder = new StringBuilder();
                        line = reader.readLine();
                        while (line != null && !line.matches(("\\.A"))) {
                            stringBuilder.append(line).append(" ");
                            line = reader.readLine();
                        }
                        // add Title to document
                        document.add(new TextField("Title", stringBuilder.toString(), Field.Store.YES));
                    } else if (line.matches("\\.A")) { // check for author
                        stringBuilder = new StringBuilder();
                        line = reader.readLine();
                        while (line != null && !line.matches(("\\.B"))) {
                            stringBuilder.append(line).append(" ");
                            line = reader.readLine();
                        }
                        // add Author to document
                        document.add(new TextField("Author", stringBuilder.toString(), Field.Store.YES));
                    } else if (line.matches("\\.B")) { // check for bibliography
                        stringBuilder = new StringBuilder();
                        line = reader.readLine();
                        while (line != null && !line.matches(("\\.W"))) {
                            stringBuilder.append(line).append(" ");
                            line = reader.readLine();
                        }
                        // add Bibliography to document
                        document.add(new TextField("Bibliography", stringBuilder.toString(), Field.Store.YES));
                    } else if (line.matches("\\.W")) { // check for Words
                        stringBuilder = new StringBuilder();
                        line = reader.readLine();
                        while (line != null && !line.matches(("(\\.I)( )(\\d)*"))) {
                            stringBuilder.append(line).append(" ");
                            line = reader.readLine();
                        }
                        // add Words to document
                        document.add(new TextField("Words",  stringBuilder.toString(), Field.Store.YES));
                    }
                }
            }
            // add document to outputIndexDir
            writer.addDocument(document);
        }
        //close the writer and reader
        writer.close();
        reader.close();
        System.out.println("Completed indexDocuments Successfully");
    }

}
