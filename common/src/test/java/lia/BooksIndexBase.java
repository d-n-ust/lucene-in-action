package lia;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Before;


public class BooksIndexBase extends TestCase {

    public static final String INDEX_PATH_BOOKS = "books_index";
    public static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMM");

    public Directory dir;
    public IndexReader reader;
    public IndexSearcher searcher;

    @Before
    public void setUp() throws IOException {
        CreateTestIndex.indexDir("../data", INDEX_PATH_BOOKS);
        dir = FSDirectory.open(Paths.get(INDEX_PATH_BOOKS));
        reader = DirectoryReader.open(dir);
        searcher = new IndexSearcher(reader);
    }

    @After
    public void tearDown() throws IOException {
        reader.close();
        dir.close();
        CreateTestIndex.deleteIndex(INDEX_PATH_BOOKS);
    }

}
