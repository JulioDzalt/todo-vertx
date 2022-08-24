package com.juli.microservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.juli.microservice.models.Todo;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;


public class TodoService {
    
    List<Todo> todos = new ArrayList<Todo>();

    public TodoService(){

        todos.add(new Todo(1, "Comprar tunas", "DONE"));
        todos.add(new Todo(2, "Lavar trastes", "TO DO"));
        todos.add(new Todo(3, "Lavar ropa", "TO DO"));

    }


    public JsonArray getAllTodos() {
        final JsonArray res = new JsonArray();
        todos.stream().forEach( (t) -> res.add(t.toJsonObject()));
        return res;
    }


    public Optional<JsonObject> getTodoById(int idTodo){
        Optional<JsonObject> res = Optional.ofNullable(null);
        Optional<Todo> todo = todos.stream().filter( t -> t.getId() == idTodo).findFirst();
        
        if(todo.isPresent()){
            res = Optional.of(todo.get().toJsonObject());
        } else{
            res = Optional.ofNullable(null);
        }

        return res;
    }

    public void getAllTodosFromDBQueryPelon(RoutingContext context, Pool db) {
        db.query("SELECT * FROM todos").execute(ar -> {
            List<Todo> lstTodos = new ArrayList<Todo>();
            if (ar.succeeded()) {
                RowSet<Row> result = ar.result();
                System.out.println("Got " + result.size() + " rows ");
                result.forEach(r -> {
                    Todo t = new Todo();
                    t.setId(r.getInteger("id"));
                    t.setName(r.getString("name"));
                    t.setStatus(r.getString("status"));
                    lstTodos.add(t);
                });
            } else {
                System.out.println("Failure: " + ar.cause().getMessage());
            }

            // Now close the pool
            // db.close();

            final JsonArray res = new JsonArray();
            lstTodos.stream().forEach((t) -> res.add(t.toJsonObject()));

            context
                    .response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                    .end(res.toBuffer());
        });
    }


}
