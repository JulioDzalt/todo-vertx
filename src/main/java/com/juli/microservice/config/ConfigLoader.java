package com.juli.microservice.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class ConfigLoader {

    //private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    
    //public static final String CONFIG_FILE_YML = "application.yml";
    public static final String CONFIG_FILE_JSON = "my-config.json";
    public static final String SERVER_PORT = "server_port";
    public static final String VERSION = "version";


    public static Future<ConfigValues> load(Vertx vertx) {
        // HAY ALGO RARO ocn los yml
        // ConfigStoreOptions ymlStore = new ConfigStoreOptions()
        //         .setType("file")
        //         .setFormat("yaml")
        //         .setConfig(new JsonObject().put("path", CONFIG_FILE_YML));

        ConfigStoreOptions jsonStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", CONFIG_FILE_JSON));

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        // Order defines overload rules
                        .addStore(jsonStore));
                        //.addStore(ymlStore));

        return retriever.getConfig().map(ConfigValues::from);
    }  
}
