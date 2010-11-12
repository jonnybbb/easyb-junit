package org.easyb.junit;

public class RunProperties {
   private static Boolean isEclipse;
   
   public static boolean isEclipse() {
      if (isEclipse == null) {
         isEclipse = false;
         for (StackTraceElement element : new Exception().getStackTrace()) {
            if (element.getClassName().startsWith("org.eclipse.jdt.internal.junit.runner."))
               isEclipse = true;
         }
      }
      return isEclipse;
   }
}
