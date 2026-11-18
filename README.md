# Alphafield — Sistema de Controle de Estoque para Mercearias (Grupo 1)

> **Universidade Federal de Goiás (UFG) — Engenharia de Computação**  
> **Disciplina:** Programação Orientada a Objetos (POO) | **Docente:** Prof. Marcelo Akira Inuzuka  
> **Status em 18/11/2026:** Etapa 1 em execução — conclusão planejada para 02/12/2026

---

## Seção 1 — Introdução

### 1.1. Contextualização e descrição do problema

Pequenas mercearias de bairro frequentemente controlam suas mercadorias de forma manual em cadernos ou planilhas isoladas. Esse processo acarreta problemas operacionais constantes:

1. **Ruptura de estoque:** ausência de alertas automáticos quando um produto atinge sua quantidade mínima de segurança.
2. **Perdas por perecibilidade:** falta de acompanhamento sistemático das datas de validade dos lotes nas prateleiras.
3. **Divergências de saldo:** erros manuais ao somar entradas de fornecedores, registrar baixas diárias ou estornar lançamentos equivocados.

### 1.2. Justificativa e motivação

Concebido inicialmente como um protótipo estruturado em linguagem C no semestre anterior, o **Alphafield** foi remodelado sob o paradigma da **Programação Orientada a Objetos em Java**. A motivação central é oferecer um software desktop ágil, intuitivo e independente de internet para computadores de balcão, aplicando encapsulamento, herança, polimorfismo, `ArrayList`, exceções personalizadas, ORMLite/SQLite e arquitetura em camadas (MVC).

### 1.3. Soluções semelhantes e referências técnicas

- **Programa NEX (Nextar):** referência para cadastro ágil de produtos e fornecedores, entrada/saída e alerta visual de estoque mínimo.
- **MarketUP:** referência para organização por categorias e painel analítico do inventário.
- **Kyte:** referência para catálogo enxuto e diferenciação de perfis de operadores.
- **LGPD e ISO/IEC 25010:** referência para minimizar os dados cadastrais, facilitar o uso da interface e tornar o saldo mais confiável.

---

## Seção 2 — Plano do Projeto

### 2.1. Objetivo geral

Desenvolver uma aplicação desktop em Java com interface gráfica em **JavaFX** e persistência em **SQLite via ORMLite** para o gerenciamento completo de estoque de pequenas mercearias, organizada nos pacotes `model`, `repository`, `controller`, `view` e `exception`.

### 2.2. Objetivos específicos

1. **Implementar cinco módulos CRUD completos:** Categoria, Fornecedor, Usuário, Produto e Movimentação.
2. **Identificação dinâmica e polimorfismo de sessão:** ler nome e perfil no início da execução, instanciando `Gerente` ou `Operador` a partir de `Usuario`.
3. **Abordagem híbrida:** persistir com `Dao<T, Integer>` do ORMLite e carregar registros em `ArrayList` para buscas, filtros, ordenações e cálculos.
4. **Robustez com exceções personalizadas:** validar entradas e permissões com `DadosInvalidosException`, `EstoqueInsuficienteException` e `OperacaoNaoPermitidaException`.

### 2.3. Evolução técnica: JavaFX e BlueJ

Após as aulas práticas de JavaFX com banco de dados, ORM e collections, a equipe adotou **JavaFX + ORMLite/SQLite + ArrayList**. Para garantir compilação no **BlueJ**, a interface (`AlphafieldApp`) e os estilos (`EstiloVisual`) foram construídos em classes Java puras, sem FXML, CSS externo ou `module-info.java`.

---

## Seção 3 — Divisão de Tarefas, Responsabilidades e Prazos

| Integrante | GitHub | Cargo | Responsabilidades e módulos | Issues (Etapa 1) |
| :--- | :--- | :--- | :--- | :--- |
| **Pedro Henrique Machado Benício** | `@phmbenicio` | **Líder (Maintainer)** | Gestão de branches/PRs e Kanban, `Database.java`, movimentações e `EstoqueController.java`. | `#1`, `#2`, `#3` |
| **Ian Nascimento Santos Costa e Souza** | `@ianz3` | **Arquiteto** | UML em PlantUML, documentação, `Categoria.java`, `CategoriaRepositorio.java` e `UsuarioRepositorio.java`. | `#12`, `#13` |
| **Marcelo Soares Martins** | `@msoaresmartins` | **QA / Testes** | Exceções personalizadas e hierarquia `Usuario`, `Gerente` e `Operador`. | `#10`, `#11` |
| **João Pedro Moreira Cavalcante** | `@xXJpm4Xx` | **Frontend** | `EstiloVisual.java`, `AlphafieldApp.java`, painel, `PieChart`, `TableView` e modais. | `#7`, `#8`, `#9` |
| **João Guilherme Passos Rocha de Andrade** | `@52joaog22` | **Backend** | Produto, fornecedor, repositórios e buscas/filtros com `ArrayList`. | `#4`, `#5`, `#6` |

