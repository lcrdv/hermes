package dev.lacrid.hermes.serialization;

import java.lang.reflect.Method;

public record PropertySetter(String property, Method method) {
}
