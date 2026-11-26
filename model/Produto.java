package model;

import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

@DatabaseTable(tableName = "produtos")
public class Produto {

    private static final DateTimeFormatter FORMATADOR_VALIDADE = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, unique = true)
    private String codigo;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Categoria categoria;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Fornecedor fornecedor;

    @DatabaseField(canBeNull = false)
    private int quantidade;

    @DatabaseField(canBeNull = false)
    private double preco;

    @DatabaseField(canBeNull = false)
    private int estoqueMinimo;

    @DatabaseField(canBeNull = false)
    private String validade;

    public Produto() {
    }

    public Produto(String codigo, String nome, Categoria categoria, Fornecedor fornecedor,
            int quantidade, double preco, int estoqueMinimo, String validade) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.quantidade = quantidade;
        this.preco = preco;
        this.estoqueMinimo = estoqueMinimo;
        this.validade = validade;
    }

    /** Lê um preço em reais com ponto ou vírgula e prefixo monetário opcional. */
    public static double interpretarPreco(String texto) {
        String valor = texto.replace('\u00a0', ' ').strip();
        if (valor.startsWith("R$")) valor = valor.substring(2).strip();
        try {
            double precoLido = new BigDecimal(valor.replace(',', '.')).doubleValue();
            if (precoLido > 0 && Double.isFinite(precoLido)) return precoLido;
        } catch (NumberFormatException e) {
            // O formulário e o Scanner apresentam a mesma orientação.
        }
        throw new NumberFormatException("Informe um preco positivo em reais, por exemplo 1,99.");
    }

    public double calcularValorTotal() {
        return calcularValorTotalDecimal().doubleValue();
    }

    public BigDecimal calcularValorTotalDecimal() {
        return BigDecimal.valueOf(preco).multiply(BigDecimal.valueOf(quantidade));
    }

    public boolean estaVencido() {
        try {
            // A data fica como texto no banco e so e convertida nesta verificacao.
            if (validade == null) return false;
            LocalDate dataValidade = LocalDate.parse(validade, FORMATADOR_VALIDADE);
            return dataValidade.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String getStatusEstoque() {
        if (estaVencido()) {
            return "Vencido";
        }
        if (quantidade <= estoqueMinimo) {
            return "Em Falta";
        }
        if (quantidade <= (long) estoqueMinimo + Math.max(1, estoqueMinimo / 5)) {
            return "Quase em Falta";
        }
        return "Em Estoque";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")";
    }
}
