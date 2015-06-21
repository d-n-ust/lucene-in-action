import lia.BooksIndexBase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;


public class TermQueryTest extends BooksIndexBase {

    @Test
    public void testKeyword() throws Exception {
        Directory dir = FSDirectory.open(Paths.get(INDEX_PATH_BOOKS));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);

        Term t = new Term("isbn", "9781935182023");
        Query query = new TermQuery(t);
        TopDocs docs = searcher.search(query, 10);
        assertEquals("JUnit in Action, Second Edition",
                1, docs.totalHits);

        reader.close();
        dir.close();
    }
}
