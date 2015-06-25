import lia.TestUtil;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import static lia.TestUtil.indexSingleFieldDocs;
import static org.junit.Assert.assertEquals;

public class WildcardQueryTest {

    Directory dir = new RAMDirectory();

    @Test
    public void testWildcard() throws Exception {
        indexSingleFieldDocs(dir, new Field[]{
                new StringField("contents", "wild", Field.Store.YES),
                new StringField("contents", "child", Field.Store.YES),
                new StringField("contents", "mild", Field.Store.YES),
                new StringField("contents", "mildew", Field.Store.YES)});

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new WildcardQuery(new Term("contents", "?ild*"));  //#1
        TopDocs matches = searcher.search(query, 10);
        assertEquals("child no match",
                3, matches.totalHits);
        assertEquals("score the same",
                matches.scoreDocs[0].score,
                matches.scoreDocs[1].score, 0.0);
        assertEquals("score the same",
                matches.scoreDocs[1].score,
                matches.scoreDocs[2].score, 0.0);
    }

}
