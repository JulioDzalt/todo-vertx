package com.juli.microservice.verticles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.config.ConfigLoader;
import com.juli.microservice.config.ConfigValues;
import com.juli.microservice.config.DataBase;
import com.juli.microservice.controllers.TodoControler;
import com.juli.microservice.handlers.ErrorHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.sqlclient.Pool;

public class RestApiVerticle extends AbstractVerticle{

    private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // ConfigStoreOptions yamlStore = new ConfigStoreOptions()
        //         .setType("file")
        //         .setFormat("yaml")
        //         .setConfig(new JsonObject().put("path", "application.yml"));
        // ConfigStoreOptions jsonStore = new ConfigStoreOptions()
        //         .setType("file")
        //         .setOptional(true)
        //         .setConfig(new JsonObject().put("path", "my-config.json"));
        
        // ConfigRetrieverOptions options = new ConfigRetrieverOptions()
        //         //.addStore(yamlStore)
        //         .addStore(jsonStore);
        // ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        // retriever.getConfig(ar -> {
        //     if (ar.failed()) {
        //         // Failed to retrieve the configuration
        //     } else {
        //         JsonObject config = ar.result();
        //         LOG.info("config: {}", config.toString());
        //     }
        // });

        ConfigLoader.load(vertx)
            .onFailure(startPromise::fail)
            .onSuccess(configuration -> {
                LOG.info("Current Config Application: {}", configuration.toString());

                this.startHttpServerAndAttachRoutes(startPromise, configuration);
            });
            

    }

    private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, ConfigValues config) {
        
        final Pool db = DataBase.createMySQLPool(vertx);

        final Router router = Router.router(vertx);

        router.route()
                .handler(BodyHandler.create())
                .failureHandler(new ErrorHandler());

        TodoControler.attach(router, vertx, db);

        vertx.createHttpServer()
                .requestHandler(router)
                .exceptionHandler(e -> LOG.error("HTTP server error {}", e))
                .listen(config.getServerPort(), http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        LOG.info("HTTP server started on port {}, the version is {} " , config.getServerPort(), config.getVersion());
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }
    
    
}
