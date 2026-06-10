package model;

/**
 * Representa um Produto no sistema.
 * Campos:
 *   cod_produto   - código único do produto (PK)
 *   nome          - nome/descrição do produto
 *   preco         - preço unitário de venda
 *   qtde_produto  - quantidade disponível em estoque
 */
public class Produto {

    // ===== ATRIBUTOS =====
    private int    cod_produto;
    private String nome;
    private double preco;
    private int    qtde_produto;

    // ===== CONSTRUTORES =====

    /** Construtor vazio necessário em algumas situações. */
    public Produto() {}

    /** Construtor completo. */
    public Produto(int cod_produto, String nome, double preco, int qtde_produto) {
        this.cod_produto  = cod_produto;
        this.nome         = nome;
        this.preco        = preco;
        this.qtde_produto = qtde_produto;
    }

    // ===== GETTERS E SETTERS =====
    // Padrão Java: getCampo() / setCampo()

    public int    getCod_produto()  { return cod_produto; }
    public String getNome()         { return nome; }
    public double getPreco()        { return preco; }
    public int    getQtde_produto() { return qtde_produto; }

    // Aliases camelCase usados pelo ProdutoController
    public int    getCodProduto()  { return cod_produto; }
    public int    getQtdeProduto() { return qtde_produto; }

    public void setCod_produto(int cod_produto)   { this.cod_produto  = cod_produto; }
    public void setNome(String nome)              { this.nome         = nome; }
    public void setPreco(double preco)            { this.preco        = preco; }
    public void setQtde_produto(int qtde_produto) { this.qtde_produto = qtde_produto; }
    public void setQtdeProduto(int qtde_produto)  { this.qtde_produto = qtde_produto; }

    // ===== SERIALIZAÇÃO CSV =====

    /**
     * Converte para linha CSV.
     * Formato: cod_produto;nome;preco;qtde_produto
     */
    public String toCSV() {
        return cod_produto + ";" + nome + ";" +
               String.format("%.2f", preco) + ";" + qtde_produto;
    }

    /**
     * Cria um Produto a partir de uma linha CSV.
     * Retorna null se a linha for inválida.
     */
    public static Produto fromCSV(String linha) {
        try {
            String[] partes = linha.split(";");
            int    cod   = Integer.parseInt(partes[0].trim());
            String nome  = partes[1].trim();
            double preco = Double.parseDouble(partes[2].trim());
            int    qtde  = Integer.parseInt(partes[3].trim());
            return new Produto(cod, nome, preco, qtde);
        } catch (Exception e) {
            System.err.println("Linha de produto inválida ignorada: " + linha);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Produto{cod=" + cod_produto +
               ", nome='" + nome + '\'' +
               ", preco=" + String.format("%.2f", preco) +
               ", estoque=" + qtde_produto + "}";
    }
}
