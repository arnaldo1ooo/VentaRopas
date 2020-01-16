/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
public final class RegistrarVenta extends javax.swing.JDialog {

    MetodosTXT metodostxt = new MetodosTXT();
    Metodos metodos = new Metodos();
    MetodosCombo metodoscombo = new MetodosCombo();
    MetodosImagen metodosimagen = new MetodosImagen();
    private final String rutaFotoProducto = "/fotoproductos/imageproducto_";
    private final String rutaFotoPorDefecto = "/fotoproductos/imageproducto_0.png";
    DefaultTableModel tablemodelo;

    public RegistrarVenta(java.awt.Frame parent, Boolean modal) {
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
            if (cantidadProductos <= 0) {
                JOptionPane.showMessageDialog(null, "No se cargó ningún producto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ComprobarCamposCompra() == true) {
                String numcompra = txtNumCompra.getText();
                int proveedor = metodoscombo.ObtenerIdComboBox(cbProveedor);
                int tipodocumento = cbTipoDocumento.getSelectedIndex();
                DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String fecharegistro = formatoFecha.format(dcFechaRegistro.getDate());
                String fechacompra = formatoFecha.format(dcFechaCompra.getDate());

                int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {

                    try {
                        //Registrar nueva compra
                        String sentencia = "CALL SP_CompraAlta('" + numcompra + "','" + proveedor + "','"
                                + tipodocumento + "','" + fecharegistro + "','" + fechacompra + "')";
                        metodos.EjecutarAltaoModi(sentencia);

                        //Obtener el id de la compra
                        Conexion conexion = metodos.ObtenerRSSentencia("SELECT MAX(com_codigo) AS ultimoid FROM compra");
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
                            //Se registran los productos de la compra
                            sentencia = "CALL SP_CompraProductosAlta('" + idultimacompra + "','" + idproducto + "','"
                                    + cantidadadquirida + "','" + preciocompra + "')";
                            metodos.EjecutarAltaoModi(sentencia);

                            //Suma la cantidad al stock
                            sentencia = "CALL SP_StockSumar('" + idproducto + "','" + cantidadadquirida + "')";
                            metodos.EjecutarAltaoModi(sentencia);
                        }

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
        //dcFechaRegistro.setEnabled(valor);
        dcFechaCompra.setEnabled(valor);
        txtCodigoProducto.setEnabled(valor);
        txtCantidadAdquirida.setEnabled(valor);
        txtPrecioUnitario.setEnabled(valor);
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

        metodosimagen.LeerImagen(piImagen, rutaFotoPorDefecto);

        txtPrecioDolares.setText("");
        txtPrecioGs.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");

        txtCantidadAdquirida.setText("");
        txtPrecioUnitario.setText("");
        cbMoneda.setSelectedIndex(0);

        tablemodelo.setRowCount(0);
        btnEliminar.setEnabled(false);
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

        if (dcFechaCompra.getDate().after(dcFechaRegistro.getDate())) {
            JOptionPane.showMessageDialog(null, "La fecha de compra no puede ser despues de la fecha de registro", "Error", JOptionPane.ERROR_MESSAGE);
            dcFechaCompra.requestFocus();
            return false;
        }
        return true;
    }

    public boolean ComprobarCamposProducto() {
        if (metodos.ValidarCampoVacio(txtCodigoProducto, lblCodigoProducto) == false) {
            System.out.println("Validar CodigoProducto false");
            return false;
        } else {
            if (txtIDProducto.getText().equals("")) { //Si no aparece su id es por que el producto no existe en la bd
                JOptionPane.showMessageDialog(this, "El Codigo de producto ingresado no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String codigoproducto = txtCodigoProducto.getText();
        String filaactual;
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            filaactual = tbPrincipal.getValueAt(i, 1).toString();
            if (codigoproducto.equals(filaactual) == true) {
                JOptionPane.showMessageDialog(null, "Este producto ya se encuentra cargado", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (metodos.ValidarCampoVacio(txtCantidadAdquirida, lblCantidadAdquirida) == false) {
            System.out.println("Validar Cantidad adquirida false");
            return false;
        }

        if (metodos.ValidarCampoVacio(txtPrecioUnitario, lblPrecioCompra) == false) {
            System.out.println("Validar Precio unitario false");
            return false;
        }

        if (metodostxt.ValidarDouble(txtPrecioUnitario.getText()) == false) {
            System.out.println("Validar Double Precio unitario false");
            return false;
        }
        return true;
    }

    public boolean ConsultaProducto() {
        String codProducto = txtCodigoProducto.getText();
        try {
            Conexion con = metodos.ObtenerRSSentencia("SELECT pro_codigo, pro_existencia, pro_descripcion, pro_codigo "
                    + "FROM producto WHERE pro_identificador = '" + codProducto + "'");
            if (con.rs.next() == true) {
                txtIDProducto.setText(con.rs.getString(1));
                txtExistenciaActual.setText(con.rs.getString(2));
                txtDescripcionProducto.setText(con.rs.getString(3));

                metodosimagen.LeerImagen(piImagen, rutaFotoProducto + con.rs.getString(4));

                double precio = con.rs.getDouble(5);
                String precioString = metodostxt.DoubleAFormatoSudamerica(precio); //Formato sudamerica: 10.100,25
                txtPrecioDolares.setText(precioString);ñ

                ConvertirCotizacion();
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
        lblFlagEeuu = new javax.swing.JLabel();
        txtPrecioDolares = new javax.swing.JTextField();
        lblDolares = new javax.swing.JLabel();
        txtIDProducto = new javax.swing.JTextField();
        lblIDProducto = new javax.swing.JLabel();
        piImagen = new org.edisoncor.gui.panel.PanelImage();
        jpProductos = new javax.swing.JPanel();
        btnEliminar = new javax.swing.JButton();
        btnAnadir = new javax.swing.JButton();
        txtPrecioUnitario = new javax.swing.JTextField();
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
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();

        setTitle("Ventana Registrar Compra");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

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
                .addGap(15, 15, 15)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
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
        btnProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedorActionPerformed(evt);
            }
        });

        cbTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN ESPECIFICAR", "NOTA", "RECIBO", "FACTURA" }));
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
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
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
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoProductoKeyTyped(evt);
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

        piImagen.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        piImagen.setFocusable(false);
        piImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenProductoSinFoto.png"))); // NOI18N

        javax.swing.GroupLayout piImagenLayout = new javax.swing.GroupLayout(piImagen);
        piImagen.setLayout(piImagenLayout);
        piImagenLayout.setHorizontalGroup(
            piImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 127, Short.MAX_VALUE)
        );
        piImagenLayout.setVerticalGroup(
            piImagenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jpDatosProductoLayout = new javax.swing.GroupLayout(jpDatosProducto);
        jpDatosProducto.setLayout(jpDatosProductoLayout);
        jpDatosProductoLayout.setHorizontalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblIDProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIDProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(lblCodigoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(57, 57, 57)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 50, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(piImagen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFlagEeuu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPrecioDolares, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(txtPrecioGs, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(txtPrecioReales, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
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
                            .addComponent(lblIDProducto)
                            .addComponent(lblCodigoProducto)
                            .addComponent(lblCodigo6))
                        .addGap(4, 4, 4)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(piImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpProductos.setBackground(new java.awt.Color(233, 255, 255));
        jpProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("Retirar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnAnadir.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnAnadir.setText("Añadir");
        btnAnadir.setEnabled(false);
        btnAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirActionPerformed(evt);
            }
        });

        txtPrecioUnitario.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtPrecioUnitario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioUnitario.setToolTipText("");
        txtPrecioUnitario.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPrecioUnitario.setEnabled(false);
        txtPrecioUnitario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPrecioUnitarioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPrecioUnitarioKeyTyped(evt);
            }
        });

        lblPrecioCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblPrecioCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrecioCompra.setText("Precio unitario");

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
                "ID", "Codigo del producto", "Descripcion", "Cantidad", "Precio unitario", "Moneda", "SubTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, false
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
        tbPrincipal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPrincipalKeyReleased(evt);
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
                            .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPrecioCompra))
                        .addGap(1, 1, 1)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTituloTotalCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTituloTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTituloTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloTotalCompra.setText("Total de la compra");

        lblTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalCompra.setText("0");

        lblTotalMoneda.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        lblTotalMoneda.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalMoneda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda.setText("Moneda");
        lblTotalMoneda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR COMPRA");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jpDatosProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpDatosCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(panel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblTituloTotalCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(lblTotalCompra)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpDatosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpDatosProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)))
                .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTituloTotalCompra)
                .addGap(2, 2, 2)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 983, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("RegistrarCompra");

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

    private void LimpiarProducto() {
        txtIDProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");
        metodosimagen.LeerImagen(piImagen, rutaFotoPorDefecto);
        txtPrecioDolares.setText("");
        txtPrecioGs.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");
    }

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        if (tbPrincipal.isEnabled() == true) {
            btnEliminar.setEnabled(true);
            Integer filaSelect = tbPrincipal.rowAtPoint(evt.getPoint()); //num de fila seleccionada
            Integer columnSelect = tbPrincipal.columnAtPoint(evt.getPoint()); //Columna seleccionada
            if (evt.getClickCount() == 2 && columnSelect == 3) { //Si se hace doble click
                double preciounitario = metodostxt.DoubleAFormatoAmericano(tbPrincipal.getValueAt(filaSelect, 4).toString());

                //Validar que sea numero valido
                int cantidad = 0;
                boolean esNumeroValido = false;

                while (esNumeroValido == false) {
                    try {
                        String cantidadString = JOptionPane.showInputDialog(this, "Ingrese la nueva cantidad: ", "Modificar cantidad", JOptionPane.INFORMATION_MESSAGE);
                        if (cantidadString == null) {
                            System.out.println("Se canceló la operación");
                            return;
                        }

                        cantidad = Integer.parseInt(cantidadString);
                        if (cantidad <= 0) {
                            JOptionPane.showMessageDialog(this, "La cantidad no puede ser menor o igual a 0");
                            return;
                        }
                        esNumeroValido = true;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Ingrese un número entero válido");
                        esNumeroValido = false;
                    }
                }

                double subtotal = cantidad * preciounitario;
                tbPrincipal.setValueAt(cantidad, filaSelect, columnSelect);
                tbPrincipal.setValueAt(subtotal, filaSelect, 6);
                SumarSubtotal();
                JOptionPane.showMessageDialog(this, "Cantidad modificada con éxito!");
            }
        }
    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        if (tbPrincipal.isEnabled() == true) {
            btnEliminar.setEnabled(true);
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void btnAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirActionPerformed
        if (ComprobarCamposProducto() == true) {
            try {
                String idproducto = txtIDProducto.getText();
                String codigoproducto = txtCodigoProducto.getText();
                String descripcion = txtDescripcionProducto.getText();
                int cantidad = Integer.parseInt(txtCantidadAdquirida.getText());
                double preciounitario = metodostxt.DoubleAFormatoAmericano(txtPrecioUnitario.getText());
                preciounitario = metodostxt.FormatearADosDecimales(preciounitario);
                String moneda = cbMoneda.getSelectedItem().toString();
                double subtotal = cantidad * preciounitario;
                tablemodelo.addRow(new Object[]{idproducto, codigoproducto, descripcion, cantidad, preciounitario,
                    moneda, subtotal});

                SumarSubtotal();

                if (tbPrincipal.getRowCount() > 0) {
                    cbMoneda.setEnabled(false);
                } else {
                    cbMoneda.setEnabled(true);
                }
                lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());

                //Limpiar
                LimpiarProducto();
                txtCodigoProducto.setText("");
                txtCantidadAdquirida.setText("");
                txtPrecioUnitario.setText("");

                txtCodigoProducto.requestFocus();
            } catch (NumberFormatException e) {
                System.out.println("Error al añadir producto a la tabla " + e);
            }
        }
    }//GEN-LAST:event_btnAnadirActionPerformed

    private void SumarSubtotal() {
        //Suma la colmna subtotal
        double totalcompra = metodos.SumarColumnaDouble(tbPrincipal, 6);
        totalcompra = metodostxt.FormatearADosDecimales(totalcompra);
        String totalcompraString = metodostxt.DoubleAFormatoSudamerica(totalcompra);
        lblTotalCompra.setText(totalcompraString); //El 5 es la columna 5, comienza de 0
    }

    private void cbMonedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMonedaItemStateChanged
        lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());
    }//GEN-LAST:event_cbMonedaItemStateChanged

    private void txtCantidadAdquiridaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadAdquiridaKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtCantidadAdquiridaKeyTyped

    private void txtCantidadAdquiridaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadAdquiridaKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCantidadAdquirida, lblCantidadAdquirida);
    }//GEN-LAST:event_txtCantidadAdquiridaKeyReleased

    private void txtPrecioUnitarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioUnitarioKeyReleased
        txtPrecioUnitario.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtPrecioUnitario.getText()));
        metodostxt.TxtColorLabelKeyReleased(txtPrecioUnitario, lblPrecioCompra);

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnAnadir.doClick();
        }
    }//GEN-LAST:event_txtPrecioUnitarioKeyReleased

    private void txtNumCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCompraKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtNumCompra, lblNumCompra);
    }//GEN-LAST:event_txtNumCompraKeyReleased

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        tablemodelo.removeRow(tbPrincipal.getSelectedRow());
        SumarSubtotal();

        if (tbPrincipal.getRowCount() > 0) {
            cbMoneda.setEnabled(false);
        } else {
            cbMoneda.setEnabled(true);
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtPrecioUnitarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPrecioUnitarioKeyTyped
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtPrecioUnitario);
    }//GEN-LAST:event_txtPrecioUnitarioKeyTyped

    private void txtCodigoProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyTyped
        //Evitar que entre espacio
        if (evt.getKeyChar() == KeyEvent.VK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_txtCodigoProductoKeyTyped

    private void txtCodigoProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCodigoProducto, lblCodigoProducto);
        //Si se oprime ENTER o si el producto ya se encontro y se cambia el codigo de producto, volver a buscar
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || txtIDProducto.getText().equals("") == false) {
            if (ConsultaProducto() == true) {
                //Pedir cantidad y precio unitario con inputmensaje justo luego de escribir el codigo de producto
            } else {
                LimpiarProducto();
            }
        }
    }//GEN-LAST:event_txtCodigoProductoKeyReleased

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased

    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void btnProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedorActionPerformed
        String provnombre = "";
        while (provnombre.equals("")) {
            provnombre = JOptionPane.showInputDialog(this, "Ingrese el nombre del proveedor(*)", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
            if (provnombre == null) {
                System.out.println("Se cancelo");
            }
        }
        String provdireccion = "";
        while (provdireccion.equals("")) {
            provdireccion = JOptionPane.showInputDialog(this, "Ingrese la dirección del proveedor(*)", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
            if (provdireccion == null) {
                System.out.println("Se cancelo");
            }
        }
        String provcel = JOptionPane.showInputDialog(this, "Ingrese el celular del proveedor", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
        if (provcel == null) {
            System.out.println("Se cancelo");
        }
        String provemail = JOptionPane.showInputDialog(this, "Ingrese el email del proveedor", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
        if (provemail == null) {
            System.out.println("Se cancelo");
        }

        metodos.EjecutarAltaoModi("CALL SP_ProveedorAlta('" + provnombre.toUpperCase() + "','" + provdireccion + "','" + provcel + "','" + provemail + "')");

        CargarComboBoxes();
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "Proveedor registrado con éxito!");
    }//GEN-LAST:event_btnProveedorActionPerformed

    private void ConvertirCotizacion() {
        //Convertir precio a las distintas monedas
        if (txtPrecioDolares.getText().equals("") == false) {
            //Precio Dolares
            String precioUSDString = txtPrecioDolares.getText();
            double precioUSDsDouble = metodostxt.DoubleAFormatoAmericano(precioUSDString);
            precioUSDsDouble = metodostxt.FormatearADosDecimales(precioUSDsDouble);
            txtPrecioDolares.setText(metodostxt.DoubleAFormatoSudamerica(precioUSDsDouble));

            //Precio, cambio a Guaranies
            double precioGsDouble = precioUSDsDouble * cotiUsdGsCompra;
            precioGsDouble = metodostxt.FormatearADosDecimales(precioGsDouble);
            String precioGsString = metodostxt.DoubleAFormatoSudamerica(precioGsDouble);
            txtPrecioGs.setText(precioGsString);

            //Precio, cambio a Reales
            double precioRsDouble = precioUSDsDouble * cotiUsdRsCompra;
            precioRsDouble = metodostxt.FormatearADosDecimales(precioRsDouble);
            String precioRsString = metodostxt.DoubleAFormatoSudamerica(precioRsDouble);
            txtPrecioReales.setText(precioRsString);

            //Precio, cambio a Pesos argentinos
            double precioPesosArgDouble = precioUSDsDouble * cotiUsdPaCompra;
            precioPesosArgDouble = metodostxt.FormatearADosDecimales(precioPesosArgDouble);
            String precioPesosArgString = metodostxt.DoubleAFormatoSudamerica(precioPesosArgDouble);
            txtPrecioPesosArg.setText(precioPesosArgString);
        }
    }

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtNumCompra);
        ordenTabulador.add(cbProveedor);
        ordenTabulador.add(dcFechaCompra);
        ordenTabulador.add(txtCodigoProducto);
        ordenTabulador.add(txtCantidadAdquirida);
        ordenTabulador.add(txtPrecioUnitario);
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
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosCompra;
    private javax.swing.JPanel jpDatosProducto;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
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
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.PanelImage piImagen;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCantidadAdquirida;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtExistenciaActual;
    private javax.swing.JTextField txtIDProducto;
    private javax.swing.JTextField txtNumCompra;
    private javax.swing.JTextField txtPrecioDolares;
    private javax.swing.JTextField txtPrecioGs;
    private javax.swing.JTextField txtPrecioPesosArg;
    private javax.swing.JTextField txtPrecioReales;
    private javax.swing.JTextField txtPrecioUnitario;
    // End of variables declaration//GEN-END:variables
}
