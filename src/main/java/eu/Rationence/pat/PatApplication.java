package eu.Rationence.pat;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@SpringBootApplication
public class PatApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatApplication.class, args);
	}

}
