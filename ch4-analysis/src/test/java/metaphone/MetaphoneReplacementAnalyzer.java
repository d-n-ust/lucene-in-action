package metaphone;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;


public class MetaphoneReplacementAnalyzer  extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new LetterTokenizer();
        TokenStream result = new MetaphoneReplacementFilter(source);
        return new TokenStreamComponents(source, result);
    }
}
