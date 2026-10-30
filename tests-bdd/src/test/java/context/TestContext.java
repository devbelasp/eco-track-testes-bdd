package context;

import io.restassured.response.Response;
import model.ColetaModel;
import services.ColetaService;
import services.ContratoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado compartilhado entre os steps e os hooks de UM cenario.
 * O PicoContainer cria uma instancia nova a cada cenario, entao um cenario nunca enxerga
 * os dados do outro (isso garante cenarios independentes).
 */
public class TestContext {

    public final ColetaService coletaService = new ColetaService();
    public final ContratoService contratoService = new ContratoService();

    /** Dados que serao enviados na proxima requisicao (POST/PUT). */
    public ColetaModel requisicao = new ColetaModel();

    /** Ultima resposta recebida da API. */
    public Response resposta;

    /** ID da coleta cadastrada no cenario (contexto), quando houver. */
    public Long idCadastrado;

    /** Todos os IDs criados no cenario: o hook @After apaga cada um do banco. */
    public final List<Long> idsParaLimpar = new ArrayList<>();
}
