/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms.inventario;

import forms.inventario.entrada.ABMEntrada;
import forms.producto.ABMProducto;
import metodos.Metodos;

/**
 *
 * @author Arnaldo Cantero
 */
public final class FormInventario extends javax.swing.JDialog {

    private ABMProducto abmproducto; //Para que tenga relacion con su form padre

    public FormInventario(ABMProducto abmproducto, java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        this.abmproducto = abmproducto;
        initComponents();
        TablaConsulta(txtBuscar.getText()); //Trae todos los registros
        txtBuscar.requestFocus();
    }

    Metodos metodos = new Metodos();

    public void TablaConsulta(String filtro) {//Realiza la consulta de los productos que tenemos en la base de datos
        String nombresp = "SP_ClaseProductoConsulta";
        String titlesJtabla[] = {"Codigo", "Descripción"};
        String titlesconsulta[] = {"cp_codigo", "cp_descripcion"};

        metodos.ConsultaFiltroTablaBD(tbTabla, titlesJtabla, titlesconsulta, nombresp, filtro, cbCampoBuscar);
        metodos.AnchuraColumna(tbTabla);
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpBanner = new javax.swing.JPanel();
        lbBanner = new javax.swing.JLabel();
        jpTabla = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbTabla = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lblBuscarCampo = new javax.swing.JLabel();
        cbCampoBuscar = new javax.swing.JComboBox();
        lblBuscarCampo3 = new javax.swing.JLabel();
        cbProductorFiltro = new javax.swing.JComboBox();
        lblBuscarCampo4 = new javax.swing.JLabel();
        cbEstablecimientoFiltro = new javax.swing.JComboBox();
        btnNuevaEntrada = new javax.swing.JButton();
        btnGuardar2 = new javax.swing.JButton();

        setTitle("Ventana Inventario");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(45, 62, 80));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpBanner.setBackground(new java.awt.Color(0, 102, 204));
        jpBanner.setPreferredSize(new java.awt.Dimension(1000, 82));

        lbBanner.setBackground(new java.awt.Color(0, 204, 204));
        lbBanner.setFont(new java.awt.Font("sansserif", 1, 30)); // NOI18N
        lbBanner.setForeground(new java.awt.Color(255, 255, 255));
        lbBanner.setText("Inventario");
        lbBanner.setMaximumSize(new java.awt.Dimension(1100, 52));
        lbBanner.setMinimumSize(new java.awt.Dimension(1100, 52));
        lbBanner.setPreferredSize(new java.awt.Dimension(1100, 52));

