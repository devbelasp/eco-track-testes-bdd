# language: pt
@regressivo
Funcionalidade: Contrato das respostas da API
  Como consumidor da API
  Quero que as respostas sempre sigam a estrutura combinada (JSON Schema)
  Para que a minha integração não quebre quando a API mudar

  Cenário: Validar o contrato da coleta agendada
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | PLASTICO           |
      | pesoKg           | 12.5               |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 201
    E que o arquivo de contrato esperado é o "Coleta agendada"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado

  Cenário: Validar o contrato da listagem paginada
    Dado que eu cadastre uma coleta válida
    Quando eu listar as coletas com tamanho de página 5
    Então o status code da resposta deve ser 200
    E que o arquivo de contrato esperado é o "Pagina de coletas"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado

  Cenário: Validar o contrato do erro de coleta não encontrada
    Quando eu buscar a coleta de id "999999999"
    Então o status code da resposta deve ser 404
    E que o arquivo de contrato esperado é o "Erro simples"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado

  Cenário: Validar o contrato do erro de validação dos dados
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | VIDRO              |
      | pesoKg           | 0                  |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 400
    E que o arquivo de contrato esperado é o "Erro de validacao"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado
