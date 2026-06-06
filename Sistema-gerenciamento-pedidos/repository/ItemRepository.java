package repository;


      import model.Item;
    import util.ArquivoUtil

    import java.util.ArrayList;
    import java.util.List;


public class ItemRepository {

    private static final String ARQUIVO = "data/itens.csv";

    // Listar todos os itens de todos os pedidos
    public List<Item> listar() {
        List<Item> itens = new ArrayList<>();

        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            Item item = linhaParaItem(linha);
            if (item != null) {
                itens.add(item);
            }
        }

        return itens;
    }

    // Listar apnas itens de um pedido especifico
    
    public List<Item> listarPorPedido(int cod_pedido){
        List<Item> resultado = new ArrayList<>(;
        
        for(Item item : listar()){
            if(item.getCodPedido() == cod_pedido) {
                resultado.add(item);
            }
        }
    }
    //item tem chave composta
    public Item consultar(int cod_pedido, int seq_item) {
        for (Item item : listar()) {
            if (p.getCodPedido() == cod_pedido && item.getSetItem() == seqItem) 
            {
                return item;
            }
           else { 
                 return null; // não encontrado
                }
       
    }

    // Incluir novo item
    public void incluir(Item item) {
        List<Item> itens = listar();
        itens.add(item);
        salvarTodos(itens);
    }

    // Alterando item existente
    public void alterar(Item itemAlterado) {
        List<Item> itens = listar();

        for (int i = 0; i < itens.size(); i++) {
            Item atual = intens.get(i);
            
            if(atual.getCod_pedido() == itemAlterado.getCod_pedido() && atual.getSeqItem() == itemAlterado.getSeqItem()){
                itens.set(i,itemAlterado);
                break;
            }
        }

        salvarTodos(itens);
    }

    // Excluir item pela chave composta, melhor forma? 
    public void excluir(int cod_pedido, int seq_item) {
        List<item> itens = listar();

        // remove o pedido com o código mandado 
        itens.removeIf(item -> item.getCodPedido == cod_pedido && item.getSeqItem() == seq_item);

        salvarTodos(itens);
    }

        //Excluir todos os itens de um pedido quando usar o excluir pedido
        public void excluirTodosDoPedido(int cod_pedido) {
            List<Item> itens = listar();
            itens.removeIf(item -> item.getCodPedido() == cod_pedido);
            salvarTodos(itens)
  
        }

        
// Formador da sequencia dentro de um pedido TEM QUE SER CORRIGIDO E adaptado
    public int proximaSequencia(int cod_pedido) {
        List<item> itens = listar();
        if (itens.isEmpty()) {
            return 1;
        }
        // pega o maior código existente e soma 1
        int maior = 0;
        for (item p : itens) {
            if (p.getCod_item() > maior) {
                maior = p.getCod_item();
            } 
        }
        return maior + 1;
    }

    // Verificando se já existe pedido com esse código
    public boolean existe(int cod_item) {
        return consultar(cod_item) != null;
    }

    // Verificando se existe item  ligado a um produto, verificar 
    public boolean existePedidoDoCliente(int cod_produto) {
        for (Item p : listar()) {
            if (p.getCod_produto() == cod_produto) {
                return true;
            }
        }
        return false;
    }

   

    //Converte uma linha CSV em objeto Item e verifica a linha
    private Item linhaParaItem (String linha) {
        try {
            String[] campos = linha.split(";");
            // para ser assim [0]cod_pedido [1]seq_item [2]cod_produto [3]qtde_itens [4]preco_uni_item [5]preco_total
            int    cod_pedido = Integer.parseInt(campos[0].trim());
            int    seq_item = Integer.parseInt(campos[1].trim());
            int cod_produto  = Integer.parseInt(campos[2].trim());
            int qtde_itens = Integer.parseInt(campos[3].trim());
            float preco_uni_item = Float.parseFloat(campos[4].trim());
            float preco_total = Float.parseFloat(campos[5].trim());

            return new Item(cod_pedido, seq_item, cod_produto, qtde_itens, preco_uni_item, preco_total);

        } catch (Exception e) {
            System.err.println("Linha inválida ignorada no CSV de pedidos: " + linha);
            return null;
        }
    }

    // Converte a lista de itens em linhas CSV e salva o arquivo
    private void salvarTodos(List<Item> itens) {
        List<String> linhas = new ArrayList<>();
        for (Item item : itens) {
            linhas.add(item.toCSV());
        }
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}
