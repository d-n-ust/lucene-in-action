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

import lia.TestUtil;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import junit.framework.TestCase;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// From chapter 2
public class LockTest extends TestCase {

    private Directory dir;
    private Path indexDir;

    protected void setUp() throws IOException {
        indexDir = Paths.get(
                System.getProperty("java.io.tmpdir", "tmp") +
                        System.getProperty("file.separator") + "index");
        dir = FSDirectory.open(indexDir);
    }

    public void testWriteLock() throws IOException {

        IndexWriter writer1 = getWriter(dir);
        IndexWriter writer2 = null;
        try {
            writer2 = getWriter(dir);
            fail("We should never reach this point");
        }
        catch (LockObtainFailedException e) {
            e.printStackTrace();  // #A
        }
        finally {
            writer1.close();
            assertNull(writer2);
            TestUtil.rmDir(indexDir);
        }
    }

    private IndexWriter getWriter(Directory directory) throws IOException {
        return new IndexWriter(directory,
                new IndexWriterConfig(
                        new SimpleAnalyzer()));
    }
}

/*
#A Expected exception: only one IndexWriter allowed at once
*/
