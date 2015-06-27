package i18n;

import lia.BooksIndexBase;
import lia.TestUtil;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class ChineseTest extends BooksIndexBase {

    public void testChinese() throws Exception {
        Query query = new TermQuery(new Term("contents", "道"));
        assertEquals("tao", 1, TestUtil.hitCount(searcher, query));
    }

}