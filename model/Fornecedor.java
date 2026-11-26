package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fornecedores")
public class Fornecedor {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String empresa;

    @DatabaseField(canBeNull = false, unique = true)
    private String cnpj;

    @DatabaseField
    private String telefone;

    public Fornecedor() {
    }

    public Fornecedor(String empresa, String cnpj, String telefone) {
        this.empresa = empresa;
        this.cnpj = cnpj;
        this.telefone = telefone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return empresa;
    }
}
