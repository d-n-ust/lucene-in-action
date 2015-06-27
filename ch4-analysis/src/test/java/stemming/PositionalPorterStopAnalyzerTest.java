package stemming;

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

import junit.framework.TestCase;
import lia.TestUtil;
import metaphone.MetaphoneReplacementAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

// From chapter 4
public class PositionalPorterStopAnalyzerTest extends TestCase {

    private static PositionalPorterStopAnalyzer porterAnalyzer =
            new PositionalPorterStopAnalyzer();

    private IndexSearcher searcher;
    private QueryParser parser;

    @Before
    public void setUp() throws Exception {

        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(porterAnalyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        doc.add(new TextField("contents", "The quick brown fox jumps over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);

        parser = new QueryParser("contents", porterAnalyzer);
    }

    @Test
    public void testWithSlop() throws Exception {
        parser.setPhraseSlop(1);

        Query query = parser.parse("\"over the lazy\"");

        assertEquals("hole accounted for", 1, TestUtil.hitCount(searcher, query));
    }

    @Test
    public void testStems() throws Exception {
        Query query = new QueryParser("contents", porterAnalyzer)
                .parse("laziness");
        assertEquals("lazi", 1, TestUtil.hitCount(searcher, query));

        query = parser.parse("\"fox jumped\"");
        assertEquals("jump jumps jumped jumping", 1, TestUtil.hitCount(searcher, query));
    }

}
