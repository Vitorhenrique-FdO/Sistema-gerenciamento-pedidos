package model;

// Classe que representa um Pedido
public class Pedido {

    // variaveis do pedido
    private int    cod_pedido;
    private int    id_cliente;
    private String dt_pedido;   // guardando data como texto mesmo
    private String dt_entrega;  // pode ficar vazia se nao tiver
    private double vlr_total;

    // construtor com tudo
    public Pedido(int cod_pedido, int id_cliente, String dt_pedido, String dt_entrega, double vlr_total) {
        this.cod_pedido  = cod_pedido;
        this.id_cliente  = id_cliente;
        // tira os espacos em branco da data, se vier vazia guarda assim mesmo
        this.dt_pedido   = (dt_pedido  != null) ? dt_pedido.trim()  : "";
        this.dt_entrega  = (dt_entrega != null) ? dt_entrega.trim() : "";
        this.vlr_total   = vlr_total;
    }

    // metodos getters e setters
    public int getCod_pedido()  { return cod_pedido; }
    public int getId_cliente()  { return id_cliente; }
    public String getDt_pedido()  { return dt_pedido; }
    public String getDt_entrega() { return dt_entrega; }
    public double getVlr_total()  { return vlr_total; }

    // apelidos pra ficar mais facil de usar nos controllers
    public int    getCodPedido()  { return cod_pedido; }
    public int    getIdCliente()  { return id_cliente; }
    public String getDtPedido()   { return dt_pedido; }
    public String getDtEntrega()  { return dt_entrega; }
    public double getVlrTotal()   { return vlr_total; }

    public void setCod_pedido(int cod_pedido) { this.cod_pedido = cod_pedido; }
    public void setId_cliente(int id_cliente) { this.id_cliente = id_cliente; }
    public void setIdCliente(int id_cliente)  { this.id_cliente = id_cliente; }
    public void setDt_pedido(String dt_pedido)   { this.dt_pedido  = (dt_pedido  != null) ? dt_pedido.trim()  : ""; }
    public void setDt_entrega(String dt_entrega) { this.dt_entrega = (dt_entrega != null) ? dt_entrega.trim() : ""; }
    public void setDtEntrega(String dt_entrega)  { this.dt_entrega = (dt_entrega != null) ? dt_entrega.trim() : ""; }
    public void setVlr_total(double vlr_total) { this.vlr_total = vlr_total; }
    public void setVlrTotal(double vlr_total)  { this.vlr_total = vlr_total; }

    // transforma o pedido em uma linha do CSV
    public String toCSV() {
        return cod_pedido + ";" +
               id_cliente + ";" +
               dt_pedido  + ";" +
               (dt_entrega != null ? dt_entrega : "") + ";" +
               String.format("%.2f", vlr_total);
    }

    // pega uma linha do CSV e transforma de volta em objeto Pedido
    public static Pedido fromCSV(String linha) {
        try {
            // divide a linha pelo ponto e virgula
            String[] partes = linha.split(";", -1);
            int    codPedido = Integer.parseInt(partes[0].trim());
            int    idCliente = Integer.parseInt(partes[1].trim());
            String dtPedido  = partes[2].trim();
            String dtEntrega = (partes.length > 3) ? partes[3].trim() : "";
            double vlrTotal  = (partes.length > 4 && !partes[4].trim().isEmpty())
                               ? Double.parseDouble(partes[4].trim()) : 0.0;
            return new Pedido(codPedido, idCliente, dtPedido, dtEntrega, vlrTotal);
        } catch (Exception e) {
            System.err.println("linha zoada ignorada: " + linha);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Pedido{cod=" + cod_pedido +
               ", cliente=" + id_cliente +
               ", data='"   + dt_pedido  + '\'' +
               ", entrega='" + dt_entrega + '\'' +
               ", total="   + String.format("%.2f", vlr_total) + "}";
    }
}
