package com.br.prova.supermercado;

import com.br.prova.supermercado.dto.*;
import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.service.EstoqueService;
import com.br.prova.supermercado.service.PedidoService;
import com.br.prova.supermercado.service.ProdutoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;

@SpringBootApplication
public class SupermercadoConsoleApp {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SupermercadoConsoleApp.class, args);

		EstoqueService estoqueService = context.getBean(EstoqueService.class);
		ProdutoService produtoService = context.getBean(ProdutoService.class);
		PedidoService pedidoService = context.getBean(PedidoService.class);

		Scanner scanner = new Scanner(System.in);
		int opcao = -1;

		while (opcao != 0) {
			System.out.println("\n=== MENU SUPERMERCADO ===");
			System.out.println("1 - Cadastrar estoque");
			System.out.println("2 - Cadastrar produto");
			System.out.println("3 - Listar produtos");
			System.out.println("4 - Mostrar estoque");
			System.out.println("5 - Criar pedido");
			System.out.println("6 - Listar itens de um pedido");
			System.out.println("0 - Sair");
			System.out.print("Escolha uma opção: ");
			opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {
				case 1:
					Estoque novoEstoque = estoqueService.criarEstoque();
					System.out.println("Estoque criado com ID: " + novoEstoque.getId());
					break;
				case 2:
					System.out.print("Nome do produto: ");
					String nome = scanner.nextLine();
					System.out.print("Preço: ");
					double preco = scanner.nextDouble();
					System.out.print("Quantidade: ");
					int quantidade = scanner.nextInt();
					System.out.print("ID do estoque: ");
					Long estoqueId = scanner.nextLong();
					scanner.nextLine();

					ProdutoDTO produtoDTO = ProdutoDTO.builder()
							.nome(nome)
							.preco(preco)
							.quantidadeEmEstoque(quantidade)
							.estoqueId(estoqueId)
							.build();
					ProdutoDTO salvo = produtoService.salvar(produtoDTO);
					System.out.println("Produto cadastrado com ID: " + salvo.getId());
					break;
				case 3:
					List<ProdutoDTO> produtos = produtoService.listarTodos();
					System.out.println("\n--- Catálogo de Produtos ---");
					produtos.forEach(p -> System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Preço: " + p.getPreco()));
					break;
				case 4:
					System.out.print("Digite o ID do estoque: ");
					Long idEstoque = scanner.nextLong();
					scanner.nextLine();
					Estoque estoque = estoqueService.buscarEstoquePorId(idEstoque);
					System.out.println("\n--- Estoque ---");
					estoque.getListaDeProdutos().forEach(p ->
							System.out.println("ID: " + p.getId() + " | Nome: " + p.getNome() + " | Quantidade: " + p.getQuantidadeEmEstoque())
					);
					break;
				case 5:
					System.out.print("Quantos itens no pedido? ");
					int nItens = scanner.nextInt();
					scanner.nextLine();
					List<ItemDTO> itens = new ArrayList<>();
					for (int i = 0; i < nItens; i++) {
						System.out.print("ID do produto: ");
						Long idProduto = scanner.nextLong();
						System.out.print("Quantidade: ");
						int qtd = scanner.nextInt();
						scanner.nextLine();
						ProdutoDTO prod = produtoService.buscarPorId(idProduto);
						itens.add(ItemDTO.builder()
								.produto(prod)
								.quantidade(qtd)
								.preco(prod.getPreco() * qtd)
								.build());
					}
					PedidoDTO pedidoDTO = PedidoDTO.builder().listaItens(itens).build();
					PedidoDTO pedidoSalvo = pedidoService.salvar(pedidoDTO);
					System.out.println("Pedido criado com ID: " + pedidoSalvo.getId() + " | Valor total: " + pedidoSalvo.getValorTotalDoPedido());
					break;
				case 6:
					System.out.print("ID do pedido: ");
					Long idPedido = scanner.nextLong();
					scanner.nextLine();
					List<ItemDTO> itensPedido = pedidoService.listarItensPorPedidoId(idPedido);
					System.out.println("--- Itens do Pedido ---");
					itensPedido.forEach(item -> System.out.println(
							"Produto: " + item.getProduto().getNome() +
									" | Quantidade: " + item.getQuantidade() +
									" | Preço: " + item.getPreco()
					));
					break;
				case 0:
					System.out.println("Saindo...");
					break;
				default:
					System.out.println("Opção inválida!");
			}
		}
		scanner.close();
	}
}