package com.juli.microservice.verticles;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.models.Todo;
import com.juli.microservice.services.TodoService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class EventBusConsumerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(EventBusConsumerVerticle.class);

    final TodoService service = new TodoService();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        
        startPromise.complete();
        
        vertx.eventBus().<String>consumer("ibm", message -> {
            LOG.info("Received Message from EventBus <ibm>: {}", message.body());

            String idTodo = message.body();
            Todo t = new Todo();
            Optional<JsonObject> maybeTodo = service.getTodoById(Integer.valueOf(idTodo));
            if (maybeTodo.isPresent()) {
                JsonObject json = maybeTodo.get();
                t = Json.decodeValue(json.encode(), Todo.class);
            } else {
                LOG.info("Todo with id <{}> not found", idTodo);
            }

            LOG.info("Reply to EventBus <ibm>: {}", t.toJsonObject());
            message.reply(t.toJsonObject());
        });
        
        
    }
    
}
