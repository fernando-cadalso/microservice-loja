package br.com.alura.microservice.loja.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.dto.CompraDto;
import br.com.alura.microservice.loja.dto.InfoEntregaDto;
import br.com.alura.microservice.loja.dto.InfoFornecedorDto;
import br.com.alura.microservice.loja.dto.InfoPedidoDto;
import br.com.alura.microservice.loja.dto.VoucherDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.model.CompraState;
import br.com.alura.microservice.loja.repo.CompraRepository;

@Service
public class CompraService {

	@Autowired
	private FornecedorClient fornecedorClient;
	
	@Autowired
	private TransportadorClient transportadorClient;

	@Autowired
	private CompraRepository compraRepo;

	/*
	 * Essa anotação faz o gerenciamento de threads nas chamadas do microserviço
	 * fornecedor através da propriedade "fallbackMethod" e cria um conjunto de threads
	 * para instâncias desse método, através da propriedade "threadPoolKey".
	 */
//	@HystrixCommand	
//	@HystrixCommand(threadPoolKey = "realizaCompraThreadPool")
	@HystrixCommand(fallbackMethod = "realizaCompraFallback", threadPoolKey = "realizaCompraThreadPool")
	public Compra realizaCompra(CompraDto compra) {

		Compra compraSalva = new Compra();
		compraSalva.setEnderecoDestino(compra.getEndereco().toString());
		compraSalva.setState(CompraState.NOVA_COMPRA_RECEBIDA);
		compraRepo.save(compraSalva);
		/*
		 * O ID gerado pela nova compra é passado para o CompraDTO carregar em caso de falha no processo
		 * e exibir em qual etapa parou.
		 */
		compra.setCompraId(compraSalva.getId());
		/*
		 * Inicia a entrega do pedido no fornecedor.
		 */
		InfoFornecedorDto info = fornecedorClient.getInfoPorEstado(compra.getEndereco().getEstado());

		InfoPedidoDto pedido = fornecedorClient.realizaCompra(compra.getItens());
		/*
		 * Atualiza o estado da compra após a entrega do pedido no fornecedor.
		 */
		compraSalva.setPedidoId(pedido.getId());
		compraSalva.setTempoDePreparo(pedido.getTempoDePreparo());
		compraSalva.setState(CompraState.PEDIDO_FORNECEDOR);
		compraRepo.save(compraSalva);
		
//		if (1==1) throw new RuntimeException();
		/*
		 * Inicia a entrega do pedido no transportador para gerar o voucher de reserva.
		 */
		InfoEntregaDto entregaDto = new InfoEntregaDto();				
		entregaDto.setPedidoId(pedido.getId());
		entregaDto.setDataParaEntrega(LocalDate.now().plusDays(pedido.getTempoDePreparo()));
		entregaDto.setEnderecoOrigem(info.getEndereco());
		entregaDto.setEnderecoDestino(compra.getEndereco().toString());
		VoucherDTO voucher = transportadorClient.reservaEntrega(entregaDto);
		/*
		 * Atualiza o estado da compra após a entrega da solicitação de reserva de transporte.
		 */
		compraSalva.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
		compraSalva.setVoucher(voucher.getNumero());
		compraSalva.setState(CompraState.RESERVA_TRANSPORTE);
		/*
		 * Persiste todo processo de compra no SGBD.
		 */
		compraRepo.save(compraSalva);

		return compraSalva;
	}

	/*
	 * Método chamado quando a lógica fallback é executada.
	 */
	public Compra realizaCompraFallback(CompraDto compra) {
//		int contaFallback = 0;
//		for(int i = 1; i <=3; i++) {
//		
//			System.out.println("Fallback " + i);
//			contaFallback = i;
//			this.realizaCompra(compra);
//		}
//		System.out.println("Tentei " + contaFallback + " vezes e não consegui. Verifique o link.");
		
		if (compra.getCompraId() != null)
			return compraRepo.findById(compra.getCompraId()).get();
		
		Compra compraSalvaFallback = new Compra();
		compraSalvaFallback.setEnderecoDestino(compra.getEndereco().toString());
		return compraSalvaFallback;

	}

	/*
	 * Essa anotação faz o gerenciamento de threads nas chamadas do método
	 * e agrupa um conjunto de threads para instâncias desse método,
	 * através da propriedade "threadPoolKey". 
	 */
	
//	@HystrixCommand
	@HystrixCommand(threadPoolKey = "getByIdThreadPool")
	public Compra getById(Long id) {
		return compraRepo.findById(id).orElse(new Compra());
	}

}
