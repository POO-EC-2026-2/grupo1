package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuarios")
public class Usuario {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String nome;

    @DatabaseField(canBeNull = false, unique = true)
    private String login;

    @DatabaseField(canBeNull = false)
    private String senha;

    @DatabaseField(canBeNull = false)
    private String perfil;

    public Usuario() {
    }

    public Usuario(String nome, String login, String senha, String perfil) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        setPerfil(perfil);
    }

    public boolean podeGerenciarUsuarios() {
        return "GERENTE".equalsIgnoreCase(perfil);
    }

    public boolean podeEstornarMovimentacao() {
        return "GERENTE".equalsIgnoreCase(perfil);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("O perfil deve ser GERENTE ou OPERADOR.");
        }
        String perfilPadronizado = perfil.trim().toUpperCase();
        if (!"GERENTE".equals(perfilPadronizado) && !"OPERADOR".equals(perfilPadronizado)) {
            throw new IllegalArgumentException("O perfil deve ser GERENTE ou OPERADOR.");
        }
        this.perfil = perfilPadronizado;
    }

    @Override
    public String toString() {
        return nome + " (" + perfil + ")";
    }
}
