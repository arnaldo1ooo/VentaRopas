/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.HeadlessException;
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
import javax.swing.table.DefaultTableModel;
import metodos.Metodos;
import metodos.MetodosCombo;
import metodos.MetodosImagen;
import metodos.MetodosTXT;
//Variables globales
import static principal.Principal.cotiUsdGsCompra;
import static principal.Principal.cotiUsdRsCompra;
import static principal.Principal.cotiUsdPaCompra;
//

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public final class RegistrarCompra extends javax.swing.JDialog {

    MetodosTXT metodostxt = new MetodosTXT();
    Metodos metodos = new Metodos();
    MetodosCombo metodoscombo = new MetodosCombo();
    MetodosImagen metodosimagen = new MetodosImagen();
    private final String rutaFotoProducto = "Fotos\\fotoproductos\\imageproducto_";
    private final String rutaFotoDefault = "/images/ImagenProductoSinFoto.png";
    DefaultTableModel tablemodelo;

    public RegistrarCompra(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        CargarComboBoxes();
        tablemodelo = (DefaultTableModel) tbPrincipal.getModel();
        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbProveedor, "SELECT prov_codigo, prov_nombre FROM proveedor ORDER BY prov_nombre");
        metodoscombo.setSelectedNombreItem(cbProveedor, "SIN PROVEEDOR");
    }

    public void RegistroNuevo() {
        //Registra la compra
        try {
            int cantidadProductos = tbPrincipal.getModel().getRowCount();
            if (ComprobarCamposCompra() == true && cantidadProductos > 0) {
                String numcompra = txtNumCompra.getText();
                int proveedor = metodoscombo.ObtenerIdComboBox(cbProveedor);
                int tipodocumento = cbTipoDocumento.getSelectedIndex();
                Date fecharegistro = dcFechaRegistro.getDate();
                Date fechacompra = dcFechaCompra.getDate();

                int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    //REGISTRAR NUEVA COMPRA
                    try {
                        Connection con;
                        con = (Connection) Conexion.ConectarBasedeDatos();
                        String sentencia = "CALL SP_CompraAlta('" + numcompra + "','" + proveedor + "','"
                                + tipodocumento + "','" + fecharegistro + "','" + fechacompra + "')";
                        System.out.println("Insertar registro: " + sentencia);
                        Statement st;
                        st = (Statement) con.createStatement();
                        st.executeUpdate(sentencia);

                        //Obtener el id de la compra
                        Conexion conexion = metodos.ObtenerRSSentencia("SELECT MAX(pro_codigo) AS ultimoid FROM producto");
                        conexion.rs.next();
                        int idultimacompra = conexion.rs.getInt("ultimoid");
                        conexion.DesconectarBasedeDatos();

                        //Registra los productos de la compra                      
                        String idproducto;
                        int cantidadadquirida;
                        double preciocompra;

                        int cantfila = tbPrincipal.getRowCount();
                        for (int fila = 0; fila < cantfila; fila++) {
                            idproducto = tbPrincipal.getValueAt(fila, 0).toString();
                            cantidadadquirida = Integer.parseInt(tbPrincipal.getValueAt(fila, 1).toString());
                            preciocompra = Double.parseDouble(tbPrincipal.getValueAt(fila, 1).toString());

                            //Comprobar en que moneda se guarda, en caso de ser distinto a dolares, se convierte a dolares
                            if (cbMoneda.getSelectedItem() != "Dolares") {
                                if (cbMoneda.getSelectedItem() == "Guaranies") {
                                    preciocompra = preciocompra * cotiUsdGsCompra;
                                }
                                if (cbMoneda.getSelectedItem() == "Reales") {
                                    preciocompra = preciocompra * cotiUsdRsCompra;
                                }
                                if (cbMoneda.getSelectedItem() == "Peso") {
                                    preciocompra = preciocompra * cotiUsdPaCompra;
                                }
                            }

                            sentencia = "CALL SP_CompraProductosAlta('" + idultimacompra + "','" + idproducto + "','"
                                    + cantidadadquirida + "','" + preciocompra + "')";
                            System.out.println("Insertar registro: " + sentencia);
                            st = (Statement) con.createStatement();
                            st.executeUpdate(sentencia);

                            //Suma la cantidad al stock
                            sentencia = "CALL SP_SumarStock('" + idproducto + "','" + cantidadadquirida + "')";
                            System.out.println("Insertar registro: " + sentencia);
                            st = (Statement) con.createStatement();
                            st.executeUpdate(sentencia);
                        }

                        con.close();
                        st.close();
                        JOptionPane.showMessageDialog(this, "Se agregó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
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
        dcFechaRegistro.setEnabled(valor);
        dcFechaCompra.setEnabled(valor);
        txtCodigoProducto.setEnabled(valor);
        txtCantidadAdquirida.setEnabled(valor);
        txtPrecioCompra.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);
        cbMoneda.setEnabled(valor);
        btnAnadir.setEnabled(valor);
        tbPrincipal.setEnabled(valor);

        txtNumCompra.requestFocus();
    }

    private void Limpiar() {
        metodoscombo.setSelectedNombreItem(cbProveedor, "SIN PROVEEDOR");
        txtNumCompra.setText("");
        metodoscombo.setSelectedCodigoItem(cbProveedor, 1);
        cbTipoDocumento.setSelectedIndex(1);
        Calendar c2 = new GregorianCalendar();
        dcFechaRegistro.setCalendar(c2);
        dcFechaCompra.setDate(null);
        txtIDProducto.setText("");
        txtCodigoProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");

        URL url = this.getClass().getResource(rutaFotoDefault);
        lbImagen.setIcon(new ImageIcon(url));

        txtPrecioDolares.setText("");
        txtPrecioGs.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");

        txtCantidadAdquirida.setText("");
        txtPrecioCompra.setText("");
        cbMoneda.setSelectedIndex(0);

        tablemodelo.setRowCount(0);
    }

    public boolean ComprobarCamposCompra() {
        if (metodos.ValidarCampoVacio(txtNumCompra, lblNumCompra) == false) {
            return false;
        }
        if (dcFechaRegistro.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Complete la fecha de registro", "Error", JOptionPane.ERROR_MESSAGE);
            dcFechaRegistro.requestFocus();
            return false;
        }
        if (dcFechaCompra.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Complete la fecha de compra", "Error", JOptionPane.ERROR_MESSAGE);
            dcFechaCompra.requestFocus();
            return false;
        }
        return true;
    }

    public boolean ComprobarCamposProducto() {
        if (metodos.ValidarCampoVacio(txtCodigoProducto, lblCodigoProducto) == false) {
            return false;
        } else {
            if (ConsultaProducto() == false) {
                JOptionPane.showMessageDialog(this, "El Codigo de producto ingresado no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (metodos.ValidarCampoVacio(txtCantidadAdquirida, lblCantidadAdquirida) == false) {
            return false;
        }

        if (metodos.ValidarCampoVacio(txtPrecioCompra, lblPrecioCompra) == false) {
            return false;
        }
        return true;
    }

    public boolean ConsultaProducto() {
        String codProducto = txtCodigoProducto.getText();
        try {
            Conexion con = metodos.ObtenerRSSentencia("SELECT pro_codigo, pro_existencia, pro_descripcion, pro_codigo, pro_precio "
                    + "FROM producto WHERE pro_identificador = '" + codProducto + "'");
            if (con.rs.next() == true) {
                txtIDProducto.setText(con.rs.getString(1));
                txtExistenciaActual.setText(con.rs.getString(2));
                txtDescripcionProducto.setText(con.rs.getString(3));

                if (metodosimagen.LeerImagen(lbImagen, rutaFotoProducto + con.rs.getString(4)) == false) {
                    URL url = this.getClass().getResource(rutaFotoDefault);
                    lbImagen.setIcon(new ImageIcon(url));
                }
                String precio = con.rs.getString(5).replace(".", ",");
                precio = metodostxt.PonerPuntosMilesKeyReleased(precio);
                txtPrecioDolares.setText(precio);

                return true;
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al intentar obtener cambio " + ex);
            System.out.println("Error al consultar producto: ");
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
        lblFechaRegistro = new javax.swing.JLabel();
        dcFechaRegistro = new com.toedter.calendar.JDateChooser();
        lblNumCompra = new javax.swing.JLabel();
        txtNumCompra = new javax.swing.JTextField();
        dcFechaCompra = new com.toedter.calendar.JDateChooser();
        lblFechaCompra = new javax.swing.JLabel();
        jpDatosProducto = new javax.swing.JPanel();
        lblCodigoProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        lblTituloDescripcion = new javax.swing.JLabel();
        txtDescripcionProducto = new javax.swing.JTextField();
        lblCodigo6 = new javax.swing.JLabel();
        txtExistenciaActual = new javax.swing.JTextField();
        lblCodigo7 = new javax.swing.JLabel();
        txtPrecioGs = new javax.swing.JTextField();
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
        txtIDProducto = new javax.swing.JTextField();
        lblIDProducto = new javax.swing.JLabel();
        jpProductos = new javax.swing.JPanel();
        btnEliminar = new javax.swing.JButton();
        btnAnadir = new javax.swing.JButton();
        txtPrecioCompra = new javax.swing.JTextField();
        lblPrecioCompra = new javax.swing.JLabel();
        txtCantidadAdquirida = new javax.swing.JTextField();
        lblCantidadAdquirida = new javax.swing.JLabel();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        cbMoneda = new javax.swing.JComboBox<>();
        lblCodigo10 = new javax.swing.JLabel();
        lblTituloTotalCompra = new javax.swing.JLabel();
        lblTotalCompra = new javax.swing.JLabel();
        lblTotalMoneda = new javax.swing.JLabel();

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

        lblFechaRegistro.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaRegistro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaRegistro.setText("Fecha de registro");
        lblFechaRegistro.setToolTipText("");

        dcFechaRegistro.setEnabled(false);
        dcFechaRegistro.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaRegistro.setMinSelectableDate(new java.util.Date(631162800000L));

        lblNumCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNumCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumCompra.setText("N° de compra");

        txtNumCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtNumCompra.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNumCompra.setEnabled(false);
        txtNumCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCompraKeyReleased(evt);
            }
        });

        dcFechaCompra.setEnabled(false);
        dcFechaCompra.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaCompra.setMinSelectableDate(new java.util.Date(631162800000L));

        lblFechaCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaCompra.setText("Fecha de compra");
        lblFechaCompra.setToolTipText("");

        javax.swing.GroupLayout jpDatosCompraLayout = new javax.swing.GroupLayout(jpDatosCompra);
        jpDatosCompra.setLayout(jpDatosCompraLayout);
        jpDatosCompraLayout.setHorizontalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                    .addComponent(cbTipoDocumento, 0, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addComponent(lblFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jpDatosCompraLayout.setVerticalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNumCompra)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaCompra))
                .addGap(1, 1, 1)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jpDatosProducto.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del producto"));

        lblCodigoProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigoProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigoProducto.setText("Código del producto");

        txtCodigoProducto.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        txtCodigoProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigoProducto.setEnabled(false);
        txtCodigoProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoProductoKeyReleased(evt);
            }
        });

        lblTituloDescripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTituloDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloDescripcion.setText("Descripción del producto");
        lblTituloDescripcion.setToolTipText("");

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

        txtPrecioGs.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPrecioGs.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioGs.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioGs.setEnabled(false);

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
        lbImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenProductoSinFoto.png"))); // NOI18N
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

        txtIDProducto.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        txtIDProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtIDProducto.setEnabled(false);

        lblIDProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblIDProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblIDProducto.setText("ID");

        javax.swing.GroupLayout jpDatosProductoLayout = new javax.swing.GroupLayout(jpDatosProducto);
        jpDatosProducto.setLayout(jpDatosProductoLayout);
        jpDatosProductoLayout.setHorizontalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblIDProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDatosProductoLayout.createSequentialGroup()
                        .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(txtPrecioDolares)
                            .addComponent(txtPrecioGs)
                            .addComponent(txtPrecioReales)
                            .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                        .addGap(2, 2, 2)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGuaraniess, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo13, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDatosProductoLayout.createSequentialGroup()
                        .addComponent(lblCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))))
        );
        jpDatosProductoLayout.setVerticalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addComponent(lblCodigo7)
                        .addGap(2, 2, 2)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDolares, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioGs, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGuaraniess, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioReales, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigoProducto)
                            .addComponent(lblCodigo6)
                            .addComponent(lblIDProducto))
                        .addGap(4, 4, 4)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpProductos.setBackground(new java.awt.Color(233, 255, 255));
        jpProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("Retirar");
        btnEliminar.setEnabled(false);

        btnAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnAnadir.setText("Añadir");
        btnAnadir.setEnabled(false);
        btnAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirActionPerformed(evt);
            }
        });

        txtPrecioCompra.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtPrecioCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioCompra.setToolTipText("");
        txtPrecioCompra.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioCompra.setEnabled(false);
        txtPrecioCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioCompraKeyReleased(evt);
            }
        });

        lblPrecioCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblPrecioCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrecioCompra.setText("Precio de compra");

        txtCantidadAdquirida.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtCantidadAdquirida.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCantidadAdquirida.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCantidadAdquirida.setEnabled(false);
        txtCantidadAdquirida.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadAdquiridaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadAdquiridaKeyTyped(evt);
            }
        });

        lblCantidadAdquirida.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCantidadAdquirida.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCantidadAdquirida.setText("Cantidad adquirida");

        tbPrincipal.setAutoCreateRowSorter(true);
        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Codigo del producto", "Descripcion", "Cantidad", "Precio", "Moneda", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbPrincipal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbPrincipal.setGridColor(new java.awt.Color(0, 153, 204));
        tbPrincipal.setOpaque(false);
        tbPrincipal.setRowHeight(20);
        tbPrincipal.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPrincipal.getTableHeader().setReorderingAllowed(false);
        tbPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPrincipalMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPrincipalMousePressed(evt);
            }
        });
        scPrincipal.setViewportView(tbPrincipal);

        cbMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMoneda.setEnabled(false);
        cbMoneda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMonedaItemStateChanged(evt);
            }
        });

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
                            .addComponent(lblCantidadAdquirida)
                            .addComponent(txtCantidadAdquirida, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrecioCompra))
                        .addGap(1, 1, 1)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                                .addComponent(btnAnadir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar))))
                    .addComponent(scPrincipal))
                .addContainerGap())
        );
        jpProductosLayout.setVerticalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCantidadAdquirida)
                    .addComponent(lblPrecioCompra)
                    .addComponent(lblCodigo10))
                .addGap(2, 2, 2)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCantidadAdquirida, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTituloTotalCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTituloTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTituloTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTituloTotalCompra.setText("Total de la compra");

        lblTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalCompra.setText("0,000");

        lblTotalMoneda.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        lblTotalMoneda.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalMoneda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda.setText("Dolares");
        lblTotalMoneda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpBanner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jpDatosProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jpDatosCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(lblTotalCompra)
                        .addGap(4, 4, 4)
                        .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTituloTotalCompra))
                .addGap(84, 84, 84))
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
                .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(lblTituloTotalCompra)
                .addGap(2, 2, 2)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jpBanner.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 945, Short.MAX_VALUE)
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
        metodostxt.TxtColorLabelKeyReleased(txtCodigoProducto, lblCodigoProducto);

        if (ConsultaProducto() == true) {
            //Convertir precio a las distintas monedas
            if (txtPrecioDolares.getText().equals("") == false) {
                DecimalFormat df3Decimales = new DecimalFormat("#.##");
                //Precio Dolares
                String precioDolaresString = txtPrecioDolares.getText().replace(".", "");
                double precioDolaresDouble = Double.parseDouble(precioDolaresString.replace(",", "."));
                precioDolaresDouble = Double.parseDouble((df3Decimales.format(precioDolaresDouble)).replace(",", "."));
                txtPrecioDolares.setText(metodos.Sacar0ADouble(precioDolaresDouble));

                //Precio, cambio a Guaranies
                double precioGsDouble = precioDolaresDouble * cotiUsdGsCompra;
                precioGsDouble = Double.parseDouble((df3Decimales.format(precioGsDouble)).replace(",", "."));
                txtPrecioGs.setText(metodos.Sacar0ADouble(precioGsDouble));

                //Precio, cambio a Reales
                double precioRsDouble = precioDolaresDouble * cotiUsdRsCompra;
                precioRsDouble = Double.parseDouble((df3Decimales.format(precioRsDouble)).replace(",", "."));
                txtPrecioReales.setText(metodos.Sacar0ADouble(precioRsDouble));

                //Precio, cambio a Pesos argentinos
                double precioPesosArgDouble = precioDolaresDouble * cotiUsdPaCompra;
                precioPesosArgDouble = Double.parseDouble((df3Decimales.format(precioPesosArgDouble)).replace(",", "."));
                txtPrecioPesosArg.setText(metodos.Sacar0ADouble(precioPesosArgDouble));
            }
        } else {
            LimpiarProducto();
        }
    }//GEN-LAST:event_txtCodigoProductoKeyReleased

    private void LimpiarProducto() {
        txtIDProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");
        URL url = this.getClass().getResource(rutaFotoDefault);
        lbImagen.setIcon(new ImageIcon(url));
        txtPrecioDolares.setText("");
        txtPrecioGs.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");
    }

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        if (tbPrincipal.isEnabled() == true) {
            btnEliminar.setEnabled(true);
        }
    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        if (tbPrincipal.isEnabled() == true) {
            btnEliminar.setEnabled(true);
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void btnAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirActionPerformed
        if (ComprobarCamposProducto() == true) {
            cbMoneda.setEnabled(false);
            String idproducto = txtIDProducto.getText();
            String codigoproducto = txtCodigoProducto.getText();
            String descripcion = txtDescripcionProducto.getText();
            int cantidad = Integer.parseInt(txtCantidadAdquirida.getText());
            double precio = Double.parseDouble(txtPrecioCompra.getText());
            String moneda = cbMoneda.getSelectedItem().toString();
            double total = cantidad * precio;

            tablemodelo.addRow(new Object[]{idproducto, codigoproducto, descripcion, cantidad, precio, moneda, total});

            lblTotalCompra.setText(metodos.SumarColumna(tbPrincipal, 6) + ""); //El 5 es la columna 5, comienza de 0
            lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());

            //Limpiar
            LimpiarProducto();
            txtCodigoProducto.setText("");
            txtCantidadAdquirida.setText("");
            txtPrecioCompra.setText("");
            cbMoneda.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnAnadirActionPerformed

    private void cbMonedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMonedaItemStateChanged
        lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());
    }//GEN-LAST:event_cbMonedaItemStateChanged

    private void txtCantidadAdquiridaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadAdquiridaKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtCantidadAdquiridaKeyTyped

    private void txtCantidadAdquiridaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadAdquiridaKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCantidadAdquirida, lblCantidadAdquirida);
    }//GEN-LAST:event_txtCantidadAdquiridaKeyReleased

    private void txtPrecioCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioCompraKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtPrecioCompra, lblPrecioCompra);
    }//GEN-LAST:event_txtPrecioCompraKeyReleased

    private void txtNumCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCompraKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtNumCompra, lblNumCompra);
    }//GEN-LAST:event_txtNumCompraKeyReleased

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtNumCompra);
        ordenTabulador.add(cbProveedor);
        ordenTabulador.add(cbTipoDocumento);
        ordenTabulador.add(dcFechaRegistro);
        ordenTabulador.add(dcFechaCompra);
        ordenTabulador.add(txtCodigoProducto);
        ordenTabulador.add(txtCantidadAdquirida);
        ordenTabulador.add(txtPrecioCompra);
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
    private javax.swing.JButton btnProveedor;
    private javax.swing.JComboBox<String> cbMoneda;
    private javax.swing.JComboBox<MetodosCombo> cbProveedor;
    private javax.swing.JComboBox<String> cbTipoDocumento;
    private com.toedter.calendar.JDateChooser dcFechaCompra;
    private com.toedter.calendar.JDateChooser dcFechaRegistro;
    private javax.swing.JPanel jpBanner;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosCompra;
    private javax.swing.JPanel jpDatosProducto;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private javax.swing.JLabel lbBanner;
    private javax.swing.JLabel lbImagen;
    private javax.swing.JLabel lblCantidadAdquirida;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo13;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblCodigoProducto;
    private javax.swing.JLabel lblDolares;
    private javax.swing.JLabel lblFechaCompra;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFlagBrasil;
    private javax.swing.JLabel lblFlagEeuu;
    private javax.swing.JLabel lblFlagGuaranies;
    private javax.swing.JLabel lblFlagPesosArg;
    private javax.swing.JLabel lblGuaraniess;
    private javax.swing.JLabel lblIDProducto;
    private javax.swing.JLabel lblNumCompra;
    private javax.swing.JLabel lblPrecioCompra;
    private javax.swing.JLabel lblReales;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula1;
    private javax.swing.JLabel lblTituloDescripcion;
    private javax.swing.JLabel lblTituloTotalCompra;
    private javax.swing.JLabel lblTotalCompra;
    private javax.swing.JLabel lblTotalMoneda;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCantidadAdquirida;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtExistenciaActual;
    private javax.swing.JTextField txtIDProducto;
    private javax.swing.JTextField txtNumCompra;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtPrecioDolares;
    private javax.swing.JTextField txtPrecioGs;
    private javax.swing.JTextField txtPrecioPesosArg;
    private javax.swing.JTextField txtPrecioReales;
    // End of variables declaration//GEN-END:variables
}
