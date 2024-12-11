package com.wsd.web.wsd_web_crawling.jobs.components;

import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.io.InputStream;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
@RequiredArgsConstructor
public class LocationResolver {

  @Value("classpath:constant/LocationCode.json") // JSON 파일 경로 업데이트
  private Resource resource;

  private Map<String, String> jsonMap;

  @PostConstruct
  public void init() {
    loadJson();
  }

  private void loadJson() {
    try (InputStream inputStream = resource.getInputStream()) {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonMap = objectMapper.readValue(inputStream, new TypeReference<HashMap<String, String>>() {});
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 위치 코드를 입력받아 위치 이름으로 변환하는 메서드이며 해당 메서드는 오로지 크롤링 시 사용되는 메서드입니다.
   * @param loc_cd 위치 코드
   * @return 위치 이름
   */
  public String resolve(String loc_cd) {
    if (loc_cd == null) {
      return "";
    }
    return jsonMap.get(loc_cd); // 입력값에 대한 value 리턴
  }
}