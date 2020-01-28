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
public final class RegistrarVenta extends javax.swing.JDialog {
    
    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();
    MetodosImagen metodosimagen = new MetodosImagen();
    String rutaFotoProducto = "C:\\VentaRopas\\fotoproductos\\imageproducto_";
    String TitlePorDefault = "PRODUCTO SIN FOTO";
    DefaultTableModel tablemodelo;
    
    public RegistrarVenta(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null); //Centrar ventana

        //Obtener fecha actual
        Calendar c2 = new GregorianCalendar();
        dcFechaVenta.setCalendar(c2);
        
        CargarComboBoxes();
        tablemodelo = (DefaultTableModel) tbProductosVendidos.getModel();
        GenerarNumVenta();
        
        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbCliente, "SELECT cli_codigo, CONCAT(cli_nombre,' ', cli_apellido) AS nomape "
                + "FROM cliente ORDER BY cli_nombre", 1);
        
        metodoscombo.CargarComboBox(cbVendedor, "SELECT fun_codigo, CONCAT(fun_nombre,' ', fun_apellido) AS nomape "
                + "FROM funcionario WHERE fun_cargo = 2 ORDER BY fun_nombre", -1);
    }
    
    public void RegistroNuevo() {
        //Registra la venta
        try {
            if (ComprobarCamposVenta() == true) {
                String numventa = lblNumVenta.getText();
                int vendedor = metodoscombo.ObtenerIDSelectComboBox(cbVendedor);
                int cliente = metodoscombo.ObtenerIDSelectComboBox(cbCliente);
                int tipodocumento = cbTipoDocumento.getSelectedIndex();
                DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String fecharegistro = formatoFecha.format(dcFechaVenta.getDate());
                double importe = metodostxt.DoubleAFormatoAmericano(txtImporte.getText());
                String moneda = cbMoneda.getSelectedItem().toString();
                double cotizacion = 1;
                
                switch (moneda) {
                    case "Dolares":
                        break;
                    case "Guaranies":
                        importe = importe / cotiUsdGsCompra;
                        importe = metodostxt.FormatearADosDecimales(importe);
                        cotizacion = cotiUsdGsCompra;
                        break;
                    case "Reales":
                        importe = importe / cotiUsdRsCompra;
                        importe = metodostxt.FormatearADosDecimales(importe);
                        cotizacion = cotiUsdRsCompra;
                        break;
                    case "Pesos argentinos":
                        importe = importe / cotiUsdPaCompra;
                        importe = metodostxt.FormatearADosDecimales(importe);
                        cotizacion = cotiUsdPaCompra;
                        break;
                    
                    default:
                        JOptionPane.showMessageDialog(this, "No se encontró la moneda seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
                
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Esta seguro de crear esta nueva venta?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    try {
                        //Registrar nueva venta
                        String sentencia = "CALL SP_VentaAlta('" + numventa + "','" + vendedor + "','" + cliente + "','"
                                + tipodocumento + "','" + fecharegistro + "','" + importe + "','" + moneda + "','" + cotizacion + "')";
                        con.EjecutarABM(sentencia);

                        //Obtener el id de la venta
                        con = con.ObtenerRSSentencia("SELECT MAX(ven_codigo) AS ultimoid FROM venta");
                        con.rs.next();
                        int idultimaventa = con.rs.getInt("ultimoid");
                        con.DesconectarBasedeDatos();

                        //Registra los productos de la venta                      
                        String idproducto;
                        int cantidadadquirida;
                        double preciocompra;
                        double precioventabruto;
                        double descuento;
                        
                        int cantfila = tbProductosVendidos.getRowCount();
                        for (int fila = 0; fila < cantfila; fila++) {
                            idproducto = tbProductosVendidos.getValueAt(fila, 0).toString();
                            cantidadadquirida = Integer.parseInt(tbProductosVendidos.getValueAt(fila, 3).toString());
                            preciocompra = UltimoPrecioCompra(idproducto);
                            precioventabruto = metodostxt.DoubleAFormatoAmericano(tbProductosVendidos.getValueAt(fila, 4).toString());
                            descuento = Double.parseDouble(tbProductosVendidos.getValueAt(fila, 5).toString());

                            //Comprobar en que moneda se guarda, en caso de ser distinto a dolares, se convierte a dolares
                            switch (moneda) {
                                case "Dolares":
                                    break;
                                case "Guaranies":
                                    precioventabruto = precioventabruto / cotiUsdGsCompra;
                                    precioventabruto = metodostxt.FormatearADosDecimales(precioventabruto);
                                    descuento = descuento / cotiUsdGsCompra;
                                    descuento = metodostxt.FormatearADosDecimales(descuento);
                                    break;
                                case "Reales":
                                    precioventabruto = precioventabruto / cotiUsdRsCompra;
                                    precioventabruto = metodostxt.FormatearADosDecimales(precioventabruto);
                                    descuento = descuento / cotiUsdRsCompra;
                                    descuento = metodostxt.FormatearADosDecimales(descuento);
                                    break;
                                case "Pesos argentinos":
                                    precioventabruto = precioventabruto / cotiUsdPaCompra;
                                    precioventabruto = metodostxt.FormatearADosDecimales(precioventabruto);
                                    descuento = descuento / cotiUsdPaCompra;
                                    descuento = metodostxt.FormatearADosDecimales(descuento);
                                    break;
                                
                                default:
                                    JOptionPane.showMessageDialog(this, "No se encontró la moneda seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }

                            //Se registran los productos de la venta
                            sentencia = "CALL SP_VentaProductosAlta('" + idultimaventa + "','" + idproducto + "','"
                                    + cantidadadquirida + "','" + preciocompra + "','" + precioventabruto + "','" + descuento + "')";
                            con.EjecutarABM(sentencia);
                        }
                        Toolkit.getDefaultToolkit().beep(); //BEEP
                        JOptionPane.showMessageDialog(this, "Se agregó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                        Limpiar();
                        GenerarNumVenta();
                    } catch (SQLException ex) {
                        Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (HeadlessException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                        Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
        }
    }
    
    private void Limpiar() {
        cbCliente.setSelectedIndex(-1);
        cbTipoDocumento.setSelectedIndex(1);
        Calendar c2 = new GregorianCalendar();
        dcFechaVenta.setCalendar(c2);
        
        cbMoneda.setSelectedIndex(1);
        cbMoneda.setEnabled(true);
        
        tablemodelo.setRowCount(0);
        btnEliminar.setEnabled(false);
        txtImporte.setEnabled(false);
        txtImporte.setText("0");
        txtImporte.setForeground(Color.BLACK);
        txtVuelto.setText("0");
        txtTotalVenta.setText("0");
        txtCodigoProducto.setForeground(Color.BLACK);
        txtCantidad.setForeground(Color.BLACK);
        txtDescuento.setForeground(Color.BLACK);
    }
    
    private boolean ComprobarCamposVenta() {
        if (cbVendedor.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione el vendedor/a", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbVendedor.requestFocus();
            return false;
        }
        
        if (dcFechaVenta.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Complete la fecha de venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaVenta.requestFocus();
            return false;
        }
        
        int cantidadProductos = tbProductosVendidos.getModel().getRowCount();
        if (cantidadProductos <= 0) {
            JOptionPane.showMessageDialog(this, "No se cargó ningún producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtImporte.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Ingrese el importe", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtImporte.requestFocus();
            return false;
        }
        
        double importe = metodostxt.DoubleAFormatoAmericano(txtImporte.getText());
        double totalventa = metodostxt.DoubleAFormatoAmericano(txtTotalVenta.getText());
        if (totalventa > importe || txtImporte.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "El importe debe ser mayor al total de la venta", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtImporte.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean ComprobarCamposProducto() {
        if (metodostxt.ValidarCampoVacioTXT(txtCodigoProducto, lblCodigoProducto) == false) {
            System.out.println("Validar CodigoProducto false");
            return false;
        } else {
            if (ConsultaProducto(txtCodigoProducto.getText()) == false) {
                JOptionPane.showMessageDialog(this, "El Codigo de producto ingresado no existe", "Advertencia", JOptionPane.WARNING_MESSAGE);
                txtCodigoProducto.requestFocus();
                return false;
            }
        }
        
        String codigoproducto = txtCodigoProducto.getText();
        String filaactual;
        for (int i = 0; i < tbProductosVendidos.getRowCount(); i++) {
            filaactual = tbProductosVendidos.getValueAt(i, 1).toString();
            if (codigoproducto.equals(filaactual) == true) {
                JOptionPane.showMessageDialog(this, "Este producto ya se encuentra cargado", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        if (metodostxt.ValidarCampoVacioTXT(txtCantidad, lblCantidad) == false) {
            System.out.println("Validar Cantidad adquirida false");
            return false;
        }
        
        if (txtDescuento.getText().equals("")) {
            txtDescuento.setText("0");
        }
        
        int cantidad = Integer.parseInt(txtCantidad.getText());
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad no puede ser menor o igual a 0");
            txtCantidad.requestFocus();
            lblCantidad.setForeground(Color.RED);
            return false;
        }
        
        int stock = Integer.parseInt(txtStock.getText());
        if (cantidad > stock) {
            JOptionPane.showMessageDialog(this, "No hay suficiente stock, el stock actual del producto es de " + stock);
            txtCantidad.requestFocus();
            txtCantidad.setForeground(Color.RED);
            return false;
        }
        
        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jpDatosVenta = new javax.swing.JPanel();
        lblRucCedula = new javax.swing.JLabel();
        cbCliente = new javax.swing.JComboBox<>();
        btnBuscarCliente = new javax.swing.JButton();
        cbTipoDocumento = new javax.swing.JComboBox<>();
        lblRucCedula1 = new javax.swing.JLabel();
        lblFechaRegistro = new javax.swing.JLabel();
        dcFechaVenta = new com.toedter.calendar.JDateChooser();
        lblRucCedula2 = new javax.swing.JLabel();
        cbVendedor = new javax.swing.JComboBox<>();
        btnABMCliente = new javax.swing.JButton();
        panelDatosProducto = new javax.swing.JPanel();
        lblCodigoProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        lblTituloDescripcion = new javax.swing.JLabel();
        txtDescripcionProducto = new javax.swing.JTextField();
        lblCodigo6 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
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
        btnPantallaCompleta = new javax.swing.JButton();
        lblImagen = new javax.swing.JLabel();
        btnBuscarProducto = new javax.swing.JButton();
        jpProductos = new javax.swing.JPanel();
        btnEliminar = new javax.swing.JButton();
        btnAnadir = new javax.swing.JButton();
        txtCantidad = new javax.swing.JTextField();
        lblCantidad = new javax.swing.JLabel();
        scPrincipal = new javax.swing.JScrollPane();
        tbProductosVendidos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        cbMoneda = new javax.swing.JComboBox<>();
        lblCodigo10 = new javax.swing.JLabel();
        lblDescuento = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JTextField();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        labelMetric1 = new org.edisoncor.gui.label.LabelMetric();
        lblNumVenta = new org.edisoncor.gui.label.LabelMetric();
        jPanel3 = new javax.swing.JPanel();
        lblTituloTotalCompra1 = new javax.swing.JLabel();
        txtImporte = new javax.swing.JTextField();
        txtVuelto = new javax.swing.JTextField();
        lblTituloTotalCompra2 = new javax.swing.JLabel();
        lblTotalMoneda = new javax.swing.JLabel();
        txtTotalVenta = new javax.swing.JTextField();
        lblTituloTotalCompra = new javax.swing.JLabel();
        lblTotalMoneda1 = new javax.swing.JLabel();
        lblTotalMoneda2 = new javax.swing.JLabel();

        setTitle("Ventana Registrar Compra");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Registrar venta");
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
        btnCancelar.setText("Limpiar campos");
        btnCancelar.setToolTipText("Cancela la acción");
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
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jpDatosVenta.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosVenta.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de la venta"));

        lblRucCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula.setText("Cliente");
        lblRucCedula.setToolTipText("");

        btnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnBuscarCliente.setToolTipText("Buscar cliente");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        cbTipoDocumento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN ESPECIFICAR", "NOTA", "RECIBO", "FACTURA" }));
        cbTipoDocumento.setSelectedIndex(1);

        lblRucCedula1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula1.setText("Tipo de documento");
        lblRucCedula1.setToolTipText("");

        lblFechaRegistro.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaRegistro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaRegistro.setText("Fecha de venta");
        lblFechaRegistro.setToolTipText("");

        dcFechaVenta.setEnabled(false);
        dcFechaVenta.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaVenta.setMinSelectableDate(new java.util.Date(631162800000L));

        lblRucCedula2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula2.setText("Vendedor/a");
        lblRucCedula2.setToolTipText("");

        btnABMCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnABMCliente.setToolTipText("Nuevo proveedor");
        btnABMCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnABMClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpDatosVentaLayout = new javax.swing.GroupLayout(jpDatosVenta);
        jpDatosVenta.setLayout(jpDatosVentaLayout);
        jpDatosVentaLayout.setHorizontalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula2))
                .addGap(18, 18, 18)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addComponent(cbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3)
                .addComponent(btnABMCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDatosVentaLayout.setVerticalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblRucCedula2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnABMCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        panelDatosProducto.setBackground(new java.awt.Color(233, 255, 255));
        panelDatosProducto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del producto"));

        lblCodigoProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigoProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigoProducto.setText("Código del producto");

        txtCodigoProducto.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtCodigoProducto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoProductoActionPerformed(evt);
            }
        });
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

        txtStock.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtStock.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtStock.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtStock.setEnabled(false);

        lblCodigo7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo7.setText("Precio del producto");

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

        btnBuscarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnBuscarProducto.setToolTipText("Buscador de productos");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosProductoLayout = new javax.swing.GroupLayout(panelDatosProducto);
        panelDatosProducto.setLayout(panelDatosProductoLayout);
        panelDatosProductoLayout.setHorizontalGroup(
            panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosProductoLayout.createSequentialGroup()
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblIDProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIDProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCodigoProducto)
                            .addComponent(lblCodigoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addComponent(btnBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelDatosProductoLayout.createSequentialGroup()
                        .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDatosProductoLayout.createSequentialGroup()
                        .addComponent(lblCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(panelDatosProductoLayout.createSequentialGroup()
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblFlagEeuu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, 0)
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPrecioDolares, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addComponent(txtPrecioGs)
                            .addComponent(txtPrecioReales)
                            .addComponent(txtPrecioPesosArg))
                        .addGap(2, 2, 2)
                        .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGuaraniess, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCodigo13, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        panelDatosProductoLayout.setVerticalGroup(
            panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosProductoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(panelDatosProductoLayout.createSequentialGroup()
                            .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelDatosProductoLayout.createSequentialGroup()
                                    .addComponent(lblCodigo7)
                                    .addGap(2, 2, 2)
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPrecioDolares, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblDolares, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(1, 1, 1)
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPrecioGs, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblGuaraniess, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(1, 1, 1)
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPrecioReales, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblReales, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(1, 1, 1)
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblFlagPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPrecioPesosArg, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblCodigo13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(panelDatosProductoLayout.createSequentialGroup()
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblIDProducto)
                                        .addComponent(lblCodigoProducto)
                                        .addComponent(lblCodigo6))
                                    .addGap(1, 1, 1)
                                    .addGroup(panelDatosProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(txtIDProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(lblTituloDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(3, 3, 3)
                                    .addComponent(txtDescripcionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(1, 1, 1)))
                    .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpProductos.setBackground(new java.awt.Color(233, 255, 255));
        jpProductos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("Quitar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnAnadir.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnAnadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnAnadir.setText("Agregar");
        btnAnadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirActionPerformed(evt);
            }
        });

        txtCantidad.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCantidad.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCantidad.setEnabled(false);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadKeyTyped(evt);
            }
        });

        lblCantidad.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCantidad.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCantidad.setText("Cantidad");

        tbProductosVendidos.setAutoCreateRowSorter(true);
        tbProductosVendidos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbProductosVendidos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbProductosVendidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Codigo del producto", "Descripcion", "Cantidad", "Precio bruto", "Descuento", "Precio neto", "Moneda", "SubTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbProductosVendidos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbProductosVendidos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbProductosVendidos.setGridColor(new java.awt.Color(0, 153, 204));
        tbProductosVendidos.setOpaque(false);
        tbProductosVendidos.setRowHeight(20);
        tbProductosVendidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbProductosVendidos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbProductosVendidos.getTableHeader().setReorderingAllowed(false);
        tbProductosVendidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProductosVendidosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbProductosVendidosMousePressed(evt);
            }
        });
        scPrincipal.setViewportView(tbProductosVendidos);

        cbMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMoneda.setSelectedIndex(1);
        cbMoneda.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMonedaItemStateChanged(evt);
            }
        });

        lblCodigo10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo10.setText("Moneda");

        lblDescuento.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDescuento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescuento.setText("Descuento");

        txtDescuento.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescuento.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDescuento.setEnabled(false);
        txtDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescuentoActionPerformed(evt);
            }
        });
        txtDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescuentoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescuentoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jpProductosLayout = new javax.swing.GroupLayout(jpProductos);
        jpProductos.setLayout(jpProductosLayout);
        jpProductosLayout.setHorizontalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scPrincipal)
                    .addGroup(jpProductosLayout.createSequentialGroup()
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCantidad))
                        .addGap(18, 18, 18)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDescuento))
                        .addGap(18, 18, 18)
                        .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jpProductosLayout.createSequentialGroup()
                                .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAnadir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar)))))
                .addContainerGap())
        );
        jpProductosLayout.setVerticalGroup(
            jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCantidad)
                    .addComponent(lblCodigo10)
                    .addComponent(lblDescuento))
                .addGap(2, 2, 2)
                .addGroup(jpProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnadir, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR VENTA");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        labelMetric1.setText("N° de venta:");
        labelMetric1.setDistanciaDeSombra(2);

        lblNumVenta.setText("00000001");
        lblNumVenta.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblNumVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNumVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(233, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTituloTotalCompra1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloTotalCompra1.setText("IMPORTE");

        txtImporte.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtImporte.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImporte.setEnabled(false);
        txtImporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtImporteActionPerformed(evt);
            }
        });
        txtImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImporteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImporteKeyTyped(evt);
            }
        });

        txtVuelto.setEditable(false);
        txtVuelto.setBackground(new java.awt.Color(0, 153, 153));
        txtVuelto.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtVuelto.setForeground(new java.awt.Color(255, 255, 255));
        txtVuelto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVuelto.setText("0");
        txtVuelto.setDisabledTextColor(new java.awt.Color(0, 51, 153));
        txtVuelto.setFocusable(false);
        txtVuelto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVueltoActionPerformed(evt);
            }
        });

        lblTituloTotalCompra2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloTotalCompra2.setText("VUELTO");

        lblTotalMoneda.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda.setText("Moneda");
        lblTotalMoneda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtTotalVenta.setEditable(false);
        txtTotalVenta.setBackground(new java.awt.Color(0, 0, 0));
        txtTotalVenta.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtTotalVenta.setForeground(new java.awt.Color(0, 154, 0));
        txtTotalVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalVenta.setText("0");
        txtTotalVenta.setFocusable(false);
        txtTotalVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalVentaActionPerformed(evt);
            }
        });

        lblTituloTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTituloTotalCompra.setText("TOTAL A PAGAR");

        lblTotalMoneda1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda1.setText("Moneda");
        lblTotalMoneda1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblTotalMoneda2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda2.setText("Moneda");
        lblTotalMoneda2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblTotalMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTituloTotalCompra1))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTotalCompra2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblTotalMoneda2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(86, 86, 86)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTotalCompra)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtTotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(lblTituloTotalCompra1)
                                .addComponent(lblTituloTotalCompra2))
                            .addComponent(lblTituloTotalCompra, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalMoneda2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTotalMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtTotalVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpPrincipalLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jpProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jpPrincipalLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jpDatosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelDatosProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGap(212, 212, 212)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpDatosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatosProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jpProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 875, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 706, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("RegistrarCompra");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void GenerarNumVenta() {
        //Generar numero venta
        try {
            con = con.ObtenerRSSentencia("SELECT MAX(ven_numventa) AS numultimaventa FROM venta");
            String numultimaventa = null;
            while (con.rs.next()) {
                numultimaventa = con.rs.getString("numultimaventa");
            }
            
            if (numultimaventa == null) {
                numultimaventa = String.format("%8s", String.valueOf(1)).replace(' ', '0');
            } else {
                numultimaventa = String.format("%8s", String.valueOf((Integer.parseInt(numultimaventa) + 1))).replace(' ', '0');
            }
            lblNumVenta.setText(numultimaventa);
            
            con.DesconectarBasedeDatos();
        } catch (SQLException e) {
            Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, e);
        }
    }

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
        txtStock.setText("");
        txtDescripcionProducto.setText("");
        lblImagen.setIcon(null);
        lblImagen.setText(TitlePorDefault);
        txtPrecioDolares.setText("");
        txtPrecioGs.setText("");
        txtPrecioReales.setText("");
        txtPrecioPesosArg.setText("");
        txtCantidad.setText("");
        txtCantidad.setEnabled(false);
        txtDescuento.setText("");
        txtDescuento.setEnabled(false);
    }

    private void tbProductosVendidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosVendidosMouseClicked

    }//GEN-LAST:event_tbProductosVendidosMouseClicked

    private void tbProductosVendidosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosVendidosMousePressed
        if (tbProductosVendidos.isEnabled() == true) {
            btnEliminar.setEnabled(true);
        }
    }//GEN-LAST:event_tbProductosVendidosMousePressed

    private void btnAnadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirActionPerformed
        if (ComprobarCamposProducto() == true) {
            try {
                String idproducto = txtIDProducto.getText();
                String codigoproducto = txtCodigoProducto.getText();
                String descripcion = txtDescripcionProducto.getText();
                int cantidad = Integer.parseInt(txtCantidad.getText());
                double preciobruto = 0.0;
                double descuento = metodostxt.DoubleAFormatoAmericano(txtDescuento.getText());
                descuento = metodostxt.FormatearADosDecimales(descuento);
                double precioneto;
                //El precio de acuerdo a la moneda seleccionada

                String moneda = cbMoneda.getSelectedItem().toString();
                switch (moneda) {
                    case "Dolares":
                        preciobruto = metodostxt.DoubleAFormatoAmericano(txtPrecioDolares.getText());
                        break;
                    case "Guaranies":
                        preciobruto = metodostxt.DoubleAFormatoAmericano(txtPrecioGs.getText());
                        break;
                    case "Reales":
                        preciobruto = metodostxt.DoubleAFormatoAmericano(txtPrecioReales.getText());
                        break;
                    case "Pesos argentinos":
                        preciobruto = metodostxt.DoubleAFormatoAmericano(txtPrecioPesosArg.getText());
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "No se encontró la moneda seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
                
                if (descuento >= preciobruto) {
                    JOptionPane.showMessageDialog(null, "El descuento no puede ser mayor o igual al precio del producto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                preciobruto = metodostxt.FormatearADosDecimales(preciobruto);
                precioneto = preciobruto - descuento; //Se aplica el descuento
                precioneto = metodostxt.FormatearADosDecimales(precioneto);
                
                double subtotal = cantidad * precioneto;
                subtotal = metodostxt.FormatearADosDecimales(subtotal);
                tablemodelo.addRow(new Object[]{idproducto, codigoproducto, descripcion, cantidad, preciobruto, descuento, precioneto,
                    moneda, subtotal});
                
                SumarSubtotal();
                
                if (tbProductosVendidos.getRowCount() > 0) {
                    txtImporte.setEnabled(true);
                    cbMoneda.setEnabled(false);
                } else {
                    txtImporte.setEnabled(false);
                    cbMoneda.setEnabled(true);
                }
                lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());
                lblTotalMoneda1.setText(cbMoneda.getSelectedItem().toString());
                lblTotalMoneda2.setText(cbMoneda.getSelectedItem().toString());
                
                LimpiarProducto();
                txtCodigoProducto.setText("");
                txtCantidad.setText("");
                
                txtCodigoProducto.requestFocus();
            } catch (NumberFormatException e) {
                System.out.println("Error al añadir producto a la tabla " + e);
            }
        }
    }//GEN-LAST:event_btnAnadirActionPerformed
    
    private void SumarSubtotal() {
        //Suma la colmna subtotal
        double totalventa = metodos.SumarColumnaDouble(tbProductosVendidos, 8);
        totalventa = metodostxt.FormatearADosDecimales(totalventa);
        String totalventaString = metodostxt.DoubleAFormatoSudamerica(totalventa);
        txtTotalVenta.setText(totalventaString); //El 5 es la columna 5, comienza de 0
    }

    private void cbMonedaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMonedaItemStateChanged
        lblTotalMoneda.setText(cbMoneda.getSelectedItem().toString());
        lblTotalMoneda1.setText(cbMoneda.getSelectedItem().toString());
        lblTotalMoneda2.setText(cbMoneda.getSelectedItem().toString());
    }//GEN-LAST:event_cbMonedaItemStateChanged

    private void txtCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtCantidadKeyTyped

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCantidad, lblCantidad);
        
        int cantidad = Integer.parseInt(txtCantidad.getText());
        int stock = Integer.parseInt(txtStock.getText());
        if (cantidad > stock) {
            JOptionPane.showMessageDialog(this, "No hay suficiente stock, el stock actual del producto es de " + stock);
            txtCantidad.setForeground(Color.RED);
        } else {
            txtCantidad.setForeground(new Color(0, 153, 51)); //Verde
        }
    }//GEN-LAST:event_txtCantidadKeyReleased

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        tablemodelo.removeRow(tbProductosVendidos.getSelectedRow());
        SumarSubtotal();
        
        if (tbProductosVendidos.getRowCount() <= 0) {
            txtImporte.setEnabled(false);
            txtImporte.setText("");
            txtVuelto.setText("");
            cbMoneda.setEnabled(true);
            btnEliminar.setEnabled(false);
            txtCodigoProducto.requestFocus();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtCodigoProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyTyped
        //Evitar que entre espacio
        if (evt.getKeyChar() == KeyEvent.VK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_txtCodigoProductoKeyTyped

    private void txtCodigoProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCodigoProducto, lblCodigoProducto);
        txtCodigoProducto.setForeground(Color.RED);
    }//GEN-LAST:event_txtCodigoProductoKeyReleased
    
    private boolean ConsultaProducto(String codigoproducto) {
        try {
            con = con.ObtenerRSSentencia("SELECT pro_codigo, pro_existencia, pro_descripcion, pro_codigo "
                    + "FROM producto WHERE pro_identificador = '" + codigoproducto + "'");
            if (con.rs.next()) {
                txtCodigoProducto.setForeground(new Color(0, 153, 51)); //Verde

                txtIDProducto.setText(con.rs.getString(1));
                txtStock.setText(con.rs.getString(2));
                txtDescripcionProducto.setText(con.rs.getString(3));
                metodosimagen.LeerImagenExterna(lblImagen, rutaFotoProducto + con.rs.getString(4), TitlePorDefault);
                
                double ultimopreciocompra = UltimoPrecioCompra(txtIDProducto.getText());
                double porcganancia = PorcGanancia();
                String precioventaString = metodostxt.DoubleAFormatoSudamerica(ultimopreciocompra + (ultimopreciocompra * (porcganancia / 100)));
                txtPrecioDolares.setText(precioventaString);
                return true;
            } else {
                txtCodigoProducto.setForeground(Color.RED); //Rojo
                LimpiarProducto();
                return false;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al intentar obtener cambio " + e);
            System.out.println("Error al consultar producto: ");
        }
        con.DesconectarBasedeDatos();
        return false;
    }

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        String nombresp = "SP_ClienteConsulta";
        String titlesJtabla[] = {"Código", "RUC/CI", "Nombre", "Apellido", "Dirección", "Teléfono", "Email", "Observación"};
        Buscador buscador = new Buscador(this, nombresp, titlesJtabla, 1);
        buscador.setVisible(true);
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        

    }//GEN-LAST:event_btnGuardarKeyPressed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        int confirmado = JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar todos los datos de la venta actual?", "Confirmación", JOptionPane.YES_OPTION);
        if (JOptionPane.YES_OPTION == confirmado) {
            Limpiar();
            txtCodigoProducto.setText("");
            LimpiarProducto();
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtDescuentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescuentoKeyReleased
        txtDescuento.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtDescuento.getText()));
        metodostxt.TxtColorLabelKeyReleased(txtDescuento, lblDescuento);
    }//GEN-LAST:event_txtDescuentoKeyReleased

    private void btnPantallaCompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPantallaCompletaActionPerformed
        VistaCompletaImagen vistacompleta = new VistaCompletaImagen(rutaFotoProducto + txtIDProducto.getText());
        vistacompleta.setVisible(true);
    }//GEN-LAST:event_btnPantallaCompletaActionPerformed

    private void btnABMClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnABMClienteActionPerformed
        ABMCliente abmcliente = new ABMCliente(null, true);
        abmcliente.setVisible(true);
    }//GEN-LAST:event_btnABMClienteActionPerformed

    private void txtDescuentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescuentoKeyTyped
        metodostxt.TxtCantidadCaracteresKeyTyped(txtDescuento, 11);
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtDescuento);
    }//GEN-LAST:event_txtDescuentoKeyTyped

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        String nombresp = "SP_ProductoConsulta";
        String titlesJtabla[] = {"Código", "Código del producto", "Descripción",
            "Marca", "Existencia", "Tamaño", "Categoria", "Subcategoria", "Observación", "Estado"};
        Buscador buscador = new Buscador(this, nombresp, titlesJtabla, 2);
        buscador.setVisible(true);
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void txtVueltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVueltoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVueltoActionPerformed

    private void txtImporteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyTyped
        metodostxt.TxtCantidadCaracteresKeyTyped(txtImporte, 11);
        
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtImporte);
    }//GEN-LAST:event_txtImporteKeyTyped
    
    private void CalcularVuelto() {
        double importe = metodostxt.DoubleAFormatoAmericano(txtImporte.getText());
        double totalventa = metodostxt.DoubleAFormatoAmericano(txtTotalVenta.getText());
        if (totalventa > importe) {
            txtImporte.setForeground(Color.RED);
            txtVuelto.setText("0");
        } else {
            txtImporte.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtImporte.getText()));
            double vuelto = importe - totalventa;
            vuelto = metodostxt.FormatearADosDecimales(vuelto);
            txtVuelto.setText(metodostxt.DoubleAFormatoSudamerica(vuelto));
            txtImporte.setForeground(new Color(0, 153, 51)); //Verde
        }
    }

    private void txtImporteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyReleased
        if (txtImporte.getText().equals("") == false) {
            txtImporte.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtImporte.getText()));
            
            CalcularVuelto();
        }

    }//GEN-LAST:event_txtImporteKeyReleased

    private void txtTotalVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalVentaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalVentaActionPerformed

    private void txtImporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtImporteActionPerformed
        CalcularVuelto();
    }//GEN-LAST:event_txtImporteActionPerformed

    private void txtCodigoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoProductoActionPerformed
        //Si se oprime ENTER o si el producto ya se encontro y se cambia el codigo de producto, volver a buscar        
        if (ConsultaProducto(txtCodigoProducto.getText()) == true) {
            ConvertirCotizacion();

            //Solicita la cantidad, y revisa que sea valido
            boolean esNumeroValido = false;
            while (esNumeroValido == false) {
                try {
                    Object cantidadString = JOptionPane.showInputDialog(this, "Ingrese la cantidad",
                            "Cantidad vendida", JOptionPane.INFORMATION_MESSAGE, null, null, "1");
                    
                    int cantidad = Integer.parseInt(cantidadString.toString());
                    int stock = Integer.parseInt(txtStock.getText());
                    if (cantidadString == null || cantidad <= 0 || cantidad > stock) {
                        if (null == cantidadString) {
                            System.out.println("Se canceló la operación");
                            return;
                        }
                        
                        if (cantidad <= 0) {
                            JOptionPane.showMessageDialog(this, "La cantidad no puede ser menor o igual a 0");
                        }
                        
                        if (cantidad > stock) {
                            JOptionPane.showMessageDialog(this, "No hay suficiente stock, el stock actual del producto es de " + stock);
                        }
                    } else {
                        txtCantidad.setText(cantidadString.toString());
                        lblCantidad.setForeground(new Color(0, 153, 51)); //Verde
                        txtDescuento.setEnabled(true);
                        txtDescuento.requestFocus();
                        esNumeroValido = true;
                    }
                } catch (NumberFormatException e) {
                    if (cantidadString == null) {
                        
                    }
                    JOptionPane.showMessageDialog(this, "Ingrese un número entero válido");
                    esNumeroValido = false;
                }
            }
        }
    }//GEN-LAST:event_txtCodigoProductoActionPerformed

    private void txtDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescuentoActionPerformed
        btnAnadir.doClick();
    }//GEN-LAST:event_txtDescuentoActionPerformed
    
    private double UltimoPrecioCompra(String codigoproducto) {
        con = con.ObtenerRSSentencia("SELECT MAX(compro_preciocompra) AS preciocompramax "
                + "FROM compra_producto WHERE compro_producto = '" + codigoproducto + "'");;
        double precioventa = 0.0;
        try {
            //Obtener el ultimo precio de compra del producto
            if (con.rs.next()) {
                precioventa = con.rs.getDouble("preciocompramax");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.DesconectarBasedeDatos();
        return precioventa;
    }
    
    private double PorcGanancia() {
        con = con.ObtenerRSSentencia("SELECT conf_porcganancia FROM configuracion WHERE conf_codigo = '1'");;
        double porcganancia = 0.0;
        try {
            if (con.rs.next()) {
                porcganancia = con.rs.getDouble("conf_porcganancia");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistrarVenta.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.DesconectarBasedeDatos();
        return porcganancia;
    }
    
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
        ordenTabulador.add(cbCliente);
        ordenTabulador.add(dcFechaVenta);
        ordenTabulador.add(txtCodigoProducto);
        ordenTabulador.add(txtCantidad);
        ordenTabulador.add(txtCantidad);
        ordenTabulador.add(txtImporte);
        ordenTabulador.add(btnGuardar);
        setFocusTraversalPolicy(new PersonalizadoFocusTraversalPolicy());
    }
    
    static void PonerClienteSeleccionado(int codigoSelect) {
        MetodosCombo metodoscombo = new MetodosCombo();
        metodoscombo.setSelectedCodigoItem(cbCliente, codigoSelect);
    }
    
    static void PonerProductoSeleccionado(String codigoproducto) {
        txtCodigoProducto.setText(codigoproducto + "");
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
    private javax.swing.JButton btnABMCliente;
    private javax.swing.JButton btnAnadir;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPantallaCompleta;
    private static javax.swing.JComboBox<MetodosCombo> cbCliente;
    private javax.swing.JComboBox<String> cbMoneda;
    private javax.swing.JComboBox<String> cbTipoDocumento;
    private javax.swing.JComboBox<MetodosCombo> cbVendedor;
    private com.toedter.calendar.JDateChooser dcFechaVenta;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosVenta;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpProductos;
    private org.edisoncor.gui.label.LabelMetric labelMetric1;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo13;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblCodigoProducto;
    private javax.swing.JLabel lblDescuento;
    private javax.swing.JLabel lblDolares;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFlagBrasil;
    private javax.swing.JLabel lblFlagEeuu;
    private javax.swing.JLabel lblFlagGuaranies;
    private javax.swing.JLabel lblFlagPesosArg;
    private javax.swing.JLabel lblGuaraniess;
    private javax.swing.JLabel lblIDProducto;
    private javax.swing.JLabel lblImagen;
    private org.edisoncor.gui.label.LabelMetric lblNumVenta;
    private javax.swing.JLabel lblReales;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula1;
    private javax.swing.JLabel lblRucCedula2;
    private javax.swing.JLabel lblTituloDescripcion;
    private javax.swing.JLabel lblTituloTotalCompra;
    private javax.swing.JLabel lblTituloTotalCompra1;
    private javax.swing.JLabel lblTituloTotalCompra2;
    private javax.swing.JLabel lblTotalMoneda;
    private javax.swing.JLabel lblTotalMoneda1;
    private javax.swing.JLabel lblTotalMoneda2;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JPanel panelDatosProducto;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbProductosVendidos;
    private javax.swing.JTextField txtCantidad;
    private static javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcionProducto;
    private javax.swing.JTextField txtDescuento;
    private javax.swing.JTextField txtIDProducto;
    private javax.swing.JTextField txtImporte;
    private javax.swing.JTextField txtPrecioDolares;
    private javax.swing.JTextField txtPrecioGs;
    private javax.swing.JTextField txtPrecioPesosArg;
    private javax.swing.JTextField txtPrecioReales;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtTotalVenta;
    private javax.swing.JTextField txtVuelto;
    // End of variables declaration//GEN-END:variables
}
