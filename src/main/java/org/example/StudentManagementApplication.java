package org.example;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;

public class StudentManagementApplication extends Application<StudentManagementConfiguration> {

    public static void main(String[] args) throws Exception {
        new StudentManagementApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<StudentManagementConfiguration> bootstrap) {
    }

    @Override
    public void run(StudentManagementConfiguration configuration, Environment environment) {
        // Set up JDBI
        DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), "S3_1");

        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        // Set up Redis
        JedisPool jedisPool = new JedisPool(configuration.getRedisHost(), configuration.getRedisPort());

        final StudentDAO studentDAO = jdbi.onDemand(StudentDAO.class);
        environment.jersey().register(new StudentResource(studentDAO, jedisPool));
        environment.jersey().register(new ExportResource(studentDAO));
    }
}
