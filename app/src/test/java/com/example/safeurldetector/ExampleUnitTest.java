package com.example.safeurldetector;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testVirusTotalAPI() {
        String urlToScan = "https://www.google.com";
        String apiKey = "your_valid_api_key_here"; // Replace with your actual API key

        boolean isSafe = GoogleSafeBrowsingAPI.isUrlSafe(urlToScan, apiKey);
        assertTrue("URL should be safe", isSafe);
    }
}