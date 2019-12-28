/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import metodos.Metodos;
import metodos.MetodosTXT;

/**
 *
 * @author Arnaldo Cantero
 */
public final class RegistrarCompra extends javax.swing.JDialog {

    MetodosTXT metodostxt = new MetodosTXT();
    Metodos metodos = new Metodos();

    public RegistrarCompra(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        /*ImagenBanner p = new ImagenBanner(); //Produce error, al ejecutar en el jar la ventana se ve blanco
        jpBanner.add(p);*/
        TablaConsultaBD(txtBuscar.getText()); //Trae todos los registros
        txtBuscar.requestFocus();

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void RegistroNuevo() {
        try {
            if (ComprobarCampos() == true) {
                String rucci = txtRucCedula.getText();
                String nombre = txtNombre.getText();
                String apellido = txtApellido.getText();
                String direccion = txtDireccion.getText();
                String email = txtEmail.getText();
                String telefono = txtTelefono.getText();
                String obs = taObs.getText();

                int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);

                if (JOptionPane.YES_OPTION == confirmado) {
                    //REGISTRAR NUEVO
                    try {
                        Connection con;
                        con = (Connection) Conexion.ConectarBasedeDatos();
                        String sentencia = "CALL SP_ClienteAlta ('" + rucci + "','" + nombre + "','"
                                + apellido + "','" + direccion + "','" + email + "','" + telefono + "','" + obs + "')";
                        System.out.println("Insertar registro: " + sentencia);
                        Statement st;
                        st = (Statement) con.createStatement();
                        st.executeUpdate(sentencia);

                        con.close();
                        st.close();
                        JOptionPane.showMessageDialog(this, "Se agrego correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                        ModoEdicion(false);
                        Limpiar();
                    } catch (HeadlessException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                    }
                } else {
                    System.out.println("No se guardó el registro");
                }
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(null, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
            txtNombre.requestFocus();
        }
    }

    public void RegistroModificar() {
        //guarda los datos que se han modificado en los campos

        if (ComprobarCampos() == true) {
            String codigo = txtCodigo.getText();
            String rucci = txtRucCedula.getText();
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String direccion = txtDireccion.getText();
            String telefono = txtTelefono.getText();
            String email = txtEmail.getText();
            String obs = taObs.getText();

            int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro de modificar este registro?", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                String sentencia = "CALL SP_ClienteModificar(" + codigo + ",'" + rucci + "','" + nombre + "','" + apellido + "','" + direccion
                        + "','" + telefono + "','" + email + "','" + obs + "')";
                System.out.println("Actualizar registro: " + sentencia);

                try {
                    Connection con;
                    con = conexion.Conexion.ConectarBasedeDatos();
                    PreparedStatement pst;
                    pst = con.prepareStatement(sentencia);
                    pst.executeUpdate();
                    getToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Registro modificado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                    con.close();
                    pst.close();
                } catch (SQLException ex) {
                    System.out.println("Error al modificar registro " + ex);
                    JOptionPane.showMessageDialog(null, "Error al intentar modificar el registro", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                ModoEdicion(false);
                Limpiar();
            } else {
                System.out.println("No se modificó el registro");
            }
        }
    }

    private void RegistroEliminar() {
        int filasel;
        String codigo;
        try {
            filasel = tbPrincipal.getSelectedRow();
            if (filasel == -1) {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
                txtBuscar.requestFocus();
            } else {
                int confirmado = javax.swing.JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar este registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (confirmado == JOptionPane.YES_OPTION) {
                    codigo = (String) tbPrincipal.getModel().getValueAt(filasel, 0);
                    try {
                        Connection con;
                        con = Conexion.ConectarBasedeDatos();
                        String sentence;
                        sentence = "CALL SP_ClienteEliminar(" + codigo + ")";
                        PreparedStatement pst;
                        pst = con.prepareStatement(sentence);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Registro eliminado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                        con.close();
                        pst.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex);
                        JOptionPane.showMessageDialog(null, "Error al intentar eliminar el registro", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    ModoEdicion(false);
                    Limpiar();
                    JOptionPane.showMessageDialog(null, "Cancelado correctamente", "Información", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (HeadlessException e) {
            System.out.println("Error al intentar eliminar registro" + e);
        }
    }

    public void TablaConsultaBD(String filtro) {//Realiza la consulta de los productos que tenemos en la base de datos
        String nombresp = "SP_ClienteConsulta";
        String titlesJtabla[] = {"Código", "RUC/CI", "Nombre", "Apellido", "Dirección", "Teléfono", "Email", "Observación"}; //Debe tener la misma cantidad que titlesconsulta
        String titlesconsulta[] = {"cli_codigo", "cli_ruccedula", "cli_nombre", "cli_apellido", "cli_direccion", "cli_telefono", "cli_email", "cli_obs"};

        metodos.ConsultaFiltroTablaBD(tbPrincipal, titlesJtabla, titlesconsulta, nombresp, filtro, cbCampoBuscar);
        metodos.AnchuraColumna(tbPrincipal);
        lbCantRegistros.setText(metodos.CantRegistros + " Registros encontrados");
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString());
        txtRucCedula.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1).toString());
        txtNombre.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2).toString());
        txtApellido.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3).toString());
        txtDireccion.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4).toString());
        txtTelefono.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5).toString());
        txtEmail.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 6).toString());
        taObs.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 7).toString());
    }

    private void ModoEdicion(boolean valor) {
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        txtRucCedula.setEnabled(valor);
        txtNombre.setEnabled(valor);
        txtApellido.setEnabled(valor);
        txtDireccion.setEnabled(valor);
        txtEmail.setEnabled(valor);
        txtTelefono.setEnabled(valor);
        taObs.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);
        btnReporte.setEnabled(!valor);

        txtRucCedula.requestFocus();
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtRucCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtDireccion.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        taObs.setText("");

        lblRucCedula.setForeground(new Color(102, 102, 102));
        lblNombre.setForeground(new Color(102, 102, 102));
        lblApellido.setForeground(new Color(102, 102, 102));
        lblDireccion.setForeground(new Color(102, 102, 102));

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();
    }

    public boolean ComprobarCampos() {
        if (txtRucCedula.getText().equals("")) {
            lblRucCedula.setText("Ingrese el RUC/CI:");
            lblRucCedula.setForeground(Color.RED);
            lblRucCedula.requestFocus();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        if (txtCodigo.getText().equals("")) {
            try {
                Conexion con = metodos.ObtenerRSSentencia("SELECT cli_ruccedula FROM cliente "
                        + "WHERE cli_ruccedula='" + txtRucCedula.getText() + "'");
                if (con.rs.next() == true) { //Si ya existe el numero de cedula en la bd de clientes
                    System.out.println("El CI ingresado ya existe en la bd");
                    lblRucCedula.setText("El Ruc o CI ya existe: ");
                    lblRucCedula.setForeground(Color.RED);
                    lblRucCedula.requestFocus();
                    con.DesconectarBasedeDatos();
                    Toolkit.getDefaultToolkit().beep();
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar si ci ya existe en bd: " + e);
            } catch (NullPointerException e) {
                System.out.println("La CI ingresada no existe en la bd, aprobado: " + e);
            }
        }

        if (txtNombre.getText().equals("")) {
            lblNombre.setText("Ingrese el nombre:");
            lblNombre.setForeground(Color.RED);
            lblNombre.requestFocus();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        if (txtApellido.getText().equals("")) {
            lblApellido.setText("Ingrese el apellido:");
            lblApellido.setForeground(Color.RED);
            lblApellido.requestFocus();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        if (txtDireccion.getText().equals("")) {
            lblDireccion.setText("Ingrese la dirección:");
            lblDireccion.setForeground(Color.RED);
            lblDireccion.requestFocus();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpBanner = new javax.swing.JPanel();
        lbBanner = new javax.swing.JLabel();
        jpBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jpDatosCompra = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblRucCedula = new javax.swing.JLabel();
        cbProveedor = new javax.swing.JComboBox<>();
        btnProveedor = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        lblRucCedula1 = new javax.swing.JLabel();
        lblRucCedula2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        lblCodigo1 = new javax.swing.JLabel();
        txtCodigo1 = new javax.swing.JTextField();
        jpDatosProducto = new javax.swing.JPanel();
        lblCodigo4 = new javax.swing.JLabel();
        txtCodigo4 = new javax.swing.JTextField();
        lblRucCedula6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        txtCodigo6 = new javax.swing.JTextField();
        lblCodigo6 = new javax.swing.JLabel();
        txtCodigo7 = new javax.swing.JTextField();
        lblCodigo7 = new javax.swing.JLabel();
        txtCodigo8 = new javax.swing.JTextField();
        lblCodigo10 = new javax.swing.JLabel();
        jpProductos = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        txtCodigo11 = new javax.swing.JTextField();
        lblCodigo9 = new javax.swing.JLabel();
        txtCodigo10 = new javax.swing.JTextField();
        lblCodigo8 = new javax.swing.JLabel();
        txtCodigo9 = new javax.swing.JTextField();
        lblCodigo5 = new javax.swing.JLabel();
        scPrincipal = new javax.swing.JScrollPane();

        setTitle("Ventana Registrar Compra");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpBanner.setBackground(new java.awt.Color(0, 51, 102));
        jpBanner.setPreferredSize(new java.awt.Dimension(1000, 82));

        lbBanner.setFont(new java.awt.Font("Franklin Gothic Medium", 1, 36)); // NOI18N
        lbBanner.setForeground(new java.awt.Color(255, 255, 255));
        lbBanner.setText("REGISTRAR COMPRA");
        lbBanner.setMaximumSize(new java.awt.Dimension(1100, 52));
        lbBanner.setMinimumSize(new java.awt.Dimension(1100, 52));
        lbBanner.setPreferredSize(new java.awt.Dimension(1100, 52));

        javax.swing.GroupLayout jpBannerLayout = new javax.swing.GroupLayout(jpBanner);
        jpBanner.setLayout(jpBannerLayout);
        jpBannerLayout.setHorizontalGroup(
            jpBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBannerLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(lbBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBannerLayout.setVerticalGroup(
            jpBannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBannerLayout.createSequentialGroup()
                .addComponent(lbBanner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnNuevo.setBackground(new java.awt.Color(14, 154, 153));
        btnNuevo.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoNuevo.png"))); // NOI18N
        btnNuevo.setText("NUEVO");
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevo.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnNuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setToolTipText("Inserta el nuevo registro");
        btnGuardar.setEnabled(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setPreferredSize(new java.awt.Dimension(128, 36));
        btnGuardar.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        btnGuardar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGuardarKeyPressed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 0, 51));
        btnCancelar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Cancela la acción");
        btnCancelar.setEnabled(false);
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelar.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnCancelar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotonesLayout = new javax.swing.GroupLayout(jpBotones);
        jpBotones.setLayout(jpBotonesLayout);
        jpBotonesLayout.setHorizontalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpDatosCompra.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosCompra.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de la compra"));

        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo.setText("Código");

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblRucCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula.setText("Proveedor");
        lblRucCedula.setToolTipText("");

        cbProveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN PROVEEDOR" }));
        cbProveedor.setEnabled(false);

        btnProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnProveedor.setEnabled(false);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Factura", "Recibo" }));
        jComboBox2.setSelectedIndex(1);
        jComboBox2.setEnabled(false);

        lblRucCedula1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula1.setText("Tipo de documento");
        lblRucCedula1.setToolTipText("");

        lblRucCedula2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula2.setText("Fecha de compra");
        lblRucCedula2.setToolTipText("");

        jDateChooser1.setEnabled(false);

        lblCodigo1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo1.setText("N° de compra");

        txtCodigo1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo1.setEnabled(false);

        javax.swing.GroupLayout jpDatosCompraLayout = new javax.swing.GroupLayout(jpDatosCompra);
        jpDatosCompra.setLayout(jpDatosCompraLayout);
        jpDatosCompraLayout.setHorizontalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblRucCedula2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCodigo)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblRucCedula1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)))
                    .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblCodigo1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                        .addComponent(txtCodigo1, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDatosCompraLayout.setVerticalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                                .addComponent(lblCodigo)
                                .addGap(2, 2, 2)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jpDatosCompraLayout.createSequentialGroup()
                                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(1, 1, 1)
                                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(2, 2, 2))
                                .addGroup(jpDatosCompraLayout.createSequentialGroup()
                                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(2, 2, 2)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                                .addComponent(lblRucCedula2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                                .addComponent(lblCodigo1)
                                .addGap(2, 2, 2)
                                .addComponent(txtCodigo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpDatosProducto.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del producto"));

        lblCodigo4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo4.setText("Código del producto");

        txtCodigo4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txtCodigo4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo4.setEnabled(false);

        lblRucCedula6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula6.setText("Descripción del producto");
        lblRucCedula6.setToolTipText("");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        jButton3.setEnabled(false);

        txtCodigo6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo6.setEnabled(false);

        lblCodigo6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo6.setText("Stock actual");

        txtCodigo7.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCodigo7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo7.setEnabled(false);

        lblCodigo7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo7.setText("Precio actual");

        txtCodigo8.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCodigo8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo8.setEnabled(false);

        lblCodigo10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo10.setText("Dólares");

        javax.swing.GroupLayout jpDatosProductoLayout = new javax.swing.GroupLayout(jpDatosProducto);
        jpDatosProducto.setLayout(jpDatosProductoLayout);
        jpDatosProductoLayout.setHorizontalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo6, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula6, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigo4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addComponent(txtCodigo4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigo7)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigo8)
                            .addComponent(lblCodigo7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4)
                        .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDatosProductoLayout.setVerticalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addComponent(lblCodigo4)
                        .addGap(3, 3, 3)
                        .addComponent(txtCodigo4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCodigo6)
                            .addComponent(lblCodigo7))
                        .addGap(3, 3, 3)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigo8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRucCedula6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(txtCodigo6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );

        jpProductos.setBackground(new java.awt.Color(233, 255, 255));
        jpProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        jButton6.setEnabled(false);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        jButton5.setEnabled(false);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        jButton4.setEnabled(false);

        txtCodigo11.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo11.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo11.setEnabled(false);

        lblCodigo9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo9.setText("TOTAL");

        txtCodigo10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo10.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo10.setEnabled(false);

        lblCodigo8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo8.setText("Precio de compra");

        txtCodigo9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo9.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo9.setEnabled(false);

        lblCodigo5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo5.setText("Cantidad adquirida");

        javax.swing.GroupLayout jpProductosLayout = new javax.swing.GroupLayout(jpProductos);
        jpProductos.setLayout(jpProductosLayout);
        jpProductosLayout.setHorizontalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCodigo5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigo9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigo8)
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addComponent(txtCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCodigo9)
                                    .addGroup(jpProductosLayout.createSequentialGroup()
                                        .addComponent(txtCodigo11, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addComponent(scPrincipal))
                .addContainerGap())
        );
        jpProductosLayout.setVerticalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo5)
                    .addComponent(lblCodigo8)
                    .addComponent(lblCodigo9))
                .addGap(4, 4, 4)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCodigo9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo11, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpBanner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jpDatosProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpDatosCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpProductos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(jpBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpDatosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpDatosProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jpBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBanner.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("Inventario");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (txtCodigo.getText().equals("")) {//Si es nuevo
            RegistroNuevo();
        } else { //Si es modificar
            RegistroModificar();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        ModoEdicion(false);
        Limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        Limpiar();
        ModoEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnGuardar.doClick();
        }
    }//GEN-LAST:event_btnGuardarKeyPressed

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtRucCedula);
        ordenTabulador.add(txtNombre);
        ordenTabulador.add(txtApellido);
        ordenTabulador.add(txtDireccion);
        ordenTabulador.add(txtTelefono);
        ordenTabulador.add(txtEmail);
        ordenTabulador.add(taObs);
        ordenTabulador.add(btnGuardar);
        setFocusTraversalPolicy(new PersonalizadoFocusTraversalPolicy());
    }

    private class PersonalizadoFocusTraversalPolicy extends FocusTraversalPolicy {

        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int currentPosition = ordenTabulador.indexOf(aComponent);
            currentPosition = (currentPosition + 1) % ordenTabulador.size();
            return (Component) ordenTabulador.get(currentPosition);
        }

        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int currentPosition = ordenTabulador.indexOf(aComponent);
            currentPosition = (ordenTabulador.size() + currentPosition - 1) % ordenTabulador.size();
            return (Component) ordenTabulador.get(currentPosition);
        }

        public Component getFirstComponent(Container cntnr) {
            return (Component) ordenTabulador.get(0);
        }

        public Component getLastComponent(Container cntnr) {
            return (Component) ordenTabulador.get(ordenTabulador.size() - 1);
        }

        public Component getDefaultComponent(Container cntnr) {
            return (Component) ordenTabulador.get(0);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnProveedor;
    private javax.swing.JComboBox<String> cbProveedor;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jpBanner;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosCompra;
    private javax.swing.JPanel jpDatosProducto;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private javax.swing.JLabel lbBanner;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCodigo1;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo2;
    private javax.swing.JLabel lblCodigo3;
    private javax.swing.JLabel lblCodigo4;
    private javax.swing.JLabel lblCodigo5;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblCodigo8;
    private javax.swing.JLabel lblCodigo9;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula1;
    private javax.swing.JLabel lblRucCedula2;
    private javax.swing.JLabel lblRucCedula3;
    private javax.swing.JLabel lblRucCedula4;
    private javax.swing.JLabel lblRucCedula5;
    private javax.swing.JLabel lblRucCedula6;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCodigo1;
    private javax.swing.JTextField txtCodigo10;
    private javax.swing.JTextField txtCodigo11;
    private javax.swing.JTextField txtCodigo2;
    private javax.swing.JTextField txtCodigo3;
    private javax.swing.JTextField txtCodigo4;
    private javax.swing.JTextField txtCodigo6;
    private javax.swing.JTextField txtCodigo7;
    private javax.swing.JTextField txtCodigo8;
    private javax.swing.JTextField txtCodigo9;
    // End of variables declaration//GEN-END:variables
}
