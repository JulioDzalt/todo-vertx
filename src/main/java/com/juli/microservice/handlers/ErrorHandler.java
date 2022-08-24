package com.juli.microservice.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.ext.web.validation.BodyProcessorException;
import io.vertx.ext.web.validation.ParameterProcessorException;
import io.vertx.ext.web.validation.RequestPredicateException;

public class ErrorHandler implements Handler<RoutingContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void handle(RoutingContext errorContext) {


        // if (errorContext.response().ended()) {
        //     // Ignore complete
        //     return;
        // } else {
        //     LOG.error("Route Error {}", errorContext.failure().getClass());
        //     LOG.error("Route Error {}", errorContext.failure().toString());
        //     LOG.error("Route Error {}", errorContext.failure().getCause());
        //     LOG.error("Route Error {}", errorContext.failure().getMessage());
        //     errorContext.response()
        //             .setStatusCode(500)
        //             .end(new JsonObject().put("message", "Algo salio mal :/").toBuffer());
        // }

        JsonObject jsonRes = new JsonObject();
        String errorMessage = "Todo Controller ErrorHandler";

        if (errorContext.response().ended()) {
            // Ignore complete
            return;
        } else if (errorContext.failure() instanceof BadRequestException) {
            
            errorContext.response().setStatusCode(400);

            if (errorContext.failure() instanceof ParameterProcessorException) {
                // Something went wrong while parsing/validating a parameter
                errorMessage = errorContext.failure().getMessage();
            } else if (errorContext.failure() instanceof BodyProcessorException) {
                // Something went wrong while parsing/validating the body
                errorMessage = errorContext.failure().getMessage();
            } else if (errorContext.failure() instanceof RequestPredicateException) {
                // A request predicate is unsatisfied
                errorMessage = errorContext.failure().getMessage();
            }
        } else {
            errorContext.response().setStatusCode(500);
            LOG.error("Route Error {}", errorContext.failure().getClass());
            LOG.error("Route Error {}", errorContext.failure().toString());
            LOG.error("Route Error {}", errorContext.failure().getCause());
            LOG.error("Route Error {}", errorContext.failure().getMessage());
            errorMessage =" Algo salio mal :/ revisa LOGs";
        }

        jsonRes.put("message", errorMessage);

        errorContext.response().end(jsonRes.toBuffer());
    }
    
}
