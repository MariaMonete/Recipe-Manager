package com.maria.recipe_manager.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "recipes.exchange";
    public static final String ROUTING_RECIPE_CREATED = "recipes.created";
    public static final String QUEUE_NEW = "recipes.new";
    public static final String DLX = "recipes.dlx";
    public static final String DLQ_NEW = "recipes.new.dlq";

    @Bean
    public Declarables topology(){
        Exchange dlx= ExchangeBuilder.directExchange(DLX).durable(true).build();

        Queue dlqNew= QueueBuilder.durable(DLQ_NEW)
                .withArgument("x-queue-type","quorum")
                .build();

        Exchange recipesEx=ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();

        Queue qNew=QueueBuilder.durable(QUEUE_NEW)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", DLX,                  // unde merge dacă eșuează
                        "x-dead-letter-routing-key", DLQ_NEW,           // cheia de rutare spre DLQ
                        "x-queue-type", "quorum"                        // tip modern, replicat
                ))
                .build();

        return new Declarables(
                dlx,dlqNew,
                recipesEx,qNew,
                BindingBuilder.bind(qNew).to((TopicExchange) recipesEx).with(ROUTING_RECIPE_CREATED),
                BindingBuilder.bind(dlqNew).to((DirectExchange) dlx).with(DLQ_NEW)
        );
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
