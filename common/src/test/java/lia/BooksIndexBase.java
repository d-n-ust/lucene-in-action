package lia;

import java.io.IOException;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;


public class BooksIndexBase extends TestCase {

    public static final String INDEX_PATH_BOOKS = "books_index";

    @Before
    public void setUp() throws IOException {
        CreateTestIndex.indexDir("../data", INDEX_PATH_BOOKS);
    }

    @After
    public void tearDown() throws IOException {
        CreateTestIndex.deleteIndex(INDEX_PATH_BOOKS);
    }

}
