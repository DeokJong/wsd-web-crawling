package com.wsd.web.wsd_web_crawling.jobs.components;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * RetryableHttpClient 클래스는 주어진 URL에 대해 재시도 가능한 HTTP 연결을 제공합니다.
 */
public class RetryableHttpClient {

    /**
     * 주어진 URL에 대해 최대 시도 횟수만큼 연결을 시도합니다.
     *
     * @param url         연결할 URL
     * @param maxAttempts 최대 시도 횟수
     * @return 연결된 Document 객체
     * @throws IOException 연결 실패 시 발생하는 예외
     */
    public static Document connectWithRetry(String url, int maxAttempts) throws IOException {
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e) {
                attempts++;
                if (attempts == maxAttempts) {
                    throw e;
                }
            }
        }
        return null;
    }
}
