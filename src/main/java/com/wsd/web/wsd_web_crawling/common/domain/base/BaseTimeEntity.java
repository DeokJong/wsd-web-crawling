package com.wsd.web.wsd_web_crawling.common.domain.base;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * BaseTimeEntity 클래스는 생성 및 수정 시간을 추적하는 기본 엔티티 클래스입니다.
 */
@Slf4j
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseTimeEntity {

  /**
   * 엔티티가 생성된 시간을 저장합니다.
   */
  @CreatedDate
  private LocalDateTime createdAt;

  /**
   * 엔티티가 마지막으로 수정된 시간을 저장합니다.
   */
  @LastModifiedDate
  private LocalDateTime updatedAt;

  private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();

  /**
   * 타입 T 기반으로 엔티티 필드 값을 업데이트하는 메서드
   * 
   * @param source 타입 T 객체 (필드명 일치해야 함)
   */
  public <T> void updateFrom(T source) {
    if (source == null) {
      log.debug("업데이트 소스가 null입니다.");
      return;
    }

    Class<?> sourceClass = source.getClass();
    Class<?> targetClass = this.getClass();

    log.debug("엔티티 클래스: {}", targetClass.getName());
    List<String> targetFieldNames = Stream.of(getFieldsFromCache(targetClass)).map(Field::getName).toList();
    log.debug("엔티티 필드들: {}", targetFieldNames);

    log.debug("엔티티 클래스: {}", sourceClass.getName());
    List<String> sourceFieldNames = Stream.of(sourceClass.getDeclaredFields()).map(Field::getName).toList();
    log.debug("엔티티 필드들: {}", sourceFieldNames);

    for (Field sourceField : getFieldsFromCache(sourceClass)) {
      try {
        Object value = sourceField.get(source);
        Field targetField = findFieldInHierarchy(targetClass, sourceField.getName());

        if (targetField == null) {
          log.debug("필드 없��: {}", sourceField.getName());
          continue;
        }

        if (Objects.nonNull(value)) {
          targetField.set(this, value);
          log.debug("필드 업데이트 성공: {} -> {}", targetField.getName(), value);
        }
      } catch (IllegalAccessException e) {
        log.error("필드 업데이트 실패: {}", sourceField.getName(), e);
      }
    }
  }

  /**
   * 주어진 클래스의 필드를 캐시에서 가져옵니다.
   * 
   * @param clazz 필드를 가져올 클래스
   * @return 클래스의 필드 배열
   */
  private Field[] getFieldsFromCache(Class<?> clazz) {
    return fieldCache.computeIfAbsent(clazz, key -> {
      Map<String, Field> fields = new ConcurrentHashMap<>();
      while (key != null) {
        for (Field field : key.getDeclaredFields()) {
          field.setAccessible(true);
          fields.putIfAbsent(field.getName(), field);
        }
        key = key.getSuperclass();
      }
      return fields;
    }).values().toArray(new Field[0]);
  }

  /**
   * 클래스 계층 구조에서 지정된 필드를 찾습니다.
   * 
   * @param clazz 필드를 찾을 클래스
   * @param fieldName 찾을 필드 이름
   * @return 찾은 필드 또는 null
   */
  private Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
    return fieldCache.computeIfAbsent(clazz, key -> {
      Map<String, Field> fieldMap = new ConcurrentHashMap<>();
      while (key != null) {
        for (Field field : key.getDeclaredFields()) {
          field.setAccessible(true);
          fieldMap.putIfAbsent(field.getName(), field);
        }
        key = key.getSuperclass();
      }
      return fieldMap;
    }).get(fieldName);
  }
}
