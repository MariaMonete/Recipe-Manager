package com.maria.recipe_manager.messaging;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.maria.recipe_manager.messaging.RabbitConfig.DLQ_NEW;
import static com.maria.recipe_manager.messaging.RabbitConfig.DLX;

@Configuration
public class RecipeListenerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory recipeListenerFactory(ConnectionFactory cf, RabbitTemplate template, Jackson2JsonMessageConverter converter){

        var f=new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setPrefetchCount(10);// câte mesaje în paralel pe thread
        f.setConcurrentConsumers(1); // 1..3 thread-uri worker
        f.setMaxConcurrentConsumers(3);

        f.setMessageConverter(converter);

        // Retry 3 încercări (1s, 2s, 4s), apoi republish în DLX/DLQ
        var recoverer = new RepublishMessageRecoverer(template, DLX, DLQ_NEW);
        f.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(1000, 2.0, 4000)
                        .recoverer(recoverer)
                        .build()
        );

        // la excepție nu requeue -> permite rutarea în DLX/DLQ
        f.setDefaultRequeueRejected(false);
        return f;
    }
}
