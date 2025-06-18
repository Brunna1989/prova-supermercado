package com.br.prova.supermercado.service;

import com.br.prova.supermercado.dto.PedidoDTO;
import com.br.prova.supermercado.dto.ItemDTO;
import com.br.prova.supermercado.dto.ProdutoDTO;
import com.br.prova.supermercado.model.Item;
import com.br.prova.supermercado.model.Pedido;
import com.br.prova.supermercado.model.Produto;
import com.br.prova.supermercado.repository.PedidoRepository;
import com.br.prova.supermercado.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoDTO salvar(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();

        List<Item> itens = new ArrayList<>();
        for (ItemDTO itemDTO : pedidoDTO.getListaItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProduto().getId()));

            if (produto.getQuantidadeEmEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - itemDTO.getQuantidade());
            produtoRepository.save(produto);

            Item item = new Item();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPreco(produto.getPreco() * itemDTO.getQuantidade());
            item.setPedido(pedido);
            itens.add(item);
        }

        pedido.setListaItens(itens);
        pedido.calcularValorTotal();

        pedidoRepository.save(pedido);

        return mapearParaDTO(pedido);
    }

    @Transactional
    public PedidoDTO registrarPagamento(Long pedidoId, double valorPago) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));
        if (valorPago < pedido.getValorTotalDoPedido()) {
            throw new IllegalArgumentException("Valor pago insuficiente para o pedido.");
        }
        pedido.registrarPagamento(valorPago);
        pedidoRepository.save(pedido);
        return mapearParaDTO(pedido);
    }

    public double calcularTroco(double valorTotal, double valorPago) {
        if (valorPago < valorTotal) throw new IllegalArgumentException("Valor pago insuficiente.");
        return valorPago - valorTotal;
    }

    public Map<Double, Integer> calcularNotasTroco(double troco) {
        double[] notas = {100, 50, 20, 10, 5, 2, 1, 0.5, 0.25, 0.10, 0.05, 0.01};
        Map<Double, Integer> resultado = new LinkedHashMap<>();
        double restante = Math.round(troco * 100.0) / 100.0;

        for (double nota : notas) {
            int qtd = (int) (restante / nota);
            if (qtd > 0) {
                resultado.put(nota, qtd);
                restante = Math.round((restante - qtd * nota) * 100.0) / 100.0;
            }
        }
        return resultado;
    }

    public List<ItemDTO> listarItensPorPedidoId(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));
        return pedido.getListaItens().stream()
                .map(item -> ItemDTO.builder()
                        .id(item.getId())
                        .quantidade(item.getQuantidade())
                        .preco(item.getPreco())
                        .produto(ProdutoDTO.builder()
                                .id(item.getProduto().getId())
                                .nome(item.getProduto().getNome())
                                .preco(item.getProduto().getPreco())
                                .quantidadeEmEstoque(item.getProduto().getQuantidadeEmEstoque())
                                .estoqueId(item.getProduto().getEstoque() != null ? item.getProduto().getEstoque().getId() : null)
                                .build())
                        .build())
                .toList();
    }

    private PedidoDTO mapearParaDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setValorTotalDoPedido(pedido.getValorTotalDoPedido());
        dto.setValorPago(pedido.getValorPago());
        dto.setTroco(pedido.getTroco());
        dto.setListaItens(
                pedido.getListaItens().stream().map(item -> {
                    Produto produto = item.getProduto();
                    return ItemDTO.builder()
                            .id(item.getId())
                            .quantidade(item.getQuantidade())
                            .preco(item.getPreco())
                            .produto(ProdutoDTO.builder()
                                    .id(produto.getId())
                                    .nome(produto.getNome())
                                    .preco(produto.getPreco())
                                    .quantidadeEmEstoque(produto.getQuantidadeEmEstoque())
                                    .estoqueId(produto.getEstoque() != null ? produto.getEstoque().getId() : null)
                                    .build())
                            .build();
                }).toList()
        );
        return dto;
    }
}