package io.teknek.gossip.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtils {

  /**
   * Create an instance of a thing. This method essentially makes code more readable by handing the various exception
   * trapping.
   * @param className
   * @param constructorTypes
   * @param constructorArgs
   * @param <T>
   * @return constructed instance of a thing.
   */
  @SuppressWarnings("unchecked")
  public static <T> T constructWithReflection(String className, Class<?>[] constructorTypes, Object[] constructorArgs) {
    try {
      Constructor<?> c = Class.forName(className).getConstructor(constructorTypes);
      c.setAccessible(true);
      return (T) c.newInstance(constructorArgs);
    } catch (InvocationTargetException e) {
      // catch ITE and throw the target if it is a RTE.
      if (e.getTargetException() != null && RuntimeException.class.isAssignableFrom(e.getTargetException().getClass())) {
        throw (RuntimeException) e.getTargetException();
      } else {
        throw new RuntimeException(e);
      }
    } catch (ReflectiveOperationException others) {
      // Note: No class in the above list should be a descendent of RuntimeException. Otherwise, we're just wrapping
      //       and making stack traces confusing.
      throw new RuntimeException(others);
    }
  }
}
