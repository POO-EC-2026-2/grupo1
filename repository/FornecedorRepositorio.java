package repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Fornecedor;

public class FornecedorRepositorio {

    private Dao<Fornecedor, Integer> fornecedorDao;

    public FornecedorRepositorio() throws SQLException {
        fornecedorDao = DaoManager.createDao(Database.getConnection(), Fornecedor.class);
    }

    public Fornecedor salvar(Fornecedor fornecedor) throws SQLException {
        if (fornecedor.getId() == 0) {
            fornecedorDao.create(fornecedor);
        } else if (fornecedorDao.update(fornecedor) != 1) {
            throw new SQLException("Registro removido. Atualize a lista.");
        }
        return fornecedor;
    }

    public void excluir(Fornecedor fornecedor) throws SQLException {
        Database.emTransacao(fornecedorDao.getConnectionSource(), () -> {
            if (fornecedor != null && new ProdutoRepositorio().possuiFornecedor(fornecedor)) {
                throw new SQLException("Fornecedor usado por produtos.");
            }
            if (fornecedor == null || fornecedorDao.deleteById(fornecedor.getId()) != 1) {
                throw new SQLException("Registro inexistente ou ja removido.");
            }
            return null;
        });
    }

    public Fornecedor buscarPorId(int id) throws SQLException {
        return fornecedorDao.queryForId(id);
    }

    public ArrayList<Fornecedor> listar() throws SQLException {
        return new ArrayList<Fornecedor>(fornecedorDao.queryForAll());
    }

    public ArrayList<Fornecedor> buscarPorEmpresa(String texto) throws SQLException {
        ArrayList<Fornecedor> encontrados = new ArrayList<Fornecedor>();
        ArrayList<Fornecedor> fornecedores = listar();
        for (Fornecedor fornecedor : fornecedores) {
            if (fornecedor.getEmpresa().toLowerCase().contains(texto.toLowerCase())) {
                encontrados.add(fornecedor);
            }
        }
        return encontrados;
    }
}
