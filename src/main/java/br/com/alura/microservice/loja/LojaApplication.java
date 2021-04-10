package br.com.alura.microservice.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.client.RestTemplate;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@SpringBootApplication
@EnableFeignClients
//@EnableCircuitBreaker
//@EnableScheduling
@EnableResourceServer
public class LojaApplication {

	/*
	 * Injeta uma instância do RequestInterceptor solicitado
	 * pela biblioteca do Feign.
	 * Zero or more RequestInterceptors may be configured for 
	 * purposes such as adding headers to all requests.
	 */
	@Bean
	public RequestInterceptor getInterceptorDaAutenticacao() {
		return new RequestInterceptor() {
			
			/*
			 * O método apply é chamado antes de qualquer requisição,
			 * pela biblioteca Feign, para receber as informações
			 * da requisição que queremos fazer.
			 */
			@Override
			public void apply(RequestTemplate template) {

				/*
				 * JavaDoc: Obtains the currently authenticated principal, or an authentication request token.
				 */
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				/*
				 * Verifica se não existe um usuário autenticado
				 */
				if (authentication == null) {
					return;
				}
				/*
				 * Se ocorreu uma autenticação, então faz a captura do header de interesse.
				 * Faz um cast para o tipo OAuth2 que implementamos na aplicação.
				 * JavaDoc: A holder of selected HTTP details related to an OAuth2 authentication request.
				 */
				
				OAuth2AuthenticationDetails token =  (OAuth2AuthenticationDetails) authentication.getDetails();
				/*
				 * Adiciona o token ao cabeçalho da requisição, no atributo 
				 */
				template.header("Authorization", "Bearer " + token.getTokenValue());
				System.out.println("Token enviado para o fornecedor: " + token.getTokenValue());
				
			}
		};
		
	}
	
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
