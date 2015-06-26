import lia.BooksIndexBase;
import lia.TestUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.junit.Test;


public class QueryParserTest extends BooksIndexBase {

    @Test
    public void testToString() throws Exception {
        BooleanQuery query = new BooleanQuery();
        query.add(new FuzzyQuery(new Term("field", "kountry")),
                BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term("title", "western")),
                BooleanClause.Occur.SHOULD);
        assertEquals("both kinds", "+kountry~2 title:western",
                query.toString("field"));
    }

    @Test
    public void testGrouping() throws Exception {
        Query query = new QueryParser("subject", new StandardAnalyzer())
                .parse("(agile OR extreme) AND methodology");
        TopDocs matches = searcher.search(query, 10);
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches,
                "Extreme Programming Explained"));
        assertTrue(TestUtil.hitsIncludeTitle(searcher,
                matches,
                "The Pragmatic Programmer"));
    }

    @Test
    public void testRangeQuery() throws Exception {
        Query query = new QueryParser("subject", new StandardAnalyzer())
                .parse("pubmonth:[200401 TO 201412]");
        assertTrue(query instanceof TermRangeQuery);
        TopDocs matchesInc = searcher.search(query, 10);
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matchesInc,
                "Lucene in Action, Second Edition"));

        query = new QueryParser("pubmonth", new StandardAnalyzer())
                .parse("{200201 TO 200208}");
        TopDocs matchesExc = searcher.search(query, 10);
        assertEquals("JDwA in 200208", 0, matchesExc.totalHits);
    }

    @Test
    public void testDateRangeToString() throws ParseException {
        QueryParser parser = new QueryParser("subject", new StandardAnalyzer());
        Query query = parser.parse("modified:[1/1/04 TO 12/31/04]");
        System.out.println(query);

        parser = new QueryParser("subject", new StandardAnalyzer());
        parser.setDateResolution("modified", DateTools.Resolution.DAY);
        query = parser.parse("modified:[1/1/04 TO 12/31/04]");
        System.out.println(query);
    }

    @Test
    public void testPhraseQuery() throws Exception {
        Query q = new QueryParser("field", new StandardAnalyzer()).parse("\"This is Some Phrase*\"");
        assertEquals("analyzed",
                "\"? ? some phrase\"", q.toString("field"));
        q = new QueryParser("field", new StandardAnalyzer()).parse("\"term\"");
        assertTrue("reduced to TermQuery", q instanceof TermQuery);
    }

    @Test
    public void testSlop() throws Exception {
        Query q = new QueryParser("field", new StandardAnalyzer())
                .parse("\"exact phrase\"");
        assertEquals("zero slop",
                "\"exact phrase\"", q.toString("field"));
        QueryParser qp = new QueryParser("field", new StandardAnalyzer());
        qp.setPhraseSlop(5);
        q = qp.parse("\"sloppy phrase\"");
        assertEquals("sloppy, implicitly",
                "\"sloppy phrase\"~5", q.toString("field"));
    }

    @Test
    public void testLowercasing() throws Exception {
        Query q = new QueryParser("field", new StandardAnalyzer())
                .parse("PrefixQuery*");
        assertEquals("lowercased",
                "prefixquery*", q.toString("field"));
        QueryParser qp = new QueryParser("field", new StandardAnalyzer());
        qp.setLowercaseExpandedTerms(false);
        q = qp.parse("PrefixQuery*");
        assertEquals("not lowercased",
                "PrefixQuery*", q.toString("field"));
    }

}
