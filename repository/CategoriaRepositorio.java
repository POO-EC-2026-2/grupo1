package repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Categoria;

public class CategoriaRepositorio {

    private Dao<Categoria, Integer> categoriaDao;

    public CategoriaRepositorio() throws SQLException {
        categoriaDao = DaoManager.createDao(Database.getConnection(), Categoria.class);
    }

    public Categoria salvar(Categoria categoria) throws SQLException {
        if (categoria.getId() == 0) {
            categoriaDao.create(categoria);
        } else if (categoriaDao.update(categoria) != 1) {
            throw new SQLException("Registro removido. Atualize a lista.");
        }
        return categoria;
    }

    public void excluir(Categoria categoria) throws SQLException {
        Database.emTransacao(categoriaDao.getConnectionSource(), () -> {
            if (categoria != null && new ProdutoRepositorio().possuiCategoria(categoria)) {
                throw new SQLException("Categoria usada por produtos.");
            }
            if (categoria == null || categoriaDao.deleteById(categoria.getId()) != 1) {
                throw new SQLException("Registro inexistente ou ja removido.");
            }
            return null;
        });
    }

    public Categoria buscarPorId(int id) throws SQLException {
        return categoriaDao.queryForId(id);
    }

    public ArrayList<Categoria> listar() throws SQLException {
        return new ArrayList<Categoria>(categoriaDao.queryForAll());
    }

    public ArrayList<Categoria> buscarPorNome(String texto) throws SQLException {
        ArrayList<Categoria> encontradas = new ArrayList<Categoria>();
        ArrayList<Categoria> categorias = listar();
        for (Categoria categoria : categorias) {
            if (categoria.getNome().toLowerCase().contains(texto.toLowerCase())) {
                encontradas.add(categoria);
            }
        }
        return encontradas;
    }
}
