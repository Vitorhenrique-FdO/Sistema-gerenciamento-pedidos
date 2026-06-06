package model;

/**
 *
 * @author vitor
 */

package model;

public class Produto {

    private int codProduto;
    private String nome;
    private float preco;
    private int qtdeProduto;

    public Produto() {}

    public Produto(int codProduto, String nome, float preco, int qtdeProduto) {
        this.codProduto   = codProduto;
        this.nome         = nome;
        this.preco        = preco;
        this.qtdeProduto  = qtdeProduto;
    }

    // Getters
    public int getCodProduto()    { return codProduto; }
    public String getNome()       { return nome; }
    public float getPreco()      
    { return preco; }
    public int getQtdeProduto()   { return qtdeProduto; }

    // Setters 
    public void setNome(String nome)            
    { this.nome = nome; }
    
    public void setPreco(float preco)          
    { this.preco = preco; }
    
    public void setQtdeProduto(int qtdeProduto) 
    { this.qtdeProduto = qtdeProduto; }

    @Override
    public String toString() {
        return codProduto + ";" + nome + ";" + preco + ";" + qtdeProduto;
    }

    public String toCSV() {
        return toString();
    }
}
