/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;


/**
 *
 * @author vitor
 */
public class incluirPedido extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(incluirPedido.class.getName());

    // Controllers 
    private final Controller.PedidoController  pedidoCtrl  = new Controller.PedidoController();
    private final Controller.ItemController    itemCtrl    = new Controller.ItemController();
    private final Controller.ProdutoController produtoCtrl = new Controller.ProdutoController();

    // Memória local do pedido antes de salvar
    private int clienteSelecionado = -1;
    private int codPedidoVirtual = 0; // Gerado apenas para visualização
    private java.util.List<model.Item> itensVirtuais = new java.util.ArrayList<>();
    private double valorTotalVirtual = 0.0;

    /**
     * Cria novo formulário incluirPedido.
     */
    public incluirPedido() {
        initComponents();
        this.setLocationRelativeTo(null);
        configurarBotoes();
        
        // Simula qual será o próximo código de pedido (apenas visual)
        java.util.List<model.Pedido> todos = pedidoCtrl.listar();
        if (!todos.isEmpty()) {
            codPedidoVirtual = todos.get(todos.size() - 1).getCodPedido() + 1;
        } else {
            codPedidoVirtual = 1;
        }
    }
    
    // Método público para a tela BuscarProdutoPedido chamar e injetar o produto
    public void adicionarProdutoVirtual(int codProduto, int qtde) {
        model.Produto p = produtoCtrl.consultar(codProduto);
        if (p != null) {
            model.Item novoItem = new model.Item();
            novoItem.setCodPedido(codPedidoVirtual);
            novoItem.setSeqItem(itensVirtuais.size() + 1); // Sequencia gerada
            novoItem.setCodProduto(codProduto);
            novoItem.setQtdeItens(qtde);
            novoItem.setPrecoUnitario(p.getPreco());
            itensVirtuais.add(novoItem);
            carregarItensNaTabela();
            atualizarTabelaPedido();
        }
    }

    //Configura os action listeners dos botões que não foram definidos no form editor.
     
    private void configurarBotoes() {
        // Ao pressionar Enter no Cod Cliente, atualiza a tabela pedido
        jTextFieldCod_cliente.addActionListener(e -> {
            try {
                clienteSelecionado = Integer.parseInt(jTextFieldCod_cliente.getText().trim());
                atualizarTabelaPedido();
                javax.swing.JOptionPane.showMessageDialog(this, "Cliente " + clienteSelecionado + " vinculado provisoriamente!");
            } catch(Exception ex){
                javax.swing.JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        // Abrir janela de busca de produto (passando esta view como pai)
        jButtonBuscarProduto.addActionListener(e -> {
            new BuscarProdutoPedido(this).setVisible(true);
        });

        // Salvar e Sair (Efetiva no Banco)
        jButtonSalvarSair.addActionListener(e -> {
            if (clienteSelecionado == -1) {
                javax.swing.JOptionPane.showMessageDialog(this, "Defina o cliente primeiro (Digite e dê Enter).");
                return;
            }
            if (itensVirtuais.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Adicione ao menos um produto.");
                return;
            }
            // 1. Incluir pedido
            String erro = pedidoCtrl.incluir(clienteSelecionado, "");
            if (!erro.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, erro); return;
            }
            // Pega o código real gerado
            java.util.List<model.Pedido> todos = pedidoCtrl.listar();
            int codReal = todos.get(todos.size() - 1).getCodPedido();
            
            // 2. Incluir itens
            for (model.Item it : itensVirtuais) {
                itemCtrl.incluir(codReal, it.getCodProduto(), it.getQtdeItens());
            }
            
            javax.swing.JOptionPane.showMessageDialog(this, "Pedido salvo com sucesso!");
            new view.PedidoPrincipalView().setVisible(true);
            this.dispose();
        });

        jButtonSair.addActionListener(e -> {
            new view.PedidoPrincipalView().setVisible(true);
            this.dispose();
        });

        // Excluir Item selecionado na Tabela
        jButtonExcluirItem.addActionListener(e -> {
            int row = jTableProdutos.getSelectedRow();
            if (row != -1) {
                itensVirtuais.remove(row); // Remove da memória
                
                // Refaz sequencial
                for(int i=0; i<itensVirtuais.size(); i++){
                    itensVirtuais.get(i).setSeqItem(i+1);
                }
                
                carregarItensNaTabela();
                atualizarTabelaPedido();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Selecione um item na tabela de produtos.");
            }
        });
        
        // Listener para mudança de quantidade (update dinâmico da tabela)
        jTableProdutos.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int col = e.getColumn();
                int row = e.getFirstRow();
                // Coluna 1 = Quantidade
                if (col == 1 && row >= 0 && row < itensVirtuais.size()) {
                    try {
                        int novaQtde = Integer.parseInt(jTableProdutos.getValueAt(row, 1).toString());
                        itensVirtuais.get(row).setQtdeItens(novaQtde);
                        
                        // Não chamo carregarItensNaTabela() aqui dentro do listener senão dá loop/flicker.
                        // Atualizo só a linha visualmente e o total virtual.
                        double p = itensVirtuais.get(row).getPrecoUnitario();
                        jTableProdutos.setValueAt(String.format("R$ %.2f", p * novaQtde), row, 3);
                        atualizarTabelaPedido();
                    } catch (Exception ex) {}
                }
            }
        });
        
        // Atalho DEL na tabela
        jTableProdutos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    jButtonExcluirItem.doClick();
                }
            }
        });
    }

    private void atualizarTabelaPedido() {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTablePedido.getModel();
        modelo.setRowCount(0);
        
        valorTotalVirtual = 0.0;
        for (model.Item it : itensVirtuais) {
            valorTotalVirtual += (it.getQtdeItens() * it.getPrecoUnitario());
        }
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String hj = sdf.format(new java.util.Date());
        
        modelo.addRow(new Object[]{hj, "", String.format("R$ %.2f", valorTotalVirtual)});
    }

    private void carregarItensNaTabela() {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableProdutos.getModel();
        modelo.setRowCount(0);
        for (model.Item item : itensVirtuais) {
            modelo.addRow(new Object[]{
                item.getCodProduto(),
                item.getQtdeItens(),
                String.format("R$ %.2f", item.getPrecoUnitario()),
                String.format("R$ %.2f", item.getPrecoUnitario() * item.getQtdeItens())
            });
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldCod_cliente = new javax.swing.JTextField();
        jButtonBuscarProduto = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jButtonSelecionarItem = new javax.swing.JButton();
        jButtonSair = new javax.swing.JButton();
        jButtonSalvarSair = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProdutos = new javax.swing.JTable();
        jButtonExcluirItem = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablePedido = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Novo Pedido");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Codido Cliente");

        jTextFieldCod_cliente.setBackground(new java.awt.Color(204, 204, 204));

        jButtonBuscarProduto.setBackground(new java.awt.Color(51, 153, 255));
        jButtonBuscarProduto.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonBuscarProduto.setForeground(new java.awt.Color(255, 255, 255));
        jButtonBuscarProduto.setText("Buscar produto");

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Produtos atuais");

        jButtonSelecionarItem.setBackground(new java.awt.Color(102, 153, 255));
        jButtonSelecionarItem.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonSelecionarItem.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSelecionarItem.setText("SELECIONAR ITEM");
        jButtonSelecionarItem.addActionListener(this::jButtonSelecionarItemActionPerformed);

        jButtonSair.setBackground(new java.awt.Color(255, 51, 51));
        jButtonSair.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonSair.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSair.setText("Retornar sem salvar");
        jButtonSair.addActionListener(this::jButtonSairActionPerformed);

        jButtonSalvarSair.setBackground(new java.awt.Color(0, 204, 51));
        jButtonSalvarSair.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonSalvarSair.setForeground(new java.awt.Color(255, 255, 255));
        jButtonSalvarSair.setText("Salvar e Sair");

        jTableProdutos.setBackground(new java.awt.Color(204, 204, 204));
        jTableProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo Produto", "Quantidade", "Preco unidade", "Valor Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableProdutos);

        jButtonExcluirItem.setBackground(new java.awt.Color(102, 153, 255));
        jButtonExcluirItem.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonExcluirItem.setForeground(new java.awt.Color(255, 255, 255));
        jButtonExcluirItem.setText("EXCLUIR ITEM");
        jButtonExcluirItem.addActionListener(this::jButtonExcluirItemActionPerformed);

        jTablePedido.setBackground(new java.awt.Color(204, 204, 204));
        jTablePedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Data Pedido", "Data Entrega", "Valor Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTablePedido);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(222, 222, 222)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(144, 144, 144))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonBuscarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldCod_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jButtonSalvarSair)
                        .addGap(146, 146, 146)
                        .addComponent(jButtonSair))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonSelecionarItem)
                            .addComponent(jButtonExcluirItem))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldCod_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jButtonBuscarProduto)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jButtonSelecionarItem)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonExcluirItem)))
                .addGap(60, 60, 60)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSalvarSair)
                    .addComponent(jButtonSair))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSelecionarItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelecionarItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSelecionarItemActionPerformed

    private void jButtonSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSairActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSairActionPerformed

    private void jButtonExcluirItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExcluirItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonExcluirItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new incluirPedido().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBuscarProduto;
    private javax.swing.JButton jButtonExcluirItem;
    private javax.swing.JButton jButtonSair;
    private javax.swing.JButton jButtonSalvarSair;
    private javax.swing.JButton jButtonSelecionarItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTablePedido;
    private javax.swing.JTable jTableProdutos;
    private javax.swing.JTextField jTextFieldCod_cliente;
    // End of variables declaration//GEN-END:variables
}
