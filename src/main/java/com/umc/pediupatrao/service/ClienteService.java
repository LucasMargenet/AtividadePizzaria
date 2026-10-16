package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.entity.TipoOperacaoAuditoria;
import com.umc.pediupatrao.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    public Cliente novoCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Método para excluir cliente
    public void excluir(String id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    // Método para salvar um novo cliente ou atualizar um cliente existente,
    // auditando inclusões e, principalmente, alterações cadastrais (RF05).
    public Cliente salvar(Cliente cliente, String usernameAtor) {
        if (cliente.getId() == null) {
            Cliente salvo = clienteRepository.save(cliente);
            auditoriaService.registrar(usernameAtor, TipoOperacaoAuditoria.CRIACAO_CLIENTE, "Cliente", salvo.getId(),
                    List.of(new AlteracaoCampo("nome", null, salvo.getNome())), null);
            return salvo;
        }

        Cliente clienteAnterior = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualização."));

        List<AlteracaoCampo> alteracoes = compararClientes(clienteAnterior, cliente);

        Cliente atualizado = clienteRepository.save(cliente);

        auditoriaService.registrar(usernameAtor, TipoOperacaoAuditoria.ALTERACAO_CLIENTE, "Cliente",
                atualizado.getId(), alteracoes, null);

        return atualizado;
    }

    private List<AlteracaoCampo> compararClientes(Cliente antigo, Cliente novo) {
        List<AlteracaoCampo> alteracoes = new ArrayList<>();
        adicionarSeMudou(alteracoes, "nome", antigo.getNome(), novo.getNome());
        adicionarSeMudou(alteracoes, "telefone", antigo.getTelefone(), novo.getTelefone());
        adicionarSeMudou(alteracoes, "cep", antigo.getCep(), novo.getCep());
        adicionarSeMudou(alteracoes, "logradouro", antigo.getLogradouro(), novo.getLogradouro());
        adicionarSeMudou(alteracoes, "numero", antigo.getNumero(), novo.getNumero());
        adicionarSeMudou(alteracoes, "complemento", antigo.getComplemento(), novo.getComplemento());
        adicionarSeMudou(alteracoes, "bairro", antigo.getBairro(), novo.getBairro());
        adicionarSeMudou(alteracoes, "cidade", antigo.getCidade(), novo.getCidade());
        adicionarSeMudou(alteracoes, "estado", antigo.getEstado(), novo.getEstado());
        adicionarSeMudou(alteracoes, "endereco", antigo.getEndereco(), novo.getEndereco());
        return alteracoes;
    }

    private void adicionarSeMudou(List<AlteracaoCampo> alteracoes, String campo, String valorAnterior, String valorNovo) {
        if (!Objects.equals(valorAnterior, valorNovo)) {
            alteracoes.add(new AlteracaoCampo(campo, valorAnterior, valorNovo));
        }
    }

}
