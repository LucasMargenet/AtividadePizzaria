package com.umc.pediupatrao.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pedidos")
public class Pedido {
    @Id
    private String id;
    private String clienteId;
    private List<ItemPedido> itens = new ArrayList<>();
    private StatusPedido status;

    // Desconto percentual (0-20), aplicado exclusivamente pelo GERENTE (RF04)
    private double desconto;
    // Sempre calculado no servidor a partir dos itens e do desconto autorizado
    private double valorTotal;

    private String responsavelRegistroId;
    private String responsavelRegistroUsername;
    private LocalDateTime dataHoraEntrada;

    // Preenchidos somente na transição para SAIU_PARA_ENTREGA_RETIRADO
    private String responsavelSaidaId;
    private String responsavelSaidaUsername;
    private LocalDateTime dataHoraSaida;

    // Preenchidos somente no cancelamento (RF04)
    private String justificativaCancelamento;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getResponsavelRegistroId() {
        return responsavelRegistroId;
    }

    public void setResponsavelRegistroId(String responsavelRegistroId) {
        this.responsavelRegistroId = responsavelRegistroId;
    }

    public String getResponsavelRegistroUsername() {
        return responsavelRegistroUsername;
    }

    public void setResponsavelRegistroUsername(String responsavelRegistroUsername) {
        this.responsavelRegistroUsername = responsavelRegistroUsername;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }

    public String getResponsavelSaidaId() {
        return responsavelSaidaId;
    }

    public void setResponsavelSaidaId(String responsavelSaidaId) {
        this.responsavelSaidaId = responsavelSaidaId;
    }

    public String getResponsavelSaidaUsername() {
        return responsavelSaidaUsername;
    }

    public void setResponsavelSaidaUsername(String responsavelSaidaUsername) {
        this.responsavelSaidaUsername = responsavelSaidaUsername;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }

    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public String getJustificativaCancelamento() {
        return justificativaCancelamento;
    }

    public void setJustificativaCancelamento(String justificativaCancelamento) {
        this.justificativaCancelamento = justificativaCancelamento;
    }
}
