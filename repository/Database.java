package repository;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import java.nio.file.Files;
import com.j256.ormlite.misc.TransactionManager;
import java.io.IOException;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import model.Categoria;
import model.Fornecedor;
import model.Movimentacao;
import model.Produto;
import model.Usuario;

public class Database {

    private static final String URL_PADRAO = "jdbc:sqlite:alphafield.db";
    private static String urlBanco = URL_PADRAO;
    private static ConnectionSource connectionSource;
    private static String urlTeste;

    public static synchronized ConnectionSource getConnection() throws SQLException {
        if (connectionSource == null) {
            String separador = urlBanco.contains("?") ? "&" : "?";
            ConnectionSource nova = new JdbcPooledConnectionSource(urlBanco + separador + "foreign_keys=on");
            try {
                // As tabelas principais precisam existir antes das chaves estrangeiras.
                TableUtils.createTableIfNotExists(nova, Categoria.class);
                TableUtils.createTableIfNotExists(nova, Fornecedor.class);
                TableUtils.createTableIfNotExists(nova, Usuario.class);
                TableUtils.createTableIfNotExists(nova, Produto.class);
                TableUtils.createTableIfNotExists(nova, Movimentacao.class);
                connectionSource = nova;
            } catch (SQLException e) {
                nova.closeQuietly();
                throw e;
            }
        }
        return connectionSource;
    }

    public static synchronized void configurarBanco(String novaUrl) throws SQLException {
        if (novaUrl == null || !novaUrl.strip().startsWith("jdbc:sqlite:")
                || novaUrl.strip().length() == "jdbc:sqlite:".length()) {
            throw new SQLException("Informe a URL do banco SQLite.");
        }
        fecharConexao();
        urlBanco = novaUrl.strip();
    }

    public static synchronized void usarBancoTeste() throws SQLException {
        try {
            urlTeste = "jdbc:sqlite:" + Files.createTempDirectory("alphafield-qa-").resolve("teste.db");
            configurarBanco(urlTeste);
        } catch (IOException e) {
            throw new SQLException("Nao foi possivel criar a base descartavel.", e);
        }
    }

    public static synchronized String getUrlBanco() {
        return urlBanco;
    }

    public static synchronized String getNomeBanco() {
        int ultimaBarra = Math.max(urlBanco.lastIndexOf(':'), Math.max(urlBanco.lastIndexOf('/'), urlBanco.lastIndexOf((char) 92)));
        if (ultimaBarra >= 0 && ultimaBarra < urlBanco.length() - 1) {
            return urlBanco.substring(ultimaBarra + 1);
        }
        return "alphafield.db";
    }

    public static synchronized void limparBancoAtual() throws SQLException {
        if (!urlBanco.equals(urlTeste)) {
            throw new SQLException("Limpeza permitida apenas na base descartavel desta execucao.");
        }
        ConnectionSource conexao = getConnection();
        TransactionManager.callInTransaction(conexao, () -> {
            // A ordem evita problemas com as chaves estrangeiras do historico.
            TableUtils.clearTable(conexao, Movimentacao.class);
            TableUtils.clearTable(conexao, Produto.class);
            TableUtils.clearTable(conexao, Usuario.class);
            TableUtils.clearTable(conexao, Categoria.class);
            TableUtils.clearTable(conexao, Fornecedor.class);
            return null;
        });
    }

    public static synchronized void fecharConexao() throws SQLException {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (Exception e) {
                throw new SQLException("Nao foi possivel fechar a conexao atual.", e);
            }
            connectionSource = null;
        }
    }
}
