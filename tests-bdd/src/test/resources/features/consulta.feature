# language: pt
@regressivo
Funcionalidade: Consulta de coletas agendadas
  Como empresa geradora de resíduos
  Quero consultar as coletas agendadas
  Para acompanhar a rastreabilidade dos resíduos

  # O Contexto roda antes de CADA cenário deste arquivo: cria a coleta que será consultada.
  Contexto:
    Dado que eu cadastre uma coleta válida

  @smoke
  Cenário: Buscar uma coleta existente pelo identificador
    Quando eu buscar a coleta cadastrada
    Então o status code da resposta deve ser 200
    E o identificador devolvido deve ser o da coleta cadastrada
    E os dados da coleta devolvidos devem ser iguais aos informados
    E o status de destinação da coleta deve ser "AGENDADO"

  Cenário: Buscar uma coleta com identificador inexistente
    Quando eu buscar a coleta de id "999999999"
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro deve retornar a mensagem "Agendamento de coleta não existe no banco de dados!"

  Cenário: Buscar uma coleta com identificador que não é um número
    Quando eu buscar a coleta de id "abc"
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro deve retornar a mensagem "O parâmetro 'id' possui um valor inválido."

  @smoke
  Cenário: Listar as coletas com paginação
    Quando eu listar as coletas com tamanho de página 5
    Então o status code da resposta deve ser 200
    E a lista não deve estar vazia
    E a lista deve ter no máximo 5 itens
