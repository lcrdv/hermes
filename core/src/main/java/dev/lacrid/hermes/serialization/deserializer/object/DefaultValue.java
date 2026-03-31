package dev.lacrid.hermes.serialization.deserializer.object;

import dev.lacrid.hermes.serialization.deserializer.ProxyDeserializerFactory;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.ProxyInitializer;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface DefaultValue<T> {
  T instance();

  static <T> DefaultValue<T> none() {
    return () -> null;
  }

  static <T> DefaultValue<T> noArgsConstructor(Constructor<T> constructor) {
    try {
      MethodHandles.Lookup lookup = MethodHandles.lookup();
      MethodHandle handle = lookup.unreflectConstructor(constructor);

      @SuppressWarnings("unchecked")
      Supplier<T> instanceSupplier = (Supplier<T>) LambdaMetafactory.metafactory(
          lookup,
          "get",
          MethodType.methodType(Supplier.class),
          MethodType.methodType(Object.class),
          handle,
          handle.type()
      ).getTarget().invokeExact();

      return instanceSupplier::get;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  class ProxyInstance<T> implements DefaultValue<T> {
    private final T proxy;

    ProxyInstance(T proxy) {
      this.proxy = proxy;
    }

    @Override
    public T instance() {
      return proxy;
    }
  }

  static <T> DefaultValue<T> proxy( Class<T> clazz, List<ProxyDeserializerFactory.ProxyProperty<T>> methods) {
    Set<Method> defaultMethods = methods.stream()
        .map(ProxyDeserializerFactory.ProxyProperty::getter)
        .filter(Method::isDefault)
        .collect(Collectors.toSet());
    T proxy = clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ProxyValuesHolder(MethodHandles.lookup(), clazz, defaultMethods)));
    return new ProxyInstance<>(proxy);
  }

  class ProxyValuesHolder implements InvocationHandler {
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.of(
        byte.class, (byte) 0,
        short.class, (short) 0,
        int.class, 0,
        long.class, 0L,
        float.class, 0.0f,
        double.class, 0.0d,
        char.class, '\u0000',
        boolean.class, false
    );

    private final MethodHandles.Lookup lookup;
    private final Class<?> clazz;
    private final Set<Method> methods;

    public ProxyValuesHolder(MethodHandles.Lookup lookup, Class<?> clazz, Set<Method> methods) {
      this.lookup = lookup;
      this.clazz = clazz;
      this.methods = methods;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if (methods.contains(method)) {
        MethodHandle handle = lookup.unreflectSpecial(method, clazz).bindTo(proxy);
        return handle.invoke();
      }

      if (method.getReturnType().isPrimitive()) {
        return PRIMITIVE_DEFAULTS.getOrDefault(method.getReturnType(), null);
      }

      return null;
    }
  }

}