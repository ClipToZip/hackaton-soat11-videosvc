package br.com.soat11.videosvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // <--- ESTA LINHA É A CHAVE
public class HackatonSoat11VideosvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackatonSoat11VideosvcApplication.class, args);
	}

}
