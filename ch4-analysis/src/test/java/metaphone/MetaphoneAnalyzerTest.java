package metaphone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import tools.AnalyzerUtils;

import static org.junit.Assert.assertEquals;

public class MetaphoneAnalyzerTest {

    @Test
    public void testKoolKat() throws Exception {

        Directory directory = new RAMDirectory();
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        doc.add(new TextField("contents", "cool cat", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new QueryParser("contents", analyzer)    //#B
                .parse("cool kat");          //#B

        TopDocs hits = searcher.search(query, 1);
        assertEquals(1, hits.totalHits);   //#C
        int docID = hits.scoreDocs[0].doc;
        doc = searcher.doc(docID);
        assertEquals("cool cat", doc.get("contents"));   //#D

        reader.close();
    }

    @Test
    public void testMetaphoneAnalizer() throws Exception {

        MetaphoneReplacementAnalyzer analyzer =
                new MetaphoneReplacementAnalyzer();
        AnalyzerUtils.displayTokens(analyzer,
                "The quick brown fox jumped over the lazy dog");

        System.out.println("");
        AnalyzerUtils.displayTokens(analyzer,
                "Tha quik brown phox jumpd ovvar tha lazi dag");
    }

    @Test
    public void testMoreMetaphone() throws Exception {

        Directory directory = new RAMDirectory();
        Analyzer analyzer = new MetaphoneReplacementAnalyzer();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(directory, config);

        Document doc = new Document();
        doc.add(new TextField("contents", "The quick brown fox jumped over the lazy dog", Field.Store.YES));
        writer.addDocument(doc);

        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = new QueryParser("contents", analyzer)    //#B
                .parse("Tha quik brown phox jumpd ovvar tha lazi dag");          //#B

        TopDocs hits = searcher.search(query, 1);
        assertEquals(1, hits.totalHits);   //#C
        int docID = hits.scoreDocs[0].doc;
        doc = searcher.doc(docID);
        assertEquals("The quick brown fox jumped over the lazy dog", doc.get("contents"));   //#D

        reader.close();
    }

}
