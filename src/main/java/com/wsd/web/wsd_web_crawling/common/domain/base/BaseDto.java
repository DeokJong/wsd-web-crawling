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

/**
 * 기본 DTO(Data Transfer Object) 클래스.
 * 
 * <p>이 클래스는 다른 DTO 클래스들이 상속받아 공통된 기능을 제공한다.
 * 주로 엔티티 객체로부터 DTO 객체를 업데이트하는 기능을 포함한다.</p>
 * 
 * @param <T> 업데이트할 소스 객체의 타입
 */
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class BaseDto {

  /**
   * 클래스와 필드의 캐시를 저장하는 맵.
   * 
   * <p>성능 향상을 위해 리플렉션을 사용하여 얻은 필드 정보를 캐싱한다.</p>
   */
  private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();

  /**
   * 소스 객체로부터 현재 DTO 객체의 필드를 업데이트한다.
   * 
   * <p>소스 객체의 필드 값이 null이 아닌 경우에만 DTO의 해당 필드를 업데이트한다.
   * 소스 객체의 클래스와 DTO 클래스 간의 필드 이름이 동일한 경우에만 동작한다.</p>
   * 
   * @param <T> 업데이트할 소스 객체의 타입
   * @param source 업데이트에 사용할 소스 객체
   */
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

  /**
   * DTO 클래스의 필드를 가져온다.
   * 
   * <p>필드 캐시에 존재하지 않으면 리플렉션을 사용하여 필드를 가져오고 캐시에 저장한다.</p>
   * 
   * @param dtoClass DTO 클래스
   * @return DTO 클래스의 필드 배열
   */
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

  /**
   * 클래스 계층 구조에서 지정된 이름의 필드를 찾는다.
   * 
   * <p>상위 클래스로도 탐색하여 필드를 찾는다. 캐시에 존재하지 않으면 리플렉션을 사용하여 필드를 찾고 캐시에 저장한다.</p>
   * 
   * @param clazz 필드를 찾을 클래스
   * @param fieldName 찾을 필드의 이름
   * @return 해당 필드가 존재하면 Field 객체, 존재하지 않으면 null
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
