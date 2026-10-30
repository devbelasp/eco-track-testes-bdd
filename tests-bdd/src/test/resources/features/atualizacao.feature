# language: pt
@regressivo
Funcionalidade: Atualização de coletas agendadas
  Como empresa geradora de resíduos
  Quero corrigir os dados de uma coleta já agendada
  Para manter o registro sempre correto

  Contexto:
    Dado que eu cadastre uma coleta válida

  Cenário: Atualizar uma coleta existente
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor                  |
      | empresaGeradora  | Metalúrgica Vale Verde |
      | categoriaResiduo | METAIS                 |
      | pesoKg           | 1150.00                |
      | dataAgendamento  | daqui a 14 dias        |
    Quando eu atualizar a coleta cadastrada com os dados informados
    Então o status code da resposta deve ser 200
    E o identificador devolvido deve ser o da coleta cadastrada
    E os dados da coleta devolvidos devem ser iguais aos informados
    E o status de destinação da coleta deve ser "AGENDADO"

  Cenário: Atualizar uma coleta com identificador inexistente
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor                  |
      | empresaGeradora  | Metalúrgica Vale Verde |
      | categoriaResiduo | METAIS                 |
      | pesoKg           | 100                    |
      | dataAgendamento  | daqui a 14 dias        |
    Quando eu atualizar a coleta de id "999999999" com os dados informados
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro deve retornar a mensagem "Agendamento de coleta não existe no banco de dados!"

  Cenário: Não atualizar uma coleta com dados inválidos
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor                  |
      | empresaGeradora  | Metalúrgica Vale Verde |
      | categoriaResiduo | METAIS                 |
      | pesoKg           | -5                     |
      | dataAgendamento  | daqui a 14 dias        |
    Quando eu atualizar a coleta cadastrada com os dados informados
    Então o status code da resposta deve ser 400
    E o campo "pesoKg" deve retornar a mensagem "O peso deve ser um valor maior que zero."
