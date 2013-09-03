package test.system.events.ast.annotation;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
@GroovyASTTransformationClass({"test.system.events.ast.transformation.OnSystemEventASTTransformation"})
public @interface OnSystemEvent {
}
