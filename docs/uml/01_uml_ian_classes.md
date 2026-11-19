# 01 — Diagrama de Classes do Domínio

**Responsável:** Ian Nascimento Santos Costa e Souza (`@ianz3`)  
**Atividade:** [LAB] AT-11-17 — Básico de UML com PlantUML

Este diagrama apresenta as entidades do sistema, a herança de usuários e os relacionamentos principais usados pelo controle de estoque.

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

class Gerente {
  + podeGerenciarUsuarios() : boolean
  + podeEstornarMovimentacao() : boolean
}

class Operador {
  + podeGerenciarUsuarios() : boolean
  + podeEstornarMovimentacao() : boolean
}

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
  - categoria : Categoria
  - fornecedor : Fornecedor
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
  - produto : Produto
  - tipo : String
  - quantidade : int
  - dataHora : String
  - usuarioResponsavel : Usuario
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
### Visualização do Diagrama
![Diagrama UML](01_uml_ian_classes.png)