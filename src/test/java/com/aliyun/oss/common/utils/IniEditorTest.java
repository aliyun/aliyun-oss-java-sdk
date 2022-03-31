/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.aliyun.oss.common.utils;

import junit.framework.*;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.*;

public class IniEditorTest extends TestCase {

    public IniEditorTest(String name) {
        super(name);
    }

    /**
     * Adding sections.
     */
    public void testAddSection() {
        IniEditor i = new IniEditor();
        i.addSection("hallo");
        Assertions.assertTrue(i.hasSection("hallo"));
        i.addSection("   HELLO\t ");
        Assertions.assertTrue(i.hasSection("hello"));
    }

    /**
     * Adding duplicate sections.
     */
    public void testAddSectionDup() {
        IniEditor i = new IniEditor();
        Assertions.assertTrue(i.addSection("hallo"));
        Assertions.assertTrue(i.hasSection("hallo"));
        Assertions.assertTrue(!i.addSection("HALLO"));
    }

    /**
     * Adding illegal sections.
     */
    public void testAddSectionIllegal() {
        IniEditor i = new IniEditor();
        try {
            i.addSection("[hallo");
            Assertions.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("hallo]");
            Assertions.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("  \t ");
            Assertions.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.addSection("");
            Assertions.fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Checking for sections.
     */
    public void testHasSection() {
        IniEditor i = new IniEditor();
        i.addSection("HaLlO");
        Assertions.assertTrue(i.hasSection("hAlLo"));
        Assertions.assertTrue(i.hasSection(" hallo\t"));
        Assertions.assertTrue(!i.hasSection("hello"));
    }

    /**
     * Removing sections.
     */
    public void testRemoveSection() {
        IniEditor i = new IniEditor("common");
        i.addSection("test");
        try {
            i.removeSection("common");
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        Assertions.assertTrue(i.removeSection("test"));
        Assertions.assertTrue(!i.hasSection("test"));
        Assertions.assertTrue(!i.removeSection("bla"));
    }

    /**
     * Setting and getting options.
     */
    public void testSetGet() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        Assertions.assertEquals(i.get("test", "hallo"), null);
        Assertions.assertTrue(!i.hasOption("test", "hallo"));
        i.set(" \t TEST  ", " HALLO \t", " \tvelo ");
        Assertions.assertEquals(i.get("test", "hallo"), "velo");
        Assertions.assertTrue(i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bike");
        Assertions.assertEquals(i.get(" TesT\t ", " \tHALLO "), "bike");
        Assertions.assertTrue(i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bi\nk\n\re\n");
        Assertions.assertEquals(i.get("test", "hallo"), "bike");
        Assertions.assertTrue(i.hasOption("test", "hallo"));
        // with common section
        i = new IniEditor("common");
        i.addSection("test");
        Assertions.assertEquals(i.get("common", "hallo"), null);
        Assertions.assertEquals(i.get("test", "hallo"), null);
        Assertions.assertTrue(!i.hasOption("test", "hallo"));
        i.set("common", "hallo", "velo");
        Assertions.assertEquals(i.get("common", "hallo"), "velo");
        Assertions.assertEquals(i.get("test", "hallo"), "velo");
        Assertions.assertTrue(i.hasOption("common", "hallo"));
        Assertions.assertTrue(!i.hasOption("test", "hallo"));
        i.set("test", "hallo", "bike");
        Assertions.assertEquals(i.get("test", "hallo"), "bike");
    }

    public void testGetSectionMap() {
        IniEditor i = new IniEditor();
        Assertions.assertNull(i.getSectionMap("test"));
        i.addSection("test");
        Assertions.assertEquals(i.getSectionMap("test"), new HashMap<String, String>());
        i.set("test", "hallo", "bike");
        Map<String, String> temp = new HashMap<String, String>();
        temp.put("hallo", "bike");
        Assertions.assertEquals(i.getSectionMap("test"), temp);
        try {
            i.getSectionMap(null);
            Assertions.fail("Should throw NullPointerException");
        } catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        i = new IniEditor("common");
        Assertions.assertEquals(i.getSectionMap("common"), new HashMap<String, String>());
    }

    /**
     * Setting options with illegal names.
     */
    public void testSetIllegalName() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        try {
            i.set("test", "hallo=", "velo");
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", " \t\t ", "velo");
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", "", "velo");
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Setting options to inexistent section.
     */
    public void testSetNoSuchSection() {
        IniEditor i = new IniEditor();
        try {
            i.set("test", "hallo", "velo");
            Assertions.fail("Should throw NoSuchSectionException");
        } catch (IniEditor.NoSuchSectionException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Setting and getting with null arguments.
     */
    public void testSetGetNull() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        try {
            i.set(null, "hallo", "velo");
            Assertions.fail("Should throw NullPointerException");
        } catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        try {
            i.set("test", null, "velo");
            Assertions.fail("Should throw NullPointerException");
        } catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        i.set("test", "hallo", null);
        try {
            i.get(null, "hallo");
            Assertions.fail("Should throw NullPointerException");
        } catch (NullPointerException ex) {
            /* ok, this should happen */
        }
        try {
            i.get("test", null);
            Assertions.fail("Should throw NullPointerException");
        } catch (NullPointerException ex) {
            /* ok, this should happen */
        }
    }

    /**
     * Removing options.
     */
    public void testRemoveOptions() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        i.set("test", "hallo", "velo");
        Assertions.assertTrue(i.hasOption("test", "hallo"));
        Assertions.assertTrue(i.remove("test", "hallo"));
        Assertions.assertEquals(i.get("test", "hallo"), null);
        Assertions.assertTrue(!i.hasOption("test", "hallo"));
        Assertions.assertTrue(!i.remove("test", "hallo"));
        try {
            i.remove("test2", "hallo");
            Assertions.fail("Should throw NoSuchSectionException");
        } catch (IniEditor.NoSuchSectionException ex) {
            /* ok, should happen */
        }
    }

    /**
     * Getting section names.
     */
    public void testSectionNames() {
        IniEditor i = new IniEditor();
        i.addSection("test");
        i.addSection("test2");
        List<String> names = i.sectionNames();
        Assertions.assertEquals(names.get(0), "test");
        Assertions.assertEquals(names.get(1), "test2");
        Assertions.assertEquals(names.size(), 2);
        // with common section
        i = new IniEditor("common");
        i.addSection("test");
        i.addSection("test2");
        names = i.sectionNames();
        Assertions.assertTrue(names.contains("test") && names.contains("test2") && names.size() == 2);
    }

    /**
     * Getting option names.
     */
    public void testOptionNames() {
        IniEditor i = new IniEditor("common");
        i.addSection("test");
        i.set("test", "hallo", "velo");
        i.set("test", "hello", "bike");
        List<String> names = i.optionNames("test");
        Assertions.assertEquals(names.get(0), "hallo");
        Assertions.assertEquals(names.get(1), "hello");
        Assertions.assertEquals(names.size(), 2);
    }

    /**
     * Adding lines.
     */
    public void testAddLines() {
        IniEditor i = new IniEditor("common");
        i.addSection("test");
        i.addBlankLine("test");
        i.addComment("test", "hollderidi");
    }

    /**
     * Saving to a file.
     */
    public void testSave() throws IOException {
        String[] expected = new String[]{"[common]", "[test]", "", "hallo = velo", "# english",
                "hello = bike"};
        IniEditor i = new IniEditor("common");
        i.addSection("test");
        i.addBlankLine("test");
        i.set("test", "hallo", "velo");
        i.addComment("test", "english");
        i.set("test", "hello", "bike");
        File f = File.createTempFile("test", null);
        // with output stream
        i.save(new FileOutputStream(f));
        Object[] saved = fileToStrings(f);
        //System.out.println(Arrays.asList(saved));
        Assertions.assertTrue(Arrays.equals(expected, saved));
        // with File
        i.save(f);
        saved = fileToStrings(f);
        Assertions.assertTrue(Arrays.equals(expected, saved));
        // with file name
        i.save(f.toString());
        saved = fileToStrings(f);
        Assertions.assertTrue(Arrays.equals(expected, saved));
    }

    /**
     * Saving and loading with a character encoding.
     */
    public void testSaveLoadCharset() throws IOException {
        String charsetName = "UTF-16";
        IniEditor i = new IniEditor("cmmn");
        i.addSection("tst");
        i.set("tst", "hllo", "vel");
        File f = File.createTempFile("test", null);
        i.save(new OutputStreamWriter(new FileOutputStream(f), charsetName));
        i = new IniEditor("cmmn");
        i.load(new InputStreamReader(new FileInputStream(f), charsetName));
        Assertions.assertEquals(i.get("tst", "hllo"), "vel");
    }

    /**
     * Closing file on load.
     */
    public void testLoadClosingStream() throws IOException {
        IniEditor i = new IniEditor();
        i.addSection("test");
        i.set("test", "hallo", "velo");
        File f = File.createTempFile("test", null);
        i.save(f.toString());
        i = new IniEditor();
        i.load(f);
        Assertions.assertTrue(f.delete());
    }

    /**
     * Closing file on load.
     */
    public void testCaseSensitivity() throws IOException {
        IniEditor i = new IniEditor("Common", true);
        Assertions.assertTrue(i.hasSection("Common"));
        Assertions.assertTrue(!i.hasSection("common"));
        i.addSection("Test");
        Assertions.assertTrue(i.hasSection("Test"));
        Assertions.assertTrue(!i.hasSection("test"));
        i.set("Test", "Hallo", "velo");
        Assertions.assertEquals("velo", i.get("Test", "Hallo"));
        Assertions.assertNull(i.get("Test", "hallo"));
        try {
            i.set("TesT", "hallo", "velo");
            Assertions.fail("should fail");
        } catch (IniEditor.NoSuchSectionException ex) {
            /* ok */
        }
    }

    public void testSetOptionFormat() throws IOException {
        IniEditor i = new IniEditor();
        try {
            i.setOptionFormatString("%s %s");
            Assertions.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            /* ok, this should happen */
        }
        i.setOptionFormatString("%s%s%s");
        i.addSection("test");
        i.set("test", "hallo", "velo");
        File f = File.createTempFile("test", null);
        i.save(new FileOutputStream(f));
        Object[] saved = fileToStrings(f);
        Assertions.assertEquals("hallo=velo", saved[1]);
    }

    private static Object[] fileToStrings(File f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        List<String> l = new LinkedList<String>();
        while (r.ready()) {
            l.add(r.readLine());
        }
        r.close();
        return l.toArray();
    }

    public static Test suite() {
        return new TestSuite(IniEditorTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
