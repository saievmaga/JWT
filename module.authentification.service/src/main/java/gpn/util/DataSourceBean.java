package gpn.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Configuration
@PropertySource("classpath:datasource.properties")
@Component
public class DataSourceBean implements gpn.interfaces.providers.DataSourceBean {

    @Autowired
    private Environment env;

    @Bean
    @Primary
    public DataSource getDataSource() {
        return DataSourceBuilder
                .create()
                .username(env.getProperty("spring.datasource.username.1"))
                .password(env.getProperty("spring.datasource.password.1"))
                .url(env.getProperty("spring.datasource.url.1"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name.1"))
                .build();
    }
}

