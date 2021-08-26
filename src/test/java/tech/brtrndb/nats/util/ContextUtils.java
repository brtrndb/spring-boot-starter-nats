package tech.brtrndb.nats.util;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@Slf4j
public class ContextUtils {

    public static <T> void noBeanInContext(ApplicationContextRunner context, Class<T> beanClass) {
        Assertions.assertThatThrownBy(() -> context
                        .run(ctx -> {
                            // Given:
                            // When:
                            T bean = ctx.getBean(beanClass);
                            // Then:
                        }))
                .isExactlyInstanceOf(NoSuchBeanDefinitionException.class)
                .hasMessageContaining(beanClass.getName());
    }

    public static <T> void beanInContext(ApplicationContextRunner context, Class<T> beanClass) {
        context.run(ctx -> {
            // Given:
            // When:
            T bean = ctx.getBean(beanClass);
            // Then:
            Assertions.assertThat(bean).isNotNull();
        });
    }

}
