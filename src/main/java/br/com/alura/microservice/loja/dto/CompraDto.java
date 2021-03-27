package br.com.alura.microservice.loja.dto;

import java.util.List;

import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.annotation.JsonIgnore;

public class CompraDto {
	
	@JsonIgnore //Ignora o atributo que vem do cliente.
	private Long compraId;

	private List<ItemDaCompraDto> itens;
	
	private EnderecoDto endereco;

	public List<ItemDaCompraDto> getItens() {
		return itens;
	}

	public void setItens(List<ItemDaCompraDto> itens) {
		this.itens = itens;
	}

	public EnderecoDto getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDto endereco) {
		this.endereco = endereco;
	}

	public Long getCompraId() {
		return compraId;
	}

	public void setCompraId(Long compraId) {
		this.compraId = compraId;
	}
	
	
}
