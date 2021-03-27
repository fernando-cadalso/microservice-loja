package br.com.alura.microservice.loja.client;

import org.springframework.cloud.openfeign.FeignClient;
/*
 * Anotação para declarar o nome do microserviço que é publicado no servidor eureka
 * pelo microserviço Transportador.
 */
import org.springframework.web.bind.annotation.PostMapping;

import br.com.alura.microservice.loja.dto.InfoEntregaDto;
import br.com.alura.microservice.loja.dto.VoucherDTO;
@FeignClient("transportador")
public interface TransportadorClient {

	@PostMapping("/entrega")
	public VoucherDTO reservaEntrega(InfoEntregaDto pedidoDto); 
}
