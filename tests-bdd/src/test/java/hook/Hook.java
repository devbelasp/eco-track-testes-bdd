package hook;

import context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import services.Ambiente;
import services.ColetaService;

/**
 * Hooks: codigo que o Cucumber executa sozinho, sem aparecer nos cenarios.
 * - @BeforeAll: antes de tudo, espera a API (e o PostgreSQL) ficarem prontos.
 * - @After: depois de CADA cenario, apaga do banco real as coletas que ele criou,
 *           deixando o banco limpo e os cenarios independentes.
 */
public class Hook {

    private final TestContext ctx;

    public Hook(TestContext ctx) {
        this.ctx = ctx;
    }

    @BeforeAll
    public static void aguardarApi() throws InterruptedException {
        ColetaService servico = new ColetaService();
        System.out.println("Aguardando a API em " + Ambiente.baseUrl() + " ...");
        for (int tentativa = 1; tentativa <= 30; tentativa++) {
            try {
                if (servico.consultarSaude().statusCode() == 200) {
                    System.out.println("API disponivel.");
                    return;
                }
            } catch (Exception ignorada) {
                // a API ainda esta subindo: tenta de novo
            }
            Thread.sleep(2000);
        }
        throw new IllegalStateException("A API nao respondeu em " + Ambiente.baseUrl()
                + ". Suba a aplicacao antes (docker compose up --build -d).");
    }

    @After
    public void limparDadosCriados() {
        for (Long id : ctx.idsParaLimpar) {
            // 204 = apagada; 404 = o proprio cenario ja tinha cancelado. Os dois estao certos.
            ctx.coletaService.cancelar(String.valueOf(id));
        }
        ctx.idsParaLimpar.clear();
    }

    @AfterAll
    public static void finalizar() {
        System.out.println("Execucao finalizada. Relatorios em target/cucumber-reports.");
    }
}
