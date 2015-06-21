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

import lia.BooksIndexBase;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

// From chapter 3
public class ExplainerTest extends BooksIndexBase {

    public static final String INDEX_PATH_BOOKS = "books_index";
    public static final String QUERY = "junit";

    @Test
    public void testExplaination() throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_PATH_BOOKS));
        QueryParser parser = new QueryParser(
                "contents", new SimpleAnalyzer());
        Query query = parser.parse(QUERY);

        System.out.println("Query: " + QUERY);

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, 10);

        for (ScoreDoc match : topDocs.scoreDocs) {
            Explanation explanation
                    = searcher.explain(query, match.doc);     //#A

            System.out.println("----------");
            Document doc = searcher.doc(match.doc);
            System.out.println(doc.get("title"));
            System.out.println(explanation.toString());  //#B
        }
        reader.close();
        directory.close();
    }
}
/*
#A Generate Explanation
#B Output Explanation
*/
