
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
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

// From chapter 2
public class IndexingTest extends TestCase {
    protected String[] ids = {"1", "2"};
    protected String[] unindexed = {"Netherlands", "Italy"};
    protected String[] unstored = {"Amsterdam has lots of bridges",
            "Venice has lots of canals"};
    protected String[] text = {"Amsterdam", "Venice"};

    private Directory directory;

    protected void setUp() throws Exception {     //1
        directory = new RAMDirectory();

        IndexWriter writer = getWriter();           //2

        for (int i = 0; i < ids.length; i++) {      //3
            Document doc = new Document();
            doc.add(new StringField("id", ids[i], Field.Store.YES));
            doc.add(new StringField("country", unindexed[i], Field.Store.YES));
            doc.add(new TextField("contents", unstored[i], Field.Store.NO));
            doc.add(new TextField("city", text[i], Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();
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

    public void testIndexWriter() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(ids.length, writer.numDocs());            //7
        writer.close();
    }

    public void testIndexReader() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        assertEquals(ids.length, reader.maxDoc());             //8
        assertEquals(ids.length, reader.numDocs());            //8
        reader.close();
    }

  /*
    #1 Run before every test
    #2 Create IndexWriter
    #3 Add documents
    #4 Create new searcher
    #5 Build simple single-term query
    #6 Get number of hits
    #7 Verify writer document count
    #8 Verify reader document count
  */


    public void testDeleteBeforeIndexMerge() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(2, writer.numDocs()); //A
        writer.deleteDocuments(new Term("id", "1"));  //B
        writer.commit();
        assertTrue(writer.hasDeletions());    //1
        assertEquals(2, writer.maxDoc());    //2
        assertEquals(1, writer.numDocs());   //2
        writer.close();
    }

    public void testDeleteAfterIndexMerge() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(2, writer.numDocs());
        writer.deleteDocuments(new Term("id", "1"));
        writer.forceMergeDeletes();                //3
        writer.commit();
        assertFalse(writer.hasDeletions());
        assertEquals(1, writer.maxDoc());  //C
        assertEquals(1, writer.numDocs()); //C
        writer.close();
    }

  /*
    #A 2 docs in the index
    #B Delete first document
    #C 1 indexed document, 0 deleted documents
    #1 Index contains deletions
    #2 1 indexed document, 1 deleted document
    #3 Optimize compacts deletes
  */

    public void testUpdate() throws IOException {

        assertEquals(1, getHitCount("city", "Amsterdam"));

        IndexWriter writer = getWriter();

        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("country", "Netherlands", Field.Store.YES));
        doc.add(new TextField("contents", "Den Haag has a lot of museums", Field.Store.NO));
        doc.add(new TextField("city", "Den Haag", Field.Store.YES));
        writer.addDocument(doc);

        writer.updateDocument(new Term("id", "1"),       //B
                doc);                      //B
        writer.close();

        assertEquals(0, getHitCount("city", "Amsterdam"));//C
        assertEquals(1, getHitCount("city", "Haag"));     //D
    }

  /*
    #A Create new document with "Haag" in city field
    #B Replace original document with new version
    #C Verify old document is gone
    #D Verify new document is indexed
  */
}

