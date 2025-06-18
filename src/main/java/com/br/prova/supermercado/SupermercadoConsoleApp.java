package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.service.EstoqueService;
import com.br.prova.supermercado.service.PedidoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class SupermercadoConsoleApp {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SupermercadoConsoleApp.class, args);

		EstoqueService estoqueService = context.getBean(EstoqueService.class);
		PedidoService pedidoService = context.getBean(PedidoService.class);
		Scanner scanner = new Scanner(System.in);
		int opcao = -1;

		while (opcao != 0) {
			System.out.println("\n=== GESTÃO COMERCIAL ===");
			System.out.println("1 - Controlar menu");
			System.out.println("2 - Mostrar estoque dos produtos");
			System.out.println("3 - Listar pedidos de venda");
			System.out.println("0 - Sair");
			System.out.print("Escolha uma opção: ");
			opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {
				case 1:
					System.out.println("Controle de menu selecionado.");
					break;
				case 2:
					System.out.print("Digite o ID do estoque: ");
					Long idEstoque = scanner.nextLong();
					scanner.nextLine();
					try {
						Estoque estoque = estoqueService.buscarEstoquePorId(idEstoque);
						System.out.println("\n--- Produtos do Estoque ---");
						for (Produto p : estoque.getListaDeProdutos()) {
							System.out.println("ID: " + p.getId() +
									" | Nome: " + p.getNome() +
									" | Preço: R$" + String.format("%.2f", p.getPreco()) +
									" | Quantidade: " + p.getQuantidadeEmEstoque());
						}
					} catch (Exception e) {
						System.out.println("Estoque não encontrado.");
					}
					break;
				case 3:
					List<PedidoDTO> pedidos = pedidoService.listarTodos();
					if (pedidos.isEmpty()) {
						System.out.println("Nenhum pedido registrado.");
					} else {
						for (PedidoDTO pedido : pedidos) {
							System.out.println("\nPedido ID: " + pedido.getId());
							System.out.println("Itens:");
							for (ItemDTO item : pedido.getListaItens()) {
								System.out.println("  Produto ID: " + item.getProduto().getId() +
										" | Nome: " + item.getProduto().getNome() +
										" | Quantidade vendida: " + item.getQuantidade() +
										" | Valor unitário: R$" + String.format("%.2f", item.getProduto().getPreco()));
							}
							System.out.println("Valor total do pedido: R$" + String.format("%.2f", pedido.getValorTotalDoPedido()));
							System.out.println("Valor pago: R$" + (pedido.getValorPago() != null ? String.format("%.2f", pedido.getValorPago()) : "-"));
							System.out.println("Troco: R$" + (pedido.getTroco() != null ? String.format("%.2f", pedido.getTroco()) : "-"));
						}
					}
					break;
				case 0:
					System.out.println("Saindo...");
					break;
				default:
					System.out.println("Opção inválida.");
			}
		}
		scanner.close();
	}
}