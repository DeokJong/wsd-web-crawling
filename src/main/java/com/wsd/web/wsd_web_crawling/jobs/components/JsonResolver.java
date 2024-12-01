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
public class JsonResolver {

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

  public String resolve(String key) {
    if (key == null) {
      return "";
    }
    return jsonMap.get(key); // 입력값에 대한 value 리턴
  }
}