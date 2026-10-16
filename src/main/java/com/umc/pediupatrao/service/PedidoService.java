package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.ItemPedidoRequest;
import com.umc.pediupatrao.dto.NovoPedidoRequest;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private static final String STATUS_INICIAL = "RECEBIDO";

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Pedido criarPedido(NovoPedidoRequest request, String usernameResponsavel) {
        if (request.getClienteId() == null || !clienteRepository.existsById(request.getClienteId())) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter ao menos um item.");
        }

        Usuario responsavel = usuarioRepository.findByUsername(usernameResponsavel)
                .orElseThrow(() -> new IllegalArgumentException("Usuário responsável não encontrado."));

        List<ItemPedido> itens = montarItens(request.getItens());
        double valorTotal = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();

        Pedido pedido = new Pedido();
        pedido.setClienteId(request.getClienteId());
        pedido.setItens(itens);
        pedido.setStatus(STATUS_INICIAL);
        pedido.setDesconto(0);
        pedido.setValorTotal(valorTotal); // calculado aqui, nunca a partir do request
        pedido.setResponsavelRegistroId(responsavel.getId());
        pedido.setResponsavelRegistroUsername(responsavel.getUsername());
        pedido.setDataHoraEntrada(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    private List<ItemPedido> montarItens(List<ItemPedidoRequest> itensRequest) {
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequest itemRequest : itensRequest) {
            if (itemRequest.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
            }

            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado: " + itemRequest.getProdutoId()));

            if (Boolean.FALSE.equals(produto.getAtivo())) {
                throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
            }

            itens.add(new ItemPedido(
                    produto.getId(),
                    produto.getNome(),
                    produto.getPreco(),
                    itemRequest.getQuantidade()
            ));
        }
        return itens;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    // TODO (RF03): validar transições de estado, perfil e registrar data/hora de saída
    public Pedido atualizarStatus(String id, String status) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        pedido.setStatus(status);
        return pedidoRepository.save(pedido);
    }

}
