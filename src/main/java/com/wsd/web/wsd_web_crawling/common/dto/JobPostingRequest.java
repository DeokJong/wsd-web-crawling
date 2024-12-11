package com.wsd.web.wsd_web_crawling.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPostingRequest {
  private String title;
  private String company;
  private String link;
  private String location;
  private String experience;
  private String education;
  private String employmentType;
  private String deadline;
  private String sector;
  private String salary;
} 