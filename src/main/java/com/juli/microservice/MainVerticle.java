package com.juli.microservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.verticles.RestApiVerticle;
import com.juli.microservice.verticles.EventBusConsumerVerticle;

import io.vertx.core.AbstractVerticle;
//import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

	public static void main(String[] args) {
		// var vertx = Vertx.vertx();
		// vertx.exceptionHandler(error -> LOG.error("Unhandled:", error));
		// vertx.deployVerticle(new MainVerticle())
		// 		.onFailure(err -> LOG.error("Failed to deploy:", err))
		// 		.onSuccess(id -> LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id));
		
		// Dentro de un clusteredVertxpara habilitar el hazelcast
		Vertx.clusteredVertx(new VertxOptions())
			.onSuccess( vertx -> {
					vertx.deployVerticle(new MainVerticle())
							.onFailure(err -> LOG.error("Failed to deploy:", err))
							.onSuccess(
									id -> LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id));					

			});
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		// vertx.deployVerticle(ApiVerticle.class.getName(), new DeploymentOptions().setInstances(
		// 		Math.max(1, Runtime.getRuntime().availableProcessors())))
		// 		.onFailure(startPromise::fail)
		// 		.onSuccess(id -> {
		// 			LOG.info("Deployed {} with id {}", ApiVerticle.class.getSimpleName(), id);
		// 			startPromise.complete();
		// 		});

		vertx.deployVerticle(new RestApiVerticle())
				.onFailure(err -> LOG.error("Failed to deploy ApiVerticle:", err))
				.onSuccess(
						id -> LOG.info("ApiVerticle deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id));
		vertx.deployVerticle(new EventBusConsumerVerticle())
				.onFailure(err -> LOG.error("Failed to deploy EventBusConsumerVerticle:", err))
				.onSuccess(
						id -> LOG.info(" EventBusConsumerVerticle deployed {} with id {}",
								EventBusConsumerVerticle.class.getSimpleName(), id));
	}

}
