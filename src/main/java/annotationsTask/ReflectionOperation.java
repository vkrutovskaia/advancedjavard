package annotationsTask;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionOperation {

  public static void main(String[] args) throws ClassNotFoundException {

    Class entity = Class.forName("annotationsTask.Entity");

    if (entity.isAnnotationPresent(Secured.class)) {
      System.out.println("Class annotated ; name  -  " + entity.getAnnotation(Secured.class));
    } else {
      System.out.println("Class Entity has not annotations \n");
    }

    System.out.println("All information about methods in Entity.class: \n");

    System.out.println("Access modifiers description: 1 - public, 2- private, 4 - protected \n");

    Method[] methods = entity.getDeclaredMethods();

    for (Method md : methods) {
      if (md.isAnnotationPresent(Secured.class)) {
        System.out.println("[Method name]: " + md.getName() + ", [Modifier]: " + md.getModifiers()
            + ", [Return type]: " + md.getReturnType().getSimpleName()
            + ", [Parameters]: " + Arrays
            .toString(md.getParameters()) + ", [Annotation parameters]: " + md
            .getAnnotation(Secured.class));

      } else if (!md.isAnnotationPresent(Secured.class)) {
        System.out.println("Has no annotation for method: " + md.getName());
      }
    }
  }
}