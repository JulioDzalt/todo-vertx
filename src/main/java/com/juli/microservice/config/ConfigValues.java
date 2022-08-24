package com.juli.microservice.config;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
public class ConfigValues {

    int serverPort;
    String version;

    public static ConfigValues from(final JsonObject config) {
        final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);
        if (Objects.isNull(serverPort)) {
            throw new RuntimeException("Value of <"+ConfigLoader.SERVER_PORT+"> is not configured in config file!");
        }
        
        final String version = config.getString(ConfigLoader.VERSION);
        if (Objects.isNull(version)) {
            throw new RuntimeException("Value of <"+ConfigLoader.VERSION+"> is not configured in config file!");
        }
        return ConfigValues.builder()
                .serverPort(serverPort)
                .version(version)
                .build();
    }
}
