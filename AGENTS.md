# AGENTS.md — ATA.I Backend

Guia para agentes e desenvolvedores que atuam neste repositório.

## 1. Produto

**ATA.I** é um copiloto de gestão escolar para escolas públicas, desenvolvido para a Global Solution CitizenTech 2026.2 (FIAP — Domain Driven Design / Java).

O sistema organiza **prioridades anuais**, registra **reuniões** e **atas**, preserva o histórico das decisões e permite rastrear cada alteração de prioridade até sua ata de origem.

### Fluxo central (planejado)

1. Diretor ou coordenador registra uma reunião.
2. Informa o ano de referência e fornece uma ata pronta ou uma transcrição textual.
3. Uma integração futura com IA gera uma minuta (quando necessário) e propõe alterações nas prioridades.
4. Diretor ou coordenador revisa o conteúdo e as propostas.
5. Somente o diretor confirma a ata ou aprova a minuta.
6. O sistema aplica as alterações aprovadas e preserva seu histórico.

**Perfis:** superadmin, diretor, coordenador, professor.
**Evolução posterior:** prestação de contas com despesas e comprovantes.

## 2. Stack

| Item | Escolha |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.x (versão estável) |
| Build | Maven |
| Web | Spring Web (API RESTful) |
| Persistência | Spring Data JPA + Hibernate |
| Banco | Oracle Database (FIAP) via Oracle JDBC Driver |
| Validação | Jakarta Bean Validation |
| Documentação da API | Springdoc OpenAPI |
| Testes | JUnit 5 + Mockito |
| Container / Deploy | Docker / Render |
| Migrações (opcional) | Flyway, se compatível com a versão e as permissões do Oracle |

Não adicionar bibliotecas sem justificativa técnica documentada.

## 3. Arquitetura

Monólito modular com arquitetura em camadas, organizado de forma coerente com o domínio.

| Camada | Responsabilidade |
|---|---|
| `controller` | Receber requisições HTTP e produzir respostas |
| `dto` | Contratos de entrada e saída |
| `bo` / `service` | Casos de uso e regras de negócio |
| `dao` / `repository` | Abstração da persistência |
| `model` / `entity` | Entidades do domínio e relacionamentos (compatíveis com Oracle) |
| `exception` | Tratamento centralizado de erros |
| `config` | Configurações de infraestrutura |

**Proibido sem necessidade comprovada:** microserviços, CQRS, Event Sourcing, mensageria ou abstrações complexas.
**Entidades JPA nunca são expostas diretamente nos contratos HTTP.**

## 4. Regras de desenvolvimento

- Seguir convenções Java e Spring.
- Código (classes, métodos, variáveis) em **inglês**; conceitos de negócio na documentação em **português**.
- Injeção de dependência por construtor.
- Regras de negócio centralizadas nos serviços (BO).
- Transações nas operações que exigem consistência.
- Códigos HTTP semanticamente corretos.
- Não implementar regras que contradigam o domínio documentado.

### Segurança e banco

- Nunca armazenar credenciais no código; usar variáveis de ambiente.
- Nunca versionar senhas, tokens ou arquivos `.env` reais.
- Não habilitar criação/alteração automática do esquema Oracle em produção (`ddl-auto` deve ser `none` ou `validate`).
- Não gerar tabelas automaticamente sem autorização.

## 5. Regras de domínio (requisitos futuros)

Aplicáveis somente quando a implementação funcional for autorizada:

- Somente o diretor pode confirmar uma ata.
- Propostas de alteração não modificam prioridades antes da aprovação.
- A confirmação é transacional.
- Toda alteração mantém rastreabilidade até sua ata de origem.
- Usuários escolares acessam apenas informações autorizadas de sua escola.
- A integração com IA não pode inventar dados não fornecidos.

## 6. Etapa atual: scaffold

**Permitido:** configuração de infraestrutura, dependências e testes mínimos de inicialização.

**Não implementar:**

- Endpoints de negócio ou CRUD.
- Autenticação ou autorização.
- Entidades, repositórios ou serviços específicos do ATA.I.
- Integração com IA ou com frontend.
- Migrações de tabelas de domínio.
- Dados fictícios ou seeds.

## 7. Critérios da disciplina (entregas futuras)

- Camada Model compatível com Oracle; camadas DAO e BO.
- Controllers com GET, POST, PUT e DELETE.
- Tratamento de exceções.
- Deploy no Render.
- Documentação: especificação do projeto, diagrama de classes fiel ao código, protótipos, tabela de endpoints, DER e instruções de execução.

## 8. Checklist de conclusão de tarefa

1. Verificar compilação (`mvn clean verify`).
2. Executar os testes disponíveis.
3. Verificar consistência das dependências.
4. Não mascarar erros com configurações inseguras.
5. Documentar decisões técnicas relevantes.
6. Informar explicitamente o que foi **implementado**, o que foi **validado** e o que está **pendente**.