### 3.1. Cronograma Diário de Execução e Commits (18/11/2026 a 02/12/2026)

O trabalho será integrado em pequenas entregas diárias. Os cinco Pull Requests de modelagem são concluídos até **22/11/2026**, antes do prazo do LAB em 23/11. Os 13 commits da Etapa 1 terminam em **02/12/2026**, dois dias antes do prazo final de 04/12.

| Data programada | Entrega diária | Responsável | PR / commit previsto | Issue(s) |
| --- | --- | --- | --- | --- |
| 18/11/2026 | Diagrama de classes do domínio e configuração inicial do projeto | Ian / Pedro | PR UML 01; Commit 01 `chore: estrutura inicial e dependências SQLite/ORMLite` | #12, #1 |
| 19/11/2026 | Sequência de saída de estoque e exceções personalizadas | Pedro / Marcelo | PR UML 02; Commit 02 `feat(exception): cria exceções personalizadas` | #12, #10 |
| 20/11/2026 | Modelagem do módulo de produtos e hierarquia de usuários | João Guilherme / Marcelo | PR UML 03; Commit 03 `feat(model): implementa Usuario, Gerente e Operador` | #4, #11 |
| 21/11/2026 | Casos de uso do operador e entidade Categoria | João Pedro / Ian | PR UML 04; Commit 04 `feat(model): implementa Categoria` | #12, #13 |
| 22/11/2026 | Herança/exceções do gerente e entidades Produto/Fornecedor | Marcelo / João Guilherme | PR UML 05; Commit 05 `feat(model): implementa Produto e Fornecedor` | #12, #4, #5 |
| 23/11/2026 | Movimentação e conexão SQLite | Pedro | Commit 06 `feat(database): implementa Movimentacao e conexão ORMLite` | #1, #2 |
| 24/11/2026 | Repositórios de Categoria e Usuário | Ian | Commit 07 `feat(repository): implementa CategoriaRepositorio e UsuarioRepositorio` | #13 |
| 25/11/2026 | Repositório de Fornecedor | João Guilherme | Commit 08 `feat(repository): implementa FornecedorRepositorio` | #5 |
| 26/11/2026 | Repositório de Produto, filtros e somatórios | João Guilherme | Commit 09 `feat(repository): implementa ProdutoRepositorio e filtros ArrayList` | #4, #6 |
| 27/11/2026 | Repositório de movimentações e regras do controlador | Pedro | Commit 10 `feat(controller): implementa MovimentacaoRepositorio e EstoqueController` | #2, #3 |
| 28/11/2026 | Testes interativos e validação dos fluxos da Etapa 1 | Marcelo | Commit 11 `test: adiciona TesteSistema e valida exceções` | #10, #11 |
| 01/12/2026 | Identidade visual JavaFX | João Pedro | Commit 12 `style(view): cria EstiloVisual` | #7 |
| 02/12/2026 | Telas JavaFX, painel, tabelas e revisão de integração | João Pedro / Pedro | Commit 13 `feat(view): implementa AlphafieldApp e revisão da Etapa 1` | #8, #9, #3 |

As Issues **#14** (refinamentos de filtros e badges) e **#15** (ampliação da suíte de QA) continuam planejadas somente para a **Etapa 2**, iniciada após 04/12/2026. Elas não fazem parte dos 13 commits acima.

### 3.2. Status do Kanban (Issues)

O Kanban documenta o acompanhamento no próprio repositório. Em 18/11/2026, as colunas devem ser atualizadas conforme cada entrega do cronograma for revisada e integrada.

| Coluna | Issues | Critério de acompanhamento |
| --- | --- | --- |
| **Backlog / To Do** | #2, #3, #4, #5, #6, #7, #8, #9, #10, #11, #13, #14, #15 | Tarefas aguardando o respectivo dia do cronograma; #14 e #15 permanecem aqui até a Etapa 2. |
| **In Progress** | #1, #12 | Configuração inicial e produção/revisão dos cinco diagramas PlantUML em andamento. |
| **Done** | Nenhuma issue no início do cronograma | A issue só entra nesta coluna após revisão do Pull Request e merge na `main`. |

---

## Seção 4 — Modelagem Inicial (UML com PlantUML)

Os artefatos detalhados da atividade **[LAB] AT-11-17: Básico de UML com PlantUML** estão na pasta [`docs/uml/`](./docs/uml/):

