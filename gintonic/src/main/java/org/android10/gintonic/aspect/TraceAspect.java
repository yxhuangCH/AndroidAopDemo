/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 * @author Fernando Cejas (the android10 coder)
 */
package org.android10.gintonic.aspect;

import android.util.Log;

import org.android10.gintonic.internal.DebugLog;
import org.android10.gintonic.internal.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

import java.lang.reflect.Field;

/**
 * Aspect representing the cross cutting-concern: Method and Constructor Tracing.
 */
@Aspect
public class TraceAspect {

  private static final String POINTCUT_METHOD =
      "execution(@org.android10.gintonic.annotation.DebugTrace * *(..))";

  private static final String POINTCUT_CONSTRUCTOR =
      "execution(@org.android10.gintonic.annotation.DebugTrace *.new(..))";

  @Pointcut(POINTCUT_METHOD)
  public void methodAnnotatedWithDebugTrace() {}

  @Pointcut(POINTCUT_CONSTRUCTOR)
  public void constructorAnnotatedDebugTrace() {}

  @Around("methodAnnotatedWithDebugTrace() || constructorAnnotatedDebugTrace()")
  public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
    // joint 对象信息
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String className = methodSignature.getDeclaringType().getSimpleName();
    String methodName = methodSignature.getName();


    final StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object result = joinPoint.proceed();
    stopWatch.stop();

    DebugLog.log(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));



    // 源代码部分信息
    SourceLocation sourceLocation = joinPoint.getSourceLocation();
    String fileName = sourceLocation.getFileName();
    int line = sourceLocation.getLine();
    String soucreClassName = sourceLocation.getWithinType().getName();
    DebugLog.log(className, "\nfileName = " + fileName + "\nline = " + line + "\nsoucreClassName = " + soucreClassName);

    // 静态部分
    JoinPoint.StaticPart staticPart = joinPoint.getStaticPart();

    return result;
  }

  /**
   * Create a log message.
   *
   * @param methodName A string with the method name.
   * @param methodDuration Duration of the method in milliseconds.
   * @return A string representing message.
   */
  private static String buildLogMessage(String methodName, long methodDuration) {
    StringBuilder message = new StringBuilder();
    message.append("Gintonic --> ");
    message.append(methodName);
    message.append(" --> ");
    message.append("[");
    message.append(methodDuration);
    message.append("ms");
    message.append("]");

    return message.toString();
  }

    private static final String POINTCUT_ONMETHOD =
            "execution(* android.app.Activity.on**(..))";

//    @Before(POINTCUT_ONMETHOD)
//    public void beforeOnMethod(JoinPoint joinPoint) {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String className = methodSignature.getDeclaringType().getSimpleName();
//        String methodName = methodSignature.getName();
//        Log.i(className, "before " + methodName + " log");
//
//    }
//
//    @After(POINTCUT_ONMETHOD)
//    public void onMethLog(JoinPoint joinPoint){
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String className = methodSignature.getDeclaringType().getSimpleName();
//        String methodName = methodSignature.getName();
//        Log.i(className, "after " + methodName + " log");
//    }

//    @Pointcut(POINTCUT_ONMETHOD)
//    public void annotationOnMethodTrace(){
//
//    }
//
//    @Around("annotationOnMethodTrace()")
//    public Object weaveOnMethodJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
//
//      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//      String className = methodSignature.getDeclaringType().getSimpleName();
//      String methodName = methodSignature.getName();
//
//      Log.i("MainActivity", "before joinPoint proceed className = " + className + " methodName = " + methodName);
//
//      Object result  = joinPoint.proceed();
//      Log.i("MainActivity", "after joinPoint proceed className = " + className + " methodName = " + methodName);
//
//      return result;
//    }




    //　set field 的切面
    private static final String POINTCUT_FILEED =
            "set(int org.android10.viewgroupperformance.activity.MainActivity.mTest) && args(newValue) && target(t)";

    @Before(POINTCUT_FILEED)
    public void onFiled(JoinPoint joinPoint, Object newValue, Object t) throws IllegalAccessException {

        FieldSignature fieldSignature = (FieldSignature) joinPoint.getSignature();
        String fileName = fieldSignature.getName();
        Field field = fieldSignature.getField();
        field.setAccessible(true);
        Class clazz = fieldSignature.getFieldType();
        String clazzName = clazz.getSimpleName();

        // 获取旧的值
        Object oldValue = field.get(t);

        Log.i("MainActivity",
                   "\nonFiled value = " + newValue.toString()
                        + "\ntarget = " + t.toString()
                        + "\n fieldSignature =" + fieldSignature.toString()
                        + "\nfield = " + field.toString()
                        + "\nFileName = " + fileName
                        + "\nclazzName = " + clazzName
                        + " \noldValue = " + oldValue.toString() );

    }
}
