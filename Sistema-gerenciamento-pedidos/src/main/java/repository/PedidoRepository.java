package repository;

import Util.ArquivoUtil;
import model.Pedido;
import java.util.ArrayList;
import java.util.List;

// salva e le os pedidos no txt/csv
public class PedidoRepository {

    private static final String ARQUIVO = "data/pedidos.csv";

    public PedidoRepository() {
        // cria a pasta data se nao existir ainda
        ArquivoUtil.garantirDiretorioDados();
    }

    // traz todos os pedidos do arquivo
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

    // acha um pedido pelo codigo
    public Pedido consultar(int codPedido) {
        for (Pedido p : listar()) {
            if (p.getCod_pedido() == codPedido) {
                return p;
            }
        }
        return null; // se nao achar nada
    }

    // cria um pedido novo
    public void incluir(Pedido pedido) {
        List<Pedido> pedidos = listar();
        pedidos.add(pedido);
        salvarTodos(pedidos);
    }

    // atualiza o pedido que ja existe
    public void alterar(Pedido pedidoAlterado) {
        List<Pedido> pedidos = listar();
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getCod_pedido() == pedidoAlterado.getCod_pedido()) {
                pedidos.set(i, pedidoAlterado); // substitui pelo novo
                break;
            }
        }
        salvarTodos(pedidos);
    }

    // apaga o pedido
    public void excluir(int codPedido) {
        List<Pedido> pedidos = listar();
        // joga fora o que tiver o codigo igual
        pedidos.removeIf(p -> p.getCod_pedido() == codPedido);
        salvarTodos(pedidos);
    }

    // descobre qual eh o proximo id livre
    public int proximoCodigo() {
        List<Pedido> pedidos = listar();
        if (pedidos.isEmpty()) return 1;
        int maior = 0;
        for (Pedido p : pedidos) {
            if (p.getCod_pedido() > maior) {
                maior = p.getCod_pedido();
            }
        }
        return maior + 1; // soma 1 no maior que achou
    }

    // ve se o pedido existe
    public boolean existe(int codPedido) {
        return consultar(codPedido) != null;
    }

    // verifica se o cliente tem algum pedido (pra barrar na hora de excluir o cliente)
    public boolean existePedidoDoCliente(int idCliente) {
        for (Pedido p : listar()) {
            if (p.getId_cliente() == idCliente) {
                return true;
            }
        }
        return false;
    }

    // passa a string do csv pro objeto pedido
    private Pedido linhaParaPedido(String linha) {
        try {
            String[] campos = linha.split(";", -1);
            int    codPedido = Integer.parseInt(campos[0].trim());
            int    idCliente = Integer.parseInt(campos[1].trim());
            String dtPedido  = campos[2].trim();
            String dtEntrega = (campos.length > 3) ? campos[3].trim() : "";
            double vlrTotal  = (campos.length > 4 && !campos[4].trim().isEmpty())
                               ? Double.parseDouble(campos[4].trim()) : 0.0;
            return new Pedido(codPedido, idCliente, dtPedido, dtEntrega, vlrTotal);
        } catch (Exception e) {
            System.err.println("linha do pedido deu erro: " + linha);
            return null;
        }
    }

    // grava tudo no arquivo de novo
    private void salvarTodos(List<Pedido> pedidos) {
        List<String> linhas = new ArrayList<>();
        for (Pedido p : pedidos) {
            linhas.add(p.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
