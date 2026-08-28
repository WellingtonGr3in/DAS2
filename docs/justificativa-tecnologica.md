# Justificativa Tecnológica — Plataforma de Visibilidade ITSM

**Projeto:** Extração ITSM → Azure Cloud → Power BI  
**Cliente:** CorpTech Soluções em TI Ltda.  
**Disciplina:** Tópicos Avançados em Programação — Turma TAPR — 2026/1  

---

## 1. Azure Functions

### O que é e qual é sua função principal?
O Azure Functions é um serviço de computação *serverless* da Microsoft que permite executar blocos de código (funções) sem precisar provisionar ou gerenciar servidores. A ideia central é simples: você define o que o código faz e configura o que dispara sua execução — pode ser um horário agendado, uma requisição HTTP, a chegada de um arquivo no Blob Storage, entre outros. A plataforma cuida de tudo que está por baixo: alocação de recursos, escalabilidade e disponibilidade.

### Por que foi escolhido para este projeto? Qual problema da CorpTech ele resolve?
No contexto deste projeto, a extração de dados da API do Jira Service Management não é uma operação que precisa rodar o tempo todo — ela acontece de 30 em 30 minutos, em horário fixo. Manter um servidor ligado 24 horas só para executar uma tarefa que dura alguns minutos por dia seria um desperdício de custo e de esforço de manutenção.

O Azure Functions resolve exatamente esse problema: configuramos um *timer trigger* com a frequência desejada e a função executa, faz as chamadas à API do JSM, salva os arquivos no Blob Storage e encerra. Enquanto não está rodando, não gera custo de computação. Para a CorpTech, que precisa dessa extração automatizada sem ter uma equipe de infraestrutura dedicada, isso é direto ao ponto.

### Qual seria uma alternativa? Por que foi descartada?
A alternativa mais direta seria usar uma máquina virtual (Azure VM) com um script agendado via *cron*. Tecnicamente funcionaria, mas traria uma série de complicações desnecessárias: a VM precisa ser mantida ligada, atualizada, monitorada e paga — mesmo quando o script não está rodando. Para um processo que executa por minutos por dia, o *overhead* operacional e financeiro de uma VM não faz sentido. O Azure Functions entrega o mesmo resultado com muito menos esforço de gestão.

> Conforme descrito na própria documentação da Microsoft, uma VM do Azure é indicada para cenários onde se precisa de controle total sobre o ambiente de computação — o que não é o caso aqui. Nossa necessidade é executar uma tarefa simples e agendada, não administrar um servidor completo.

