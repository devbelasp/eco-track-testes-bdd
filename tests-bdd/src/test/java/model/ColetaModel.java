package model;

import java.math.BigDecimal;

/**
 * Representa uma coleta, tanto no corpo enviado (POST/PUT) quanto na resposta da API.
 * Campos nulos (id e statusDestinacao) nao sao enviados: o Gson ignora nulos por padrao.
 */
public class ColetaModel {

    private Long id;
    private String empresaGeradora;
    private String categoriaResiduo;
    private BigDecimal pesoKg;
    private String dataAgendamento;
    private String statusDestinacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpresaGeradora() { return empresaGeradora; }
    public void setEmpresaGeradora(String empresaGeradora) { this.empresaGeradora = empresaGeradora; }

    public String getCategoriaResiduo() { return categoriaResiduo; }
    public void setCategoriaResiduo(String categoriaResiduo) { this.categoriaResiduo = categoriaResiduo; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public String getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(String dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public String getStatusDestinacao() { return statusDestinacao; }
    public void setStatusDestinacao(String statusDestinacao) { this.statusDestinacao = statusDestinacao; }
}
