package lia;

/**
 * Copyright Manning Publications Co.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;

public class CreateTestIndex {

    /** Default maximum allowed token length */
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

    public static Document getDocument(String rootDir, File file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));

        Document doc = new Document();

        // category comes from relative path below the base directory
        String category = file.getParent().substring(rootDir.length());    //1
        category = category.replace(File.separatorChar, '/');              //1

        String isbn = props.getProperty("isbn");         //2
        String title = props.getProperty("title");       //2
        String author = props.getProperty("author");     //2
        String url = props.getProperty("url");           //2
        String subject = props.getProperty("subject");   //2

        String pubmonth = props.getProperty("pubmonth"); //2

        System.out.println(title + "\n" + author + "\n" + subject + "\n" + pubmonth + "\n" + category + "\n---------");

        doc.add(new StringField("isbn",                     // 3
                isbn,                       // 3
                Field.Store.YES)); // 3
        doc.add(new StringField("category",                 // 3
                category,                   // 3
                Field.Store.YES)); // 3
        doc.add(new Field("title",                    // 3
                title,                      // 3
                Field.Store.YES,            // 3
                Field.Index.ANALYZED,       // 3
                Field.TermVector.WITH_POSITIONS_OFFSETS));   // 3
        doc.add(new Field("title2",                   // 3
                title.toLowerCase(),        // 3
                Field.Store.YES,            // 3
                Field.Index.NOT_ANALYZED_NO_NORMS,   // 3
                Field.TermVector.WITH_POSITIONS_OFFSETS));  // 3

        // split multiple authors into unique field instances
        String[] authors = author.split(",");            // 3
        for (String a : authors) {                       // 3
            doc.add(new Field("author",                    // 3
                    a,                           // 3
                    Field.Store.YES,             // 3
                    Field.Index.NOT_ANALYZED,    // 3
                    Field.TermVector.WITH_POSITIONS_OFFSETS));   // 3
        }

        doc.add(new Field("url",                        // 3
                url,                           // 3
                Field.Store.YES,                // 3
                Field.Index.NOT_ANALYZED_NO_NORMS));   // 3
        doc.add(new Field("subject",                     // 3  //4
                subject,                       // 3  //4
                Field.Store.YES,               // 3  //4
                Field.Index.ANALYZED,          // 3  //4
                Field.TermVector.WITH_POSITIONS_OFFSETS)); // 3  //4

        doc.add(new IntField("pubmonth",          // 3
                Integer.parseInt(pubmonth),
                Field.Store.YES));   // 3

        Date d; // 3
        try { // 3
            d = DateTools.stringToDate(pubmonth); // 3
        } catch (ParseException pe) { // 3
            throw new RuntimeException(pe); // 3
        }                                             // 3
        doc.add(new IntField("pubmonthAsDay",      // 3
                (int) (d.getTime() / (1000 * 3600 * 24)),
                Field.Store.YES));   // 3

        for (String text : new String[]{title, subject, author, category}) {           // 3 // 5
            doc.add(new Field("contents", text,                             // 3 // 5
                    Field.Store.NO, Field.Index.ANALYZED,         // 3 // 5
                    Field.TermVector.WITH_POSITIONS_OFFSETS));    // 3 // 5
        }

        return doc;
    }

    private static String aggregate(String[] strings) {
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            buffer.append(strings[i]);
            buffer.append(" ");
        }

        return buffer.toString();
    }

    private static void findFiles(List<File> result, File dir) {
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".properties")) {
                result.add(file);
            } else if (file.isDirectory()) {
                findFiles(result, file);
            }
        }
    }

    private static class MyStandardAnalyzer extends StopwordAnalyzerBase {  // 6
        public MyStandardAnalyzer() {                 // 6
            super();                                            // 6
        }                                                                 // 6

        // copied from StandardAnalyzer with few corrections
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final Tokenizer src;
            if (getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
                StandardTokenizer t = new StandardTokenizer();
                t.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
                src = t;
            } else {
                StandardTokenizer40 t = new StandardTokenizer40();
                t.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
                src = t;
            }
            TokenStream tok = new StandardFilter(src);
            tok = new LowerCaseFilter(tok);
            tok = new StopFilter(tok, stopwords);
            return new TokenStreamComponents(src, tok) {
                @Override
                protected void setReader(final Reader reader) throws IOException {
                    int m = DEFAULT_MAX_TOKEN_LENGTH;
                    if (src instanceof StandardTokenizer) {
                        ((StandardTokenizer)src).setMaxTokenLength(m);
                    } else {
                        ((StandardTokenizer40)src).setMaxTokenLength(m);
                    }
                    super.setReader(reader);
                }
            };
        }

        @Override
        public int getPositionIncrementGap(String field) {                // 6
            if (field.equals("contents")) {                                 // 6
                return 100;                                                   // 6
            } else {                                                        // 6
                return 0;                                                     // 6
            }
        }
    }

    public static void indexDir(String dataDir, String indexDir) throws IOException {
        List<File> results = new ArrayList<File>();
        findFiles(results, new File(dataDir));
        System.out.println(results.size() + " books to index");

        IndexWriterConfig config  = new IndexWriterConfig(new MyStandardAnalyzer());
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriter w = new IndexWriter(dir, config);           //3

        for (File file : results) {
            Document doc = getDocument(dataDir, file);
            w.addDocument(doc);
        }
        w.close();
        dir.close();
    }

    public static void deleteIndex(String indexDir) throws IOException {
        Path directory = Paths.get(indexDir);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public static void main(String[] args) throws IOException {
        String dataDir = args[0];
        String indexDir = args[1];
        indexDir(dataDir, indexDir);
    }
}

/*
  #1 Get category
  #2 Pull fields
  #3 Add fields to Document instance
  #4 Flag subject field
  #5 Add catch-all contents field
  #6 Custom analyzer to override multi-valued position increment
*/