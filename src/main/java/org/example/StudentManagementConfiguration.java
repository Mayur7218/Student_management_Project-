package org.example;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class StudentManagementConfiguration extends Configuration {
    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    @NotNull
    @JsonProperty("redisHost")
    private String redisHost;

    @NotNull
    @JsonProperty("redisPort")
    private int redisPort;

    public @NotNull int getRedisPort() {
        return redisPort;
    }

    public @NotNull String getRedisHost() {
        return redisHost;
    }
}
