<p style="color:#ffd55b;font-size:30px;text-align:center">Título: Simulador de Sistema de Arquivos</p>

# Resumo:

Este trabalho propõe o desenvolvimento de um simulador para compreender um sistema de arquivos

# Introdução:

O gerenciamento eficiente de arquivos é crucial para o funcionamento dos sistemas operacionais.

Para isso entender como é montado e organizado um sistema é a base para a compreensão dos sistemas operacionais.

# Objetivo:

Desenvolver um simulador de sistema de arquivos em Java que implemente funcionalidades básicas de manipulação de arquivos e diretórios, com suporte a Journaling para garantir a integridade dos dados. Este simulador deverá permitir a criação de um arquivo que simule o sistema de arquivos e realizar operações como copiar, apagar, renomear arquivos e diretórios, bem como listar o conteúdo de um diretório.

# Atenção aos detalhes do simulador:

- Linguagem de Programação: Java.

- Operações a serem implementadas:

  1. Copiar arquivos

  2. Apagar arquivos

  3. Renomear arquivos

  4. Criar diretórios

  5. Apagar diretórios

  6. Renomear diretórios

  7. Listar arquivos de um diretório

## DICA

> Usem:
>
> - chamadas de médodos para realizar os comandos. Ex: Em cada execução realiza uma tarefa.
> - Modo Avançado - Criar o programa em um modo Shell, onde o sistema roda em um arquivo no formato journaling.

# Estrutura do relatório

**ATENÇÃO!!! Façam esse como README no formato Markdown na primeira página do projeto no GITHUB**

## Metodologia:

O simulador será desenvolvido em linguagem de programação Java. Ele receberá as chamadas de métodos com os devido parâmetros. Em seguida, serão implementados os métodos correspondentes aos comandos de um SO.

O programa executará cada funcionalidade e exibirá o resultado na tela quando necessário.

### Parte 1: Introdução ao Sistema de Arquivos com Journaling

Descrição do sistema de arquivos: Explique o que é um sistema de arquivos, sua importância e o conceito de journaling.

Journaling: Descreva o propósito e funcionamento do Journaling em sistemas de arquivos, incluindo os tipos de journaling (write-ahead logging, log-structured, etc.).

### Parte 2: Arquitetura do Simulador

Estrutura de Dados: Descreva as estruturas de dados utilizadas para representar o sistema de arquivos. Utilizaremos classes Java para representar arquivos, diretórios e o sistema de arquivos em si.

Journaling: Explique como o journaling será implementado, incluindo a estrutura do log e as operações registradas.

### Parte 3: Implementação em Java

Classe "FileSystemSimulator": Implementa o simulador do sistema de arquivos, incluindo métodos para cada operação.

Classes File e Directory: Representam arquivos e diretórios.

Classe Journal: Gerencia o log de operações.

### Parte 4: Instalação e funcionamento\*\*

Aqui indiquem um passo a passo sobre os recursos usados na implementação e orientações sobre a execução do simulador.

<p style="color: #ef4540;">NÃO ESQUEÇAM DE COLOCAR O LINK DO GITHUB - Esse README deve ser impresso no formato PDF para ser enviado pelo AVA.</p>

# Resultados Esperados:

Espera-se que o simulador forneça insights sobre o funcionamento de um sistema de arquivos. Com base nos resultados obtidos, poderemos avaliar e entender como funciona esse elemento de um SO.

A entrega no AVA deve ser um relatório no formato PDF, IMPRIMIR O README NO FORMATO e não esqueçam de colocar um LINK para o projeto no GITHUB

A atividade deve ser feita em DUPLA e vale 2 pontos.
