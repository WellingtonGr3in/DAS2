# ADR-XXX: [Cloud da Azure]

**Status:**  Aceito  **Data:** [2026-08-27]  |  **Autores:** [Wellington Grein]

## Contexto 

A CorpTech é uma empresa de tecnologia que tem enfrentado desafios relacionados a transparência e controle de dados sobre sua plataforma de ITSM (Jira Service Management). A empresa tem dificuldades em monitorar visualmente a gestão de SLA, backlog e filas, tendências e volume, performance da equipe, e satisfação do cliente. Consequentemente, a CorpTech monta manualmente exportações em CSV e planilhas para ter que apresentar em reuniões semanalmente, tornando-se um processo improdutivo.

## Decisão

Adotar o Microsoft Azure como plataforma do projeto

Usando tecnologias como o Azure Funtions, Azure Data Base, Power BI e o Azure Storage Account 

## Consequências

Postiva: Utilizamos apenas recursos da Microsoft ou seja, integração Facilitada

Negativa: Dificuldade  na configuração das Azure Functions com o JAVA

## Alternativas rejeitadas

Infraestura própria, devido ao ao custo

## Links

[Azure Storage Account ](https://azure.microsoft.com/pt-br)