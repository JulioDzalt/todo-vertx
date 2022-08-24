package com.juli.microservice.controllers;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.juli.microservice.models.Todo;
import com.juli.microservice.services.TodoService;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;
import io.vertx.json.schema.draft7.dsl.Keywords;
import io.vertx.sqlclient.Pool;

// Hay que pasar los validadores a Vert.x Json Schema https://vertx.io/docs/4.2.7/vertx-json-schema/java/

// https://github.com/vert-x3/vertx-examples/blob/4.x/web-examples/src/main/java/io/vertx/example/web/validation/ValidationExampleServer.java
//https://github.com/eclipse-vertx/vertx-json-schema/blob/master/src/main/java/examples/JsonSchemaDslExamples.java

public class TodoControler {

	private static final Logger LOG = LoggerFactory.getLogger(TodoControler.class);

	static TodoService service = new TodoService();
	
	public static void attach(Router parent, Vertx vertx, Pool db) {
		

		
		// The schema parser is required to create new schemas
		//This is dreprecated
		//SchemaParser schemaParser = SchemaParser.createDraft7SchemaParser(
		///		SchemaRouter.create(vertx, new SchemaRouterOptions()));

		SchemaRouter schemaRouter = SchemaRouter.create(vertx, new SchemaRouterOptions());
		SchemaParser schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter);

		ValidationHandler validationHandlerPath = ValidationHandler
				.builder(schemaParser)
				// .queryParameter(
				// 		Parameters.param(
				// 				"idTodo",
				// 				intSchema()))
				.pathParameter(
					Parameters.param(
								"idTodo", Schemas. intSchema())	
				)
				.build();
		
		ObjectSchemaBuilder bodySchemaBuilder = Schemas.objectSchema()
				.property("id", Schemas.intSchema().with(Keywords.maximum(10)))
				.property("name", Schemas.stringSchema().with(Keywords.maxLength(5))) //Not null como se hace?
				.property("status", Schemas.stringSchema());
		ValidationHandler validationHandlerBody1 = ValidationHandler
				.builder(schemaParser)
				.body(Bodies.json(bodySchemaBuilder))
				//.body(Bodies.formUrlEncoded(bodySchemaBuilder))
				.build();
		
		
		parent.get("/todos")
				.handler(context -> {
					final JsonArray res = service.getAllTodos();
					LOG.info("Path <{}> response <{}>", context.normalizedPath(), res.encode());
					context
							.response()
							.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
							.end(res.toBuffer());
				});

		parent.get("/todos/db")
				.handler(context -> {
					service.getAllTodosFromDBQueryPelon(context, db);
				});

		parent.get("/todos/id/:idTodo")
				.handler(validationHandlerPath)
				.handler(context -> {
					final String idTodo = context.pathParam("idTodo");
					LOG.info("Todo Param <{}> ", idTodo);

					Optional<JsonObject> maybeTodo = service.getTodoById(Integer.valueOf(idTodo));
					if (maybeTodo.isPresent()) {
						final JsonObject res = maybeTodo.get();
						LOG.info("Path <{}> response <{}>", context.normalizedPath(), res.encode());
						context.response()
								.putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
								.setStatusCode(HttpResponseStatus.OK.code())
								.end(res.toBuffer());
					} else {
						final JsonObject res = new JsonObject()
								.put("message", "Todo with id <" + idTodo + "> not found")
								.put("path", context.normalizedPath());
						context.response()
								.setStatusCode(HttpResponseStatus.NOT_FOUND.code())
								.end(res.toBuffer());
					}
				});


		
		parent.post("/todos")
			.handler(validationHandlerBody1)
			.handler(context -> {
			Todo a = Json.decodeValue(context.body().asString(), Todo.class);
			LOG.info("Todo recived <{}>", a);
			final JsonObject res = a.toJsonObject();
			LOG.info("Path <{}> response <{}>", context.normalizedPath(), res.encode());
			context.response().end(res.toBuffer());
		});

		parent.get("/mal").handler(context -> {
			throw new UnsupportedOperationException("Lo tire");
		});


	}
}
