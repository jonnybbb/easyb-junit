package org.easyb.junit;

public class RunProperties {

    /*
   private static Boolean isEclipse;

    //todo: add distinction between eclipse and idea
   public static boolean isEclipse() {
      if (isEclipse == null) {
         isEclipse = false;
         for (StackTraceElement element : new Exception().getStackTrace()) {
            if (element.getClassName().startsWith("org.eclipse.jdt.internal.junit.runner.") || element.getClassName().startsWith("com.intellij.rt.execution.junit"))
               isEclipse = true;
         }
      }
      return isEclipse;
   }
   */

    private static Boolean isEclipse;
    private static Boolean isIDEA;

    public static boolean isIDEA() {
        if (isIDEA == null) {
            isIDEA = checkForIDE("com.intellij.rt.execution.junit");
        }
        return isIDEA;
    }

    public static boolean isEclipse() {
       if (isEclipse == null) {
           isEclipse = checkForIDE("org.eclipse.jdt.internal.junit.runner");
       }
       return isEclipse;
   }

    private static boolean checkForIDE(String exceptionBegin) {
        boolean isWantedIDE =  false;
        for (StackTraceElement element : new Exception().getStackTrace()) {
            if (element.getClassName().startsWith(exceptionBegin)) isWantedIDE = true;
        }

        return isWantedIDE;
    }

}
