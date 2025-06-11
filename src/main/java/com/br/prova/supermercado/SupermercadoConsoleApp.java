package com.br.prova.supermercado;

import com.br.prova.supermercado.model.Estoque;
import com.br.prova.supermercado.service.EstoqueService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class SupermercadoConsoleApp {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(SupermercadoConsoleApp.class, args);

		EstoqueService estoqueService = context.getBean(EstoqueService.class);
		Scanner scanner = new Scanner(System.in);
		int opcao = -1;

		while (opcao != 0) {
			System.out.println("\n=== MENU SUPERMERCADO ===");
			System.out.println("1 - Controlar menu");
			System.out.println("2 - Mostrar estoque dos produtos");
			System.out.println("0 - Sair");
			System.out.print("Escolha uma opção: ");
			opcao = scanner.nextInt();
			scanner.nextLine();

			switch (opcao) {
				case 1:
					System.out.println("Controle de menu selecionado.");
					// Lógica de controle de menu pode ser adicionada aqui
					break;
				case 2:
					System.out.print("Digite o ID do estoque: ");
					Long idEstoque = scanner.nextLong();
					scanner.nextLine();
					try {
						Estoque estoque = estoqueService.buscarEstoquePorId(idEstoque);
						System.out.println("\n--- Estoque ---");
						estoque.getListaDeProdutos().forEach(p ->
								System.out.println("ID: " + p.getId()
										+ " | Nome: " + p.getNome()
										+ " | Quantidade: " + p.getQuantidadeEmEstoque()
										+ " | Preço: R$ " + p.getPreco())
						);
					} catch (Exception e) {
						System.out.println("Estoque não encontrado.");
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