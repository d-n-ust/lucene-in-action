package stopanalyzer;

/**
 * Copyright Manning Publications Co.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific lan      
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.*;
import org.apache.lucene.analysis.util.CharArraySet;

// From chapter 4

/**
 * Stop words actually not necessarily removed due to filtering order
 */
public class StopAnalyzerFlawed extends Analyzer {
    private CharArraySet stopWords;

    public StopAnalyzerFlawed() {
        stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    public StopAnalyzerFlawed(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(stopWords);
    }

    /**
     * Ordering mistake here
     */
    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer source = new LetterTokenizer();
        return new TokenStreamComponents(source,
                new LowerCaseFilter(
                        new StopFilter(source, stopWords)));
    }
}
