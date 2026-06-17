
package view;

public class AlterarPedidoView extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AlterarPedidoView.class.getName());
    private final Controller.PedidoController pedidoCtrl = new Controller.PedidoController();
    private final Controller.ItemController itemCtrl = new Controller.ItemController();
    private final Controller.ProdutoController produtoCtrl = new Controller.ProdutoController();
    
    private model.Pedido pedidoAtual = null;
    /**
     * Creates new form AlterarPedidoView
     */
    public AlterarPedidoView() {
        initComponents();
        this.setLocationRelativeTo(null);
        configurarBotoes();
        configurarTabelas();
    }

   
    private void configurarBotoes() {
        // Buscar Pedido
        jButtonBuscarPedido.addActionListener(e -> buscarPedido());
        jTextFieldCod_Pedido.addActionListener(e -> buscarPedido());

        // Buscar Item
        if (jButtonBuscarItem != null) {
            jButtonBuscarItem.addActionListener(e -> buscarItem());
            jTextFieldCod_Pedido1.addActionListener(e -> buscarItem()); // O TextField1 está como Codigo do Item
        }

        // Salvar
        jButtonSalvar.addActionListener(e -> salvarAlteracao());

        // Sair
        jButtonSair.addActionListener(e -> {
            new view.PedidoPrincipalView().setVisible(true);
            this.dispose();
        });
    }

    private void configurarTabelas() {
        // Tabela AlteraPedido: recalcula quando houver edição
        jTableAlteraPedido.getModel().addTableModelListener(e -> {
            // Lógica para quando o usuário editar manualmente o ID do Cliente ou as datas
            // O valor total não é editável, é sempre atualizado pelo backend ou via cálculo
        });

        // Tabela AlteraItem: permite alterar quantidade e pressionar DEL para excluir
        jTableAlteraItem.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int col = e.getColumn();
                int row = e.getFirstRow();
                // Coluna 3 = Quantidade
                if (col == 3 && row >= 0) {
                    try {
                        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel();
                        int novaQtde = Integer.parseInt(modelo.getValueAt(row, 3).toString());
                        double precoUni = Double.parseDouble(modelo.getValueAt(row, 4).toString().replace("R$ ", "").replace(",", "."));
                        modelo.setValueAt(String.format("R$ %.2f", novaQtde * precoUni), row, 5); // Atualiza Total Item
                        
                        // Recalcula total do pedido visualmente
                        recalcularTotalVisual();
                    } catch (Exception ex) {
                        // Ignora formatações inválidas enquanto digita
                    }
                }
            }
        });

        // Adiciona atalho de teclado para exclusão de Item (já que o botão jButtonExcluir pode não existir no layout)
        jTableAlteraItem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE || e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    excluirItemSelecionado();
                }
            }
        });
    }

    private void recalcularTotalVisual() {
        double total = 0.0;
        javax.swing.table.DefaultTableModel modelItem = (javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel();
        for (int i = 0; i < modelItem.getRowCount(); i++) {
            String valStr = modelItem.getValueAt(i, 5).toString().replace("R$ ", "").replace(",", ".");
            total += Double.parseDouble(valStr);
        }
        javax.swing.table.DefaultTableModel modelPedido = (javax.swing.table.DefaultTableModel) jTableAlteraPedido.getModel();
        if (modelPedido.getRowCount() > 0) {
            modelPedido.setValueAt(String.format("R$ %.2f", total), 0, 4);
        }
    }

    private void excluirItemSelecionado() {
        int row = jTableAlteraItem.getSelectedRow();
        if (row != -1) {
            int op = javax.swing.JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o item selecionado?", "Excluir Item", javax.swing.JOptionPane.YES_NO_OPTION);
            if (op == javax.swing.JOptionPane.YES_OPTION) {
                ((javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel()).removeRow(row);
                recalcularTotalVisual();
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecione um item na tabela primeiro.");
        }
    }

    private void buscarPedido() {
        String codStr = jTextFieldCod_Pedido.getText().trim();
        if (codStr.isEmpty()) return;
        try {
            int cod = Integer.parseInt(codStr);
            pedidoAtual = pedidoCtrl.consultar(cod);
            javax.swing.table.DefaultTableModel modelo =
                (javax.swing.table.DefaultTableModel) jTableAlteraPedido.getModel();
            modelo.setRowCount(0);
            
            javax.swing.table.DefaultTableModel modeloItem = (javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel();
            modeloItem.setRowCount(0);

            if (pedidoAtual == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Pedido " + cod + " não encontrado.", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                modelo.addRow(new Object[]{
                    pedidoAtual.getCodPedido(),
                    pedidoAtual.getIdCliente(),
                    pedidoAtual.getDtPedido(),
                    pedidoAtual.getDtEntrega(),
                    String.format("R$ %.2f", pedidoAtual.getVlrTotal())
                });
                
                // Carrega todos os itens deste pedido automaticamente
                for (model.Item it : itemCtrl.listarPorPedido(cod)) {
                    modeloItem.addRow(new Object[]{
                        it.getCodPedido(),
                        it.getSeqItem(),
                        it.getCodProduto(),
                        it.getQtdeItens(),
                        String.format("R$ %.2f", it.getPrecoUnitario()),
                        String.format("R$ %.2f", it.getPrecoTotal())
                    });
                }
            }
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Código inválido.", "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarItem() {
        String codStr = jTextFieldCod_Pedido1.getText().trim(); // Assumindo que este é o campo do item
        if (codStr.isEmpty()) return;
        
        // Seleciona na tabela o item correspondente ao codStr buscado
        javax.swing.table.DefaultTableModel modeloItem = (javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel();
        boolean achou = false;
        for (int i = 0; i < modeloItem.getRowCount(); i++) {
            if (modeloItem.getValueAt(i, 2).toString().equals(codStr)) { // Código do produto na coluna 2
                jTableAlteraItem.setRowSelectionInterval(i, i);
                achou = true;
                break;
            }
        }
        if (!achou) {
            javax.swing.JOptionPane.showMessageDialog(this, "Item/Produto não encontrado na lista atual.");
        }
    }

    private void salvarAlteracao() {
        if (pedidoAtual == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Busque um pedido primeiro.");
            return;
        }
        
        // Salva Pedido
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableAlteraPedido.getModel();
        if (modelo.getRowCount() == 0) return;
        
        try {
            int idCliente  = Integer.parseInt(modelo.getValueAt(0, 1).toString().trim());
            String dtEntrega = modelo.getValueAt(0, 3) != null ? modelo.getValueAt(0, 3).toString().trim() : "";
            
            // 1. Atualiza pedido
            String erro = pedidoCtrl.alterar(pedidoAtual.getCodPedido(), idCliente, dtEntrega);
            if (!erro.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, erro, "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Atualiza Itens
            // Como a interface visual pode ter removido ou alterado as quantidades:
            // A forma mais segura de não quebrar sequência é pegar todos originais e comparar
            java.util.List<model.Item> itensOriginais = itemCtrl.listarPorPedido(pedidoAtual.getCodPedido());
            javax.swing.table.DefaultTableModel modeloItem = (javax.swing.table.DefaultTableModel) jTableAlteraItem.getModel();
            
            // Verifica o que ficou na tabela para atualizar
            java.util.List<Integer> seqsMantis = new java.util.ArrayList<>();
            
            for (int i = 0; i < modeloItem.getRowCount(); i++) {
                int seqItem = Integer.parseInt(modeloItem.getValueAt(i, 1).toString());
                int novaQtde = Integer.parseInt(modeloItem.getValueAt(i, 3).toString());
                seqsMantis.add(seqItem);
                
                // Tenta alterar a quantidade (o controller gerencia o estoque)
                String erroItem = itemCtrl.alterarQuantidade(pedidoAtual.getCodPedido(), seqItem, novaQtde);
                if (!erroItem.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Erro no item seq " + seqItem + ": " + erroItem);
                }
            }
            
            // Exclui os que não estão mais na tabela visual
            for (model.Item original : itensOriginais) {
                if (!seqsMantis.contains(original.getSeqItem())) {
                    itemCtrl.excluir(original.getCodPedido(), original.getSeqItem());
                }
            }

            javax.swing.JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            buscarPedido(); // Recarrega do banco
            
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Dados inválidos: " + ex.getMessage(), "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
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
        jTextFieldCod_Pedido = new javax.swing.JTextField();
        jButtonSalvar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAlteraPedido = new javax.swing.JTable();
        jButtonBuscarPedido = new javax.swing.JButton();
        jButtonSair = new javax.swing.JButton();
        jTextFieldCod_Pedido1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButtonBuscarItem = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableAlteraItem = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setText("ALTERAÇÃO PEDIDO");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 204));
        jLabel2.setText("CODIGO PEDIDO :");

        jTextFieldCod_Pedido.setBackground(new java.awt.Color(255, 255, 255));

        jButtonSalvar.setBackground(new java.awt.Color(102, 255, 102));
        jButtonSalvar.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonSalvar.setForeground(new java.awt.Color(0, 0, 0));
        jButtonSalvar.setText("SALVAR");

        jTableAlteraPedido.setBackground(new java.awt.Color(204, 204, 204));
        jTableAlteraPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Codigo Pedido", "ID Cliente", "Data Pedido", "Data Entrega", "Valor Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableAlteraPedido);

        jButtonBuscarPedido.setBackground(new java.awt.Color(51, 204, 255));
        jButtonBuscarPedido.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonBuscarPedido.setForeground(new java.awt.Color(0, 0, 0));
        jButtonBuscarPedido.setText("BUSCAR");

        jButtonSair.setBackground(new java.awt.Color(255, 51, 51));
        jButtonSair.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonSair.setForeground(new java.awt.Color(0, 0, 0));
        jButtonSair.setText("SAIR");

        jTextFieldCod_Pedido1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 204));
        jLabel3.setText("CODIGO ITEM :");

        jButtonBuscarItem.setBackground(new java.awt.Color(51, 204, 255));
        jButtonBuscarItem.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jButtonBuscarItem.setForeground(new java.awt.Color(0, 0, 0));
        jButtonBuscarItem.setText("BUSCAR");

        jTableAlteraItem.setBackground(new java.awt.Color(204, 204, 204));
        jTableAlteraItem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo Pedido", "Sequencia Item", "Codigo Produto", "Quantidade", "Preco unidade", "Valor Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, true, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableAlteraItem);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(52, 52, 52)
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldCod_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(36, 36, 36)
                            .addComponent(jButtonBuscarPedido))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(37, 37, 37)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 740, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(197, 197, 197)
                            .addComponent(jButtonSalvar)
                            .addGap(254, 254, 254)
                            .addComponent(jButtonSair))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(228, 228, 228)
                            .addComponent(jLabel1))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(47, 47, 47)
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextFieldCod_Pedido1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(62, 62, 62)
                            .addComponent(jButtonBuscarItem))))
                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldCod_Pedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBuscarPedido))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCod_Pedido1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButtonBuscarItem))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSalvar)
                    .addComponent(jButtonSair))
                .addContainerGap(179, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new AlterarPedidoView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBuscarItem;
    private javax.swing.JButton jButtonBuscarPedido;
    private javax.swing.JButton jButtonSair;
    private javax.swing.JButton jButtonSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableAlteraItem;
    private javax.swing.JTable jTableAlteraPedido;
    private javax.swing.JTextField jTextFieldCod_Pedido;
    private javax.swing.JTextField jTextFieldCod_Pedido1;
    // End of variables declaration//GEN-END:variables
}
