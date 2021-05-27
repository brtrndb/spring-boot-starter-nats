package tech.brtrndb.nats.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import tech.brtrndb.nats.autoconfigure.NatsAutoConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NatsAutoConfiguration.class)
public @interface EnableNatsAutoConfiguration {
    /**/
}
