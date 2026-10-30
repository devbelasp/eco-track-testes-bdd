package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Execucao rapida: apenas os cenarios marcados com @smoke (o essencial da API).
 * Comando: mvn test -Dtest=SmokeRunner
 * Relatorios: target/cucumber-reports/smoke.html
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hook"},
        tags = "@smoke",
        monochrome = true,
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-reports/smoke.html",
                "json:target/cucumber-reports/smoke.json"
        }
)
public class SmokeRunner {
}
