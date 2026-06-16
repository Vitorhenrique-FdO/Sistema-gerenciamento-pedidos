package repository;


  
      import model.Pedido;
    //import util.ArquivoUtil; verificar se eh isso mesmo 

    import java.util.ArrayList;
    import java.util.List;


public class PedidoRepository {

    private static final String ARQUIVO = "data/pedidos.csv";

    // Listar todos os pedidos
    public List<Pedido> listar() {
        List<Pedido> pedidos = new ArrayList<>();

        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            Pedido p = linhaParaPedido(linha);
            if (p != null) {
                pedidos.add(p);
            }
        }

        return pedidos;
    }

    // Consultar pedido pelo código
    public Pedido consultar(int codPedido) {
        for (Pedido p : listar()) {
            if (p.getCod_pedido() == cod_pedido) {
                return p;
            }
        } else { 
                 return null; // não encontrado
                }
       
    }

    // Incluir novo pedido
    public void incluir(Pedido pedido) {
        List<Pedido> pedidos = listar();
        pedidos.add(pedido);
        salvarTodos(pedidos);
    }

    // Alterando pedido existente
    public void alterar(Pedido pedidoAlterado) {
        List<Pedido> pedidos = listar();

        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getCod_pedido() == pedidoAlterado.getCod_pedido()) {
                pedidos.set(i, pedidoAlterado); // substitui na posição, verificar se o nome esta certo
                break;
            }
        }

        salvarTodos(pedidos);
    }

    // Excluir pedido pelo código, melhor forma? 
    public void excluir(int codPedido) {
        List<Pedido> pedidos = listar();

        // remove o pedido com o código mandado 
        pedidos.removeIf(p -> p.getCod_pedido() == cod_pedido);

        salvarTodos(pedidos);
    }

    // Formador de codigos VERIFICARRRRRR 
    public int proximoCodigo() {
        List<Pedido> pedidos = listar();
        if (pedidos.isEmpty()) {
            return 1;
        }
        // pega o maior código existente e soma 1
        int maior = 0;
        for (Pedido p : pedidos) {
            if (p.getCod_pedido() > maior) {
                maior = p.getCod_pedido();
            } 
        }
        return maior + 1;
    }

    // Verificando se já existe pedido com esse código
    public boolean existe(int cod_pedido) {
        return consultar(cod_pedido) != null;
    }

    // Verificando se existe pedido  ligado a um cliente 
    public boolean existePedidoDoCliente(int id_cliente) {
        for (Pedido p : listar()) {
            if (p.getId_cliente() == id_cliente) {
                return true;
            }
        }
        return false;
    }

   

    //Converte uma linha CSV em objeto Pedido e verifica a linha
    private Pedido linhaParaPedido(String linha) {
        try {
            String[] campos = linha.split(";");
            // para ser assim [0]cod_pedido [1]id_cliente [2]dt_pedido [3]dt_entrega [4]vlr_total
            int    cod_pedido = Integer.parseInt(campos[0].trim());
            int    id_cliente = Integer.parseInt(campos[1].trim());
            String dt_dedido  = campos[2].trim();
            String dt_entrega = campos[3].trim(); // pode ser vazio
            double vlr_total  = Double.parseDouble(campos[4].trim());

            return new Pedido(cod_pedido, id_ciente, dt_pedido, dt_entrega, vlr_total);

        } catch (Exception e) {
            System.err.println("Linha inválida ignorada no CSV de pedidos: " + linha);
            return null;
        }
    }

    // Converte a lista de pedidos em linhas CSV e salva o arquivo
    private void salvarTodos(List<Pedido> pedidos) {
        List<String> linhas = new ArrayList<>();
        for (Pedido p : pedidos) {
            linhas.add(p.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
    


