package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.IndicadoresGeraisDTO;
import com.umc.pediupatrao.dto.ProdutoMaisVendidoDTO;
import com.umc.pediupatrao.dto.StatusPedidoCountDTO;
import com.umc.pediupatrao.dto.VendaPeriodoDTO;
import com.umc.pediupatrao.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
@Tag(name = "Dashboard", description = "Indicadores, gráficos e rankings para o painel administrativo")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(
            summary = "Indicadores gerais de vendas (RF01)",
            description = "Totais do período: pedidos, faturamento, ticket médio, cancelamentos. "
                    + "Pedidos CANCELADO não entram no faturamento nem no ticket médio.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Indicadores calculados com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Autenticado, mas sem perfil ADMIN/GERENTE", content = @Content)
            }
    )
    @GetMapping("/indicadores")
    public IndicadoresGeraisDTO indicadores(
            @Parameter(description = "Data inicial do período (inclusive). Omitido = sem limite inferior.", example = "2026-10-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Data final do período (inclusive). Omitido = até hoje.", example = "2026-10-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return dashboardService.obterIndicadores(inicio, fim);
    }

    @Operation(
            summary = "Análise de vendas por dia (RF02)",
            description = "Série temporal de faturamento e quantidade de pedidos por dia, para montar o gráfico. "
                    + "Pedidos CANCELADO não são contabilizados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Série calculada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Sem perfil ADMIN/GERENTE", content = @Content)
            }
    )
    @GetMapping("/vendas-por-periodo")
    public List<VendaPeriodoDTO> vendasPorPeriodo(
            @Parameter(example = "2026-10-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(example = "2026-10-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return dashboardService.vendasPorPeriodo(inicio, fim);
    }

    @Operation(
            summary = "Situação dos pedidos por status (RF03)",
            description = "Quantidade de pedidos em cada status do fluxo (RECEBIDO, EM_PREPARACAO, PRONTO, "
                    + "SAIU_PARA_ENTREGA_RETIRADO, FINALIZADO, CANCELADO) dentro do período. Todos os status são "
                    + "retornados, mesmo com contagem zero.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Contagem calculada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Sem perfil ADMIN/GERENTE", content = @Content)
            }
    )
    @GetMapping("/status-pedidos")
    public List<StatusPedidoCountDTO> statusPedidos(
            @Parameter(example = "2026-10-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(example = "2026-10-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return dashboardService.contarPorStatus(inicio, fim);
    }

    @Operation(
            summary = "Produtos mais vendidos (RF04)",
            description = "Ranking dos produtos por quantidade vendida no período, com o faturamento de cada um. "
                    + "Pedidos CANCELADO não são contabilizados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ranking calculado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Parâmetro 'limite' inválido (deve ser >= 1)", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem perfil ADMIN/GERENTE", content = @Content)
            }
    )
    @GetMapping("/produtos-mais-vendidos")
    public List<ProdutoMaisVendidoDTO> produtosMaisVendidos(
            @Parameter(example = "2026-10-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(example = "2026-10-31") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @Parameter(description = "Quantidade máxima de produtos no ranking", example = "10", schema = @Schema(minimum = "1", defaultValue = "10"))
            @RequestParam(defaultValue = "10") int limite) {
        if (limite < 1) {
            throw new IllegalArgumentException("O parâmetro 'limite' deve ser maior ou igual a 1.");
        }
        return dashboardService.produtosMaisVendidos(inicio, fim, limite);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarRequisicaoInvalida(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

}
