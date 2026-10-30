# language: pt
@regressivo
Funcionalidade: Cancelamento de coletas agendadas
  Como empresa geradora de resíduos
  Quero cancelar um agendamento
  Para liberar a agenda quando a coleta não for mais necessária

  Contexto:
    Dado que eu cadastre uma coleta válida

  Cenário: Cancelar uma coleta existente
    Quando eu cancelar a coleta cadastrada
    Então o status code da resposta deve ser 204
    E a coleta cadastrada não deve mais existir

  Cenário: Cancelar uma coleta com identificador inexistente
    Quando eu cancelar a coleta de id "999999999"
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro deve retornar a mensagem "Agendamento de coleta não existe no banco de dados!"
