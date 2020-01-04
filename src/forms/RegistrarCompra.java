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
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import metodos.Metodos;
import metodos.MetodosCombo;
import metodos.MetodosImagen;
import metodos.MetodosTXT;

/**
 *
 * @author Arnaldo Cantero
 */
public final class RegistrarCompra extends javax.swing.JDialog {

    MetodosTXT metodostxt = new MetodosTXT();
    Metodos metodos = new Metodos();
    MetodosCombo metodoscombo = new MetodosCombo();
    MetodosImagen metodosimagen = new MetodosImagen();
    private final String rutaimagenproducto = "imagenproductos\\imageproducto_";

    public RegistrarCompra(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        lblCambioGuaranies.setText(metodos.ObtenerCambios("Dolares", "Guaranies"));
        lblCambioReales.setText(metodos.ObtenerCambios("Dolares", "Reales"));
        lblCambioPesosArg.setText(metodos.ObtenerCambios("Dolares", "Pesos argentinos"));

        CargarComboBoxes();

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbProveedor, "SELECT prov_codigo, prov_nombre FROM proveedor ORDER BY prov_nombre");
        metodoscombo.setSelectedNombreItem(cbProveedor, "SIN PROVEEDOR");
    }

    public void RegistroNuevo() {
        try {
            if (ComprobarCampos() == true) {
                int proveedor = metodoscombo.ObtenerIdComboBox(cbProveedor);
                int tiporecibo = cbTipoDocumento.getSelectedIndex();
                Date fecha = dcFechaCompra.getDate();
                String numcompra = txtNumCompra.getText();
                String codproducto = txtCodigoProducto.getText();
                String cantidad = txtCantidadAdquirida.getText();
                Double preciocompra = Double.parseDouble(txtPrecioCompra.getText());
                Double totalcompra = Double.parseDouble(lblTotalCompra.getText());

                int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);

                if (JOptionPane.YES_OPTION == confirmado) {
                    //REGISTRAR NUEVO
                    try {
                        Connection con;
                        con = (Connection) Conexion.ConectarBasedeDatos();
                        String sentencia = "CALL SP_AltaCompraAlta ('" + proveedor + "','" + tiporecibo + "','"
                                + fecha + "','" + numcompra + "','" + codproducto + "','" + cantidad + "','" + preciocompra + "','" + totalcompra + "')";
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
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("No se guardó el registro");
                }
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(null, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
        }
    }

    private void ModoEdicion(boolean valor) {
        txtNumCompra.setEnabled(valor);
        cbProveedor.setEnabled(valor);
        btnProveedor.setEnabled(valor);
        cbTipoDocumento.setEnabled(valor);
        dcFechaCompra.setEnabled(valor);
        txtCodigoProducto.setEnabled(valor);
        btnProducto.setEnabled(valor);
        txtCantidadAdquirida.setEnabled(valor);
        txtPrecioCompra.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);
        cbMoneda.setEnabled(valor);

        txtNumCompra.requestFocus();
    }

    private void Limpiar() {
        metodoscombo.setSelectedNombreItem(cbProveedor, "SIN PROVEEDOR");
        cbTipoDocumento.setSelectedIndex(1);
        Calendar c2 = new GregorianCalendar();
        dcFechaCompra.setCalendar(c2);
        txtNumCompra.setText("");

        txtCodigoProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");
        URL url = this.getClass().getResource("/images/IconoProductoSinFoto.png");
        lbImagen.setIcon(new ImageIcon(url));
        txtPrecioDolares.setText("");
        txtPrecioGuaranies.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");

        txtCantidadAdquirida.setText("0");
        txtPrecioCompra.setText("0");
        cbMoneda.setSelectedIndex(0);
    }

    public boolean ComprobarCampos() {
        if (dcFechaCompra.getDate().equals("")) {
            lblRucCedula.setText("Ingrese el RUC/CI:");
            lblRucCedula.setForeground(Color.RED);
            lblRucCedula.requestFocus();
            Toolkit.getDefaultToolkit().beep();
            return false;
        }

        return true;
    }

    public boolean ConsultaProducto() {
        String codProducto = txtCodigoProducto.getText();
        try {
            Conexion con = metodos.ObtenerRSSentencia("SELECT pro_existencia, pro_descripcion, pro_codigo, pro_precio "
                    + "FROM producto WHERE pro_identificador = '" + codProducto + "'");
            if (con.rs.next() == true) {
                txtExistenciaActual.setText(con.rs.getString(1));
                txtDescripcionProducto.setText(con.rs.getString(2));

                if (metodosimagen.LeerImagen(lbImagen, rutaimagenproducto + con.rs.getString(3)) == false) {
                    URL url = this.getClass().getResource("/images/IconoProductoSinFoto.png");
                    lbImagen.setIcon(new ImageIcon(url));
                }
                String precio = con.rs.getString(4).replace(".", ",");
                precio = metodostxt.PonerPuntosMilesKeyReleased(precio);
                txtPrecioDolares.setText(precio);

                return true;
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al intentar obtener cambio " + ex);
            System.out.println("Error al consultar producto: ");
            ex.printStackTrace();
        }
        return false;
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
        lblRucCedula = new javax.swing.JLabel();
        cbProveedor = new javax.swing.JComboBox<>();
        btnProveedor = new javax.swing.JButton();
        cbTipoDocumento = new javax.swing.JComboBox<>();
        lblRucCedula1 = new javax.swing.JLabel();
        lblRucCedula2 = new javax.swing.JLabel();
        dcFechaCompra = new com.toedter.calendar.JDateChooser();
        lblCodigo1 = new javax.swing.JLabel();
        txtNumCompra = new javax.swing.JTextField();
        jpDatosProducto = new javax.swing.JPanel();
        lblCodigo4 = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        lblRucCedula6 = new javax.swing.JLabel();
        btnProducto = new javax.swing.JButton();
        txtDescripcionProducto = new javax.swing.JTextField();
        lblCodigo6 = new javax.swing.JLabel();
        txtExistenciaActual = new javax.swing.JTextField();
        lblCodigo7 = new javax.swing.JLabel();
        txtPrecioGuaranies = new javax.swing.JTextField();
        lblGuaraniess = new javax.swing.JLabel();
        lblFlagGuaranies = new javax.swing.JLabel();
        lblFlagBrasil = new javax.swing.JLabel();
        txtPrecioReales = new javax.swing.JTextField();
        lblReales = new javax.swing.JLabel();
        lblFlagPesosArg = new javax.swing.JLabel();
        txtPrecioPesosArg = new javax.swing.JTextField();
        lblCodigo13 = new javax.swing.JLabel();
        lbImagen = new javax.swing.JLabel();
        lblFlagEeuu = new javax.swing.JLabel();
        txtPrecioDolares = new javax.swing.JTextField();
        lblDolares = new javax.swing.JLabel();
        jpProductos = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnAnadir = new javax.swing.JButton();
        txtPrecioCompra = new javax.swing.JTextField();
        lblCodigo8 = new javax.swing.JLabel();
        txtCantidadAdquirida = new javax.swing.JTextField();
        lblCodigo5 = new javax.swing.JLabel();
        scPrincipal = new javax.swing.JScrollPane();
        cbMoneda = new javax.swing.JComboBox<>();
        lblCodigo10 = new javax.swing.JLabel();
        jpBotones1 = new javax.swing.JPanel();
        lblCambioGuaranies = new javax.swing.JLabel();
        lbldolares3 = new javax.swing.JLabel();
        lbldolares4 = new javax.swing.JLabel();
        lblCambioReales = new javax.swing.JLabel();
        lblCambioPesosArg = new javax.swing.JLabel();
        lbldolares7 = new javax.swing.JLabel();
        lblCodigo9 = new javax.swing.JLabel();
        lblTotalCompra = new javax.swing.JLabel();
        lblCodigo11 = new javax.swing.JLabel();

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
        jpBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
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
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoGuardar40.png"))); // NOI18N
        btnGuardar.setText("GUARDAR");
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

        btnCancelar.setBackground(new java.awt.Color(255, 101, 101));
        btnCancelar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoCancelar40.png"))); // NOI18N
        btnCancelar.setText("CANCELAR");
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
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jpDatosCompra.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosCompra.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de la compra"));

        lblRucCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula.setText("Proveedor");
        lblRucCedula.setToolTipText("");

        cbProveedor.setEnabled(false);

        btnProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnProveedor.setEnabled(false);

        cbTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN ESPECIFICAR", "NOTA", "RECIBO", "FACTURA" }));
        cbTipoDocumento.setSelectedIndex(1);
        cbTipoDocumento.setEnabled(false);

        lblRucCedula1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula1.setText("Tipo de documento");
        lblRucCedula1.setToolTipText("");

        lblRucCedula2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula2.setText("Fecha");
        lblRucCedula2.setToolTipText("");

        dcFechaCompra.setEnabled(false);

        lblCodigo1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo1.setText("N° de compra");

        txtNumCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtNumCompra.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNumCompra.setEnabled(false);

        javax.swing.GroupLayout jpDatosCompraLayout = new javax.swing.GroupLayout(jpDatosCompra);
        jpDatosCompra.setLayout(jpDatosCompraLayout);
        jpDatosCompraLayout.setHorizontalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCodigo1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbTipoDocumento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );
        jpDatosCompraLayout.setVerticalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo1)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jpDatosProducto.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del producto"));

        lblCodigo4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo4.setText("Código del producto");

        txtCodigoProducto.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txtCodigoProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigoProducto.setEnabled(false);
        txtCodigoProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoProductoKeyReleased(evt);
            }
        });

        lblRucCedula6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula6.setText("Descripción del producto");
        lblRucCedula6.setToolTipText("");

        btnProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnProducto.setEnabled(false);

        txtDescripcionProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtDescripcionProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDescripcionProducto.setEnabled(false);

        lblCodigo6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo6.setText("Stock actual");

        txtExistenciaActual.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtExistenciaActual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtExistenciaActual.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtExistenciaActual.setEnabled(false);

        lblCodigo7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo7.setText("Precio actual");

        txtPrecioGuaranies.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPrecioGuaranies.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioGuaranies.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioGuaranies.setEnabled(false);

        lblGuaraniess.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblGuaraniess.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblGuaraniess.setText("Guaraníes");

        lblFlagGuaranies.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paraguay.png"))); // NOI18N

        lblFlagBrasil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/brasil.png"))); // NOI18N

        txtPrecioReales.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPrecioReales.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioReales.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioReales.setEnabled(false);
        txtPrecioReales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioRealesActionPerformed(evt);
            }
        });

        lblReales.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblReales.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblReales.setText("Reales");

        lblFlagPesosArg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/argentina.png"))); // NOI18N

        txtPrecioPesosArg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPrecioPesosArg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioPesosArg.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioPesosArg.setEnabled(false);
        txtPrecioPesosArg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioPesosArgActionPerformed(evt);
            }
        });

        lblCodigo13.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo13.setText("Pesos arg.");

        lbImagen.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        lbImagen.setForeground(new java.awt.Color(255, 255, 255));
        lbImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/IconoProductoSinFoto.png"))); // NOI18N
        lbImagen.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        lbImagen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblFlagEeuu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        txtPrecioDolares.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPrecioDolares.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioDolares.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioDolares.setEnabled(false);
        txtPrecioDolares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioDolaresActionPerformed(evt);
            }
        });

        lblDolares.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDolares.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDolares.setText("Dólares");

        javax.swing.GroupLayout jpDatosProductoLayout = new javax.swing.GroupLayout(jpDatosProducto);
        jpDatosProducto.setLayout(jpDatosProductoLayout);
        jpDatosProductoLayout.setHorizontalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigo4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(txtExistenciaActual)))
                    .addComponent(lblRucCedula6, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescripcionProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblFlagEeuu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0)
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPrecioDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioReales, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblCodigo13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblDolares, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGuaraniess, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        jpDatosProductoLayout.setVerticalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigo4)
                            .addComponent(lblCodigo6))
                        .addGap(2, 2, 2)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblRucCedula6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addComponent(lblCodigo7)
                                .addGap(2, 2, 2)
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPrecioDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPrecioGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblGuaraniess, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPrecioReales, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCodigo13, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpProductos.setBackground(new java.awt.Color(233, 255, 255));
        jpProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        jButton6.setEnabled(false);

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        btnEliminar.setEnabled(false);

        btnAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnAnadir.setEnabled(false);

        txtPrecioCompra.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtPrecioCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioCompra.setText("0");
        txtPrecioCompra.setToolTipText("");
        txtPrecioCompra.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioCompra.setEnabled(false);

        lblCodigo8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo8.setText("Precio de compra");

        txtCantidadAdquirida.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtCantidadAdquirida.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCantidadAdquirida.setText("0");
        txtCantidadAdquirida.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCantidadAdquirida.setEnabled(false);

        lblCodigo5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo5.setText("Cantidad adquirida");

        cbMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMoneda.setEnabled(false);

        lblCodigo10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo10.setText("Moneda");

        javax.swing.GroupLayout jpProductosLayout = new javax.swing.GroupLayout(jpProductos);
        jpProductos.setLayout(jpProductosLayout);
        jpProductosLayout.setHorizontalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigo5)
                            .addComponent(txtCantidadAdquirida, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo8))
                        .addGap(1, 1, 1)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAnadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scPrincipal))
                .addContainerGap())
        );
        jpProductosLayout.setVerticalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigo5)
                            .addComponent(lblCodigo8)))
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lblCodigo10)))
                .addGap(2, 2, 2)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCantidadAdquirida, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBotones1.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cambio"));
        jpBotones1.setPreferredSize(new java.awt.Dimension(100, 50));

        lblCambioGuaranies.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCambioGuaranies.setForeground(new java.awt.Color(0, 0, 153));
        lblCambioGuaranies.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCambioGuaranies.setText("0,000");
        lblCambioGuaranies.setToolTipText("");

        lbldolares3.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        lbldolares3.setForeground(new java.awt.Color(0, 0, 153));
        lbldolares3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares3.setText("Dólar x Guaraníes");

        lbldolares4.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        lbldolares4.setForeground(new java.awt.Color(0, 0, 153));
        lbldolares4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares4.setText("Dólar x Real");

        lblCambioReales.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCambioReales.setForeground(new java.awt.Color(0, 0, 153));
        lblCambioReales.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCambioReales.setText("0,000");

        lblCambioPesosArg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCambioPesosArg.setForeground(new java.awt.Color(0, 0, 153));
        lblCambioPesosArg.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCambioPesosArg.setText("0,000");

        lbldolares7.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        lbldolares7.setForeground(new java.awt.Color(0, 0, 153));
        lbldolares7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares7.setText("Dólar x Pesos arg.");

        javax.swing.GroupLayout jpBotones1Layout = new javax.swing.GroupLayout(jpBotones1);
        jpBotones1.setLayout(jpBotones1Layout);
        jpBotones1Layout.setHorizontalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbldolares7)
                    .addComponent(lbldolares4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbldolares3, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
                .addContainerGap(13, Short.MAX_VALUE))
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCambioGuaranies, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCambioReales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCambioPesosArg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotones1Layout.setVerticalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbldolares3)
                .addGap(1, 1, 1)
                .addComponent(lblCambioGuaranies)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbldolares4)
                .addGap(1, 1, 1)
                .addComponent(lblCambioReales)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbldolares7)
                .addGap(1, 1, 1)
                .addComponent(lblCambioPesosArg)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        lblCodigo9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo9.setForeground(new java.awt.Color(0, 102, 51));
        lblCodigo9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo9.setText("Total de la compra");

        lblTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalCompra.setText("0,000");

        lblCodigo11.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        lblCodigo11.setForeground(new java.awt.Color(0, 102, 51));
        lblCodigo11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo11.setText("Dólares");
        lblCodigo11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpBanner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jpDatosCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpDatosProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpPrincipalLayout.createSequentialGroup()
                                .addComponent(lblTotalCompra)
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigo11, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblCodigo9))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(jpBanner, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpDatosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpDatosProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCodigo9)
                        .addGap(5, 5, 5)
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBanner.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 920, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("Inventario");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
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

    private void txtPrecioDolaresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioDolaresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioDolaresActionPerformed

    private void txtPrecioRealesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioRealesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioRealesActionPerformed

    private void txtPrecioPesosArgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioPesosArgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioPesosArgActionPerformed

    private void txtCodigoProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyReleased
        if (ConsultaProducto() == true) {
            //Convertir precio a las distintas monedas
            if (txtPrecioDolares.getText().equals("") == false) {
                System.out.println("No esta vacio");
                Double precioDolares = Double.parseDouble((txtPrecioDolares.getText().replace(".", "")).replace(",", "."));

                Double cambioGuaranies = Double.parseDouble(lblCambioGuaranies.getText().replace(",", "."));
                String precioGuaranies = ((precioDolares * cambioGuaranies) + "").replace(".", ",");
                precioGuaranies = metodostxt.PonerPuntosMilesKeyReleased(precioGuaranies + "");

                Double cambioReales = Double.parseDouble(lblCambioReales.getText().replace(",", "."));
                String precioReales = ((precioDolares * cambioReales) + "").replace(".", ",");
                precioReales = metodostxt.PonerPuntosMilesKeyReleased(precioReales + "");

                Double cambioPesosArg = Double.parseDouble(lblCambioPesosArg.getText().replace(",", "."));
                String precioPesosArg = ((precioDolares * cambioPesosArg) + "").replace(".", ",");
                precioPesosArg = metodostxt.PonerPuntosMilesKeyReleased(precioPesosArg + "");
                txtPrecioGuaranies.setText(precioGuaranies);
                txtPrecioReales.setText(precioReales);
                txtPrecioPesosArg.setText(precioPesosArg);
            }
        } else {
            txtExistenciaActual.setText("");
            txtDescripcionProducto.setText("");
            URL url = this.getClass().getResource("/images/IconoProductoSinFoto.png");
            lbImagen.setIcon(new ImageIcon(url));
            txtPrecioDolares.setText("");
            txtPrecioGuaranies.setText("");
            txtPrecioReales.setText("");
            txtPrecioPesosArg.setText("");
        }
    }//GEN-LAST:event_txtCodigoProductoKeyReleased

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(cbProveedor);
        ordenTabulador.add(cbTipoDocumento);
        ordenTabulador.add(dcFechaCompra);
        ordenTabulador.add(txtNumCompra);
        ordenTabulador.add(txtCodigoProducto);
        ordenTabulador.add(txtCantidadAdquirida);
        ordenTabulador.add(txtCantidadAdquirida);
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
    private javax.swing.JButton btnAnadir;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnProducto;
    private javax.swing.JButton btnProveedor;
    private javax.swing.JComboBox<String> cbMoneda;
    private javax.swing.JComboBox<MetodosCombo> cbProveedor;
    private javax.swing.JComboBox<String> cbTipoDocumento;
    private com.toedter.calendar.JDateChooser dcFechaCompra;
    private javax.swing.JButton jButton6;
    private javax.swing.JPanel jpBanner;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones1;
    private javax.swing.JPanel jpDatosCompra;
    private javax.swing.JPanel jpDatosProducto;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private javax.swing.JLabel lbBanner;
    private javax.swing.JLabel lbImagen;
    private javax.swing.JLabel lblCambioGuaranies;
    private javax.swing.JLabel lblCambioPesosArg;
    private javax.swing.JLabel lblCambioReales;
    private javax.swing.JLabel lblCodigo1;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo11;
    private javax.swing.JLabel lblCodigo13;
    private javax.swing.JLabel lblCodigo4;
    private javax.swing.JLabel lblCodigo5;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblCodigo8;
    private javax.swing.JLabel lblCodigo9;
    private javax.swing.JLabel lblDolares;
    private javax.swing.JLabel lblFlagBrasil;
    private javax.swing.JLabel lblFlagEeuu;
    private javax.swing.JLabel lblFlagGuaranies;
    private javax.swing.JLabel lblFlagPesosArg;
    private javax.swing.JLabel lblGuaraniess;
    private javax.swing.JLabel lblReales;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula1;
    private javax.swing.JLabel lblRucCedula2;
    private javax.swing.JLabel lblRucCedula6;
    private javax.swing.JLabel lblTotalCompra;
    private javax.swing.JLabel lbldolares3;
    private javax.swing.JLabel lbldolares4;
    private javax.swing.JLabel lbldolares7;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTextField txtCantidadAdquirida;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtExistenciaActual;
    private javax.swing.JTextField txtNumCompra;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtPrecioDolares;
    private javax.swing.JTextField txtPrecioGuaranies;
    private javax.swing.JTextField txtPrecioPesosArg;
    private javax.swing.JTextField txtPrecioReales;
    // End of variables declaration//GEN-END:variables
}
