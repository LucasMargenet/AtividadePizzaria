package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.CancelamentoRequest;
import com.umc.pediupatrao.dto.DescontoRequest;
import com.umc.pediupatrao.dto.ItemPedidoRequest;
import com.umc.pediupatrao.dto.NovoPedidoRequest;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    // Fluxo obrigatório e sequencial do pedido (RF03). CANCELADO não entra
    // aqui: é tratado como exceção, exclusiva do GERENTE, com justificativa (RF04).
    private static final Map<StatusPedido, StatusPedido> PROXIMO_STATUS = new EnumMap<>(StatusPedido.class);
    static {
        PROXIMO_STATUS.put(StatusPedido.RECEBIDO, StatusPedido.EM_PREPARACAO);
        PROXIMO_STATUS.put(StatusPedido.EM_PREPARACAO, StatusPedido.PRONTO);
        PROXIMO_STATUS.put(StatusPedido.PRONTO, StatusPedido.SAIU_PARA_ENTREGA_RETIRADO);
        PROXIMO_STATUS.put(StatusPedido.SAIU_PARA_ENTREGA_RETIRADO, StatusPedido.FINALIZADO);
    }

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
        pedido.setStatus(StatusPedido.RECEBIDO);
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

    // Avança o pedido para o próximo status do fluxo obrigatório. O cancelamento
    // (exceção ao fluxo linear) tem endpoint e regras próprias (RF04).
    public Pedido atualizarStatus(String id, StatusPedido novoStatus, String usernameResponsavel) {
        if (novoStatus == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException(
                    "Cancelamento não é feito por aqui: use a operação de cancelamento, exclusiva do GERENTE.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new IllegalArgumentException(
                    "Pedido " + pedido.getStatus() + " não admite mais alterações de status.");
        }

        StatusPedido statusEsperado = PROXIMO_STATUS.get(pedido.getStatus());
        if (statusEsperado != novoStatus) {
            throw new IllegalArgumentException(
                    "Transição inválida: de " + pedido.getStatus() + " só é permitido ir para " + statusEsperado + ".");
        }

        pedido.setStatus(novoStatus);

        if (novoStatus == StatusPedido.SAIU_PARA_ENTREGA_RETIRADO) {
            Usuario responsavel = usuarioRepository.findByUsername(usernameResponsavel)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário responsável não encontrado."));
            pedido.setResponsavelSaidaId(responsavel.getId());
            pedido.setResponsavelSaidaUsername(responsavel.getUsername());
            pedido.setDataHoraSaida(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    // RF04 - Cancelamento: exclusivo do GERENTE (garantido pelo @PreAuthorize no
    // controller), permitido apenas antes da saída para entrega/retirada, exige
    // justificativa e preserva o histórico do pedido (nada é apagado).
    public Pedido cancelarPedido(String id, CancelamentoRequest request, String usernameResponsavel) {
        if (request == null || !StringUtils.hasText(request.getJustificativa())) {
            throw new IllegalArgumentException("É obrigatório informar a justificativa do cancelamento.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        validarPodeSerAlterado(pedido);

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setJustificativaCancelamento(request.getJustificativa());

        // TODO (RF05): registrar evento de auditoria (cancelamento) com usuario/perfil/data/justificativa
        return pedidoRepository.save(pedido);
    }

    // RF04 - Desconto: exclusivo do GERENTE, percentual limitado a 20%,
    // permitido apenas antes da saída para entrega/retirada.
    public Pedido aplicarDesconto(String id, DescontoRequest request, String usernameResponsavel) {
        if (request == null || request.getPercentual() < 0 || request.getPercentual() > 20) {
            throw new IllegalArgumentException("O desconto deve ser um percentual entre 0 e 20.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        validarPodeSerAlterado(pedido);

        double valorBruto = pedido.getItens().stream().mapToDouble(ItemPedido::getSubtotal).sum();
        pedido.setDesconto(request.getPercentual());
        pedido.setValorTotal(valorBruto * (1 - request.getPercentual() / 100));

        // TODO (RF05): registrar evento de auditoria (desconto) com usuario/perfil/data/valores anterior e novo
        return pedidoRepository.save(pedido);
    }

    private void validarPodeSerAlterado(Pedido pedido) {
        if (pedido.getStatus() == StatusPedido.SAIU_PARA_ENTREGA_RETIRADO
                || pedido.getStatus() == StatusPedido.FINALIZADO
                || pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalArgumentException(
                    "Pedido " + pedido.getStatus() + " não admite cancelamento ou desconto.");
        }
    }

}
