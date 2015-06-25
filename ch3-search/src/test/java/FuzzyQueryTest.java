import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import static lia.TestUtil.indexSingleFieldDocs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FuzzyQueryTest {

    Directory dir = new RAMDirectory();

    @Test
    public void testFuzzy() throws Exception {

        indexSingleFieldDocs(dir, new Field[] {
                new StringField("contents", "fuzzy", Field.Store.YES),
                new StringField("contents", "wuzzy", Field.Store.YES)
        });

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new FuzzyQuery(new Term("contents", "wuzza"));
        TopDocs matches = searcher.search(query, 10);

        assertEquals("both close enough",
                2, matches.totalHits);
        assertTrue("wuzzy closer than fuzzy",
                matches.scoreDocs[0].score != matches.scoreDocs[1].score);

        Document doc = searcher.doc(matches.scoreDocs[0].doc);
        assertEquals("wuzza bear",
                "wuzzy", doc.get("contents"));
    }

}