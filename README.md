<style>
  :root {
    --primary-blue: #5A9BD4;
  }
  h1, h2, h3 {
    color: var(--primary-blue);
  }
</style>

<h1>Simulador de Sistema de Arquivos com Journaling</h2>

<h2>Metodologia</h2>

O simulador foi desenvolvido utilizando a linguagem Java. Ele recebe chamadas de métodos com os devidos parâmetros, e executa funcionalidades similares às de um sistema operacional (SO).

Cada comando do SO foi implementado como um método específico no código. O simulador exibe o resultado das operações na tela sempre que necessário.

<h2>Parte 1: Introdução ao Sistema de Arquivos com Journaling</h2>

<h3>O que é um Sistema de Arquivos?</h3>

Um sistema de arquivos é responsável por organizar, armazenar e recuperar dados em dispositivos de armazenamento, como HDs e SSDs. Ele fornece a estrutura lógica para nomear, armazenar e acessar arquivos e diretórios.

<h3>Journaling</h3>

O journaling é uma técnica usada para garantir a integridade do sistema de arquivos em caso de falhas. Ele mantém um **log** das operações antes que sejam aplicadas ao disco.

Tipos comuns de journaling:

- **Write-ahead logging (WAL):** Registra todas as operações antes da execução.
- **Log-structured file system (LFS):** Todas as modificações são tratadas como entradas em um log contínuo.

No nosso trabalho, usamos uma versão simplificado do **LFS**. Todas as operações de sucesso e falha foram registradas no arquivo journal.txt que fica dentro da pasta _data_ na raiz do projeto.

<h2>Parte 2: Arquitetura do Simulador</h2>

<h3>Estruturas de Dados</h3>

Nosso sistema de arquivos é baseado em árvore de entradas (arquvios e diretórios). Cada objeto **_Directory_** possui dois HashMaps, um para seus subdiretórios e outro para seus arquivos. Como toda entrada possui um nome único, fica fácil e rápido de armazenar e recuperar os objetos das Maps.

O motivo de termos escolhi a abordagem de árvore com uso de Maps foi pela facilidade de percorrer as árvores. Outro motivo foi a facilidade na hora da persistência dos dados. Utilizamos as classes **_ObjectOutputStream e ObjectInputStream_** do Java para fazer a persistência e a recuperação dos dados, respectivamente. Então basta termos a referência do ojeto root para salvarmos tudo, pois a **_ObjectOutputStream_** percorre, de maneira recursiva, todos os objetos filhos (os Maps no nosso caso) e faz a persistêna. No caso do carregamento, acontece o mesmo, mas ao contrário, retornando um único objeto, o diretório root.

O sistema possui 2 estrutura de dados principais criadas por nós, o **_Directory_** e o **_File_**, que representam um diretório e arquivo, respectivamente. Ambos herdam da classes abstrata **_FileSystemEntry_**, que possui os métodos e atributos padrões de ambos os tipos de entrada do sistema de arquivos. Para um melhor detalhamento ver o item **Parte 3: Implementação em Java**, onde as classes são explicadas com mais detalhes.

<h3>Journaling</h3>

O journaling é gerenciado pela classe **_Journal_**, que mantém um log de todas as operações, mostrando sucessos e fracassos delas.

O log segue o seguinte modelo:

```
Hora                 Comando   Status     Path               Info
----                 -------   ----       -----              ----
02/06/2025  22:47    ls        SUCCESS    root/              listagem da pasta
02/06/2025  22:48    mkdir     CREATED    root/pasta_1       diretório criado
02/06/2025  22:48    mkdir     CREATED    root/pasta_2       diretório criado
02/06/2025  22:48    ls        SUCCESS    root/              listagem da pasta
02/06/2025  22:49    rnd       FAILED     root/facul         não existe arquivo com nome: facul
```

<h2>Parte 3: Implementação em Java</h2>

Nosso projeto foi desenvolvido com o Princípio da Responsabilidade Única (SRP) em mente. Cada classe possui um propósito bem definido e conciso.

<h3>Classes princiáis</h3>

- **FileSystemEntry:**

  Classe abstrata base que representa uma entrada no sistema de arquivos (arquivo ou diretório). Ela armazena nome, caminho, timestamps de criação e modificação, e o tipo da entrada. Também fornece métodos para manipulação e validação de nomes, além de gerar o caminho completo da entrada.

- **Directory:**

  Classe que representa um diretório no sistema de arquivos. Ela armazena subdiretórios e arquivos em HashMaps, fornecendo métodos para insersão e remoção.

- **File:**

  Classe que representa um arquivo no sistema de arquivos.

- **FileSystemSimulator:**

  O coração do projeto. É ela quem faz todo o gerenciamento. Armazena uma referênncia para o diretório root e uma do diretório atual (para onde o usuário navegou). Sempre que o comando **_cd_** é executado com sucesso, o **_currentDir_** é atualizado.
  Possui métodos para inicializar e finalizar o sistema, salvando ao sair e carregando ao inicializar. A persistência é feita no arquivo root.so.

  O método **_processCommand_** interpreta e executa comandos:

  ```
  mkd <dir>           Criar uma nova pasta
  mkf <file>          Criar um novo arquivo
  ls                  Listar o conteúdo de uma pasta
  rmf <arquivo>       Remover um arquivo
  rmd <dir>           Remover uma pasta
  cd <dir>            Ir para outro diretório
  pwd                 Mostrar o caminho atual
  help                Mostrar esta ajuda
  dmesg               Mostrar mensagens do sistema
  sysctl              Exibir informações do sistema
  cls                 Limpar o terminal
  cpf <orig> <dest>   Copiar um arquivo
  cpd <orig> <dest>   Copiar uma pasta
  rnf <orig> <novo>   Renomear um arquivo
  rnd <orig> <novo>   Renomear uma pasta
  exit                Sair do sistema
  ```

<h3>Outras classes</h3>

- **PersistanceManager:** Classe estática que lida com o salvamento e carregamento do arquvio root.so.
- **PrintManager:** Classe estática que possi métodos que _printam_ algo no terminal.
- **Utils:** Classe estática que possui métodos de uso geral.

<h2>Parte 4: Instalação e Funcionamento:</h2>

<h3>Requisitos:</h3>

- Java 21 ou superior
- IDE com suporte à Java (utilizamos o VSCode)

<h3>Passos para execução:</h3>

Utilizamos o Maven como Builder, mas sem utilização de biblioteca externa. Não é preciso instalar nada além do Java. Mas por garantia, rodar o comando **_mvn install_**.

Agora basta rodas o projeto na sua IDE.

<h1>Lik do repositório</h1>

https://github.com/gustavonogvi/simulador-sistema-arquivos
