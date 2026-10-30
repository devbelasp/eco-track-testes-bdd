package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Suite completa: executa todos os cenarios marcados com @regressivo.
 * Comando: mvn clean test
 * Relatorios: target/cucumber-reports/regressivo.html (e .json / .xml)
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "hook"},
        tags = "@regressivo",
        monochrome = true,
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-reports/regressivo.html",
                "json:target/cucumber-reports/regressivo.json",
                "junit:target/cucumber-reports/regressivo.xml"
        }
)
public class TestRunner {
}
