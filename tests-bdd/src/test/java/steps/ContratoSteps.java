package steps;

import com.networknt.schema.ValidationMessage;
import context.TestContext;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/** Steps do teste de contrato (JSON Schema). */
public class ContratoSteps {

    private final TestContext ctx;

    public ContratoSteps(TestContext ctx) {
        this.ctx = ctx;
    }

    @E("que o arquivo de contrato esperado é o {string}")
    public void queOArquivoDeContratoEsperadoEO(String contrato) {
        ctx.contratoService.selecionar(contrato);
    }

    @Então("a resposta da requisição deve estar em conformidade com o contrato selecionado")
    public void aRespostaDaRequisicaoDeveEstarEmConformidadeComOContratoSelecionado() throws IOException {
        Set<ValidationMessage> violacoes = ctx.contratoService.validar(ctx.resposta);
        assertTrue("A resposta viola o contrato: " + violacoes + " | Corpo: " + ctx.resposta.asString(),
                violacoes.isEmpty());
    }
}
