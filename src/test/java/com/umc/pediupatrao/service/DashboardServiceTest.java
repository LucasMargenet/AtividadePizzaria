package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.IndicadoresGeraisDTO;
import com.umc.pediupatrao.dto.ProdutoMaisVendidoDTO;
import com.umc.pediupatrao.dto.VendaPeriodoDTO;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void montarCenario() {
        Pedido pedido1 = novoPedido("2026-10-01T12:00:00", StatusPedido.FINALIZADO,
                item("p1", "Pizza Calabresa", 40.0, 2));
        Pedido pedido2 = novoPedido("2026-10-01T19:00:00", StatusPedido.RECEBIDO,
                item("p2", "Pizza Marguerita", 35.0, 1));
        Pedido pedido3 = novoPedido("2026-10-02T13:00:00", StatusPedido.EM_PREPARACAO,
                item("p1", "Pizza Calabresa", 40.0, 1));
        Pedido pedido4 = novoPedido("2026-10-02T20:00:00", StatusPedido.CANCELADO,
                item("p1", "Pizza Calabresa", 40.0, 5));

        when(pedidoRepository.findByDataHoraEntradaBetween(any(), any()))
                .thenReturn(List.of(pedido1, pedido2, pedido3, pedido4));
    }

    private Pedido novoPedido(String dataHora, StatusPedido status, ItemPedido item) {
        Pedido pedido = new Pedido();
        pedido.setDataHoraEntrada(LocalDateTime.parse(dataHora));
        pedido.setStatus(status);
        pedido.setItens(List.of(item));
        pedido.setValorTotal(item.getSubtotal());
        return pedido;
    }

    private ItemPedido item(String produtoId, String nome, double preco, int quantidade) {
        return new ItemPedido(produtoId, nome, preco, quantidade);
    }

    @Test
    void indicadoresDevemExcluirPedidosCancelados() {
        IndicadoresGeraisDTO dto = dashboardService.obterIndicadores(null, null);

        assertThat(dto.getTotalPedidos()).isEqualTo(4);
        assertThat(dto.getPedidosCancelados()).isEqualTo(1);
        assertThat(dto.getFaturamentoTotal()).isCloseTo(155.0, within(0.001));
        assertThat(dto.getTicketMedio()).isCloseTo(155.0 / 3, within(0.001));
        assertThat(dto.getTaxaCancelamento()).isCloseTo(25.0, within(0.001));
    }

    @Test
    void indicadoresComListaVaziaNaoDevemQuebrar() {
        when(pedidoRepository.findByDataHoraEntradaBetween(any(), any())).thenReturn(List.of());

        IndicadoresGeraisDTO dto = dashboardService.obterIndicadores(null, null);

        assertThat(dto.getTotalPedidos()).isZero();
        assertThat(dto.getTicketMedio()).isZero();
    }

    @Test
    void vendasPorPeriodoDevemAgruparPorDiaEIgnorarCancelados() {
        List<VendaPeriodoDTO> vendas = dashboardService.vendasPorPeriodo(null, null);

        assertThat(vendas).hasSize(2);
        assertThat(vendas.get(0).getData().toString()).isEqualTo("2026-10-01");
        assertThat(vendas.get(0).getFaturamento()).isCloseTo(115.0, within(0.001));
        assertThat(vendas.get(1).getQuantidadePedidos()).isEqualTo(1);
    }

    @Test
    void produtosMaisVendidosDevemSomarQuantidadesEIgnorarCancelados() {
        List<ProdutoMaisVendidoDTO> ranking = dashboardService.produtosMaisVendidos(null, null, 10);

        ProdutoMaisVendidoDTO primeiro = ranking.get(0);
        assertThat(primeiro.getNomeProduto()).isEqualTo("Pizza Calabresa");
        assertThat(primeiro.getQuantidadeVendida()).isEqualTo(3);
        assertThat(primeiro.getFaturamento()).isCloseTo(120.0, within(0.001));
    }
}
