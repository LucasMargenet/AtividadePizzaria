# PediuPatrão

Sistema de gestão de pedidos para pizzaria, desenvolvido como Atividade Prática M1 (Engenharia de Software). O projeto evolui uma base fornecida em sala de aula, preservando sua arquitetura em camadas (entidade, repositório, serviço, controlador) e suas tecnologias originais.

**Tecnologias:** Java 21, Spring Boot 3, Spring Security, Spring Data MongoDB, Thymeleaf.

## Como executar

### 1. Banco de dados (MongoDB local via Docker)

```bash
docker compose up -d
```

Isso sobe um MongoDB em `localhost:27017`, persistido no volume `pediupatrao-mongo-data`. Não é necessário instalar MongoDB localmente.

> O `application.properties` aponta por padrão para esse banco local (`mongodb://localhost:27017/pizzaria`). Para usar outro banco (ex: um Atlas próprio), defina a variável de ambiente `MONGODB_URI` antes de iniciar a aplicação — nenhuma credencial fica hardcoded no código.

### 2. Aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Usuários de teste

Na primeira execução, três usuários são criados automaticamente (um para cada perfil), com senha `teste123`:

| Usuário        | Perfil     |
|----------------|------------|
| `admin`        | ADMIN      |
| `gerente01`    | GERENTE    |
| `atendente01`  | ATENDENTE  |

## Requisitos implementados

### RF01 — Controle de usuários e permissões
- Três perfis (`ADMIN`, `GERENTE`, `ATENDENTE`), autenticados via Spring Security com base no perfil real gravado no banco (antes, todo usuário autenticado recebia a role `USER` fixa, independente do cadastro).
- Autorização aplicada no **backend**, via `@PreAuthorize` e regras de rota no `SecurityConfig` — ocultar botão na tela não é usado como mecanismo de proteção.
- Gestão de usuários (`/usuarios/**`, `/api/usuarios/**`) restrita a `ADMIN`.

### RF02 — Evolução do cadastro de pedidos
- `Pedido` agora tem itens (produto, quantidade, preço unitário "congelado" no momento da compra), desconto, valor total, responsável pelo registro e data/hora de entrada.
- O valor total é **sempre calculado no servidor** a partir dos itens e do desconto autorizado; qualquer valor de total enviado na requisição é ignorado.

### RF03 — Controle de entrada, saída e transições dos pedidos
- Fluxo obrigatório: `RECEBIDO → EM_PREPARACAO → PRONTO → SAIU_PARA_ENTREGA_RETIRADO → FINALIZADO`.
- Transições fora de ordem (ex: pular etapas) são bloqueadas; nenhuma alteração é permitida após `FINALIZADO` ou `CANCELADO`.
- Responsável e data/hora são registrados tanto na entrada (criação) quanto na saída para entrega/retirada.

### RF04 — Cancelamentos e descontos
- Operações exclusivas do **GERENTE** (inclusive comparado ao ADMIN, que não tem acesso a elas).
- Cancelamento exige justificativa obrigatória, preserva o histórico do pedido e só é permitido antes da saída para entrega/retirada.
- Desconto percentual, limitado a 20%, também só antes da saída.

### RF05 — Auditoria de pedidos e clientes
- Coleção própria no MongoDB (`auditoria`), com entidade/repositório/serviço dedicados.
- Auditados: criação de pedido, mudança de status, desconto, cancelamento (com justificativa) e alterações cadastrais de clientes (endereço, telefone, etc.).
- Cada registro guarda: usuário autenticado e seu perfil, data/hora, tipo de operação, entidade afetada e seu ID, campos alterados (valor anterior/novo) e justificativa quando aplicável.
- Consulta restrita a `ADMIN`/`GERENTE`. **Não existe nenhum endpoint de alteração ou exclusão** dos registros — uma vez criado, um registro de auditoria é permanente.
- Nenhuma senha, token ou credencial é auditada (a auditoria nunca toca o cadastro de usuários).

## Principais mudanças em relação à base original

- `UsuarioDetailsService` corrigido: usava uma role fixa `"USER"` para qualquer login, ignorando o cadastro.
- `SecurityConfig`: `/api/usuarios` era público (qualquer um listava todos os usuários); agora exige `ADMIN`.
- `Pedido`: modelo expandido de um único sabor/quantidade para uma lista de itens com cálculo server-side.
- Telas novas: criação de pedido (`/pedidos/novo` — não existia antes), avanço de status, desconto/cancelamento e consulta de auditoria (`/auditoria`).
- `produtos/lista.html`: corrigido um bug pré-existente da base (a página não carregava — fragmento Thymeleaf inexistente) e o CRUD de produtos, que não funcionava, foi reescrito.
- Credenciais do MongoDB removidas do código-fonte; banco local via Docker Compose.

## Débitos técnicos conhecidos

- CSRF está desabilitado no `SecurityConfig` (herdado da base original). Não é um requisito pontuado pelo enunciado, mas fica registrado como melhoria possível.
- Produtos (`/api/produtos`) não têm restrição de perfil própria — o enunciado não define regras de negócio específicas para esse cadastro.

## Vídeo de demonstração

_(link a adicionar)_