- [01 — Diagrama de Classes do Domínio (Ian Nascimento)](./docs/uml/01_uml_ian_classes.md)
- [02 — Diagrama de Sequência e Caso de Uso: Saída de Estoque (Pedro Henrique)](./docs/uml/02_uml_pedro_sequencia.md)
- [03 — Diagrama de Classes e Caso de Uso: Módulo de Produtos (João Guilherme)](./docs/uml/03_uml_joao_guilherme_produto.md)
- [04 — Diagrama de Caso de Uso (Operador) e Sequência de Sessão (João Pedro)](./docs/uml/04_uml_joao_pedro_casos_uso.md)
- [05 — Diagrama de Classes (Herança e Exceções) e Caso de Uso: Gerente (Marcelo Soares)](./docs/uml/05_uml_marcelo_heranca_excecoes.md)

### 4.1. Diagrama de classes geral

```plantuml
@startuml
skinparam classAttributeIconSize 0
class Usuario {
  - id : int
  - nome : String
  - login : String
  - senha : String
  - perfil : String
  + podeGerenciarUsuarios() : boolean
  + podeEstornarMovimentacao() : boolean
}
class Gerente
class Operador
class Categoria {
  - id : int
  - nome : String
  - descricao : String
}
class Fornecedor {
  - id : int
  - empresa : String
  - cnpj : String
  - telefone : String
}
class Produto {
  - id : int
  - codigo : String
  - nome : String
  - quantidade : int
  - preco : double
  - estoqueMinimo : int
  - validade : String
  + calcularValorTotal() : double
  + estaVencido() : boolean
  + getStatusEstoque() : String
}
class Movimentacao {
  - id : int
  - tipo : String
  - quantidade : int
  - dataHora : String
  - observacao : String
}
Usuario <|-- Gerente
Usuario <|-- Operador
Categoria "1" <-- "0..*" Produto
Fornecedor "1" <-- "0..*" Produto
Produto "1" <-- "0..*" Movimentacao
Usuario "1" <-- "0..*" Movimentacao
@enduml
```

### 4.2. Diagrama de casos de uso

```plantuml
@startuml
left to right direction
actor "Operador" as Op
actor "Gerente" as Ger
Ger --|> Op
rectangle "Sistema Alphafield (JavaFX + SQLite)" {
  usecase "Iniciar Sessão e Identificar Perfil" as UC1
  usecase "Visualizar Painel Geral e Alertas" as UC2
  usecase "Gerenciar Produtos, Categorias e Fornecedores" as UC3
  usecase "Registrar Entrada e Saída de Estoque" as UC4
  usecase "Estornar Movimentação" as UC5
  usecase "Gerenciar Usuários" as UC6
}
Op --> UC1
Op --> UC2
Op --> UC3
Op --> UC4
Ger --> UC5
Ger --> UC6
@enduml
```

### 4.3. Diagrama de sequência — saída de estoque

```plantuml
@startuml
autonumber
actor "Operador / Gerente" as U
participant "AlphafieldApp" as V
participant "EstoqueController" as C
participant "Produto" as P
participant "ProdutoRepositorio" as PR
participant "MovimentacaoRepositorio" as MR
U -> V : Solicita saída de estoque
V -> C : registrarSaida(produto, quantidade, obs)
C -> P : getQuantidade()
alt quantidade > saldo disponível
  C --> V : throw EstoqueInsuficienteException
  V --> U : Exibe Alert de Erro
else saldo suficiente
  C -> P : setQuantidade(saldo - quantidade)
  C -> PR : salvar(produto)
  C -> MR : salvar(novaMovimentacao)
  C --> V : Movimentação registrada
end
@enduml
```

---

## Relatórios Individuais de Produção — Etapa 1

- [Relatório Individual — Pedro Henrique Machado Benício](./relatorios/RELATORIO_PEDRO_HENRIQUE.md)
- [Relatório Individual — Ian Nascimento Santos Costa e Souza](./relatorios/RELATORIO_IAN_NASCIMENTO.md)
- [Relatório Individual — Marcelo Soares Martins](./relatorios/RELATORIO_MARCELO_SOARES.md)
- [Relatório Individual — João Guilherme Passos Rocha de Andrade](./relatorios/RELATORIO_JOAO_GUILHERME.md)
- [Relatório Individual — João Pedro Moreira Cavalcante](./relatorios/RELATORIO_JOAO_PEDRO.md)

## Fluxo de Branches e Pull Requests

As branches usam os prefixos `feat/`, `fix/` ou `refactoring/` e são integradas à `main` por Pull Request revisado pelo Líder (Maintainer). As issues `#1` a `#13` pertencem à Etapa 1; as issues `#14` e `#15` ficam abertas para a Etapa 2.

| Branch | Responsável | Principais issues |
| --- | --- | --- |
| `feat/excecoes-usuarios` | Marcelo | #10, #11 |
| `feat/categorias-uml` | Ian | #12, #13 |
| `feat/produtos-fornecedores` | João Guilherme | #4, #5, #6 |
| `feat/movimentacoes-controller` | Pedro | #1, #2, #3 |
| `feat/interface-javafx` | João Pedro | #7, #8, #9 |
