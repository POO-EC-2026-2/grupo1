package repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Movimentacao;
import model.Produto;

public class MovimentacaoRepositorio {

    private Dao<Movimentacao, Integer> movimentacaoDao;

    public MovimentacaoRepositorio() throws SQLException {
        movimentacaoDao = DaoManager.createDao(Database.getConnection(), Movimentacao.class);
    }

    public Movimentacao salvar(Movimentacao movimentacao) throws SQLException {
        return Database.emTransacao(movimentacaoDao.getConnectionSource(), () -> {
            if (movimentacao.getProduto() == null
                    || new ProdutoRepositorio().buscarPorId(movimentacao.getProduto().getId()) == null
                    || movimentacao.getUsuarioResponsavel() == null
                    || new UsuarioRepositorio().buscarPorId(movimentacao.getUsuarioResponsavel().getId()) == null) {
                throw new SQLException("Produto ou usuario inexistente.");
            }
            if (movimentacao.getId() == 0) {
                movimentacaoDao.create(movimentacao);
            } else if (movimentacaoDao.update(movimentacao) != 1) {
                throw new SQLException("Registro removido. Atualize a lista.");
            }
            return movimentacao;
        });
    }

    public boolean possuiUsuario(int id) throws SQLException {
        return movimentacaoDao.queryBuilder().where().eq("usuarioResponsavel_id", id).countOf() > 0;
    }

    public void excluir(Movimentacao movimentacao) throws SQLException {
        if (movimentacao == null || movimentacaoDao.deleteById(movimentacao.getId()) != 1) {
            throw new SQLException("Registro inexistente ou ja removido.");
        }
    }

    public Movimentacao buscarPorId(int id) throws SQLException {
        return movimentacaoDao.queryForId(id);
    }

    public ArrayList<Movimentacao> listar() throws SQLException {
        return new ArrayList<Movimentacao>(movimentacaoDao.queryForAll());
    }

    public ArrayList<Movimentacao> listarPorProduto(Produto produto) throws SQLException {
        ArrayList<Movimentacao> encontradas = new ArrayList<Movimentacao>();
        ArrayList<Movimentacao> movimentacoes = listar();
        for (Movimentacao movimentacao : movimentacoes) {
            if (movimentacao.getProduto() != null && movimentacao.getProduto().getId() == produto.getId()) {
                encontradas.add(movimentacao);
            }
        }
        return encontradas;
    }
}
