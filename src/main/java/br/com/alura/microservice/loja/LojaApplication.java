package br.com.alura.microservice.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
@Configuration
//@EnableHystrixDashboard
public class LojaApplication {

	@Bean
	@LoadBalanced //Essa anotação resolve nomes de aplicação para endereço IP e porta.
	public RestTemplate getRestTemplate() {
		/*	
		 * Permite fazer requisições HTTP para outras aplicações.
		 */
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(LojaApplication.class, args);
	}

}
