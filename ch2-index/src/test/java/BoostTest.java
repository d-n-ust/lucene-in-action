
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
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// From chapter 2
public class BoostTest extends TestCase {

    private Directory directory;

    protected void setUp() throws Exception {     //1
        directory = new RAMDirectory();
    }

    private IndexWriter getWriter() throws IOException {            // 2
        return new IndexWriter(directory,
                new IndexWriterConfig(
                        new WhitespaceAnalyzer())); // 2
    }

    protected int getHitCount(String fieldName, String searchString)
            throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader); //4
        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);                        //5
        int hitCount = TestUtil.hitCount(searcher, query);     //6
        reader.close();
        return hitCount;
    }

    protected List<Document> getHits(String fieldName, String searchString)
            throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);
        TopDocs hits = searcher.search(query, 10);

        List<Document> result = new ArrayList<Document>();
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            result.add(searcher.doc(scoreDoc.doc));
        }

        reader.close();
        return result;
    }

    protected int getNumDocs()
            throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        return reader.numDocs();
    }

    public void testBoostField() throws IOException {

        IndexWriter writer = getWriter();
        writer.deleteAll();
        writer.commit();
        assertEquals(0, getNumDocs());

        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new TextField("contents", "Moscow", Field.Store.NO));
        writer.addDocument(doc);

        Document doc2 = new Document();
        doc2.add(new StringField("id", "2", Field.Store.YES));
        doc2.add(new TextField("contents", "Moscow", Field.Store.NO));
        writer.addDocument(doc2);

        Document doc3 = new Document();
        doc3.add(new StringField("id", "3", Field.Store.YES));
        doc3.add(new TextField("contents", "Moscow", Field.Store.NO));
        writer.addDocument(doc3);

        writer.close();

        List<Document> docs = getHits("contents", "Moscow");

        assertEquals(3, getNumDocs());
        assertEquals(3, docs.size());
        assertEquals("1", docs.get(0).get("id"));
        assertEquals("2", docs.get(1).get("id"));
        assertEquals("3", docs.get(2).get("id"));

        writer = getWriter();
        writer.deleteAll();
        writer.commit();

        assertEquals(0, getNumDocs());

        Document doc2_1 = new Document();
        doc2_1.add(new StringField("id", "1", Field.Store.YES));
        Field f = new TextField("contents", "Moscow", Field.Store.NO);
        f.setBoost(1.0f);
        doc2_1.add(f);
        writer.addDocument(doc2_1);

        Document doc2_2 = new Document();
        doc2_2.add(new StringField("id", "2", Field.Store.YES));
        Field f2 = new TextField("contents", "Moscow", Field.Store.NO);
        f2.setBoost(100.0f);
        doc2_2.add(f2);
        writer.addDocument(doc2_2);

        Document doc2_3 = new Document();
        doc2_3.add(new StringField("id", "3", Field.Store.YES));
        Field f3 = new TextField("contents", "Moscow", Field.Store.NO);
        f3.setBoost(10.0f);
        doc2_3.add(f3);
        writer.addDocument(doc2_3);

        writer.close();

        docs = getHits("contents", "Moscow");

        assertEquals(3, getNumDocs());
        assertEquals(3, docs.size());
        assertEquals("2", docs.get(0).get("id"));
        assertEquals("3", docs.get(1).get("id"));
        assertEquals("1", docs.get(2).get("id"));
    }
}

