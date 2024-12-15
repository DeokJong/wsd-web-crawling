package com.wsd.web.wsd_web_crawling.common.domain.base;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class BaseDto {

  private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();

  public <T> void updateFrom(T source) {
    if (source == null) {
      log.debug("업데이트 소스가 null입니다.");
      return;
    }

    Class<?> targetClass = this.getClass();
    Class<?> sourceClass = source.getClass();

    log.debug("DTO 클래스: {}", targetClass.getName());
    List<String> dtoFieldNames = Stream.of(getDtoFields(targetClass)).map(Field::getName).toList();
    log.debug("DTO 필드들: {}", dtoFieldNames);

    log.debug("엔티티 클래스: {}", sourceClass.getName());
    List<String> sourceFieldNames = Stream.of(sourceClass.getDeclaredFields()).map(Field::getName).toList();
    log.debug("엔티티 필드들: {}", sourceFieldNames);

    for (Field dtoField : getDtoFields(targetClass)) {
        try {
            Field sourceField = findFieldInHierarchy(sourceClass, dtoField.getName());

            if (sourceField == null) {
                log.debug("엔티티에 해당 필드가 존재하지 않음: {}", dtoField.getName());
                continue;
            }

            Object newValue = sourceField.get(source);

            if (Objects.nonNull(newValue)) {
                log.debug("DTO 필드 업데이트 - {}: {} -> {}", dtoField.getName(), dtoField.get(this), newValue);
                dtoField.set(this, newValue);
            } else {
                log.debug("엔티티 필드 {}의 값이 null이므로 DTO에 업데이트되지 않음.", dtoField.getName());
            }
        } catch (IllegalAccessException e) {
            log.error("DTO 필드 업데이트 실패: {}", dtoField.getName(), e);
            throw new RuntimeException("DTO 필드 업데이트 실패: " + dtoField.getName(), e);
        }
    }

    log.debug("DTO 업데이트가 완료되었습니다: {}", this);
  }

  private Field[] getDtoFields(Class<?> dtoClass) {
    return fieldCache.computeIfAbsent(dtoClass, key -> {
        Map<String, Field> fields = new ConcurrentHashMap<>();
        for (Field field : key.getDeclaredFields()) {
            field.setAccessible(true);
            fields.put(field.getName(), field);
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
