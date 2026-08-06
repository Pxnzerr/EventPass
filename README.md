# 🎫 EventPass

Sistema de gestão de eventos com venda e validação de ingressos via terminal (CLI), desenvolvido em Java.

## Funcionalidades

- **Cadastrar Evento** — Show, Workshop ou Conferência
- **Vender Ingresso** — Pista, VIP ou Meia Entrada
- **Validar Entrada** — Busca pelo código do ingresso e marca como usado
- **Relatório do Evento** — Quantidade de ingressos vendidos e receita total
- **Listar Eventos** — Visualização de todos os eventos cadastrados

## Estrutura do Projeto

```
src/eventpass/
├── model/
│   ├── TipoIngresso.java      # Enum: PISTA, VIP, MEIA_ENTRADA
│   ├── Ingresso.java           # Código UUID, tipo, preço, status
│   ├── Evento.java             # Classe abstrata base
│   ├── Show.java               # Artista + Gênero Musical
│   ├── Workshop.java           # Instrutor + Carga Horária
│   └── Conferencia.java        # Palestrante + Tema
├── service/
│   └── GerenciadorEventos.java # Lógica de negócio
└── EventPass.java              # Menu CLI principal
```

## Conceitos de POO Aplicados

| Conceito | Aplicação |
|---|---|
| Herança | `Show`, `Workshop`, `Conferencia` → `Evento` |
| Polimorfismo | `getTipoEvento()`, `getDetalhesEspecificos()` |
| Encapsulamento | Atributos privados, listas imutáveis |
| Enum | `TipoIngresso` com multiplicador de preço |
| Composição | `Evento` contém `List<Ingresso>` |

## Como Executar

```bash
# Compilar
javac -encoding UTF-8 -d out src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java

# Executar
java -cp out eventpass.EventPass
```

## Requisitos

- Java 17+
