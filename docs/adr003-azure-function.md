# ADR-003: [Azure Functions ]

**Status:**  Aceito  **Data:** [2026-08-27]  |  **Autores:** [Wellington Grein]

## Contexto 

A CorpTech é uma empresa de tecnologia que tem enfrentado desafios relacionados a transparência e controle de dados sobre sua plataforma de ITSM (Jira Service Management). A empresa tem dificuldades em monitorar visualmente a gestão de SLA, backlog e filas, tendências e volume, performance da equipe, e satisfação do cliente. Consequentemente, a CorpTech monta manualmente exportações em CSV e planilhas para ter que apresentar em reuniões semanalmente, tornando-se um processo improdutivo.

## Decisão

Implentar as Azure Function com Java 21, para as extrações de dados

## Consequências    

Postiva: Extração de dados sendo possível cadastrar com o cron

Negativa: Dificuldade na implementação com Java 21

## Alternativas rejeitadas  



## Links    
    
[Azure Functions](https://learn.microsoft.com/pt-br/azure/azure-functions/functions-overview) ·
