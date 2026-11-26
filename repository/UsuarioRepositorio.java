package repository;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Gerente;
import model.Operador;
import model.Usuario;

public class UsuarioRepositorio {

    private Dao<Usuario, Integer> usuarioDao;

    public UsuarioRepositorio() throws SQLException {
        usuarioDao = DaoManager.createDao(Database.getConnection(), Usuario.class);
    }

    public Usuario salvar(Usuario usuario) throws SQLException {
        if (usuario.getId() == 0) {
            usuarioDao.create(usuario);
        } else if (usuarioDao.update(usuario) != 1) {
            throw new SQLException("Registro removido. Atualize a lista.");
        }
        return usuario;
    }

    public long contarGerentes() throws SQLException {
        return usuarioDao.queryBuilder().where().eq("perfil", "GERENTE").countOf();
    }

    public void excluir(Usuario usuario) throws SQLException {
        Database.emTransacao(usuarioDao.getConnectionSource(), () -> {
            if (usuario != null && new MovimentacaoRepositorio().possuiUsuario(usuario.getId())) {
                throw new SQLException("Usuario possui movimentacoes.");
            }
            if (usuario == null || usuarioDao.deleteById(usuario.getId()) != 1) {
                throw new SQLException("Registro inexistente ou ja removido.");
            }
            return null;
        });
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return criarUsuarioPolimorfico(usuarioDao.queryForId(id));
    }

    public Usuario buscarPorLogin(String login) throws SQLException {
        Usuario encontrado = null;
        for (Usuario usuario : usuarioDao.queryForAll()) {
            if (usuario.getLogin().equalsIgnoreCase(login)) {
                if (encontrado != null) throw new SQLException("Login ambiguo no banco. Revise os cadastros.");
                encontrado = usuario;
            }
        }
        return criarUsuarioPolimorfico(encontrado);
    }

    public ArrayList<Usuario> listar() throws SQLException {
        ArrayList<Usuario> usuariosDoBanco = new ArrayList<Usuario>(usuarioDao.queryForAll());
        ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
        for (Usuario usuario : usuariosDoBanco) {
            usuarios.add(criarUsuarioPolimorfico(usuario));
        }
        return usuarios;
    }

    public ArrayList<Usuario> buscarPorNome(String texto) throws SQLException {
        ArrayList<Usuario> encontrados = new ArrayList<Usuario>();
        ArrayList<Usuario> usuarios = listar();
        for (Usuario usuario : usuarios) {
            if (usuario.getNome().toLowerCase().contains(texto.toLowerCase())) {
                encontrados.add(usuario);
            }
        }
        return encontrados;
    }

    private Usuario criarUsuarioPolimorfico(Usuario usuario) throws SQLException {
        if (usuario == null) {
            return null;
        }
        Usuario usuarioPolimorfico;
        if ("GERENTE".equalsIgnoreCase(usuario.getPerfil())) {
            usuarioPolimorfico = new Gerente(usuario.getNome(), usuario.getLogin(), usuario.getSenha());
        } else if ("OPERADOR".equalsIgnoreCase(usuario.getPerfil())) {
            usuarioPolimorfico = new Operador(usuario.getNome(), usuario.getLogin(), usuario.getSenha());
        } else {
            throw new SQLException("Perfil armazenado invalido. Revise o cadastro.");
        }
        usuarioPolimorfico.setId(usuario.getId());
        return usuarioPolimorfico;
    }
}
