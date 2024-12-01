package com.wsd.web.wsd_web_crawling.jobs.components;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * HtmlParser 클래스는 HTML 문서를 파싱하는 기능을 제공합니다.
 */
public class HtmlParser {

  /**
   * 주어진 URL에 연결하여 Document 객체를 반환합니다.
   *
   * @param url 연결할 URL
   * @return 연결된 Document 객체
   * @throws IOException 연결 중 오류 발생 시
   */
  public static Document connectToUrl(String url) throws IOException {
    try {
      return Jsoup.connect(url)
          .timeout(10000) // 타임아웃 설정 (10초)
          .userAgent(
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36") // User-Agent
                                                                                                                                     // 추가
          .header("Accept-Language", "en-US,en;q=0.9")
          .get();
    } catch (IOException e) {
      System.err.println("Error connecting to URL: " + url);
      throw e;
    }
  }

  /**
   * 주어진 Document에서 CSS 쿼리를 사용하여 요소를 선택합니다.
   *
   * @param document 파싱할 Document 객체
   * @param cssQuery 선택할 요소의 CSS 쿼리
   * @return 선택된 요소들
   */
  public static Elements selectElements(Document document, String cssQuery) {
    Elements elements = document.select(cssQuery);
    if (elements.isEmpty()) {
      System.err.println("No elements found for query: " + cssQuery);
    }
    return elements;
  }
}
