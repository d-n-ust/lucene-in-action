package commongramsnutch;

/**
 * Copyright Manning Publications Co.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan
 */

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import tools.AnalyzerUtils;
import java.io.IOException;

// From chapter 4
public class CommonGramsTest {

    @Test
    public void testCommonGrams() throws IOException {
        Analyzer analyzer = new CommonGramsAnalyzer();   //1
        AnalyzerUtils.displayTokensWithFullDetails(analyzer, "The quick brown fox...");
    }
}