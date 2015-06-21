import lia.BooksIndexBase;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;


public class TermRangeQueryTest extends BooksIndexBase {

    @Test
    public void testInclusive() throws Exception {
        // pub date of TTC was October 1988
        TermRangeQuery query = TermRangeQuery.newStringRange("pubmonth",
                "198805", "198810",
                true, true);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 2, matches.totalHits);
    }

    @Test
    public void testExclusive() throws Exception {
        // pub date of TTC was October 1988
        TermRangeQuery query = TermRangeQuery.newStringRange("pubmonth",
                "198805", "198810",
                false, false);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 0, matches.totalHits);
    }
}
