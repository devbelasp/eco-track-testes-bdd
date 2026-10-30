package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Teste de contrato: confere se a resposta da API segue o JSON Schema combinado
 * (campos obrigatorios, tipos e valores permitidos). Os schemas ficam em src/test/resources/schemas.
 */
public class ContratoService {

    private static final String PASTA_SCHEMAS = "/schemas/";
    private final ObjectMapper mapper = new ObjectMapper();
    private String arquivoSchema;

    /** Escolhe o contrato pelo nome usado no cenario. Para novo contrato, basta um novo "case". */
    public void selecionar(String contrato) {
        switch (contrato) {
            case "Coleta agendada" -> arquivoSchema = "coleta.json";
            case "Pagina de coletas" -> arquivoSchema = "pagina-coletas.json";
            case "Erro simples" -> arquivoSchema = "erro.json";
            case "Erro de validacao" -> arquivoSchema = "erro-validacao.json";
            default -> throw new IllegalStateException("Contrato nao previsto: " + contrato);
        }
    }

    /** Retorna a lista de violacoes do contrato. Lista vazia = resposta em conformidade. */
    public Set<ValidationMessage> validar(Response resposta) throws IOException {
        if (arquivoSchema == null) {
            throw new IllegalStateException("Nenhum contrato foi selecionado no cenario.");
        }
        JsonSchema schema = JsonSchemaFactory
                .getInstance(SpecVersion.VersionFlag.V7)
                .getSchema(lerSchema(arquivoSchema));
        JsonNode corpo = mapper.readTree(resposta.asString());
        return schema.validate(corpo);
    }

    private String lerSchema(String arquivo) throws IOException {
        try (InputStream entrada = getClass().getResourceAsStream(PASTA_SCHEMAS + arquivo)) {
            if (entrada == null) {
                throw new IOException("Schema nao encontrado no classpath: " + PASTA_SCHEMAS + arquivo);
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
