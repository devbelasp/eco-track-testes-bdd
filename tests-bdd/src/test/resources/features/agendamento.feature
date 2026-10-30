# language: pt
@regressivo
Funcionalidade: Agendamento de coletas de resíduos
  Como empresa geradora de resíduos
  Quero agendar a coleta dos meus resíduos
  Para que eles tenham a destinação correta e rastreável (ESG)

  @smoke
  Cenário: Agendar uma coleta válida
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | PLASTICO           |
      | pesoKg           | 12.5               |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 201
    E os dados da coleta devolvidos devem ser iguais aos informados
    E o status de destinação da coleta deve ser "AGENDADO"

  Esquema do Cenário: Agendar uma coleta para cada categoria de resíduo aceita
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Recicla Mais S/A   |
      | categoriaResiduo | <categoria>        |
      | pesoKg           | 30                 |
      | dataAgendamento  | daqui a 3 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 201
    E o status de destinação da coleta deve ser "AGENDADO"

    Exemplos:
      | categoria       |
      | PLASTICO        |
      | PAPEL_PAPELAO   |
      | LIXO_ELETRONICO |
      | METAIS          |
      | VIDRO           |

  Cenário: Não agendar coleta com peso igual a zero
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | VIDRO              |
      | pesoKg           | 0                  |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro deve retornar a mensagem "Dados inválidos."
    E o campo "pesoKg" deve retornar a mensagem "O peso deve ser um valor maior que zero."

  Cenário: Não agendar coleta com data no passado
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | METAIS             |
      | pesoKg           | 15                 |
      | dataAgendamento  | 10 dias atrás      |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 400
    E o campo "dataAgendamento" deve retornar a mensagem "A data de agendamento não pode estar no passado."

  Cenário: Não agendar coleta com categoria de resíduo inexistente
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | Empresa Verde Ltda |
      | categoriaResiduo | MADEIRA            |
      | pesoKg           | 8                  |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro deve começar com "Corpo da requisição inválido."

  Cenário: Não agendar coleta sem o nome da empresa geradora
    Dado que eu tenha os seguintes dados da coleta:
      | campo            | valor              |
      | empresaGeradora  | [vazio]            |
      | categoriaResiduo | PLASTICO           |
      | pesoKg           | 5                  |
      | dataAgendamento  | daqui a 7 dias     |
    Quando eu enviar a requisição de agendamento para o endpoint "/api/residuos/agendamentos"
    Então o status code da resposta deve ser 400
    E o campo "empresaGeradora" deve retornar a mensagem "O nome da empresa geradora é obrigatório."
