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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

  @CreatedDate
  private LocalDateTime createdAt;

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
          log.debug("필드 없음: {}", sourceField.getName());
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
