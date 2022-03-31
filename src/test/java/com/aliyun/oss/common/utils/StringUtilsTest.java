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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.jupiter.api.*;
import org.junit.Test;

/**
 * Unit tests for the StringUtils class.
 */
public class StringUtilsTest {

    /**
     * Tests that we can correctly convert Bytes to strings.
     */
    @Test
    public void testFromByte() {
        Assertions.assertEquals("123", StringUtils.fromByte(new Byte("123")));
        Assertions.assertEquals("-99", StringUtils.fromByte(new Byte("-99")));
    }

    @Test(timeout = 10 * 1000)
    public void replace_ReplacementStringContainsMatchString_DoesNotCauseInfiniteLoop() {
        Assertions.assertEquals("aabc", StringUtils.replace("abc", "a", "aa"));
    }

    @Test
    public void replace_EmptyReplacementString_RemovesAllOccurencesOfMatchString() {
        Assertions.assertEquals("bbb", StringUtils.replace("ababab", "a", ""));
    }

    @Test
    public void replace_MatchNotFound_ReturnsOriginalString() {
        Assertions.assertEquals("abc", StringUtils.replace("abc", "d", "e"));
    }

    @Test
    public void lowerCase_NonEmptyString() {
        String input = "x-amz-InvocAtion-typE";
        String expected = "x-amz-invocation-type";
        Assertions.assertEquals(expected, StringUtils.lowerCase(input));
    }

    @Test
    public void lowerCase_NullString() {
        Assertions.assertNull(StringUtils.lowerCase(null));
    }

    @Test
    public void lowerCase_EmptyString() {
        Assertions.assertEquals(StringUtils.lowerCase(""), "");
    }

    @Test
    public void upperCase_NonEmptyString() {
        String input = "dHkdjj139_)(e";
        String expected = "DHKDJJ139_)(E";
        Assertions.assertEquals(expected, StringUtils.upperCase(input));
    }

    @Test
    public void upperCase_NullString() {
        Assertions.assertNull(StringUtils.upperCase((null)));
    }

    @Test
    public void upperCase_EmptyString() {
        Assertions.assertEquals(StringUtils.upperCase(""), "");
    }

    @Test
    public void testCompare() {
        Assertions.assertTrue(StringUtils.compare("truck", "Car") > 0);
        Assertions.assertTrue(StringUtils.compare("", "dd") < 0);
        Assertions.assertTrue(StringUtils.compare("dd", "") > 0);
        Assertions.assertEquals(0, StringUtils.compare("", ""));
        Assertions.assertTrue(StringUtils.compare(" ", "") > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompare_String1Null() {
        String str1 = null;
        String str2 = "test";
        StringUtils.compare(str1, str2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompare_String2Null() {
        String str1 = "test";
        String str2 = null;
        StringUtils.compare(str1, str2);
    }

    @Test
    public void testAppendAndCompact() {
        String[] pieces = {" ", "\t", "\n", "\u000b", "\r", "\f", "word", "foo", "bar", "baq"};
        int ITERATIONS = 10000;
        Random rng = new Random();

        for (int i = 0; i < ITERATIONS; i++) {
            int parts = rng.nextInt(10);
            String s = "";
            for (int j = 0; j < parts; j++) {
                s = s + pieces[rng.nextInt(pieces.length)];
            }

            StringBuilder sb = new StringBuilder();
            StringUtils.appendCompactedString(sb, s);
            String compacted = s.replaceAll("\\s+", " ");
            Assertions.assertEquals('[' + compacted + ']', sb.toString(), compacted);
        }
    }

    @Test
    public void begins_with_ignore_case() {
        Assertions.assertTrue(StringUtils.beginsWithIgnoreCase("foobar", "FoO"));
    }

    @Test
    public void begins_with_ignore_case_returns_false_when_seq_doesnot_match() {
        Assertions.assertFalse(StringUtils.beginsWithIgnoreCase("foobar", "baz"));
    }

    @Test
    public void hasValue() {
        Assertions.assertTrue(StringUtils.hasValue("something"));
        Assertions.assertFalse(StringUtils.hasValue(null));
        Assertions.assertFalse(StringUtils.hasValue(""));
    }

    @Test
    public void testToFromValue() {
        StringBuilder value;

        value = new StringBuilder();
        value.append(1);
        Assertions.assertEquals(new Integer(1), StringUtils.toInteger(value));

        value = new StringBuilder();
        value.append("hello");
        Assertions.assertEquals("hello", StringUtils.toString(value));

        value = new StringBuilder();
        value.append("false");
        Assertions.assertEquals(false, StringUtils.toBoolean(value));

        Assertions.assertEquals(BigInteger.valueOf(123), StringUtils.toBigInteger("123"));

        Assertions.assertEquals(BigDecimal.valueOf(123), StringUtils.toBigDecimal("123"));

        Assertions.assertEquals("123", StringUtils.fromInteger(123));
        Assertions.assertEquals("123", StringUtils.fromLong((long) 123));
        Assertions.assertEquals("hello", StringUtils.fromString("hello"));
        Assertions.assertEquals("false", StringUtils.fromBoolean(false));
        Assertions.assertEquals("123", StringUtils.fromBigInteger(BigInteger.valueOf(123)));
        Assertions.assertEquals("123", StringUtils.fromBigDecimal(BigDecimal.valueOf(123)));
        Assertions.assertEquals("123.1", StringUtils.fromFloat((float) 123.1));
        Assertions.assertEquals("123.2", StringUtils.fromDouble((double) 123.2));
    }

    @Test
    public void testJoinString() {
        String part1 = "hello";
        String part2 = "world";
        String join = StringUtils.join("-", part1, part2);
        Assertions.assertEquals("hello-world", join);

        Collection<String> collection = new ArrayList<String>();
        collection.add("hello");
        collection.add("world");
        join = StringUtils.join("-", collection);
        Assertions.assertEquals("hello-world", join);
    }
}
