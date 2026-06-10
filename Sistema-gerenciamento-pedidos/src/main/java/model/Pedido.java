package model;


public class Pedido {
      //CLASSES 
        
    int cod_pedido;
    int id_cliente;
    String dt_pedido; 
    String dt_entrega;
    float vlr_total;

    //GETTERS E SETTERS
    
    public int getCod_pedido() {
        return cod_pedido;
    }

    public void setCod_pedido(int cod_pedido) {
        this.cod_pedido = cod_pedido;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public date getDt_pedido() {
        return dt_pedido;
    }

    public void setDt_pedido(date dt_pedido) {
        this.dt_pedido = dt_pedido;
    }

    public date getDt_entrega() {
        return dt_entrega;
    }

    public void setDt_entrega(date dt_entrega) {
        this.dt_entrega = dt_entrega;
    }

    public float getVlr_total() {
        return vlr_total;
    }

    public void setVlr_total(float vlr_total) {
        this.vlr_total = vlr_total;
    }
    
    //ATRIBUTOS GENERICOS 
    
    //CONSTRUTORES 
    
    public Pedido(int cod_pedido, int id_cliente, String dt_pedido, String dt_entrega, double vlr_total) {
        this.cod_pedido = cod_pedido;
        this.id_cliente = id_cliente;
        this.dt_pedido  = (dt_pedido  != null) ? dt_pedido.trim()  : "";
        this.dt_entrega = (dt_entrega != null) ? dt_entrega.trim() : "";
        this.vlr_total = vlr_total;
    }
    
    //Criando pedido ja para uma linha CSV
    public incluirPedido(int cod_produto, String nome, float preco, int qtde_produto){
        
        public static Pedido fromCSV(String linha) {
        String[] partes = linha.split(";", -1); // como nao ignorar campos vazios? com -1
        int codPedido   = Integer.parseInt(partes[0].trim());
        int idCliente   = Integer.parseInt(partes[1].trim());
        String dtPedido  = partes[2].trim();
        String dtEntrega = partes[3].trim(); // pode ser vazio
        double vlrTotal  = Double.parseDouble(partes[4].trim());
        
        return new Pedido(codPedido, idCliente, dtPedido, dtEntrega, vlrTotal);
        
        }
        
    }
            
    public alterarPedido(){
        
    }
    
    public excluirPedido(){
        
    }
    
    public consultarPedido(){
        
    }
    
    public listarPedido(){
        
    }
    
    //Converter o objeto para linha no CSV 
    
    public String toCSV() {
        return cod_pedido + ";" +
               id_cliente + ";" +
               dt_pedido + ";" +
               (dt_entrega != null ? dt_entrega : "") + ";" +
               String.format("%.2f", vlr_total);
    }

    @Override
    public String toString() {
        return "Pedido{cod=" + cod_pedido +
               ", cliente=" + id_cliente +
               ", data=" + dt_pedido +
               ", entrega=" + dt_entrega +
               ", total=" + vlr_total + "}";
    }
    
    public static Pedido fromCSV(String linha) {
        try {
            // -1 para não ignorar campos vazios (dt_entrega pode ser vazia)
            String[] partes = linha.split(";", -1);
            int    codPedido  = Integer.parseInt(partes[0].trim());
            int    idCliente  = Integer.parseInt(partes[1].trim());
            String dtPedido   = partes[2].trim();
            String dtEntrega  = (partes.length > 3) ? partes[3].trim() : "";
            double vlrTotal   = (partes.length > 4 && !partes[4].trim().isEmpty())
                                ? Double.parseDouble(partes[4].trim()) : 0.0;
            return new Pedido(codPedido, idCliente, dtPedido, dtEntrega, vlrTotal);
        } catch (Exception e) {
            System.err.println("Linha de pedido inválida: " + linha);
            return null;
        }
    }
        
    @Override
    public String toString() {
        return "Pedido{cod=" + cod_pedido +
               ", cliente=" + id_cliente +
               ", data='" + dt_pedido + '\'' +
               ", entrega='" + dt_entrega + '\'' +
               ", total=" + String.format("%.2f", vlr_total) + "}";
    }
    
}

