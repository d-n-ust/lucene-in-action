package synonim;

import lia.TestUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class SynonymAnalyzerTest {

    private IndexReader reader;
    private IndexSearcher searcher;
    private static SynonymAnalyzer synonymAnalyzer =
            new SynonymAnalyzer(new TestSynonymEngine());

    @Before
    public void setUp() throws Exception {
        RAMDirectory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(synonymAnalyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        doc.add(new TextField("content", "The quick brown fox jumps over the lazy dog", Field.Store.YES));  //#2
        writer.addDocument(doc);

        writer.close();

        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
    }

    @After
    public void tearDown() throws Exception {
        reader.close();
    }

    @Test
    public void testJumps() throws Exception {
        TokenStream stream = synonymAnalyzer.tokenStream("contents",                   // #A
                new StringReader("jumps"));   // #A
        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

        int i = 0;
        String[] expected = new String[]{"jumps", "hops", "leaps"};             // #B
        stream.reset();
        while (stream.incrementToken()) {
            assertEquals(expected[i], term.toString());

            int expectedPos;        // #C
            if (i == 0) {           // #C
                expectedPos = 1;    // #C
            } else {                // #C
                expectedPos = 0;    // #C
            }                       // #C
            assertEquals(expectedPos,                      // #C
                    posIncr.getPositionIncrement());  // #C
            i++;
        }
        stream.end();
        stream.close();
        assertEquals(3, i);
    }

  /*
    #A Analyze with SynonymAnalyzer
    #B Check for correct synonyms
    #C Verify synonyms positions
  */

    @Test
    public void testSearchByAPI() throws Exception {

        TermQuery tq = new TermQuery(new Term("content", "hops"));  //#1
        assertEquals(1, TestUtil.hitCount(searcher, tq));

        PhraseQuery pq = new PhraseQuery();    //#2
        pq.add(new Term("content", "fox"));    //#2
        pq.add(new Term("content", "hops"));   //#2
        assertEquals(1, TestUtil.hitCount(searcher, pq));
    }

  /*
    #1 Search for "hops"
    #2 Search for "fox hops"
  */

    @Test
    public void testWithQueryParser() throws Exception {
        Query query = new QueryParser("content", synonymAnalyzer)
                .parse("\"fox jumps\"");  // 1
        assertEquals(1, TestUtil.hitCount(searcher, query));                   // 1
        System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " +
                query.toString("content"));

        query = new QueryParser("content", new StandardAnalyzer())
                .parse("\"fox jumps\""); // B
        assertEquals(1, TestUtil.hitCount(searcher, query));                   // 2
        System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " +
                query.toString("content"));
    }

  /*
    #1 SynonymAnalyzer finds the document
    #2 StandardAnalyzer also finds document
  */

}