import lia.BooksIndexBase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;


public class TermQueryTest extends BooksIndexBase {

    @Test
    public void testKeyword() throws Exception {
        Term t = new Term("isbn", "9781935182023");
        Query query = new TermQuery(t);
        TopDocs docs = searcher.search(query, 10);
        assertEquals("JUnit in Action, Second Edition",
                1, docs.totalHits);
    }
}
