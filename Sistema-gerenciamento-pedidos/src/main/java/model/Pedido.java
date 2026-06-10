package model;


import java.util.Date;
import java.util.Calendar; 

public class Pedido {
      //CLASSES 
        
    int cod_pedido;
    int id_cliente;
    date dt_pedido; //como usar o date? 
    date dt_entrega;
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
    
    public Pedido(int cod_pedido, int id_cliente, date dt_pedido, date dt_entrega, float vlr_total) {
        this.cod_pedido = cod_pedido;
        this.id_cliente = id_cliente;
        this.dt_pedido = dt_pedido;
        this.dt_entrega = dt_entrega;
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
        return codPedido + ";" +
               idCliente + ";" +
               dtPedido + ";" +
               (dtEntrega != null ? dtEntrega : "") + ";" +
               String.format("%.2f", vlrTotal);
    }

    @Override
    public String toString() {
        return "Pedido{cod=" + codPedido +
               ", cliente=" + idCliente +
               ", data=" + dtPedido +
               ", entrega=" + dtEntrega +
               ", total=" + vlrTotal + "}";
    }
    
}

