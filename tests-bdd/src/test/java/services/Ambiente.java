package services;

/**
 * Configuracao do ambiente de teste. Nada fica fixo no codigo:
 * a URL da API vem da variavel de ambiente BASE_URL (ou da propriedade -Dbase.url).
 * Padrao: http://localhost:8080
 */
public final class Ambiente {

    private Ambiente() {
    }

    public static String baseUrl() {
        String valor = System.getProperty("base.url");
        if (valor == null || valor.isBlank()) {
            valor = System.getenv("BASE_URL");
        }
        if (valor == null || valor.isBlank()) {
            valor = "http://localhost:8080";
        }
        return valor.endsWith("/") ? valor.substring(0, valor.length() - 1) : valor;
    }
}
