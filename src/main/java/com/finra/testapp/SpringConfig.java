package com.finra.testapp;

import com.finra.testapp.init.AppInitializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:spring-context.xml")
public class SpringConfig implements CommandLineRunner {
    private final static Logger LOGGER = Logger.getLogger(SpringConfig.class);

    @Autowired
    private AppInitializer initializer;

    @Override
    public void run(String... strings) throws Exception {
        LOGGER.info("Initializing ...");
        initializer.init();
    }
}
