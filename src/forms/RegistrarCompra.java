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
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import metodos.Metodos;
import metodos.MetodosCombo;
import metodos.MetodosImagen;
import metodos.MetodosTXT;
import metodos.VistaCompletaImagen;
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
    String rutaFotoProducto = "C:\\VentaRopas\\fotoproductos\\imageproducto_";
    String TitlePorDefault = "PRODUCTO SIN FOTO";
    DefaultTableModel tablemodelo;

    public RegistrarCompra(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        GenerarNumCompra();
        tablemodelo = (DefaultTableModel) tbPrincipal.getModel();
        CargarComboBoxes();
        //Obtener fecha actual
        Calendar c2 = new GregorianCalendar();
        dcFechaRegistro.setCalendar(c2);

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
                String numcompra = lblNumCompra.getText();
                String identificador = txtIdentificador.getText();
                int proveedor = metodoscombo.ObtenerIdComboBox(cbProveedor);
                int tipodocumento = cbTipoDocumento.getSelectedIndex();
                DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String fecharegistro = formatoFecha.format(dcFechaRegistro.getDate());
                String fechacompra = formatoFecha.format(dcFechaCompra.getDate());

                int confirmado = JOptionPane.showConfirmDialog(null, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {

                    try {
                        //Registrar nueva compra
                        String sentencia = "CALL SP_CompraAlta('" + numcompra + "','" + identificador + "','" + proveedor + "','"
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
                            cantidadadquirida = Integer.parseInt(tbPrincipal.getValueAt(fila, 3).toString());
                            preciocompra = Double.parseDouble(tbPrincipal.getValueAt(fila, 4).toString());

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
                        }

                        JOptionPane.showMessageDialog(this, "Se agregó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                        Limpiar();
                    } catch (HeadlessException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                        Logger.getLogger(RegistrarCompra.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                        Logger.getLogger(RegistrarCompra.class.getName()).log(Level.SEVERE, null, ex);
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

    private void Limpiar() {
        metodoscombo.setSelectedNombreItem(cbProveedor, "SIN PROVEEDOR");
        metodoscombo.setSelectedCodigoItem(cbProveedor, 1);
        cbTipoDocumento.setSelectedIndex(1);
        dcFechaCompra.setDate(null);
        txtIDProducto.setText("");
        txtCodigoProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");

        lblImagen.setIcon(null);
        lblImagen.setText(TitlePorDefault);

        txtCantidadAdquirida.setText("");
        txtPrecioUnitario.setText("");
        cbMoneda.setSelectedIndex(0);

        tablemodelo.setRowCount(0);
        btnEliminar.setEnabled(false);
    }

    public boolean ComprobarCamposCompra() {
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
                metodosimagen.LeerImagenExterna(lblImagen, rutaFotoProducto + con.rs.getString(4), TitlePorDefault);
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
        jpDatosCompra = new javax.swing.JPanel();
        lblRucCedula = new javax.swing.JLabel();
        cbProveedor = new javax.swing.JComboBox<>();
        btnProveedor = new javax.swing.JButton();
        cbTipoDocumento = new javax.swing.JComboBox<>();
        lblRucCedula1 = new javax.swing.JLabel();
        lblFechaRegistro = new javax.swing.JLabel();
        dcFechaRegistro = new com.toedter.calendar.JDateChooser();
        dcFechaCompra = new com.toedter.calendar.JDateChooser();
        lblFechaCompra = new javax.swing.JLabel();
        txtIdentificador = new javax.swing.JTextField();
        lblIDProducto1 = new javax.swing.JLabel();
        jpDatosProducto = new javax.swing.JPanel();
        lblCodigoProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        lblTituloDescripcion = new javax.swing.JLabel();
        txtDescripcionProducto = new javax.swing.JTextField();
        lblCodigo6 = new javax.swing.JLabel();
        txtExistenciaActual = new javax.swing.JTextField();
        txtIDProducto = new javax.swing.JTextField();
        lblIDProducto = new javax.swing.JLabel();
        btnABMProducto = new javax.swing.JButton();
        btnPantallaCompleta = new javax.swing.JButton();
        lblImagen = new javax.swing.JLabel();
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
        lblNumCompra = new javax.swing.JLabel();
        lblTitleNumCompra = new javax.swing.JLabel();
        jpBotones1 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setTitle("Ventana Registrar Compra");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpDatosCompra.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosCompra.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de la compra"));

        lblRucCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula.setText("Proveedor");
        lblRucCedula.setToolTipText("");

        btnProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedorActionPerformed(evt);
            }
        });

        cbTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN ESPECIFICAR", "NOTA", "RECIBO", "FACTURA" }));

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

        dcFechaCompra.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaCompra.setMinSelectableDate(new java.util.Date(631162800000L));

        lblFechaCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaCompra.setText("Fecha de compra");
        lblFechaCompra.setToolTipText("");

        txtIdentificador.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtIdentificador.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        lblIDProducto1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblIDProducto1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblIDProducto1.setText("Código de identificación");

        javax.swing.GroupLayout jpDatosCompraLayout = new javax.swing.GroupLayout(jpDatosCompra);
        jpDatosCompra.setLayout(jpDatosCompraLayout);
        jpDatosCompraLayout.setHorizontalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtIdentificador)
                    .addComponent(lblIDProducto1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDatosCompraLayout.setVerticalGroup(
            jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosCompraLayout.createSequentialGroup()
                .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosCompraLayout.createSequentialGroup()
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIDProducto1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(jpDatosCompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaCompra))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jpDatosProducto.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del producto"));

        lblCodigoProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigoProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigoProducto.setText("Código del producto");

        txtCodigoProducto.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCodigoProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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

        txtDescripcionProducto.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtDescripcionProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDescripcionProducto.setEnabled(false);

        lblCodigo6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo6.setText("Stock actual");

        txtExistenciaActual.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        txtExistenciaActual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtExistenciaActual.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtExistenciaActual.setEnabled(false);

        txtIDProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtIDProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtIDProducto.setEnabled(false);

        lblIDProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblIDProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblIDProducto.setText("ID");

        btnABMProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnABMProducto.setEnabled(false);
        btnABMProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnABMProductoActionPerformed(evt);
            }
        });

        btnPantallaCompleta.setBackground(new java.awt.Color(0, 255, 255));
        btnPantallaCompleta.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnPantallaCompleta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoPantallacompleta.png"))); // NOI18N
        btnPantallaCompleta.setToolTipText("Ampliar vista de Imagen del producto");
        btnPantallaCompleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPantallaCompletaActionPerformed(evt);
            }
        });

        lblImagen.setFont(new java.awt.Font("Segoe UI Black", 1, 10)); // NOI18N
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setText("PRODUCTO SIN FOTO");
        lblImagen.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jpDatosProductoLayout = new javax.swing.GroupLayout(jpDatosProducto);
        jpDatosProducto.setLayout(jpDatosProductoLayout);
        jpDatosProductoLayout.setHorizontalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                        .addComponent(txtCodigoProducto)
                                        .addGap(0, 0, 0)
                                        .addComponent(btnABMProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33))
                                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                        .addComponent(lblCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(lblCodigo6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(76, 76, 76))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );
        jpDatosProductoLayout.setVerticalGroup(
            jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDatosProductoLayout.createSequentialGroup()
                .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosProductoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpDatosProductoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblIDProducto)
                            .addComponent(lblCodigoProducto)
                            .addComponent(lblCodigo6))
                        .addGap(3, 3, 3)
                        .addGroup(jpDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnABMProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
        btnAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirActionPerformed(evt);
            }
        });

        txtPrecioUnitario.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtPrecioUnitario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioUnitario.setToolTipText("");
        txtPrecioUnitario.setDisabledTextColor(new java.awt.Color(0, 0, 0));
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPrincipalMousePressed(evt);
            }
        });
        tbPrincipal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPrincipalKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPrincipalKeyReleased(evt);
            }
        });
        scPrincipal.setViewportView(tbPrincipal);

        cbMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMoneda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMonedaItemStateChanged(evt);
            }
        });

        lblCodigo10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo10.setText("Moneda");

        lblTituloTotalCompra.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTituloTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTituloTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTituloTotalCompra.setText("Total de la compra");

        lblTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTotalCompra.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalCompra.setText("0");

        lblTotalMoneda.setFont(new java.awt.Font("SansSerif", 0, 16)); // NOI18N
        lblTotalMoneda.setForeground(new java.awt.Color(0, 102, 51));
        lblTotalMoneda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda.setText("Moneda");
        lblTotalMoneda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                                .addComponent(btnAnadir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar))))
                    .addComponent(scPrincipal)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpProductosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTituloTotalCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTotalCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTituloTotalCompra)
                .addGap(2, 2, 2)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblTotalCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR COMPRA");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        lblNumCompra.setFont(new java.awt.Font("sansserif", 0, 20)); // NOI18N
        lblNumCompra.setForeground(new java.awt.Color(255, 255, 255));
        lblNumCompra.setText("00000000");

        lblTitleNumCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTitleNumCompra.setForeground(new java.awt.Color(255, 255, 255));
        lblTitleNumCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTitleNumCompra.setText("N° de compra:");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(281, 281, 281)
                .addComponent(lblTitleNumCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(139, 139, 139))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNumCompra)
                        .addComponent(lblTitleNumCompra))
                    .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jpBotones1.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jpBotones1.setPreferredSize(new java.awt.Dimension(100, 50));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Registrar compra");
        btnGuardar.setToolTipText("Inserta el nuevo registro");
        btnGuardar.setPreferredSize(new java.awt.Dimension(128, 36));
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
        btnCancelar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Cancela la acción");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotones1Layout = new javax.swing.GroupLayout(jpBotones1);
        jpBotones1.setLayout(jpBotones1Layout);
        jpBotones1Layout.setHorizontalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotones1Layout.setVerticalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotones1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(234, 234, 234)
                        .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jpDatosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 863, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jpDatosProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpDatosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpDatosProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 877, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("RegistrarCompra");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void LimpiarProducto() {
        txtIDProducto.setText("");
        txtExistenciaActual.setText("");
        txtDescripcionProducto.setText("");
        lblImagen.setIcon(null);
        lblImagen.setText(TitlePorDefault);
    }

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        if (tbPrincipal.isEnabled() == true) {
            Integer filaSelect = tbPrincipal.rowAtPoint(evt.getPoint()); //num de fila seleccionada
            Integer columnSelect = tbPrincipal.columnAtPoint(evt.getPoint()); //Columna seleccionada
            if (evt.getClickCount() == 2 && columnSelect == 3) { //Si se hace doble click
                double preciounitario = metodostxt.DoubleAFormatoAmericano(tbPrincipal.getValueAt(filaSelect, 4).toString());
                System.out.println("precio " + preciounitario);
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

    private void GenerarNumCompra() {
        //Generar numero venta
        try {
            Conexion con = metodos.ObtenerRSSentencia("SELECT MAX(com_numcompra) AS numultimacompra FROM compra");
            String numultimacompra = null;
            while (con.rs.next()) {
                numultimacompra = con.rs.getString("numultimacompra");
            }

            if (numultimacompra == null) {
                numultimacompra = String.format("%8s", String.valueOf(1)).replace(' ', '0');
            } else {
                numultimacompra = String.format("%8s", String.valueOf((Integer.parseInt(numultimacompra) + 1))).replace(' ', '0');
            }
            lblNumCompra.setText(numultimacompra);

            con.DesconectarBasedeDatos();
        } catch (SQLException e) {
            Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, e);
        }
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

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        tablemodelo.removeRow(tbPrincipal.getSelectedRow());
        SumarSubtotal();

        if (tbPrincipal.getRowCount() > 0) {
            cbMoneda.setEnabled(false);
        } else {
            cbMoneda.setEnabled(true);
            btnEliminar.setEnabled(false);
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

        //Si se oprime espacio se entrara en la ventana de productos en donde se debe seleccionar el  producto
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {

        }

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
                return;
            }
        }
        String provdireccion = "";
        while (provdireccion.equals("")) {
            provdireccion = JOptionPane.showInputDialog(this, "Ingrese la dirección del proveedor(*)", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
            if (provdireccion == null) {
                System.out.println("Se cancelo");
                return;
            }
        }
        String provcel = JOptionPane.showInputDialog(this, "Ingrese el celular del proveedor", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
        if (provcel == null) {
            System.out.println("Se cancelo");
            return;
        }
        String provemail = JOptionPane.showInputDialog(this, "Ingrese el email del proveedor", "Nuevo proveedor", JOptionPane.INFORMATION_MESSAGE);
        if (provemail == null) {
            System.out.println("Se cancelo");
            return;
        }

        metodos.EjecutarAltaoModi("CALL SP_ProveedorAlta('" + provnombre.toUpperCase() + "','" + provdireccion + "','" + provcel + "','" + provemail + "')");

        CargarComboBoxes();
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "Proveedor registrado con éxito!");
    }//GEN-LAST:event_btnProveedorActionPerformed

    private void btnABMProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnABMProductoActionPerformed
        ABMProducto abmproducto = new ABMProducto(null, true);
        abmproducto.setAlwaysOnTop(true); //Siempre al frente
        abmproducto.setVisible(true);
    }//GEN-LAST:event_btnABMProductoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed

    }//GEN-LAST:event_btnGuardarKeyPressed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnPantallaCompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPantallaCompletaActionPerformed
        VistaCompletaImagen vistacompleta = new VistaCompletaImagen(rutaFotoProducto + txtIDProducto.getText());
        vistacompleta.setVisible(true);
    }//GEN-LAST:event_btnPantallaCompletaActionPerformed

    private void tbPrincipalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPrincipalKeyPressed

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
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
    private javax.swing.JButton btnABMProducto;
    private javax.swing.JButton btnAnadir;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPantallaCompleta;
    private javax.swing.JButton btnProveedor;
    private javax.swing.JComboBox<String> cbMoneda;
    private javax.swing.JComboBox<MetodosCombo> cbProveedor;
    private javax.swing.JComboBox<String> cbTipoDocumento;
    private com.toedter.calendar.JDateChooser dcFechaCompra;
    private com.toedter.calendar.JDateChooser dcFechaRegistro;
    private javax.swing.JPanel jpBotones1;
    private javax.swing.JPanel jpDatosCompra;
    private javax.swing.JPanel jpDatosProducto;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblCantidadAdquirida;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigoProducto;
    private javax.swing.JLabel lblFechaCompra;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblIDProducto;
    private javax.swing.JLabel lblIDProducto1;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblNumCompra;
    private javax.swing.JLabel lblPrecioCompra;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula1;
    private javax.swing.JLabel lblTitleNumCompra;
    private javax.swing.JLabel lblTituloDescripcion;
    private javax.swing.JLabel lblTituloTotalCompra;
    private javax.swing.JLabel lblTotalCompra;
    private javax.swing.JLabel lblTotalMoneda;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtCantidadAdquirida;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtExistenciaActual;
    private javax.swing.JTextField txtIDProducto;
    private javax.swing.JTextField txtIdentificador;
    private javax.swing.JTextField txtPrecioUnitario;
    // End of variables declaration//GEN-END:variables
}
