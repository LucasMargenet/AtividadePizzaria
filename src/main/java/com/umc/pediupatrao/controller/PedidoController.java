package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.CancelamentoRequest;
import com.umc.pediupatrao.dto.DescontoRequest;
import com.umc.pediupatrao.dto.NovoPedidoRequest;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Registro de pedidos é responsabilidade de ATENDENTE/GERENTE (ADMIN apenas consulta)
    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE')")
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody NovoPedidoRequest request, Authentication authentication) {
        Pedido pedido = pedidoService.criarPedido(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    @PreAuthorize("hasAnyRole('ATENDENTE', 'GERENTE')")
    @PutMapping("/{id}/status")
    public Pedido atualizarStatus(@PathVariable String id, @RequestParam StatusPedido status,
            Authentication authentication) {
        return pedidoService.atualizarStatus(id, status, authentication.getName());
    }

    // Cancelamento: exclusivo do GERENTE, mesmo comparado ao ADMIN
    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping("/{id}/cancelar")
    public Pedido cancelarPedido(@PathVariable String id, @RequestBody CancelamentoRequest request,
            Authentication authentication) {
        return pedidoService.cancelarPedido(id, request, authentication.getName());
    }

    // Desconto: exclusivo do GERENTE, mesmo comparado ao ADMIN
    @PreAuthorize("hasRole('GERENTE')")
    @PutMapping("/{id}/desconto")
    public Pedido aplicarDesconto(@PathVariable String id, @RequestBody DescontoRequest request,
            Authentication authentication) {
        return pedidoService.aplicarDesconto(id, request, authentication.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarRequisicaoInvalida(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

}
