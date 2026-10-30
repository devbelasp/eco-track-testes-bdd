package steps;

import context.TestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import model.ColetaModel;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Steps das funcionalidades de coleta: cada linha escrita em Gherkin vira um metodo aqui.
 * O estado do cenario (resposta, IDs) fica no TestContext, injetado pelo PicoContainer.
 */
public class ColetaSteps {

    private final TestContext ctx;

    public ColetaSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    // ------------------------------------------------------------------ Dado

    @Dado("que eu tenha os seguintes dados da coleta:")
    public void queEuTenhaOsSeguintesDadosDaColeta(DataTable tabela) {
        ctx.requisicao = new ColetaModel();
        for (Map<String, String> linha : tabela.asMaps(String.class, String.class)) {
            ctx.coletaService.definirCampo(ctx.requisicao, linha.get("campo"), linha.get("valor"));
        }
    }

    /** Usado no Contexto: cria no banco a coleta que o cenario vai consultar, alterar ou cancelar. */
    @Dado("que eu cadastre uma coleta válida")
    public void queEuCadastreUmaColetaValida() {
        ctx.requisicao = ctx.coletaService.coletaValidaPadrao();
        Response resposta = ctx.coletaService.cadastrar("/api/residuos/agendamentos", ctx.requisicao);
        assertEquals("Falha ao cadastrar a coleta do contexto. Resposta: " + resposta.asString(),
                201, resposta.statusCode());
        registrarCriacao(resposta);
    }

    // ---------------------------------------------------------------- Quando

    @Quando("eu enviar a requisição de agendamento para o endpoint {string}")
    public void euEnviarARequisicaoDeAgendamentoParaOEndpoint(String endpoint) {
        ctx.resposta = ctx.coletaService.cadastrar(endpoint, ctx.requisicao);
        if (ctx.resposta.statusCode() == 201) {
            registrarCriacao(ctx.resposta);
        }
    }

    @Quando("eu buscar a coleta cadastrada")
    public void euBuscarAColetaCadastrada() {
        ctx.resposta = ctx.coletaService.buscarPorId(String.valueOf(ctx.idCadastrado));
    }

    @Quando("eu buscar a coleta de id {string}")
    public void euBuscarAColetaDeId(String id) {
        ctx.resposta = ctx.coletaService.buscarPorId(id);
    }

    @Quando("eu listar as coletas com tamanho de página {int}")
    public void euListarAsColetasComTamanhoDePagina(int tamanho) {
        ctx.resposta = ctx.coletaService.listar(tamanho);
    }

    @Quando("eu atualizar a coleta cadastrada com os dados informados")
    public void euAtualizarAColetaCadastradaComOsDadosInformados() {
        ctx.resposta = ctx.coletaService.atualizar(String.valueOf(ctx.idCadastrado), ctx.requisicao);
    }

    @Quando("eu atualizar a coleta de id {string} com os dados informados")
    public void euAtualizarAColetaDeIdComOsDadosInformados(String id) {
        ctx.resposta = ctx.coletaService.atualizar(id, ctx.requisicao);
    }

    @Quando("eu cancelar a coleta cadastrada")
    public void euCancelarAColetaCadastrada() {
        ctx.resposta = ctx.coletaService.cancelar(String.valueOf(ctx.idCadastrado));
    }

    @Quando("eu cancelar a coleta de id {string}")
    public void euCancelarAColetaDeId(String id) {
        ctx.resposta = ctx.coletaService.cancelar(id);
    }

    @Quando("eu consultar a saúde da aplicação")
    public void euConsultarASaudeDaAplicacao() {
        ctx.resposta = ctx.coletaService.consultarSaude();
    }

    // ----------------------------------------------------------------- Então

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        assertEquals("Corpo da resposta: " + ctx.resposta.asString(),
                statusCode, ctx.resposta.statusCode());
    }

    @E("o status de destinação da coleta deve ser {string}")
    public void oStatusDeDestinacaoDaColetaDeveSer(String status) {
        ColetaModel coleta = ctx.coletaService.lerColeta(ctx.resposta);
        assertEquals(status, coleta.getStatusDestinacao());
    }

    @E("os dados da coleta devolvidos devem ser iguais aos informados")
    public void osDadosDaColetaDevolvidosDevemSerIguaisAosInformados() {
        ColetaModel devolvida = ctx.coletaService.lerColeta(ctx.resposta);
        ColetaModel enviada = ctx.requisicao;
        assertNotNull("A resposta deve trazer o id da coleta", devolvida.getId());
        assertEquals(enviada.getEmpresaGeradora(), devolvida.getEmpresaGeradora());
        assertEquals(enviada.getCategoriaResiduo(), devolvida.getCategoriaResiduo());
        assertEquals(0, enviada.getPesoKg().compareTo(devolvida.getPesoKg()));
        assertEquals(enviada.getDataAgendamento(), devolvida.getDataAgendamento());
    }

    @E("o identificador devolvido deve ser o da coleta cadastrada")
    public void oIdentificadorDevolvidoDeveSerODaColetaCadastrada() {
        ColetaModel devolvida = ctx.coletaService.lerColeta(ctx.resposta);
        assertEquals(ctx.idCadastrado, devolvida.getId());
    }

    @E("o corpo de resposta de erro deve retornar a mensagem {string}")
    public void oCorpoDeRespostaDeErroDeveRetornarAMensagem(String mensagem) {
        assertEquals(mensagem, ctx.resposta.jsonPath().getString("erro"));
    }

    @E("o corpo de resposta de erro deve começar com {string}")
    public void oCorpoDeRespostaDeErroDeveComecarCom(String inicio) {
        String erro = ctx.resposta.jsonPath().getString("erro");
        assertNotNull("A resposta deve ter o campo erro", erro);
        assertTrue("Mensagem recebida: " + erro, erro.startsWith(inicio));
    }

    @E("o campo {string} deve retornar a mensagem {string}")
    public void oCampoDeveRetornarAMensagem(String campo, String mensagem) {
        assertEquals(mensagem, ctx.resposta.jsonPath().getString("campos." + campo));
    }

    @E("a lista deve ter no máximo {int} itens")
    public void aListaDeveTerNoMaximoItens(int maximo) {
        List<Object> itens = ctx.resposta.jsonPath().getList("content");
        assertNotNull("A resposta deve ter o campo content", itens);
        assertTrue("Itens recebidos: " + itens.size(), itens.size() <= maximo);
    }

    @E("a lista não deve estar vazia")
    public void aListaNaoDeveEstarVazia() {
        List<Object> itens = ctx.resposta.jsonPath().getList("content");
        assertNotNull("A resposta deve ter o campo content", itens);
        assertFalse("A lista de coletas veio vazia", itens.isEmpty());
    }

    @E("o status da aplicação deve ser {string}")
    public void oStatusDaAplicacaoDeveSer(String status) {
        assertEquals(status, ctx.resposta.jsonPath().getString("status"));
    }

    @E("a coleta cadastrada não deve mais existir")
    public void aColetaCadastradaNaoDeveMaisExistir() {
        Response consulta = ctx.coletaService.buscarPorId(String.valueOf(ctx.idCadastrado));
        assertEquals("A coleta ainda existe depois do cancelamento", 404, consulta.statusCode());
    }

    // ---------------------------------------------------------------- apoio

    /** Guarda o id da coleta criada: serve para consultar depois e para o hook @After apagar. */
    private void registrarCriacao(Response resposta) {
        Long id = ctx.coletaService.lerColeta(resposta).getId();
        ctx.idCadastrado = id;
        ctx.idsParaLimpar.add(id);
    }
}
