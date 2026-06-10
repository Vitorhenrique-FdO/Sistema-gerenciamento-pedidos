package model;

// Classe do item que vai dentro do pedido
public class Item {

    // atributos basicos do item
    private int    cod_pedido; // pra saber de qual pedido eh
    private int    seq_item;   // numero do item no pedido (1, 2, 3...)
    private int    cod_produto;
    private int    qtde_itens;
    private double preco_uniItem; // preco na hora que comprou
    private double preco_total;   // qtd * preco unitario

    // construtor que usamos na hora de ler o csv
    public Item(int cod_pedido, int seq_item, int cod_produto,
                int qtde_itens, double preco_uniItem, double preco_total) {
        this.cod_pedido    = cod_pedido;
        this.seq_item      = seq_item;
        this.cod_produto   = cod_produto;
        this.qtde_itens    = qtde_itens;
        this.preco_uniItem = preco_uniItem;
        this.preco_total   = preco_total;
    }

    // construtor mais facil, que ja calcula o preco total sozinho
    public Item(int cod_pedido, int seq_item, int cod_produto,
                int qtde_itens, double preco_uniItem) {
        this(cod_pedido, seq_item, cod_produto, qtde_itens,
             preco_uniItem, qtde_itens * preco_uniItem);
    }

    // getters e setters
    public int    getCod_pedido()    { return cod_pedido; }
    public int    getSeq_item()      { return seq_item; }
    public int    getCod_produto()   { return cod_produto; }
    public int    getQtde_itens()    { return qtde_itens; }
    public double getPreco_uniItem() { return preco_uniItem; }
    public double getPreco_total()   { return preco_total; }

    // metodos de atalho pro controller usar sem errar o nome
    public int    getCodPedido()   { return cod_pedido; }
    public int    getSeqItem()     { return seq_item; }
    public int    getCodProduto()  { return cod_produto; }
    public int    getQtdeItens()   { return qtde_itens; }
    public double getPrecoUni()    { return preco_uniItem; }
    public double getPrecoTotal()  { return preco_total; }

    public void setCod_pedido(int cod_pedido)       { this.cod_pedido  = cod_pedido; }
    public void setSeq_item(int seq_item)           { this.seq_item    = seq_item; }
    public void setCod_produto(int cod_produto)     { this.cod_produto = cod_produto; }
    
    // quando muda a quantidade, ja calcula o preco total de novo
    public void setQtde_itens(int qtde_itens) {
        this.qtde_itens  = qtde_itens;
        this.preco_total = qtde_itens * this.preco_uniItem;
    }
    public void setQtdeItens(int qtde_itens)        { setQtde_itens(qtde_itens); }
    public void setPreco_uniItem(double preco_uniItem) { this.preco_uniItem = preco_uniItem; }
    public void setPreco_total(double preco_total)  { this.preco_total = preco_total; }

    // pra salvar no arquivo txt/csv
    public String toCSV() {
        return cod_pedido + ";" +
               seq_item   + ";" +
               cod_produto + ";" +
               qtde_itens + ";" +
               String.format("%.2f", preco_uniItem) + ";" +
               String.format("%.2f", preco_total);
    }

    // le a linha do csv e monta o item
    public static Item fromCSV(String linha) {
        try {
            String[] partes = linha.split(";");
            int    codPedido  = Integer.parseInt(partes[0].trim());
            int    seqItem    = Integer.parseInt(partes[1].trim());
            int    codProduto = Integer.parseInt(partes[2].trim());
            int    qtde       = Integer.parseInt(partes[3].trim());
            double precoUni   = Double.parseDouble(partes[4].trim());
            double precoTot   = Double.parseDouble(partes[5].trim());
            return new Item(codPedido, seqItem, codProduto, qtde, precoUni, precoTot);
        } catch (Exception e) {
            System.err.println("linha de item zoada: " + linha);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Item{pedido=" + cod_pedido +
               ", seq="      + seq_item   +
               ", produto="  + cod_produto +
               ", qtde="     + qtde_itens +
               ", precoUni=" + String.format("%.2f", preco_uniItem) +
               ", total="    + String.format("%.2f", preco_total) + "}";
    }
}
