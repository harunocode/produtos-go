# Guia de Branches e Pull Requests 🚀

Este documento define o fluxo de trabalho (Git Flow simplificado) utilizado em nosso projeto, explicando o propósito de cada branch, o padrão de commits adotado e o passo a passo correto para criar uma nova branch e abrir uma Pull Request (PR), além de conter as diretrizes essenciais para o desenvolvimento em **Java**.

---

## 📌 Entendendo Nossas Branches

O repositório possui três branches principais com papéis bem definidos:

1. **`main`** (Produção)
   * Contém apenas código estável que está rodando (ou pronto para rodar) em produção.
   * **Ninguém commita diretamente aqui.** Apenas a branch de `release` (ou hotfixes urgentes) faz merge para a `main`.

2. **`dev`** (Desenvolvimento / Integração)
   * É a branch onde todas as novas funcionalidades, melhorias e correções do dia a dia se encontram.
   * **É a partir daqui que você deve criar a sua nova branch** para trabalhar em novas tarefas.

3. **`release`** (Homologação / Pré-produção)
   * Usada para preparar o ambiente de testes finais (Staging/QA) antes de mandar para a `main`.
   * Quando a `dev` está pronta para ir ao ar, fazemos um merge de `dev` para `release`.

---

## 📜 Padrões de Commits (Conventional Commits)

Utilizamos commits semânticos para manter um histórico limpo e legível, facilitando a identificação de alterações e a automação de versionamento.

### Formato da Mensagem

```text
:emoji: tipo(escopo opcional): descrição
```

* **`:emoji:`**: Representa o commit visualmente (pode ser o código como `:sparkles:` ou o emoji real ✨). Recomendado, mas aceito pelo hook.
* **`tipo`**: Define a intenção do commit (ex: `feat`, `fix`).
* **`(escopo)`**: *(Opcional)* Indica a área afetada (ex: `auth`, `ui`, `api`).
* **`!`**: *(Opcional)* Indica uma mudança que quebra a compatibilidade (*breaking change*), ex: `feat(api)!: ...`.
* **`descrição`**: Resumo curto do que foi feito.

### Tipos Principais e Emojis 🦄

| Tipo | Emoji | Descrição |
| :--- | :---: | :--- |
| **`feat`** | ✨ | Novo recurso ou funcionalidade (relacionado ao MINOR do versionamento). |
| **`fix`** | 🐛 | Correção de um problema ou bug (relacionado ao PATCH do versionamento). |
| **`docs`** | 📝 | Mudanças exclusivas na documentação (ex: README). |
| **`test`** | 🧪 | Criação, alteração ou exclusão de testes unitários. |
| **`build`** | 📦 | Modificações em arquivos de build e dependências. |
| **`perf`** | ⚡ | Alterações de código focadas em melhoria de performance. |
| **`style`** | 💄 | Formatação de código, espaços, ponto e vírgula, lints (sem mudança no código funcional). |
| **`refactor`** | ♻️ | Refatorações que não alteram a funcionalidade existente. |
| **`chore`** | 🔧 | Atualizações de tarefas de build, configs de admin, pacotes (ex: `.gitignore`). |
| **`ci`** | 👷 | Mudanças relacionadas à integração contínua (CI). |
| **`raw`** | 📄 | Mudanças em arquivos de configurações, dados, features ou parâmetros. |
| **`cleanup`**| 🧹 | Remoção de código comentado ou trechos desnecessários para limpeza. |
| **`remove`** | 🔥 | Exclusão de arquivos, diretórios ou funcionalidades obsoletas. |
| **`init`** | 🎉 | Commit inicial do projeto. |
| **`revert`** | ⏪ | Reversão de um commit anterior. |
| **`security`**| 🔒 | Correção de vulnerabilidades, falhas de segurança ou hardening. |

### Exemplos de Commits

* ✨ `feat(auth): adicionar página de login`
* 🐛 `fix(button): corrigir espaçamento do botão de envio`
* 📝 `docs: atualizar instruções de instalação no readme`

---

## ☕ Padrões de Código e Boas Práticas em Java

Para garantir a coesão, legibilidade e manutenibilidade da nossa aplicação Java em equipe, seguimos as diretrizes abaixo.

### 1. Versão do Java e Gerenciamento de Dependências
* **Versão do JDK:** Todos os desenvolvedores devem utilizar a mesma versão do Java definida no projeto (ex: **Java 17 LTS** ou **Java 21 LTS**). Verifique a versão configurada no arquivo de build.
* **Maven / Gradle:** Utilize sempre o wrapper do gerenciador de dependências (`./mvnw` ou `./gradlew`) para evitar inconsistências de versões locais de plugins e bibliotecas.

