import lia.BooksIndexBase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

public class PrefixQueryTest extends BooksIndexBase {

    @Test
    public void testPrefix() throws Exception {

        Term term = new Term("category",                              //#A
                "/technology/computers/programming");    //#A
        PrefixQuery query = new PrefixQuery(term);                    //#A

        TopDocs matches = searcher.search(query, 10);                 //#A
        int programmingAndBelow = matches.totalHits;

        matches = searcher.search(new TermQuery(term), 10);           //#B
        int justProgramming = matches.totalHits;

        assertTrue(programmingAndBelow > justProgramming);
    }
}
