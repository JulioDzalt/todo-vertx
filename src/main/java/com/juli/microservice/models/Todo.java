package com.juli.microservice.models;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private int id;
    private String name;
    private String status;

    public JsonObject toJsonObject() {
        return JsonObject.mapFrom(this);
    }
}
