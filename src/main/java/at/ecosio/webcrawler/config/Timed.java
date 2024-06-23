package at.ecosio.webcrawler.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//TODO :  Track execution time using spring aop @Around aspect
public @interface Timed {
}