### 2. Convenções de Nomenclatura (Naming Conventions)
Seguimos estritamente o padrão da comunidade Java (Oracle Java Conventions):
* **Classes e Interfaces (`PascalCase`):** Devem ser substantivos que descrevem bem o seu papel (ex: `ClienteService`, `PedidoRepository`, `SecurityConfig`).
* **Métodos (`camelCase`):** Devem iniciar com um verbo que indique a ação realizada (ex: `calcularTotal()`, `encontrarPorId()`, `salvarCliente()`).
* **Variáveis e Atributos (`camelCase`):** Nomes claros e significativos, evitando abreviações obscuras (ex: `dataNascimento`, `ativo`, `totalItens`).
* **Constantes (`UPPER_SNAKE_CASE`):** Variáveis estáticas e finais (`public static final`) devem usar letras maiúsculas separadas por underline (ex: `TIMEOUT_MAXIMO`, `TAXA_JUROS_PADRAO`).
* **Pacotes (`lowercase`):** Apenas letras minúsculas, sem hífens ou underscores (ex: `com.empresa.projeto`).

### 3. Organização de Pacotes (Arquitetura em Camadas)
Organize as classes por responsabilidade dentro da estrutura base do projeto (`com.seuprojeto.nome`):

```text
com.seuprojeto.nome/
│
├── config/            # Configurações globais (Spring Security, Beans, WebConfig)
├── controller/        # Endpoints REST (Controllers / Handlers de API)
├── service/           # Regras de negócio e lógica da aplicação
├── repository/        # Camada de persistência (Interfaces que estendem JpaRepository, etc.)
├── model/             # Entidades de banco de dados (@Entity) e objetos de domínio
├── dto/               # Objetos de Transferência de Dados (Request/Response DTOs)
└── exception/         # Exceções personalizadas e Global Exception Handler
```

### 4. Ferramentas de Formatação Automática
Para evitar discussões sobre espaçamento e identação em Code Reviews:
* Utilize a formatação padrão da IDE (IntelliJ IDEA / Eclipse) configurada para seguir o guia de estilo do Java (espaço de 4 colunas para identação, chaves na mesma linha).
* Recomendamos o uso de plugins de linter e formatadores automáticos no build (como **Spotless** ou **Checkstyle**) para validar o padrão antes de commitar.

### 5. Boas Práticas Adicionais em Java
* **Evite NullPointerException (NPE):** Utilize a classe `Optional<T>` para métodos que podem retornar valores nulos e evite retornar `null` em coleções (retorne listas vazias `Collections.emptyList()` em vez de `null`).
* **Tratamento de Exceções:** Não capture exceções genéricas (`catch (Exception e)`) sem necessidade. Crie exceções customizadas quando o negócio exigir e utilize um `@ControllerAdvice` global para centralizar as mensagens de erro da API.
* **Testes Automatizados:** Escreva testes unitários (JUnit 5 + Mockito) para as regras de negócio críticas contidas nos services. Mantenha nomes descritivos para os testes seguindo o padrão: `metodoTestado_cenario_resultadoEsperado`.

---

## 🛠️ Passo a Passo: De Onde e Como Criar uma Nova Branch

Sempre que for iniciar uma nova tarefa, garanta que sua base está atualizada a partir da **`dev`**.

### 1. Atualize sua branch local `dev`
Abra o terminal na pasta do projeto e execute:
```bash
git checkout dev
git pull origin dev
```

### 2. Crie sua nova branch de trabalho
Crie e mude para a sua branch usando um nome descritivo (ex: prefixos `feature/`, `fix/`, `chore/`):
```bash
git checkout -b feature/nome-da-sua-tarefa
```

### 3. Faça seus commits seguindo o padrão
Escreva mensagens de commit utilizando os tipos e emojis corretos:
```bash
git add .
git commit -m "✨ feat(login): adicionar tela de autenticação"
```

---

## 📤 Como Abrir uma Pull Request (PR)

Quando terminar o desenvolvimento e testar o seu código localmente, siga estes passos:

### 1. Envie a branch para o repositório remoto
```bash
git push origin feature/nome-da-sua-tarefa
```

### 2. Abra a Pull Request na plataforma (GitHub/GitLab)
1. Acesse o repositório no navegador.
2. Clique no aviso recente com o botão **"Compare & pull request"**.
3. **Atenção ao destino da PR:**
   * **Base:** `dev` (O seu código deve entrar na `dev` primeiro, e nunca direto na `main` ou `release`).
   * **Compare:** `feature/nome-da-sua-tarefa` (A branch que você acabou de enviar).
4. Preencha o título e a descrição da PR seguindo as orientações do que foi desenvolvido.
5. Solicite a revisão de pelo menos um colega de equipe e clique em **Create pull request**.

---

## ✅ Boas Práticas Gerais

* **Commits limpos:** Mantenha os commits atômicos e use o padrão convencional.
* **Branches atualizadas:** Se a branch `dev` avançar enquanto você trabalha, atualize sua branch para evitar conflitos futuros (`git pull origin dev`).
* **Escopo único:** Evite misturar múltiplos assuntos em uma mesma PR.