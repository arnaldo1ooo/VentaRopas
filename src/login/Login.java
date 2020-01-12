/**
 *
 * @author Lic. Arnaldo Cantero
 */
package login;

import conexion.Conexion;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import metodos.MetodosTXT;
import metodos.PlaceHolder;
import principal.SplashScreen;

public class Login extends javax.swing.JFrame {

    public static String CodUsuario;
    public static String NomApeUsuario;
    public static String Alias;
    private String Pass;

    public Login() {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        lblError.setVisible(false);
        PlaceHolder placeholder;
        placeholder = new PlaceHolder("Alias", txtAlias);
        placeholder = new PlaceHolder("Contraseña", txtContrasena);
    }

    //-------------METODOS----------//
    public void IniciarSesion() {
        Alias = txtAlias.getText();
        Pass = String.valueOf(txtContrasena.getPassword());

        String consulta = "CALL SP_UsuarioConsulta ('" + Alias + "','" + Pass + "') ";
        System.out.println("consulta: " + consulta);
        try {
            Connection conexion;
            conexion = Conexion.ConectarBasedeDatos();
            Statement st;
            st = conexion.createStatement();
            ResultSet rs;
            rs = st.executeQuery(consulta);

            //Si se encontro coincidencia
            if (rs.next() == true) {
                CodUsuario = rs.getString("usu_codigo");
                NomApeUsuario = rs.getString("usu_nombre") + " " + rs.getString("usu_apellido");

                SplashScreen splash = new SplashScreen(this, true);
                splash.setVisible(true);
                dispose(); //Cerrar jdialog
            } else {
                JOptionPane.showMessageDialog(this, "Nombre de usuario o contraseña incorrecta!");
                //txtUsuario.setText("");
                txtAlias.requestFocus();
                txtContrasena.setText("");

                lblError.setVisible(true);
            }
            rs.close();
            st.close();
            conexion.close();
        } catch (SQLException e) {
            System.out.println("Error en SQL " + e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("El intento de conexion a la bd trajo NULL " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblError = new javax.swing.JLabel();
        btncancelar = new javax.swing.JButton();
        btnCambiarPass = new javax.swing.JButton();
        panelNice1 = new org.edisoncor.gui.panel.PanelNice();
        txtAlias = new javax.swing.JTextField();
        txtContrasena = new javax.swing.JPasswordField();
        btnok = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inicio de sesión del sistema");
        setBackground(new java.awt.Color(255, 255, 255));
        setIconImage(new ImageIcon(getClass().getResource("/login/iconos/IconoUser.png")).getImage());
        setMinimumSize(new java.awt.Dimension(420, 350));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel1.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(0, 153, 255));
        panel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/principal/iconos/FondoSplash.png"))); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/login/iconos/IconoTituloLogin.png"))); // NOI18N
        jLabel1.setOpaque(true);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/login/iconos/IconoLogin.png"))); // NOI18N

        lblError.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        lblError.setForeground(new java.awt.Color(255, 0, 0));
        lblError.setText("No se pudo iniciar sesión !!!");

