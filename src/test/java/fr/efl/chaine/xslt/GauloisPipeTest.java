/**
 * This Source Code Form is subject to the terms of 
 * the Mozilla Public License, v. 2.0. If a copy of 
 * the MPL was not distributed with this file, You 
 * can obtain one at https://mozilla.org/MPL/2.0/.
 */
package fr.efl.chaine.xslt;

import fr.efl.chaine.xslt.config.Config;
import fr.efl.chaine.xslt.config.ConfigUtil;
import fr.efl.chaine.xslt.config.Xslt;
import fr.efl.chaine.xslt.utils.ParameterValue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Christophe Marchand
 */
public class GauloisPipeTest {
    private static Collection<ParameterValue> emptyInputParams;
    
    public GauloisPipeTest() {
    }
    
    @BeforeClass
    public static void initialize() {
        emptyInputParams = new ArrayList<>();
    }

    @Test
    public void testOldConfig() throws Exception {
        ConfigUtil cu = new ConfigUtil("./src/test/resources/old-config.xml");
        GauloisPipe piper = new GauloisPipe(cu.buildConfig(emptyInputParams),"OLD_CONFIG");
        piper.launch();
        assertEquals(true, new File("target/generated-test-files/result.xml").exists());
        assertEquals(3, piper.getXsltCacheSize());
        assertEquals(0, piper.getDocumentCacheSize());
    }
    @Test
    public void testNewConfig() throws Exception {
        ConfigUtil cu = new ConfigUtil("./src/test/resources/new-config.xml");
        GauloisPipe piper = new GauloisPipe(cu.buildConfig(emptyInputParams),"NEW_CONFIG");
        piper.launch();
        assertTrue(new File("target/generated-test-files/step1-output.xml").exists());
        assertTrue(new File("target/generated-test-files/step2-output.xml").exists());
        assertTrue(new File("target/generated-test-files/step3-output.xml").exists());
        assertEquals(0, piper.getDocumentCacheSize());
    }
    
    @Test
    public void testSameInputFile() throws Exception {
        ConfigUtil cu = new ConfigUtil("./src/test/resources/same-source-file.xml");
        Config config = cu.buildConfig(emptyInputParams);
        assertEquals(2, config.getSources().getFiles().size());
        GauloisPipe piper = new GauloisPipe(config,"SAME_INPUT");
        piper.launch();
        assertTrue(new File("target/generated-test-files/source-first-result.xml").exists());
        assertTrue(new File("target/generated-test-files/source-second-result.xml").exists());
        assertEquals(1, piper.getDocumentCacheSize());
    }

    @Test
    public void testSubstitution() throws Exception {
        ConfigUtil cu = new ConfigUtil("./src/test/resources/substitution.xml");
        Config config = cu.buildConfig(emptyInputParams);
        config.verify();
        GauloisPipe piper = new GauloisPipe(config,"SUBSTITUTION");
        piper.launch();
        assertTrue(new File("./target/generated-test-files/source-substitution-result.xml").exists());
    }

    @Test
    public void testNoSourceFile() throws Exception {
        ConfigUtil cu = new ConfigUtil("./src/test/resources/no-source.xml");
        Config config = cu.buildConfig(emptyInputParams);
        config.verify();
        // on veut juste s'assurer qu'on a pas d'exception
        assertTrue(true);
        GauloisPipe piper = new GauloisPipe(config,"NO_SOURCE");
        piper.launch();
    }
    @Test
    public void validateComment() {
        try {
            Config config = new ConfigUtil("./src/test/resources/comment-xslt.xml").buildConfig(emptyInputParams);
            config.verify();
            Iterator<Xslt> it = config.getPipe().getXslts();
            int count=0;
            while(it.hasNext()) {
                it.next();
                count++;
            }
            assertEquals(3, count);
        } catch (InvalidSyntaxException | SaxonApiException ex) {
            fail(ex.getMessage());
        }
    }

}