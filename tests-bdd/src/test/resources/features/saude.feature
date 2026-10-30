# language: pt
@regressivo
Funcionalidade: Saúde da aplicação
  Como equipe de operação
  Quero saber se a API e o banco de dados estão funcionando
  Para detectar problemas antes dos usuários

  @smoke
  Cenário: Verificar que a aplicação está saudável
    Quando eu consultar a saúde da aplicação
    Então o status code da resposta deve ser 200
    E o status da aplicação deve ser "UP"
