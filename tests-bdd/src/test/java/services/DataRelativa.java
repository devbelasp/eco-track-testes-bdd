package services;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte datas escritas em linguagem natural nos cenarios ("daqui a 7 dias",
 * "10 dias atras", "hoje") em datas reais no formato AAAA-MM-DD.
 * Assim os testes nunca "vencem": a API so aceita data de hoje em diante.
 * Usa UTC para ficar igual ao relogio do container da API.
 */
public final class DataRelativa {

    private static final Pattern DAQUI = Pattern.compile("^daqui a (\\d+) dias?$");
    private static final Pattern ATRAS = Pattern.compile("^(\\d+) dias? atr[aá]s$");

    private DataRelativa() {
    }

    public static String converter(String texto) {
        String t = texto.trim().toLowerCase();
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);

        if (t.equals("hoje")) {
            return hoje.toString();
        }
        Matcher daqui = DAQUI.matcher(t);
        if (daqui.matches()) {
            return hoje.plusDays(Long.parseLong(daqui.group(1))).toString();
        }
        Matcher atras = ATRAS.matcher(t);
        if (atras.matches()) {
            return hoje.minusDays(Long.parseLong(atras.group(1))).toString();
        }
        // Qualquer outro valor (ex.: 2030-01-31 ou um formato invalido de proposito) segue como escrito
        return texto.trim();
    }
}
