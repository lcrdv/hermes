package dev.lacrid.hermes.serialization;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TypeNames.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
public @interface TypeName {
  Class<?> type() default Object.class;

  String typeClass() default "";

  String[] name();
}
