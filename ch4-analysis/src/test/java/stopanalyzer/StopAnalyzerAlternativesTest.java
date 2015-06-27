package stopanalyzer;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.junit.Test;
import tools.AnalyzerUtils;

public class StopAnalyzerAlternativesTest {

    @Test
    public void testStopAnalyzer() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer(),
                "The quick brown...",
                new String[]{"quick", "brown"});
    }

    @Test
    public void testStopAnalyzerFlawed() throws Exception {
        AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(),
                "The quick brown...",
                new String[] {"the", "quick", "brown"});
    }
}
