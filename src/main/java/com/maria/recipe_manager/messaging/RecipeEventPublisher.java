package com.maria.recipe_manager.messaging;

import com.maria.recipe_manager.messaging.model.RecipeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class RecipeEventPublisher {
    private static final Logger log =  LoggerFactory.getLogger(RecipeEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RecipeEventPublisher(RabbitTemplate rabbitTemplate, Jackson2JsonMessageConverter converter) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(converter);

        // Confirmare că brokerul a primit mesajul
        this.rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
            if (ack) log.debug("ACK for {}", correlation != null ? correlation.getId() : "null");
            else log.error("NACK: {}", cause);
        });

        // Dacă exchange-ul sau cheia de rutare nu există
        this.rabbitTemplate.setReturnsCallback(returned ->
                log.error("Returned message from exchange={}, routingKey={}, replyText={}",
                        returned.getExchange(), returned.getRoutingKey(), returned.getReplyText())
        );
    }

    public CompletableFuture<Void> publishRecipeCreated(RecipeCreatedEvent event) {
        String correlationId = event.eventId();
        CorrelationData cd = new CorrelationData(correlationId);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_RECIPE_CREATED,
                event,
                msg -> {
                    msg.getMessageProperties().getHeaders().putAll(Map.of(
                            "eventId", event.eventId(),
                            "schemaVersion", event.schemaVersion(),
                            "source", event.source()
                    ));
                    return msg;
                },
                cd
        );

        return cd.getFuture().thenApply(conf -> {
            if (!Boolean.TRUE.equals(conf.isAck())) {
                throw new IllegalStateException("Broker NACK: " + conf.getReason());
            }
            return null;
        });
    }
}
