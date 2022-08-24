package com.juli.microservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.MainVerticle;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestOptions;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.report.ReportOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class Test2 {

  Vertx vertx = Vertx.vertx();

  private static final Logger LOG = LoggerFactory.getLogger(Test2.class);
  
  @Disabled
  @Test
  public void test1() {
    TestSuite suite = TestSuite.create("the_test_suite");
    suite.test("my_test_case", context -> {
      String s = "value";
      context.assertEquals("value", s);
    });
    suite.test("my_test_case_2", context -> {
      String s = "julio";
      context.assertEquals("julio", s);
    });
    suite.run(new TestOptions().addReporter(new ReportOptions().setTo("console")));
  }


  private void getSomeItems(Handler<List<String>> handler) {
    // Just there to mimic some IO, and the answer arrive later.
    vertx.setTimer(4000, l -> handler.handle(Arrays.asList("a", "b", "c")));
  }

  @Test //Espera un respuesta asincrona
  public void testAsyncOperation() {
    TestSuite suite = TestSuite.create("the_test_suite");
    suite.test("my_test_async", context -> {
      Async async = context.async();

      getSomeItems(list -> {
        Assertions.assertThat(list).contains("a", "b", "c");
        async.complete();
      });
    });
   
    suite.run(new TestOptions().addReporter(new ReportOptions().setTo("console")));
  }

  @Disabled
  @Test//Levtan un server
  public void test2() {

    TestSuite suite = TestSuite.create("the_test_suite");
    suite.test("test2", context -> {
      Vertx vertx = Vertx.vertx();
      Async async = context.async(2);
      HttpServer server = vertx.createHttpServer();
      //server.requestHandler(requestHandler);
      server.listen(8080, ar -> {
        context.assertTrue(ar.succeeded());
        async.countDown();
      });

      vertx.setTimer(1000, id -> {
        async.complete();
      });

      // Wait until completion of the timer and the http request
      async.awaitSuccess();
    });
    suite.run(new TestOptions().addReporter(new ReportOptions().setTo("console")));
  }

  @Disabled
  @Test
  void returnTodo(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
      testContext.completeNow();
    }));

    int idTodo = 1;
    String requestURI = MessageFormat.format("/todos/id/{0}", idTodo);

    vertx.setTimer(1000, id -> {
      WebClient client = WebClient.create(vertx);
      client.get(8888, "localhost", requestURI)
          .send()
          .onComplete(testContext.succeeding(response -> {
            LOG.info("Response: {}", response);
            JsonObject json = response.bodyAsJsonObject();

            int todoId = json.getInteger("id");

            assertEquals(todoId, 1);
            assertEquals(response.statusCode(), 200);

            testContext.completeNow();
          }));
    });

   

  }

}
