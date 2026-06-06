package model;

/**
 *
 * @author vitor
 */

public class Produto {
    private int cod_Produto;
    private String nome;
    private float preco;
    private int Qtdeproduto;
    
    public Produto(){}
    
    public Produto(int cod_Porduo, String nome, float preco, int QtdeProdutow ){
        this.cod_Produto = cod_Produto;
        this.nome = nome;
        this.preco = preco;
        this.Qtdeproduto = Qtdeproduto;
    }
    
    //getters
    public int Getcod_Produto(){
        return cod_Produto;
    }
    public String Getnome(){
        return nome;
    }
    public float Getpreco(){
        return preco;
    }
    public int GetQtdeproduto(){
        return Qtdeproduto;
    }
    
    //setters
    public void Setcod_Produto( int cod_Produto){
            this.cod_Produto = cod_Produto;
    }
    
    public void Setnome(String nome){
        this.nome = nome;
    }
    
    public void Setpreco( float preco){
        this.preco = preco;
    }
    
    public void SetQtdeproduto( int Qtdeproduto){
        this.Qtdeproduto = Qtdeproduto;
    }
    
    @Override
    public String toString() {
    return cod_Produto + ";" + nome + ";" + preco + ";" + Qtdeproduto;
}
 }
