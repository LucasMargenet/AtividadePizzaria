package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.IndicadoresGeraisDTO;
import com.umc.pediupatrao.dto.ProdutoMaisVendidoDTO;
import com.umc.pediupatrao.dto.StatusPedidoCountDTO;
import com.umc.pediupatrao.dto.VendaPeriodoDTO;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PedidoRepository pedidoRepository;

    private List<Pedido> buscarPedidos(LocalDate inicio, LocalDate fim) {
        LocalDateTime de = inicio != null ? inicio.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime ate = fim != null ? fim.atTime(23, 59, 59) : LocalDateTime.now();
        return pedidoRepository.findByDataHoraEntradaBetween(de, ate);
    }

    public IndicadoresGeraisDTO obterIndicadores(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = buscarPedidos(inicio, fim);

        long total = pedidos.size();
        long cancelados = pedidos.stream().filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();
        long finalizados = pedidos.stream().filter(p -> p.getStatus() == StatusPedido.FINALIZADO).count();

        List<Pedido> validos = pedidos.stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .toList();
        double faturamento = validos.stream().mapToDouble(Pedido::getValorTotal).sum();
        double ticketMedio = validos.isEmpty() ? 0 : faturamento / validos.size();
        double taxaCancelamento = total == 0 ? 0 : (cancelados * 100.0) / total;

        IndicadoresGeraisDTO dto = new IndicadoresGeraisDTO();
        dto.setTotalPedidos(total);
        dto.setPedidosFinalizados(finalizados);
        dto.setPedidosCancelados(cancelados);
        dto.setTaxaCancelamento(taxaCancelamento);
        dto.setFaturamentoTotal(faturamento);
        dto.setTicketMedio(ticketMedio);
        return dto;
    }

    public List<VendaPeriodoDTO> vendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = buscarPedidos(inicio, fim).stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .toList();

        Map<LocalDate, List<Pedido>> porDia = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getDataHoraEntrada().toLocalDate()));

        return porDia.entrySet().stream()
                .map(entry -> {
                    VendaPeriodoDTO dto = new VendaPeriodoDTO();
                    dto.setData(entry.getKey());
                    dto.setQuantidadePedidos(entry.getValue().size());
                    dto.setFaturamento(entry.getValue().stream().mapToDouble(Pedido::getValorTotal).sum());
                    return dto;
                })
                .sorted(Comparator.comparing(VendaPeriodoDTO::getData))
                .toList();
    }

    public List<StatusPedidoCountDTO> contarPorStatus(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = buscarPedidos(inicio, fim);

        Map<StatusPedido, Long> contagem = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getStatus, Collectors.counting()));

        return Arrays.stream(StatusPedido.values())
                .map(status -> new StatusPedidoCountDTO(status, contagem.getOrDefault(status, 0L)))
                .toList();
    }

    public List<ProdutoMaisVendidoDTO> produtosMaisVendidos(LocalDate inicio, LocalDate fim, int limite) {
        List<Pedido> pedidos = buscarPedidos(inicio, fim).stream()
                .filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                .toList();

        Map<String, ProdutoMaisVendidoDTO> acumulado = new LinkedHashMap<>();
        for (Pedido pedido : pedidos) {
            for (ItemPedido item : pedido.getItens()) {
                ProdutoMaisVendidoDTO dto = acumulado.computeIfAbsent(item.getProdutoId(),
                        id -> new ProdutoMaisVendidoDTO(id, item.getNomeProduto(), 0, 0));
                dto.setQuantidadeVendida(dto.getQuantidadeVendida() + item.getQuantidade());
                dto.setFaturamento(dto.getFaturamento() + item.getSubtotal());
            }
        }

        return acumulado.values().stream()
                .sorted(Comparator.comparingInt(ProdutoMaisVendidoDTO::getQuantidadeVendida).reversed())
                .limit(limite)
                .toList();
    }

}