**Referências oficiais consultadas:**
* [Azure Functions: Overview](https://learn.microsoft.com/pt-br/azure/azure-functions/functions-overview)
* [Azure Virtual Machines (alternativa descartada)](https://learn.microsoft.com/pt-br/azure/virtual-machines/overview)

---

## 2. Azure Blob Storage

### O que é e qual é sua função principal?
O Azure Blob Storage é o serviço de armazenamento de objetos da Microsoft na nuvem. Ele é otimizado para guardar grandes volumes de dados não estruturados — arquivos de qualquer tipo: JSONs, CSVs, logs, imagens, vídeos e por aí vai. Não tem esquema, não tem tabela, não tem relação: é um repositório de arquivos acessível de qualquer lugar via HTTP/HTTPS ou pelos SDKs da Azure.

### Por que foi escolhido para este projeto? Qual problema da CorpTech ele resolve?
Quando a Azure Function chama a API do JSM e recebe os dados dos chamados, esse retorno vem em JSON bruto — do jeito que a API devolve, sem nenhum tratamento. Antes de qualquer transformação ou cálculo de métricas, esses arquivos precisam ser guardados em algum lugar.

O Blob Storage cumpre esse papel como camada de armazenamento bruto (*raw*). Ele permite organizar os arquivos em contêineres e simular uma estrutura de pastas por data de extração — o que facilita reprocessar um dia específico se algo der errado na transformação. É barato, escala automaticamente e integra nativamente com todos os outros serviços da Azure que usamos na *pipeline*. Para a CorpTech, o benefício prático é ter um "cofre" confiável dos dados originais, independente de qualquer falha posterior no processamento.

### Qual seria uma alternativa? Por que foi descartada?
Uma alternativa seria salvar os arquivos diretamente no Azure SQL Database, pulando a etapa de armazenamento em arquivo. O problema é que o Azure SQL não é projetado para guardar JSON bruto de forma eficiente — ele é um banco relacional, e inserir dados sem tratamento nele exigiria *parsing* na hora da ingestão, sem possibilidade de reprocessar o dado original depois. 

> O Blob Storage como camada intermediária garante que o dado bruto sempre esteja disponível, o que é uma prática importante em qualquer *pipeline* de dados.

**Referências oficiais consultadas:**
* [Azure Blob Storage: Introdução](https://learn.microsoft.com/pt-br/azure/storage/blobs/storage-blobs-introduction)
* [Azure SQL Database (alternativa descartada para esta camada)](https://learn.microsoft.com/pt-br/azure/azure-sql/database/sql-database-paas-overview)

---

## 3. Azure SQL Database

### O que é e qual é sua função principal?
O Azure SQL Database é um banco de dados relacional totalmente gerenciado (PaaS) na nuvem da Microsoft, baseado no mecanismo do SQL Server. A parte "totalmente gerenciado" significa que *backups*, *patches*, atualizações e alta disponibilidade são responsabilidade da plataforma — não do time que está usando. É possível criar tabelas, escrever *queries* SQL, definir relacionamentos e índices exatamente como em um SQL Server tradicional, sem precisar gerenciar a infraestrutura por baixo.

### Por que foi escolhido para este projeto? Qual problema da CorpTech ele resolve?
Depois que os dados brutos do JSM são extraídos e salvos no Blob Storage, a segunda Azure Function os lê, calcula as métricas (MTTR, MTTA, SLA *compliance*, CSAT por segmento) e precisa persistir os resultados em um lugar que o Power BI consiga consultar de forma eficiente.

Um banco relacional é a escolha certa aqui porque os dados de chamados têm uma estrutura clara: um chamado tem um analista, uma categoria, um cliente, um histórico de *status* — são entidades com relacionamentos entre si. O Azure SQL permite modelar isso em tabelas normalizadas, escrever *queries* que cruzam essas entidades e servir os dados para o Power BI com boa *performance*. Para a CorpTech, o resultado é que os *dashboards* não dependem de exportações manuais: o Power BI conecta direto no Azure SQL e os dados já estão calculados e organizados.

### Qual seria uma alternativa? Por que foi descartada?
Uma alternativa seria usar o Azure Cosmos DB, que é um banco NoSQL totalmente gerenciado da Microsoft. Ele seria adequado se os dados tivessem uma estrutura muito variável ou se a prioridade fosse latência extremamente baixa em escala global. Mas os dados de chamados do JSM têm uma estrutura bem definida e previsível, e o Power BI tem integração nativa e madura com SQL.

> Montar as *queries* de KPI em um banco relacional é mais direto e menos sujeito a complexidade desnecessária do que modelar isso em documentos NoSQL. A própria documentação do Cosmos DB indica que ele é mais indicado para aplicações com dados sem esquema fixo, jogos, IoT e cargas de trabalho distribuídas globalmente — um perfil bem diferente do nosso.

**Referências oficiais consultadas:**
* [Azure SQL Database: Visão Geral](https://learn.microsoft.com/pt-br/azure/azure-sql/database/sql-database-paas-overview)
* [Azure Cosmos DB (alternativa descartada)](https://learn.microsoft.com/pt-br/azure/cosmos-db/introduction)

---

## 4. Ferramenta de Visualização — Power BI

### O que é e qual é sua função principal?
O Power BI é a plataforma de análise de negócios da Microsoft. Ele permite conectar fontes de dados, criar relatórios e *dashboards* interativos e compartilhá-los com outras pessoas da organização. É composto basicamente por duas partes: o Power BI Desktop, onde os *dashboards* são desenvolvidos, e o Power BI Service, onde eles são publicados e acessados via navegador com atualização agendada dos dados.

### Por que foi escolhido para este projeto? Qual problema da CorpTech ele resolve?
O problema central da Patrícia (Gerente de TI da CorpTech) é que ela passa tempo toda semana exportando CSVs do JSM manualmente para apresentar em reuniões. O Power BI resolve isso de forma direta: os *dashboards* ficam publicados no Power BI Service, conectados ao Azure SQL, com atualização diária automática. Na manhã da reunião, os dados já estão atualizados — sem exportação, sem planilha, sem trabalho manual.

Além disso, o Power BI tem integração nativa com o Azure SQL — basta apontar para o banco e os dados aparecem. As cinco páginas de *dashboard* que o projeto prevê (Visão Geral, SLA & Breaches, Performance da Equipe, CSAT e Tendências) são tipos de visualização que o Power BI entrega com recursos nativos, sem necessidade de desenvolvimento customizado.

### Por que as alternativas foram descartadas?
* **Metabase:** É uma boa ferramenta de BI *open source*, mas exige uma instância própria rodando em servidor — o que significa mais infraestrutura para manter. Para um projeto que já usa a Azure, adicionar um servidor só para o Metabase não faz sentido. Além disso, a integração dele com o ecossistema Microsoft é menos direta do que a do Power BI.
* **Streamlit:** É uma biblioteca Python para criar aplicações *web* de dados. É excelente para prototipagem rápida e *dashboards* customizados onde se precisa de muita flexibilidade de código. Porém, para o perfil de usuário deste projeto — a liderança da CorpTech, que precisa de um painel gerencial simples e confiável —, o Streamlit exigiria manutenção de código e implantação de uma aplicação *web*, o que aumenta a complexidade sem trazer benefícios reais em comparação ao Power BI.

**Referências oficiais consultadas:**
* [Power BI: Visão Geral](https://learn.microsoft.com/pt-br/power-bi/fundamentals/power-bi-overview)
* [Streamlit (alternativa descartada)](https://docs.streamlit.io)
* [Metabase (alternativa descartada)](https://www.metabase.com/docs/latest/)

## 5. Liguagem de programação — Java
* **Versão:** Java 21.
* **Motivo da escolha:** A equipe tem experiência prévia com Java, e a Azure Functions tem suporte nativo para Java, o que facilita a integração.
Além disso, a robustez e a maturidade do ecossistema Java garantem que as bibliotecas necessárias para consumir APIs REST, interagir e fazer operações com JPA para extração e transformação dos dados.


## 6. Banco de dados — PostgreSQL
* **Versão:** PostgreSQL 17.
* **Motivo da escolha:** O PostgreSQL é um dos únicos banco de dados que a equipe tem experiência prévia, costume de utilizar, além disso é um banco robusto, relacional e por conta do Azure Database for PostgreSQL, oferece uma boa integração com o Function App (Azure Functions).