import lia.BooksIndexBase;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;


public class NumericRangeQueryTest extends BooksIndexBase {

    //int

    @Test
    public void testInclusive() throws Exception {
        // pub date of TTC was October 1988
        NumericRangeQuery query = NumericRangeQuery.newIntRange("pubmonth_num",
                198805, 198810,
                true, true);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 2, matches.totalHits);
    }

    @Test
    public void testExclusive() throws Exception {
        // pub date of TTC was October 1988
        NumericRangeQuery query = NumericRangeQuery.newIntRange("pubmonth",
                198805, 198810,
                false, false);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 0, matches.totalHits);
    }


    //long - timestamp

    @Test
    public void testInclusiveLongTs() throws Exception {
        // pub date of TTC was October 1988
        NumericRangeQuery query = NumericRangeQuery.newLongRange("pubmonth_ts",
                FORMATTER.parse("198805").getTime(),
                FORMATTER.parse("198810").getTime(),
                true, true);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 2, matches.totalHits);
    }

    @Test
    public void testExclusiveLongTs() throws Exception {
        // pub date of TTC was October 1988
        NumericRangeQuery query = NumericRangeQuery.newLongRange("pubmonth_ts",
                FORMATTER.parse("198805").getTime(),
                FORMATTER.parse("198810").getTime(),
                false, false);
        TopDocs matches = searcher.search(query, 10);
        assertEquals("tao", 0, matches.totalHits);
    }
}