        btncancelar.setForeground(new java.awt.Color(255, 255, 255));
        btncancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCancelar.png"))); // NOI18N
        btncancelar.setText("Limpiar campos");
        btncancelar.setToolTipText("Cancelar");
        btncancelar.setContentAreaFilled(false);
        btncancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarActionPerformed(evt);
            }
        });

        btnCambiarPass.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarPass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCambiarPass.png"))); // NOI18N
        btnCambiarPass.setText("Cambiar pass");
        btnCambiarPass.setToolTipText("Cambiar Contraseña");
        btnCambiarPass.setContentAreaFilled(false);
        btnCambiarPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCambiarPassActionPerformed(evt);
            }
        });

        panelNice1.setBackground(new java.awt.Color(51, 51, 51));
        panelNice1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtAlias.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtAlias.setToolTipText("Teclea tu nombre de usuario para ingresar");
        txtAlias.setPreferredSize(new java.awt.Dimension(9, 25));
        txtAlias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAliasKeyPressed(evt);
            }
        });
        panelNice1.add(txtAlias, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 240, 30));

        txtContrasena.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtContrasena.setToolTipText("Teclea tu contraseña para ingresar");
        txtContrasena.setPreferredSize(new java.awt.Dimension(9, 25));
        txtContrasena.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtContrasenaKeyPressed(evt);
            }
        });
        panelNice1.add(txtContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 240, 30));

        btnok.setBackground(new java.awt.Color(0, 153, 153));
        btnok.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        btnok.setForeground(new java.awt.Color(255, 255, 255));
        btnok.setText("Registrarse");
        btnok.setToolTipText("Permite registrarse en el sistema");
        btnok.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnok.setContentAreaFilled(false);
        btnok.setOpaque(true);
        btnok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnokActionPerformed(evt);
            }
        });
        btnok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnokKeyPressed(evt);
            }
        });
        panelNice1.add(btnok, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 164, -1));

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(208, 208, 208)
                        .addComponent(jLabel3))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btncancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCambiarPass)
                        .addGap(27, 27, 27)
                        .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(panelNice1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelNice1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btncancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCambiarPass, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblError, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 520, 370));

        jMenuBar1.setPreferredSize(new java.awt.Dimension(199, 30));

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoArchivo.png"))); // NOI18N
        jMenu1.setText("Archivo");
        jMenu1.setToolTipText("Menú archivo");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoSalir.png"))); // NOI18N
        jMenuItem1.setText("Salir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoOpcion.png"))); // NOI18N
        jMenu2.setText("Opciones");
        jMenu2.setToolTipText("Menu Opciones");
        jMenuBar1.add(jMenu2);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoAyuda.png"))); // NOI18N
        jMenu3.setText("Ayuda");
        jMenu3.setToolTipText("Menu Ayuda");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        getAccessibleContext().setAccessibleName("Login");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnokActionPerformed
        IniciarSesion();
    }//GEN-LAST:event_btnokActionPerformed

    private void btnCambiarPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarPassActionPerformed
        CambiarPass cambiarpass = new CambiarPass(this, true);
        cambiarpass.setVisible(true);
    }//GEN-LAST:event_btnCambiarPassActionPerformed

    private void txtAliasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAliasKeyPressed
        lblError.setVisible(false);
        SiguienteFoco(evt);
    }//GEN-LAST:event_txtAliasKeyPressed

    private void txtContrasenaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContrasenaKeyPressed
        lblError.setVisible(false);
        SiguienteFoco(evt);
    }//GEN-LAST:event_txtContrasenaKeyPressed

    private void btncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarActionPerformed
        Limpiar();
    }//GEN-LAST:event_btncancelarActionPerformed

    private void Limpiar() {
        lblError.setVisible(false);
        txtAlias.setText("");
        txtContrasena.setText("");
        txtAlias.requestFocus();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Realmente desea salir?", "Advertencia!", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btnokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnokKeyPressed
        char car = (char) evt.getKeyCode();
        if (car == KeyEvent.VK_ENTER) {//Al apretar ENTER QUE HAGA ALGO
            btnok.doClick();
        }
    }//GEN-LAST:event_btnokKeyPressed

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
                new Login().setVisible(true);
            }
        });
    }

    public void SiguienteFoco(KeyEvent evt) {
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            ((JComponent) evt.getSource()).transferFocus();//Con esta parte transfieres el foco al siguiente campo sea un Jtextfield, Jpasswordfield, boton, etc..
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiarPass;
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btnok;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JLabel lblError;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.PanelNice panelNice1;
    public static javax.swing.JTextField txtAlias;
    public static javax.swing.JPasswordField txtContrasena;
    // End of variables declaration//GEN-END:variables
}
