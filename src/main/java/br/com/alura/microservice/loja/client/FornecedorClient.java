package br.com.alura.microservice.loja.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.alura.microservice.loja.dto.InfoFornecedorDto;
import br.com.alura.microservice.loja.dto.InfoPedidoDto;
import br.com.alura.microservice.loja.dto.ItemDaCompraDto;

@FeignClient("fornecedor")
public interface FornecedorClient {

	@RequestMapping("/info/{estado}")
	InfoFornecedorDto getInfoPorEstado(@PathVariable String estado);

	@PostMapping("/pedido")
	InfoPedidoDto realizaCompra(List<ItemDaCompraDto> itens);
}
