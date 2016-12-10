package com.andrewmccall.fn.gateway;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests of the path parser.
 */
public class PathParserTest {

    private final PathParser parser = new PathParser();

    @Test
    public void testBasicExtract() {
        String path = "/asdf/";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractWithQueryString() {
        String path = "/asdf/?this=that&other=something";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractNoFinalSlash() {
        String path = "/asdf";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractNoFinalSlashWithQueryString() {
        String path = "/asdf?this=that&other=something";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractNoBeginSlash() {
        String path = "asdf/";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractNoBeginSlashWithQuerystring() {
        String path = "asdf/?this=that&other=something";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }



    @Test
    public void testBasicExtractNoSlash() {
        String path = "asdf";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testBasicExtractNoSlashWithQuerystring() {
        String path = "asdf?this=that&other=something";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testExtractLongPath() {
        String path = "/asdf/133";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }

    @Test
    public void testExtractLongPathWithQuerystring() {
        String path = "/asdf/133?this=that&other=something";
        String expectedFunctionId = "asdf";
        assertExtract(path, expectedFunctionId);
    }


    private void assertExtract(String path, String expectedFunctionId) {
        String result = parser.extractFunctionId(path);
        assertEquals("Path parser did not extract the expected functionId!", expectedFunctionId, result);
    }

}
