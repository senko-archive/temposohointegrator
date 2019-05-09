package de.bamero.tempoZohoMiddleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TempoZohoMiddlewareApplication implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(TempoZohoMiddlewareApplication.class);
	
	@Autowired
	Runner runner;

	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(TempoZohoMiddlewareApplication.class);
		ConfigurableApplicationContext ctx = app.run(args);
		// when need to close program
		//ctx.close();
		
	}
	
	public void run(String... args) throws Exception {
		logger.info("System started...");
		runner.run();
		
		
	}

}
