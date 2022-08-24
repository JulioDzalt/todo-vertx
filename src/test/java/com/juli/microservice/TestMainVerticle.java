package com.juli.microservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.verticles.RestApiVerticle;
import com.juli.microservice.MainVerticle;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  Vertx vertx = Vertx.vertx();

  private static final Logger LOG = LoggerFactory.getLogger(TestMainVerticle.class);

  //NO funciona
  // @BeforeEach
  // void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
  // vertx.deployVerticle(new MainVerticle(),
  // testContext.succeedingThenComplete());
  // }
  @Disabled
  @Test
  void start_http_server() throws Throwable {
    VertxTestContext testContext = new VertxTestContext();

    vertx.createHttpServer()
        .requestHandler(req -> req.response().end())
        .listen(16969)
        .onComplete(testContext.succeedingThenComplete());

    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }
  
  @Disabled
  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Disabled
  @Test
  void returnAllTodos(Vertx vertx, VertxTestContext testContext) throws Throwable {

    vertx.deployVerticle(new MainVerticle(), testContext.succeedingThenComplete());

    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    client.get("/todos")
        .send()
        .onComplete(testContext.succeeding(response -> {
          JsonArray jsonA = response.bodyAsJsonArray();
          LOG.info("Response: {}", jsonA);
          assertNotNull(jsonA.encode());
          // assertEquals("", jsonA.encode());
          assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(),
              response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
          testContext.completeNow();
        })
        );

    client.get("/todosasdfasd")
        .send()
        .onSuccess(response -> LOG.info("Response: {}", response.statusCode()))
        .onFailure(err -> LOG.info("Something went wrong: {}", err.getMessage()));

  }

  @Disabled
  @Test
  void returnTodo(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.deployVerticle(new RestApiVerticle(), testContext.succeeding(id -> {
      testContext.completeNow();
    }));

    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    client.get("/todos/id/1")
        .send()
        .onComplete(testContext.succeeding(response -> {
          LOG.info("Response: {}", response);
          JsonObject json = response.bodyAsJsonObject();

          int todoId = json.getInteger("id");

          assertEquals(todoId, 1);
          assertEquals(response.statusCode(), 200);

          testContext.completeNow();
        }));

  }

  // @Test
  // void returnNotFoundQuote(Vertx vertx, VertxTestContext testContext) throws
  // Throwable {
  // WebClient client = WebClient.create(vertx, new
  // WebClientOptions().setDefaultPort(8888));
  // client.get("/quotes/NOEXISTS")
  // .send()
  // .onComplete(testContext.succeeding(response -> {
  // JsonObject json = response.bodyAsJsonObject();
  // LOG.info("Response: {}", json);

  // assertEquals(response.statusCode(), 404);

  // testContext.completeNow();
  // }));

  // }

}
