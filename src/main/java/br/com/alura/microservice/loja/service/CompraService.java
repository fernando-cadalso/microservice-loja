package br.com.alura.microservice.loja.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.alura.microservice.loja.dto.CompraDto;
import br.com.alura.microservice.loja.dto.InfoFornecedorDto;

@Service
public class CompraService {

	/*
	 * Injeta a dependência RestTemplate para consultar
	 * aplicação Eureka e resolver o IP e a porta da aplicação "fornecedor" 
	 */
	@Autowired
	private RestTemplate cliente;
	
	public void realizaCompra(CompraDto compra) {
		
		ResponseEntity<InfoFornecedorDto> infoFornecedor = cliente.exchange("http://fornecedor/info/" + 
				compra.getEndereco().getEstado(),
				HttpMethod.GET,null,InfoFornecedorDto.class);
		
		/*
		 * Exibe o endereço no console.
		 */
		System.out.println(infoFornecedor.getBody().getEndereco());
	}

}
