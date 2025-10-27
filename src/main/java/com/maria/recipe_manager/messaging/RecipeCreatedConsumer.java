package com.maria.recipe_manager.messaging;

import com.maria.recipe_manager.messaging.model.RecipeCreatedEvent;
import com.maria.recipe_manager.service.ingest.RecipeIngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static com.maria.recipe_manager.messaging.RabbitConfig.QUEUE_NEW;

@Component
public class RecipeCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(RecipeCreatedConsumer.class);

    private final RecipeIngestService ingest;

    public RecipeCreatedConsumer(RecipeIngestService ingest) {
        this.ingest = ingest;
    }

    @RabbitListener(queues = QUEUE_NEW, containerFactory = "recipeListenerFactory")
    public void onMessage(RecipeCreatedEvent event,
                          @Header(name = "eventId", required = false) String eventId) {
        var id = eventId != null ? eventId : event.eventId();
        log.info("Consuming RecipeCreated eventId={} name={}", id,
                event.recipe() != null ? event.recipe().name() : "n/a");

        // idempotent: exteriorId/eventId
        ingest.ingest(event);
    }
}