        javax.swing.GroupLayout jpBannerLayout = new javax.swing.GroupLayout(jpBanner);
        jpBanner.setLayout(jpBannerLayout);
        jpBannerLayout.setHorizontalGroup(
            jpBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBannerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 808, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );
        jpBannerLayout.setVerticalGroup(
            jpBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBannerLayout.createSequentialGroup()
                .addComponent(lbBanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jpTabla.setBackground(new java.awt.Color(45, 62, 80));
        jpTabla.setBorder(null);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");
        jLabel10.setBorder(null);

        txtBuscar.setBackground(new java.awt.Color(0, 0, 0));
        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(0, 204, 204));
        txtBuscar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        txtBuscar.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscar.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarKeyTyped(evt);
            }
        });

        tbTabla.setAutoCreateRowSorter(true);
        tbTabla.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(153, 153, 153), null, new java.awt.Color(102, 102, 102)));
        tbTabla.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbTabla.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbTabla.setGridColor(new java.awt.Color(0, 153, 204));
        tbTabla.setOpaque(false);
        tbTabla.setRowHeight(20);
        tbTabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbTabla.getTableHeader().setReorderingAllowed(false);
        tbTabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTablaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbTablaMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tbTabla);

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Buscar por:");

        lblBuscarCampo3.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo3.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampo3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo3.setText(" Productor:");
        lblBuscarCampo3.setToolTipText("");

        cbProductorFiltro.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        cbProductorFiltro.setName("ProductorFiltro"); // NOI18N
        cbProductorFiltro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbProductorFiltroItemStateChanged(evt);
            }
        });

        lblBuscarCampo4.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo4.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampo4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo4.setText(" Establecimiento:");
        lblBuscarCampo4.setToolTipText("");

        cbEstablecimientoFiltro.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        cbEstablecimientoFiltro.setName("EstablecimientoFiltro"); // NOI18N
        cbEstablecimientoFiltro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbEstablecimientoFiltroItemStateChanged(evt);
            }
        });

        btnNuevaEntrada.setBackground(new java.awt.Color(0, 153, 102));
        btnNuevaEntrada.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnNuevaEntrada.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevaEntrada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEntrada.png"))); // NOI18N
        btnNuevaEntrada.setText("Nueva entrada");
        btnNuevaEntrada.setToolTipText("Inserta el nuevo registro");
        btnNuevaEntrada.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevaEntrada.setIconTextGap(0);
        btnNuevaEntrada.setPreferredSize(new java.awt.Dimension(128, 36));
        btnNuevaEntrada.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevaEntrada.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevaEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaEntradaActionPerformed(evt);
            }
        });
        btnNuevaEntrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnNuevaEntradaKeyPressed(evt);
            }
        });

        btnGuardar2.setBackground(new java.awt.Color(153, 0, 0));
        btnGuardar2.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnGuardar2.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoSalida.png"))); // NOI18N
        btnGuardar2.setText("Nueva salida");
        btnGuardar2.setToolTipText("Inserta el nuevo registro");
        btnGuardar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar2.setIconTextGap(0);
        btnGuardar2.setPreferredSize(new java.awt.Dimension(128, 36));
        btnGuardar2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardar2ActionPerformed(evt);
            }
        });
        btnGuardar2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGuardar2KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jpTablaLayout = new javax.swing.GroupLayout(jpTabla);
        jpTabla.setLayout(jpTablaLayout);
        jpTablaLayout.setHorizontalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblBuscarCampo4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblBuscarCampo3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtBuscar)
                            .addComponent(cbProductorFiltro, 0, 421, Short.MAX_VALUE)
                            .addComponent(cbEstablecimientoFiltro, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(9, 9, 9)
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpTablaLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(btnNuevaEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGuardar2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(12, 12, 12))
        );
        jpTablaLayout.setVerticalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevaEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                    .addComponent(btnGuardar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jpTablaLayout.createSequentialGroup()
                            .addComponent(cbCampoBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                            .addGap(1, 1, 1))
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscarCampo3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbProductorFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscarCampo4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEstablecimientoFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(305, 305, 305))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jpBanner, javax.swing.GroupLayout.DEFAULT_SIZE, 909, Short.MAX_VALUE)
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(jpBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBanner.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 909, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Inventario");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        //actualiza la tabla conforme a la letra que teclea
        if (txtBuscar.getText().trim().length() >= 1) {
            TablaConsulta(txtBuscar.getText());
            tbTabla.setVisible(true);
        } else {
            TablaConsulta(txtBuscar.getText());
        }

        //Convertir a mayuscula
        Character s = evt.getKeyChar();
        if (Character.isLetter(s)) {
            txtBuscar.setText(txtBuscar.getText().toUpperCase());
        }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void tbTablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTablaMouseClicked

    }//GEN-LAST:event_tbTablaMouseClicked

    private void tbTablaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTablaMousePressed

    }//GEN-LAST:event_tbTablaMousePressed

    private void txtBuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyTyped
        // Verificar si la tecla pulsada no es '
        char caracter = evt.getKeyChar();
        if (caracter == "'".charAt(0)) {
            evt.consume(); // ignorar el evento de teclado
        }
    }//GEN-LAST:event_txtBuscarKeyTyped

    private void cbProductorFiltroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbProductorFiltroItemStateChanged

    }//GEN-LAST:event_cbProductorFiltroItemStateChanged

    private void cbEstablecimientoFiltroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbEstablecimientoFiltroItemStateChanged

    }//GEN-LAST:event_cbEstablecimientoFiltroItemStateChanged

    private void btnNuevaEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaEntradaActionPerformed
        ABMEntrada abmentrada = new ABMEntrada(this,false);
        abmentrada.setVisible(true);
    }//GEN-LAST:event_btnNuevaEntradaActionPerformed

    private void btnNuevaEntradaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnNuevaEntradaKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnNuevaEntrada.doClick();
        }
    }//GEN-LAST:event_btnNuevaEntradaKeyPressed

    private void btnGuardar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardar2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardar2ActionPerformed

    private void btnGuardar2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardar2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardar2KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar2;
    private javax.swing.JButton btnNuevaEntrada;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox cbEstablecimientoFiltro;
    private javax.swing.JComboBox cbProductorFiltro;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpBanner;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JLabel lbBanner;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblBuscarCampo3;
    private javax.swing.JLabel lblBuscarCampo4;
    private javax.swing.JTable tbTabla;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
