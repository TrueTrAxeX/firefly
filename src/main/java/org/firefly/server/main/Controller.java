package org.firefly.server.main;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String path();
}
