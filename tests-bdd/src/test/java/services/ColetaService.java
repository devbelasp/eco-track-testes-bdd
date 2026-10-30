package services;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.ColetaModel;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;

/**
 * Centraliza todas as chamadas HTTP a API (Rest Assured), para os steps nao repetirem codigo.
 */
public class ColetaService {

    public static final String ENDPOINT = "/api/residuos/agendamentos";
    public static final String HEALTH = "/actuator/health";

    private final String baseUrl = Ambiente.baseUrl();
    private final Gson gson = new Gson();

    /** Preenche um campo do modelo a partir de uma linha da tabela do cenario. */
    public void definirCampo(ColetaModel coleta, String campo, String valor) {
        // "[vazio]" permite escrever um texto em branco na tabela do Gherkin
        String texto = "[vazio]".equals(valor) ? "" : valor;
        switch (campo) {
            case "empresaGeradora" -> coleta.setEmpresaGeradora(texto);
            case "categoriaResiduo" -> coleta.setCategoriaResiduo(texto);
            case "pesoKg" -> coleta.setPesoKg(new BigDecimal(texto));
            case "dataAgendamento" -> coleta.setDataAgendamento(DataRelativa.converter(texto));
            default -> throw new IllegalArgumentException("Campo desconhecido na tabela: " + campo);
        }
    }

    /** Coleta valida padrao, usada quando o cenario so precisa "ter uma coleta cadastrada". */
    public ColetaModel coletaValidaPadrao() {
        ColetaModel coleta = new ColetaModel();
        coleta.setEmpresaGeradora("Empresa Verde Ltda");
        coleta.setCategoriaResiduo("PLASTICO");
        coleta.setPesoKg(new BigDecimal("12.5"));
        coleta.setDataAgendamento(DataRelativa.converter("daqui a 7 dias"));
        return coleta;
    }

    public ColetaModel lerColeta(Response resposta) {
        return gson.fromJson(resposta.asString(), ColetaModel.class);
    }

    // ---- Chamadas HTTP ----

    public Response cadastrar(String endpoint, ColetaModel coleta) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gson.toJson(coleta))
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response buscarPorId(String id) {
        return given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .when()
                .get(ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    public Response listar(int tamanhoPagina) {
        return given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .queryParam("size", tamanhoPagina)
                .queryParam("page", 0)
                .when()
                .get(ENDPOINT)
                .then()
                .extract()
                .response();
    }

    public Response atualizar(String id, ColetaModel coleta) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(gson.toJson(coleta))
                .when()
                .put(ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    public Response cancelar(String id) {
        return given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .when()
                .delete(ENDPOINT + "/" + id)
                .then()
                .extract()
                .response();
    }

    public Response consultarSaude() {
        return given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .when()
                .get(HEALTH)
                .then()
                .extract()
                .response();
    }
}
