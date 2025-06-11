package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.service.EstoqueService;
import com.br.prova.supermercado.service.PedidoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

@SpringBootApplication
public class SupermercadoConsoleApp {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SupermercadoConsoleApp.class, args);

		EstoqueService estoqueService = context.getBean(EstoqueService.class);
		PedidoService pedidoService = context.getBean(PedidoService.class);
		Scanner scanner = new Scanner(System.in);
		int opcao = -1;

		while (opcao != 0) {
			System.out.println("\n=== MENU SUPERMERCADO ===");
			System.out.println("1 - Mostrar estoque dos produtos");
			System.out.println("2 - Criar pedido de venda");
			System.out.println("0 - Sair");
			System.out.print("Escolha uma opção: ");
			try {
				opcao = Integer.parseInt(scanner.nextLine());
			} catch (Exception e) {
				System.out.println("Opção inválida.");
				continue;
			}

			switch (opcao) {
				case 1:
					System.out.print("Digite o ID do estoque: ");
					Long idEstoque;
					try {
						idEstoque = Long.parseLong(scanner.nextLine());
					} catch (Exception e) {
						System.out.println("ID inválido.");
						break;
					}
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
				case 2:
					System.out.print("Digite o ID do estoque para comprar: ");
					Long estoqueId;
					try {
						estoqueId = Long.parseLong(scanner.nextLine());
					} catch (Exception e) {
						System.out.println("ID inválido.");
						break;
					}
					Estoque estoque;
					try {
						estoque = estoqueService.buscarEstoquePorId(estoqueId);
					} catch (Exception e) {
						System.out.println("Estoque não encontrado.");
						break;
					}
					List<ItemDTO> itensPedido = new ArrayList<>();
					boolean adicionarMais = true;

					while (adicionarMais) {
						System.out.println("\n--- Produtos Disponíveis ---");
						for (Produto p : estoque.getListaDeProdutos()) {
							System.out.println("ID: " + p.getId() +
									" | Nome: " + p.getNome() +
									" | Preço: R$" + String.format("%.2f", p.getPreco()) +
									" | Quantidade: " + p.getQuantidadeEmEstoque());
						}
						System.out.print("Digite o ID do produto para adicionar ao pedido: ");
						Long produtoId;
						try {
							produtoId = Long.parseLong(scanner.nextLine());
						} catch (Exception e) {
							System.out.println("ID inválido.");
							continue;
						}

						Produto produto = estoqueService.encontraProdutoPorId(estoque, produtoId);
						if (produto == null) {
							System.out.println("Produto não existente ou ID não existente.");
							continue;
						}

						System.out.print("Digite a quantidade desejada: ");
						int quantidade;
						try {
							quantidade = Integer.parseInt(scanner.nextLine());
						} catch (Exception e) {
							System.out.println("Quantidade inválida.");
							continue;
						}

						if (quantidade > produto.getQuantidadeEmEstoque()) {
							System.out.println("Quantidade do produto de ID " + produtoId + " não disponível.");
							continue;
						}

						ProdutoDTO produtoDTO = ProdutoDTO.builder()
								.id(produto.getId())
								.nome(produto.getNome())
								.preco(produto.getPreco())
								.quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
								.estoqueId(estoque.getId())
								.build();

						ItemDTO itemDTO = ItemDTO.builder()
								.produto(produtoDTO)
								.quantidade(quantidade)
								.preco(produto.getPreco() * quantidade)
								.build();

						itensPedido.add(itemDTO);

						System.out.print("Deseja adicionar outro produto ao pedido? (s/n): ");
						String resp = scanner.nextLine();
						adicionarMais = resp.equalsIgnoreCase("s");
					}

					if (!itensPedido.isEmpty()) {
						double valorTotal = itensPedido.stream().mapToDouble(ItemDTO::getPreco).sum();
						PedidoDTO pedidoDTO = PedidoDTO.builder()
								.listaItens(itensPedido)
								.valorTotalDoPedido(valorTotal)
								.build();

						try {
							PedidoDTO pedidoSalvo = pedidoService.salvar(pedidoDTO);
							System.out.println("Pedido criado com sucesso! ID do pedido: " + pedidoSalvo.getId());
							System.out.println("Valor total: R$" + String.format("%.2f", pedidoSalvo.getValorTotalDoPedido()));
						} catch (Exception e) {
							System.out.println("Erro ao criar pedido: " + e.getMessage());
						}
					} else {
						System.out.println("Nenhum item adicionado ao pedido.");
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