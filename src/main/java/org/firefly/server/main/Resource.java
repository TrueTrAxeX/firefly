package org.firefly.server.main;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    String path() default "";
    RequestMethod method() default RequestMethod.GET;
}
