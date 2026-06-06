
package model;
 

public class Item {
    
    private int cod_pedido;
    private int seq_item;
    private int cod_produto;
    private int qtde_itens;
    private double preco_uniItem;
    private double preco_total;  
    
    // GET E SET 
    public int getCod_pedido() {
        return cod_pedido;
    }

    public void setCod_pedido(int cod_pedido) {
        this.cod_pedido = cod_pedido;
    }

    public int getSeq_item() {
        return seq_item;
    }

    public void setSeq_item(int seq_item) {
        this.seq_item = seq_item;
    }

    public int getCod_produto() {
        return cod_produto;
    }

    public void setCod_produto(int cod_produto) {
        this.cod_produto = cod_produto;
    }

    public int getQtde_itens() {
        return qtde_itens;
    }

    public void setQtde_itens(int qtde_itens) {
        this.qtde_itens = qtde_itens;
    }

    public double getPreco_uniItem() {
        return preco_uniItem;
    }

    public void setPreco_uniItem(double preco_uniItem) {
        this.preco_uniItem = preco_uniItem;
    }

    public double getPreco_total() {
        return preco_total;
    }

    public void setPreco_total(double preco_total) {
        this.preco_total = preco_total;
    }
 
    // CONSTRUTOR ITEM 
    
    public Item(int cod_pedido, int seq_item, int cod_produto, int qtde_itens, double preco_uniItem, double preco_total) {
        this.cod_pedido = cod_pedido;
        this.seq_item = seq_item;
        this.cod_produto = cod_produto;
        this.qtde_itens = qtde_itens;
        this.preco_uniItem = preco_uniItem;
        this.preco_total = preco_total;
    }
    
 
    // Construtor que calcula o preco total direto
    public Item(int codPedido, int seqItem, int codProduto, int qtdeItens, double precoUniItem) {
        this(codPedido, seqItem, codProduto, qtdeItens, precoUniItem,
             qtdeItens * precoUniItem);
    }
 
   
 
   //Converter objeto para CSV
    
    public String toCSV() {
        return cod_pedido + ";" +
               seq_item + ";" +
               cod_produto + ";" +
               qtde_itens + ";" +
               String.format("%.2f", preco_uniItem) + ";" +
               String.format("%.2f", preco_total);
    }
 
 
    
    //Criando item direto de uma linha CSV
    public static Item fromCSV(String linha) {
        String[] partes = linha.split(";");
        int cod_pedido    = Integer.parseInt(partes[0].trim());
        int seq_item      = Integer.parseInt(partes[1].trim());
        int cod_produto   = Integer.parseInt(partes[2].trim());
        int qtde_itens    = Integer.parseInt(partes[3].trim());
        double preco_uni  = Double.parseDouble(partes[4].trim());
        double preco_total  = Double.parseDouble(partes[5].trim());
        return new Item(cod_pedido, seq_item, cod_produto, qtde_itens, preco_uni, preco_total);
    }
 
    @Override
    public String toString() {
        return "Item{pedido=" + codPedido +
               ", seq=" + seqItem +
               ", produto=" + codProduto +
               ", qtde=" + qtdeItens +
               ", precoUni=" + precoUniItem +
               ", total=" + precoTotal + "}";
    }
}
