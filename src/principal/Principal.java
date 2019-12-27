/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import conexion.Conexion;
import forms.ABMCliente;
import forms.producto.ABMProducto;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import static login.Login.Alias;
import metodos.ImagenFondo;
import metodos.Metodos;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Principal extends javax.swing.JFrame implements Runnable {

    Metodos metodos = new Metodos();
    Thread hilo;

    public Principal() {
        initComponents();
        dpEscritorio.setBorder(new ImagenFondo());//Imagen de fondo al jdesktopane
        this.setExtendedState(Principal.MAXIMIZED_BOTH);//Maximizar ventana
        ObtenerHorayFecha();
        setVisible(true);
        lbAlias.setText(Alias);

        PerfilUsuario();
    }

    private void PerfilUsuario() {
        String consulta = "CALL SP_UsuarioPerfilConsulta('" + Alias + "')";
        System.out.println("consulta: " + consulta);
        try {
            Connection conexion;
            conexion = Conexion.ConectarBasedeDatos();
            Statement st;
            st = conexion.createStatement();
            ResultSet rs;
            rs = st.executeQuery(consulta);

            //Si se encontro coincidencia
            while (rs.next() == true) {
                lblPerfil.setText(rs.getString("pe_denominacion"));
            }
            rs.close();
            st.close();
            conexion.close();
        } catch (SQLException SQL) {
            System.out.println("Error en SQL " + SQL.getMessage());
        }
    }

    private void ObtenerHorayFecha() {
        //Obtener fecha y hora
        hilo = new Thread(this);
        hilo.start();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dpEscritorio = new javax.swing.JDesktopPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jpBarra = new javax.swing.JPanel();
        lbAlias = new javax.swing.JLabel();
        lbFechaTitulo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbHoraTitulo = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        lbFecha = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblPerfil = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Principal");
        setName("Fm_Principal"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        dpEscritorio.setBackground(new java.awt.Color(204, 204, 204));
        dpEscritorio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dpEscritorioKeyPressed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoProducto.png"))); // NOI18N
        jButton1.setText("PRODUCTOS");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoClientes.png"))); // NOI18N
        jButton2.setText("CLIENTES");
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoZafra.png"))); // NOI18N
        jButton3.setText("ZAFRAS");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        dpEscritorio.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dpEscritorioLayout = new javax.swing.GroupLayout(dpEscritorio);
        dpEscritorio.setLayout(dpEscritorioLayout);
        dpEscritorioLayout.setHorizontalGroup(
            dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dpEscritorioLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dpEscritorioLayout.setVerticalGroup(
            dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dpEscritorioLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addContainerGap(213, Short.MAX_VALUE))
        );

        jpBarra.setPreferredSize(new java.awt.Dimension(1586, 25));

        lbAlias.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbAlias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbAlias.setText("Error de usuario");

        lbFechaTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbFechaTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFechaTitulo.setText("Fecha:");
        lbFechaTitulo.setFocusable(false);
        lbFechaTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoUsuario.png"))); // NOI18N
        jLabel1.setText("Usuario:");

        lbHoraTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbHoraTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbHoraTitulo.setText("Hora:");
        lbHoraTitulo.setFocusable(false);
        lbHoraTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbHora.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbHora.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbHora.setText("00:00:00");
        lbHora.setFocusable(false);
        lbHora.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbFecha.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbFecha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbFecha.setText("00/00/0000");
        lbFecha.setFocusable(false);
        lbFecha.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Perfil:");

        lblPerfil.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblPerfil.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPerfil.setText("Error de perfil");

        javax.swing.GroupLayout jpBarraLayout = new javax.swing.GroupLayout(jpBarra);
        jpBarra.setLayout(jpBarraLayout);
        jpBarraLayout.setHorizontalGroup(
            jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBarraLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbAlias, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(634, 634, 634)
                .addComponent(lbFechaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFecha)
                .addGap(29, 29, 29)
                .addComponent(lbHoraTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpBarraLayout.setVerticalGroup(
            jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBarraLayout.createSequentialGroup()
                .addGroup(jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lbAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpBarraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lbFechaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbHoraTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbFechaTitulo.getAccessibleContext().setAccessibleName("");

        jMenuBar1.setMinimumSize(new java.awt.Dimension(120, 70));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(120, 70));

        jMenu8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoInventario.png"))); // NOI18N
        jMenu8.setText("INVENTARIOS");
        jMenu8.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu8.setPreferredSize(new java.awt.Dimension(270, 70));
        jMenu8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu8ActionPerformed(evt);
            }
        });

        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEntrada.png"))); // NOI18N
        jMenuItem11.setText("ENTRADAS");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem11);
        jMenu8.add(jSeparator11);

        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoSalida.png"))); // NOI18N
        jMenuItem18.setText("SALIDAS");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem18);

        jMenuBar1.add(jMenu8);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoZafra.png"))); // NOI18N
        jMenu2.setText("ZAFRAS");
        jMenu2.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu2.setMinimumSize(new java.awt.Dimension(200, 70));
        jMenu2.setPreferredSize(new java.awt.Dimension(270, 70));
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItem10.setText("PARCELAS");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);
        jMenu2.add(jSeparator16);

        jMenuItem7.setText("ESTABLECIMIENTOS");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);
        jMenu2.add(jSeparator14);

        jMenuItem2.setText("SOCIOS");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoProducto.png"))); // NOI18N
        jMenu3.setText("PRODUCTOS");
        jMenu3.setToolTipText("");
        jMenu3.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu3.setMinimumSize(new java.awt.Dimension(200, 70));
        jMenu3.setPreferredSize(new java.awt.Dimension(270, 70));
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        jMenuItem22.setText("FABRICANTES");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem22);
        jMenu3.add(jSeparator5);

        jMenuItem8.setText("EMPRESAS REGISTRANTES");
        jMenuItem8.setToolTipText("");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);
        jMenu3.add(jSeparator8);

        jMenuItem16.setText("FORMULACIONES");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem16);
        jMenu3.add(jSeparator9);

        jMenuItem3.setText("INGREDIENTES ACTIVOS");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);
        jMenu3.add(jSeparator10);

        jMenuItem21.setText("CLASES DE PRODUCTO");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem21);
        jMenu3.add(jSeparator12);

        jMenuItem20.setText("TIPOS DE AGROQUIMICOS");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem20);
        jMenu3.add(jSeparator13);

        jMenuItem23.setText("DOSIS RECOMENDADA");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem23);

        jMenuBar1.add(jMenu3);

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario.png"))); // NOI18N
        jMenu4.setText("USUARIOS");
        jMenu4.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu4.setPreferredSize(new java.awt.Dimension(270, 70));

        jMenuItem4.setText("CLIENTE");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);
        jMenu4.add(jSeparator7);

        jMenuItem9.setText("PRODUCTO");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);
        jMenu4.add(jSeparator6);

        jMenuItem17.setText("USUARIOS");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem17);

        jMenuBar1.add(jMenu4);

        jMenu7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoConfiguracion.png"))); // NOI18N
        jMenu7.setText("CONFIGURACIÓN");
        jMenu7.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu7.setPreferredSize(new java.awt.Dimension(270, 70));

        jMenuItem6.setText("TRUNCAR TABLAS");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem6);

        jMenuBar1.add(jMenu7);

        jMenu6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoSalir.png"))); // NOI18N
        jMenu6.setText("SALIR");
        jMenu6.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu6.setPreferredSize(new java.awt.Dimension(270, 70));

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem19.setText("OK");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem19);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dpEscritorio)
                    .addComponent(jpBarra, javax.swing.GroupLayout.DEFAULT_SIZE, 1597, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dpEscritorio)
                .addGap(0, 0, 0)
                .addComponent(jpBarra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        ABMCliente abmproductor = new ABMCliente(this, false);
        abmproductor.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        /*ABMIngredienteActivo abmingredienteactivo = new ABMIngredienteActivo(null, this, false);
        abmingredienteactivo.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        /*ABMEmpresaRegistrante abmempresaregistrante = new ABMEmpresaRegistrante(null, this, false);
        abmempresaregistrante.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

    }//GEN-LAST:event_jMenuItem9ActionPerformed


    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        /*ABMFormulacion abmformulacion = new ABMFormulacion(null, this, false);
        abmformulacion.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed

    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void ObtenerFechayHora() {
        Date fecha = new Date();
        //Formateando la fecha:
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        lbFecha.setText(formatoFecha.format(fecha));
        DateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        lbHora.setText(formatoHora.format(fecha));
    }


    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Realmente desea salir?", "Advertencia!", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(1);
        }
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ABMProducto abmproducto = new ABMProducto(this, false);
        abmproducto.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        /*ABMTipoAgroquimico abmtipoagroquimico = new ABMTipoAgroquimico(null, null, false);
        abmtipoagroquimico.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        /* ABMClaseProducto abmpaisorigen = new ABMClaseProducto(null, this, false);
        abmpaisorigen.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed

    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void dpEscritorioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dpEscritorioKeyPressed

    }//GEN-LAST:event_dpEscritorioKeyPressed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        /*TruncarTabla truncartabla = new TruncarTabla(this, true);
        truncartabla.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed

    }//GEN-LAST:event_jMenu2ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        /*ABMEstablecimiento abmestablecimiento = new ABMEstablecimiento(this, false);
        abmestablecimiento.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        /*  ABMDosis abmdosis = new ABMDosis(this, false);
        abmdosis.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed

    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        /*ABMParcela abmparcela = new ABMParcela(this, true);
        abmparcela.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ABMCliente abmcliente = new ABMCliente(this, false);
        abmcliente.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        /*ABMZafra abmzafra = new ABMZafra(this, false);
        abmzafra.setVisible(true);*/
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        /* ABMEmpresaVendedora abmempresavendedora = new ABMEmpresaVendedora(this, false);
        abmempresavendedora.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenu8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu8ActionPerformed

    }//GEN-LAST:event_jMenu8ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        /* TablaEntrada tablaentrada = new TablaEntrada(this, false);
        tablaentrada.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem11ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JDesktopPane dpEscritorio;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JPanel jpBarra;
    private javax.swing.JLabel lbAlias;
    private javax.swing.JLabel lbFecha;
    private javax.swing.JLabel lbFechaTitulo;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbHoraTitulo;
    private javax.swing.JLabel lblPerfil;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        Thread current = Thread.currentThread();

        while (current == hilo) {
            ObtenerFechayHora();

        }
    }
}
