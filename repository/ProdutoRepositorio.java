package repository;

import java.math.BigDecimal;
import java.util.Locale;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Categoria;
import model.Fornecedor;
import model.Produto;

public class ProdutoRepositorio {

    private Dao<Produto, Integer> produtoDao;

    public ProdutoRepositorio() throws SQLException {
        produtoDao = DaoManager.createDao(Database.getConnection(), Produto.class);
    }

    public Produto salvar(Produto produto) throws SQLException {
        return Database.emTransacao(produtoDao.getConnectionSource(), () -> {
            if (produto.getCategoria() == null
                    || new CategoriaRepositorio().buscarPorId(produto.getCategoria().getId()) == null
                    || produto.getFornecedor() == null
                    || new FornecedorRepositorio().buscarPorId(produto.getFornecedor().getId()) == null) {
                throw new SQLException("Categoria ou fornecedor inexistente.");
            }
            if (produto.getId() == 0) {
                produtoDao.create(produto);
            } else if (produtoDao.update(produto) != 1) {
                throw new SQLException("Registro removido. Atualize a lista.");
            }
            return produto;
        });
    }

    public void excluir(Produto produto) throws SQLException {
        Database.emTransacao(produtoDao.getConnectionSource(), () -> {
            if (produto != null && !new MovimentacaoRepositorio().listarPorProduto(produto).isEmpty()) {
                throw new SQLException("Produto possui movimentacoes.");
            }
            if (produto == null || produtoDao.deleteById(produto.getId()) != 1) {
                throw new SQLException("Registro inexistente ou ja removido.");
            }
            return null;
        });
    }

    public Produto buscarPorId(int id) throws SQLException {
        return produtoDao.queryForId(id);
    }

    public Produto buscarPorCodigo(String codigo) throws SQLException {
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            if (produto.getCodigo().equalsIgnoreCase(codigo)) {
                return produto;
            }
        }
        return null;
    }

    public ArrayList<Produto> listar() throws SQLException {
        return new ArrayList<Produto>(produtoDao.queryForAll());
    }

    public ArrayList<Produto> buscarPorNome(String texto) throws SQLException {
        String termo = texto == null ? "" : texto.strip().toLowerCase(Locale.ROOT);
        ArrayList<Produto> encontrados = new ArrayList<Produto>();
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            String nome = produto.getNome().toLowerCase(Locale.ROOT);
            String codigo = produto.getCodigo().toLowerCase(Locale.ROOT);
            if (nome.contains(termo) || codigo.contains(termo)) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    public ArrayList<Produto> listarEmFalta() throws SQLException {
        ArrayList<Produto> encontrados = new ArrayList<Produto>();
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            if (produto.getQuantidade() <= produto.getEstoqueMinimo()) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    public ArrayList<Produto> listarVencidos() throws SQLException {
        ArrayList<Produto> encontrados = new ArrayList<Produto>();
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            if (produto.estaVencido()) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    public boolean possuiCategoria(Categoria categoria) throws SQLException {
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            if (produto.getCategoria() != null && produto.getCategoria().getId() == categoria.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean possuiFornecedor(Fornecedor fornecedor) throws SQLException {
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            if (produto.getFornecedor() != null && produto.getFornecedor().getId() == fornecedor.getId()) {
                return true;
            }
        }
        return false;
    }

    public double calcularValorTotalEstoque() throws SQLException {
        double total = calcularValorTotalEstoqueDecimal().doubleValue();
        if (!Double.isFinite(total)) {
            throw new SQLException("Valor do estoque excede a faixa de double. Consulte o total decimal.");
        }
        return total;
    }

    public BigDecimal calcularValorTotalEstoqueDecimal() throws SQLException {
        BigDecimal total = BigDecimal.ZERO;
        ArrayList<Produto> produtos = listar();
        for (Produto produto : produtos) {
            total = total.add(produto.calcularValorTotalDecimal());
        }
        return total;
    }

    public int calcularUnidadesEstoque() throws SQLException {
        long total = calcularUnidadesEstoqueLong();
        if (total > Integer.MAX_VALUE) throw new SQLException("Total de unidades excede o limite inteiro.");
        return (int) total;
    }

    public long calcularUnidadesEstoqueLong() throws SQLException {
        long total = 0;
        for (Produto produto : listar()) total += produto.getQuantidade();
        return total;
    }
}
