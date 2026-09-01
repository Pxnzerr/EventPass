# 🎫 EventPass

Sistema robusto de gestão de eventos, bilheteria, validação de acesso e relatórios financeiros via terminal (CLI), desenvolvido em Java com foco em boas práticas de **Programação Orientada a Objetos (POO)**, **Tratamento Estruturado de Exceções**, **Java Streams** e **Testes Automatizados**.

---

## 🚀 Funcionalidades

- **Cadastrar Evento** — Suporte polimórfico a Shows, Workshops e Conferências com dados e regras específicas.
- **Venda de Ingressos** — Categorias Pista (1.0x), VIP (2.5x) e Meia Entrada (0.5x) com verificação rigorosa de capacidade máxima.
- **Validação de Entrada (Check-in)** — Localização instantânea por código alfanumérico com bloqueio de reuso e rejeição de ingressos cancelados.
- **Cancelamento e Estorno** — Cancelamento seguro de ingressos não utilizados com restituição automática da vaga no evento e recálculo da receita líquida.
- **Busca e Filtros Avançados** — Consulta por nome/palavra-chave, tipo de evento, faixa de preço e eventos com vagas disponíveis via Java Streams.
- **Relatório Completo e Exportação de Arquivos** — Métricas financeiras e ocupação em tempo real, com exportação para arquivo `.txt` formatado e `.csv` estruturado.
- **Dashboard Consolidado e Taxa de Ocupação** — Visão analítica global da plataforma com receita consolidada, ocupação percentual e exportação em `.txt`.
- **Suíte de Testes Automatizados** — Test runner nativo validando 100% das regras de negócio, modelos, taxas e casos de borda (13 testes).

---

## 📁 Estrutura do Projeto

```
EventPass/
├── src/eventpass/
│   ├── exception/                      # Exceções Customizadas de Domínio
│   │   ├── EventPassException.java      # Classe base de runtime exceptions
│   │   ├── EventoNaoEncontradoException.java
│   │   ├── CapacidadeEsgotadaException.java
│   │   ├── IngressoInvalidoException.java
│   │   └── OperacaoInvalidaException.java
│   ├── model/                          # Modelos de Domínio e Entidades
│   │   ├── StatusIngresso.java         # Enum: VALIDO, UTILIZADO, CANCELADO
│   │   ├── TipoIngresso.java           # Enum: PISTA, VIP, MEIA_ENTRADA (multiplicadores)
│   │   ├── Ingresso.java               # Entidade do ingresso com código UUID e ciclo de vida
│   │   ├── Evento.java                 # Classe abstrata base com taxa de ocupação e gestão de ingressos
│   │   ├── Show.java                   # Especialização: Artista + Gênero Musical
│   │   ├── Workshop.java               # Especialização: Instrutor + Carga Horária
│   │   └── Conferencia.java            # Especialização: Palestrante + Tema
│   ├── service/
│   │   └── GerenciadorEventos.java     # Serviços, filtros, dashboard consolidado e exportação
│   └── EventPass.java                  # Ponto de entrada e interface CLI interativa
├── test/
│   └── eventpass/
│       └── EventPassTest.java          # Suíte completa de testes automatizados (13 testes)
├── run.bat                             # Script para compilar e executar o CLI (Windows)
├── run.sh                              # Script para compilar e executar o CLI (Linux/macOS)
├── test.bat                            # Script para compilar e rodar a suíte de testes (Windows)
├── test.sh                             # Script para compilar e rodar a suíte de testes (Linux/macOS)
├── LICENSE                             # Licença MIT
└── README.md                           # Documentação técnica do projeto
```

---

## 🧠 Conceitos de POO & Engenharia Aplicados

| Conceito | Aplicação no EventPass |
|---|---|
| **Herança** | `Show`, `Workshop` e `Conferencia` estendem a classe abstrata base `Evento` |
| **Polimorfismo** | Sobrescrita dinâmica de `getTipoEvento()` e `getDetalhesEspecificos()` |
| **Encapsulamento** | Atributos privados, imutabilidade com `List.copyOf` e métodos de acesso controlados |
| **Enums Ricos** | `TipoIngresso` com multiplicadores e `StatusIngresso` com badges visuais |
| **Composição** | `Evento` encapsula coleções de `Ingresso` e gerencia seu ciclo de vida |
| **Exceções de Domínio** | Hierarquia customizada (`EventPassException`, `CapacidadeEsgotadaException`, etc.) |
| **Java Streams & Lambdas** | Filtros avançados, somatórios de receita e mapeamentos declarativos |
| **File I/O Moderno (NIO.2)** | Exportação com `Files.writeString` e `StandardCharsets.UTF_8` |

---

## ⚙️ Como Executar

### 1. Executando a Aplicação (CLI)

**No Windows:**
```cmd
run.bat
```

**No Linux / macOS:**
```bash
chmod +x run.sh && ./run.sh
```

**Ou manualmente via terminal:**
```bash
# Compilar
javac -encoding UTF-8 -d out src/eventpass/exception/*.java src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java

# Executar
java -cp out eventpass.EventPass
```

---

### 2. Executando a Suíte de Testes Automatizados

**No Windows:**
```cmd
test.bat
```

**No Linux / macOS:**
```bash
chmod +x test.sh && ./test.sh
```

**Ou manualmente via terminal:**
```bash
# Compilar testes
javac -encoding UTF-8 -d out src/eventpass/exception/*.java src/eventpass/model/*.java src/eventpass/service/*.java src/eventpass/EventPass.java test/eventpass/EventPassTest.java

# Executar suíte de testes
java -cp out eventpass.EventPassTest
```

---

## 📋 Requisitos

- **Java JDK 17** ou superior

---

## 👤 Autor

Arthur Clark Francisco ([@Pxnzerr](https://github.com/Pxnzerr))
