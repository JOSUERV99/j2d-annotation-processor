package dabba.doo.annotationprocessor.core.annotations.entity.types;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BigIntColumn {
    int length() default 20;
}
