/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import utilidades.Metodos;
import utilidades.MetodosTXT;
//Variables globales
import static principal.Principal.cotiUsdGsCompra;
import static principal.Principal.cotiUsdRsCompra;
import static principal.Principal.cotiUsdPaCompra;

/**
 *
 * @author Owicron-CodesBlue
 */
public class Reporte extends javax.swing.JDialog {

    private Conexion con = new Conexion();
    private Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    private String laTablaSelect;

    public Reporte(java.awt.Frame parent, boolean modal, String tabla) {
        super(parent, modal);
        initComponents();

        laTablaSelect = tabla;
        lblTitulo.setText("Reporte de " + laTablaSelect);
        switch (laTablaSelect) {
            case "Ventas":
                dcDesdeFechaVenta.setDate(PrimerDiaDelAnho());
                dcHastaFechaVenta.setDate(FechaActual());
                TabPane.setSelectedIndex(0);

                break;
            case "Compras":
                dcDesdeFechaRegistro.setDate(PrimerDiaDelAnho());
                dcDesdeFechaCompra.setDate(PrimerDiaDelAnho());
                dcHastaFechaRegistro.setDate(FechaActual());
                dcHastaFechaCompra.setDate(FechaActual());
                TabPane.setSelectedIndex(1);
                break;

            case "Productos":
                TabPane.setSelectedIndex(2);
                break;

            default:
                JOptionPane.showMessageDialog(this, "No se encontró", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private Date PrimerDiaDelAnho() {
        //Asignar fechas por defecto
        Date primerdiadelanho = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            int añoactual = cal.get(Calendar.YEAR);
            primerdiadelanho = formato.parse("01/01/" + añoactual);
        } catch (ParseException e) {
        }
        return primerdiadelanho;
    }

    private Date FechaActual() {
        Date fechaactual = new Date();
        return fechaactual;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel2 = new org.edisoncor.gui.panel.Panel();
        lblTitulo = new org.edisoncor.gui.label.LabelMetric();
        panel3 = new org.edisoncor.gui.panel.Panel();
        buttonSeven2 = new org.edisoncor.gui.button.ButtonSeven();
        TabPane = new javax.swing.JTabbedPane();
        pnVentas = new org.edisoncor.gui.panel.Panel();
        jLabel11 = new javax.swing.JLabel();
        panel8 = new org.edisoncor.gui.panel.Panel();
        jLabel12 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtDesdeNumVenta = new javax.swing.JTextField();
        txtHastaNumVenta = new javax.swing.JTextField();
        btnFiltroCompras3 = new org.edisoncor.gui.button.ButtonSeven();
        btnTraerTodoCompras1 = new org.edisoncor.gui.button.ButtonSeven();
        panel13 = new org.edisoncor.gui.panel.Panel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtDesdeVendedor = new javax.swing.JTextField();
        txtHastaVendedor = new javax.swing.JTextField();
        panel14 = new org.edisoncor.gui.panel.Panel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        dcDesdeFechaVenta = new com.toedter.calendar.JDateChooser();
        dcHastaFechaVenta = new com.toedter.calendar.JDateChooser();
        btnFiltroVentas = new org.edisoncor.gui.button.ButtonSeven();
        btnFiltroCompras5 = new org.edisoncor.gui.button.ButtonSeven();
        cbMonedaVentas = new javax.swing.JComboBox();
        lblBuscarCampo2 = new javax.swing.JLabel();
        panel19 = new org.edisoncor.gui.panel.Panel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtDesdeCliente = new javax.swing.JTextField();
        txtHastaCliente = new javax.swing.JTextField();
        pnCompras = new org.edisoncor.gui.panel.Panel();
        jLabel2 = new javax.swing.JLabel();
        panel7 = new org.edisoncor.gui.panel.Panel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDesdeNumCompra = new javax.swing.JTextField();
        txtHastaNumCompra = new javax.swing.JTextField();
        btnFiltroCompras = new org.edisoncor.gui.button.ButtonSeven();
        panel9 = new org.edisoncor.gui.panel.Panel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        dcDesdeFechaRegistro = new com.toedter.calendar.JDateChooser();
        dcHastaFechaRegistro = new com.toedter.calendar.JDateChooser();
        btnTraerTodoCompras = new org.edisoncor.gui.button.ButtonSeven();
        panel10 = new org.edisoncor.gui.panel.Panel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtDesdeProveedor = new javax.swing.JTextField();
        txtHastaProveedor = new javax.swing.JTextField();
        panel11 = new org.edisoncor.gui.panel.Panel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        dcDesdeFechaCompra = new com.toedter.calendar.JDateChooser();
        dcHastaFechaCompra = new com.toedter.calendar.JDateChooser();
        btnFiltroCompras1 = new org.edisoncor.gui.button.ButtonSeven();
        btnFiltroCompras2 = new org.edisoncor.gui.button.ButtonSeven();
        cbMonedaCompras = new javax.swing.JComboBox();
        lblBuscarCampo1 = new javax.swing.JLabel();
        pnProductos = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel3 = new javax.swing.JLabel();
        txtDesdeIdentificador = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtHastaIdentificador = new javax.swing.JTextField();
        btnFiltroProductos = new org.edisoncor.gui.button.ButtonSeven();
        panel6 = new org.edisoncor.gui.panel.Panel();
        jLabel7 = new javax.swing.JLabel();
        txtDesdeMarca = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtHastaMarca = new javax.swing.JTextField();
        panel5 = new org.edisoncor.gui.panel.Panel();
        jLabel5 = new javax.swing.JLabel();
        txtDesdeExistencia = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtHastaExistencia = new javax.swing.JTextField();
        btnTraeTodoProductos = new org.edisoncor.gui.button.ButtonSeven();
        panel4 = new org.edisoncor.gui.panel.Panel();
        lblBuscarCampo = new javax.swing.JLabel();
        cbOrdenar = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistros = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reportes");
        setBackground(new java.awt.Color(255, 255, 255));

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitulo.setText("REPORTES");
        lblTitulo.setDireccionDeSombra(110);
        lblTitulo.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(462, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        panel3.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        buttonSeven2.setBackground(new java.awt.Color(0, 153, 153));
        buttonSeven2.setText("Generar reporte");
        buttonSeven2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSeven2ActionPerformed(evt);
            }
        });

        TabPane.setBackground(new java.awt.Color(255, 255, 255));
        TabPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        TabPane.setEnabled(false);
        TabPane.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N

        pnVentas.setColorPrimario(new java.awt.Color(255, 255, 255));
        pnVentas.setColorSecundario(new java.awt.Color(233, 255, 255));

        jLabel11.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel11.setText("FILTRAR POR:");

        panel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Número de venta  "));
        panel8.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel8.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel8.setOpaque(false);

        jLabel12.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Desde");

        jLabel19.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Hasta");

        txtDesdeNumVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesdeNumVenta.setText("0");
        txtDesdeNumVenta.setToolTipText("");
        txtDesdeNumVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeNumVentaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDesdeNumVentaKeyTyped(evt);
            }
        });

        txtHastaNumVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHastaNumVenta.setText("999999");
        txtHastaNumVenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHastaNumVentaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel8Layout = new javax.swing.GroupLayout(panel8);
        panel8.setLayout(panel8Layout);
        panel8Layout.setHorizontalGroup(
            panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel8Layout.createSequentialGroup()
                .addGroup(panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaNumVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeNumVenta)))
        );
        panel8Layout.setVerticalGroup(
            panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel8Layout.createSequentialGroup()
                .addGroup(panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(txtDesdeNumVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaNumVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnFiltroCompras3.setText("mes actual");
        btnFiltroCompras3.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnFiltroCompras3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroCompras3ActionPerformed(evt);
            }
        });

        btnTraerTodoCompras1.setText("todos");
        btnTraerTodoCompras1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnTraerTodoCompras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraerTodoCompras1ActionPerformed(evt);
            }
        });

        panel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Proveedor"));
        panel13.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel13.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel13.setOpaque(false);

        jLabel22.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Desde");

        jLabel23.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Hasta");

        txtDesdeVendedor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDesdeVendedor.setText("A");
        txtDesdeVendedor.setToolTipText("");
        txtDesdeVendedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeVendedorKeyReleased(evt);
            }
        });

        txtHastaVendedor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtHastaVendedor.setText("Z");
        txtHastaVendedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHastaVendedorKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panel13Layout = new javax.swing.GroupLayout(panel13);
        panel13.setLayout(panel13Layout);
        panel13Layout.setHorizontalGroup(
            panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel13Layout.createSequentialGroup()
                .addGroup(panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaVendedor, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeVendedor)))
        );
        panel13Layout.setVerticalGroup(
            panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel13Layout.createSequentialGroup()
                .addGroup(panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel22)
                    .addComponent(txtDesdeVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addContainerGap())
        );

        panel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fecha de venta   "));
        panel14.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel14.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel14.setOpaque(false);

        jLabel24.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Desde");

        jLabel25.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Hasta");

        javax.swing.GroupLayout panel14Layout = new javax.swing.GroupLayout(panel14);
        panel14.setLayout(panel14Layout);
        panel14Layout.setHorizontalGroup(
            panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel14Layout.createSequentialGroup()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcHastaFechaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
            .addGroup(panel14Layout.createSequentialGroup()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcDesdeFechaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel14Layout.setVerticalGroup(
            panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel14Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel24)
                    .addComponent(dcDesdeFechaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcHastaFechaVenta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        btnFiltroVentas.setBackground(new java.awt.Color(153, 0, 153));
        btnFiltroVentas.setText("FILTRAR");
        btnFiltroVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroVentasActionPerformed(evt);
            }
        });

        btnFiltroCompras5.setText("mes anterior");
        btnFiltroCompras5.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnFiltroCompras5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroCompras5ActionPerformed(evt);
            }
        });

        cbMonedaVentas.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        cbMonedaVentas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMonedaVentas.setSelectedIndex(1);
        cbMonedaVentas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMonedaVentasItemStateChanged(evt);
            }
        });

        lblBuscarCampo2.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblBuscarCampo2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo2.setText("Moneda:");

        panel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente  "));
        panel19.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel19.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel19.setOpaque(false);

        jLabel34.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Desde");

        jLabel35.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Hasta");

        txtDesdeCliente.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDesdeCliente.setText("A");
        txtDesdeCliente.setToolTipText("");
        txtDesdeCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeClienteKeyReleased(evt);
            }
        });

        txtHastaCliente.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtHastaCliente.setText("Z");
        txtHastaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHastaClienteKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panel19Layout = new javax.swing.GroupLayout(panel19);
        panel19.setLayout(panel19Layout);
        panel19Layout.setHorizontalGroup(
            panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel19Layout.createSequentialGroup()
                .addGroup(panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel19Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeCliente)))
        );
        panel19Layout.setVerticalGroup(
            panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel19Layout.createSequentialGroup()
                .addGroup(panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34)
                    .addComponent(txtDesdeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnVentasLayout = new javax.swing.GroupLayout(pnVentas);
        pnVentas.setLayout(pnVentasLayout);
        pnVentasLayout.setHorizontalGroup(
            pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnVentasLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblBuscarCampo2)
                    .addComponent(cbMonedaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnVentasLayout.createSequentialGroup()
                        .addGroup(pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnFiltroCompras3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnFiltroCompras5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTraerTodoCompras1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(257, 257, 257)
                        .addComponent(btnFiltroVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnVentasLayout.setVerticalGroup(
            pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnVentasLayout.createSequentialGroup()
                        .addGroup(pnVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(panel8, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFiltroVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnVentasLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(7, 7, 7)
                        .addComponent(btnFiltroCompras5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltroCompras3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTraerTodoCompras1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBuscarCampo2)
                        .addGap(0, 0, 0)
                        .addComponent(cbMonedaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TabPane.addTab("Ventas", pnVentas);

        pnCompras.setColorPrimario(new java.awt.Color(255, 255, 255));
        pnCompras.setColorSecundario(new java.awt.Color(233, 255, 255));

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel2.setText("FILTRAR POR:");

        panel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Número de compra"));
        panel7.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel7.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel7.setOpaque(false);

        jLabel9.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Desde");

        jLabel10.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Hasta");

        txtDesdeNumCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesdeNumCompra.setText("0");
        txtDesdeNumCompra.setToolTipText("");
        txtDesdeNumCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeNumCompraKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDesdeNumCompraKeyTyped(evt);
            }
        });

        txtHastaNumCompra.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHastaNumCompra.setText("999999");
        txtHastaNumCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHastaNumCompraKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel7Layout = new javax.swing.GroupLayout(panel7);
        panel7.setLayout(panel7Layout);
        panel7Layout.setHorizontalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel7Layout.createSequentialGroup()
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaNumCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeNumCompra)))
        );
        panel7Layout.setVerticalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(txtDesdeNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaNumCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnFiltroCompras.setText("mes actual");
        btnFiltroCompras.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnFiltroCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroComprasActionPerformed(evt);
            }
        });

        panel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fecha de registro  "));
        panel9.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel9.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel9.setOpaque(false);

        jLabel13.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Desde");

        jLabel14.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Hasta");

        javax.swing.GroupLayout panel9Layout = new javax.swing.GroupLayout(panel9);
        panel9.setLayout(panel9Layout);
        panel9Layout.setHorizontalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel9Layout.createSequentialGroup()
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcHastaFechaRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
            .addGroup(panel9Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcDesdeFechaRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel9Layout.setVerticalGroup(
            panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel9Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13)
                    .addComponent(dcDesdeFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel9Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addComponent(dcHastaFechaRegistro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        btnTraerTodoCompras.setText("todos");
        btnTraerTodoCompras.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnTraerTodoCompras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraerTodoComprasActionPerformed(evt);
            }
        });

        panel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Proveedor"));
        panel10.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel10.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel10.setOpaque(false);

        jLabel15.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Desde");

        jLabel16.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Hasta");

        txtDesdeProveedor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDesdeProveedor.setText("A");
        txtDesdeProveedor.setToolTipText("");
        txtDesdeProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeProveedorKeyReleased(evt);
            }
        });

        txtHastaProveedor.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtHastaProveedor.setText("Z");
        txtHastaProveedor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHastaProveedorKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panel10Layout = new javax.swing.GroupLayout(panel10);
        panel10.setLayout(panel10Layout);
        panel10Layout.setHorizontalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel10Layout.createSequentialGroup()
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeProveedor)))
        );
        panel10Layout.setVerticalGroup(
            panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel10Layout.createSequentialGroup()
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(txtDesdeProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap())
        );

        panel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fecha de compra  "));
        panel11.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel11.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel11.setOpaque(false);

        jLabel17.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Desde");

        jLabel18.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Hasta");

        javax.swing.GroupLayout panel11Layout = new javax.swing.GroupLayout(panel11);
        panel11.setLayout(panel11Layout);
        panel11Layout.setHorizontalGroup(
            panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel11Layout.createSequentialGroup()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcHastaFechaCompra, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
            .addGroup(panel11Layout.createSequentialGroup()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcDesdeFechaCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel11Layout.setVerticalGroup(
            panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel11Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17)
                    .addComponent(dcDesdeFechaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel11Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addComponent(dcHastaFechaCompra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        btnFiltroCompras1.setBackground(new java.awt.Color(0, 51, 204));
        btnFiltroCompras1.setText("FILTRAR");
        btnFiltroCompras1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroCompras1ActionPerformed(evt);
            }
        });

        btnFiltroCompras2.setText("mes anterior");
        btnFiltroCompras2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnFiltroCompras2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroCompras2ActionPerformed(evt);
            }
        });

        cbMonedaCompras.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        cbMonedaCompras.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dolares", "Guaranies", "Reales", "Pesos argentinos" }));
        cbMonedaCompras.setSelectedIndex(1);
        cbMonedaCompras.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMonedaComprasItemStateChanged(evt);
            }
        });

        lblBuscarCampo1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblBuscarCampo1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo1.setText("Moneda:");

        javax.swing.GroupLayout pnComprasLayout = new javax.swing.GroupLayout(pnCompras);
        pnCompras.setLayout(pnComprasLayout);
        pnComprasLayout.setHorizontalGroup(
            pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnComprasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBuscarCampo1)
                    .addComponent(cbMonedaCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnComprasLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnComprasLayout.createSequentialGroup()
                        .addGroup(pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnFiltroCompras, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnFiltroCompras2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTraerTodoCompras, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(257, 257, 257)
                        .addComponent(btnFiltroCompras1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnComprasLayout.setVerticalGroup(
            pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnComprasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnComprasLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(7, 7, 7)
                        .addComponent(btnFiltroCompras2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltroCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTraerTodoCompras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBuscarCampo1)
                        .addGap(0, 0, 0)
                        .addComponent(cbMonedaCompras, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnComprasLayout.createSequentialGroup()
                        .addGroup(pnComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(panel7, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFiltroCompras1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TabPane.addTab("Compras", pnCompras);

        pnProductos.setColorPrimario(new java.awt.Color(255, 255, 255));
        pnProductos.setColorSecundario(new java.awt.Color(233, 255, 255));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel1.setText("FILTRAR POR:");

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Código del producto  "));
        panel1.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel1.setOpaque(false);

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Desde");

        txtDesdeIdentificador.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesdeIdentificador.setText("0");
        txtDesdeIdentificador.setToolTipText("");
        txtDesdeIdentificador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeIdentificadorKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDesdeIdentificadorKeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Hasta");

        txtHastaIdentificador.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHastaIdentificador.setText("999999");
        txtHastaIdentificador.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHastaIdentificadorKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaIdentificador, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeIdentificador)))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtDesdeIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(3, 3, 3)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaIdentificador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)))
        );

        btnFiltroProductos.setBackground(new java.awt.Color(0, 153, 153));
        btnFiltroProductos.setText("FILTRAR");
        btnFiltroProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroProductosActionPerformed(evt);
            }
        });

        panel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Marca  "));
        panel6.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel6.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel6.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Desde");

        txtDesdeMarca.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDesdeMarca.setText("A");
        txtDesdeMarca.setToolTipText("");
        txtDesdeMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDesdeMarcaKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Hasta");

        txtHastaMarca.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtHastaMarca.setText("Z");
        txtHastaMarca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHastaMarcaKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeMarca)))
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtDesdeMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(3, 3, 3)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)))
        );

        panel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Stock/Existencia"));
        panel5.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel5.setColorSecundario(new java.awt.Color(255, 255, 255));
        panel5.setOpaque(false);

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Desde");

        txtDesdeExistencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDesdeExistencia.setText("0");
        txtDesdeExistencia.setToolTipText("");
        txtDesdeExistencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDesdeExistenciaKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Hasta");

        txtHastaExistencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtHastaExistencia.setText("999999");
        txtHastaExistencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtHastaExistenciaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtHastaExistencia, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(txtDesdeExistencia)))
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel5Layout.createSequentialGroup()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtDesdeExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(3, 3, 3)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtHastaExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );

        btnTraeTodoProductos.setBackground(new java.awt.Color(0, 153, 153));
        btnTraeTodoProductos.setText("Todos");
        btnTraeTodoProductos.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnTraeTodoProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraeTodoProductosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnProductosLayout = new javax.swing.GroupLayout(pnProductos);
        pnProductos.setLayout(pnProductosLayout);
        pnProductosLayout.setHorizontalGroup(
            pnProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProductosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnProductosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProductosLayout.createSequentialGroup()
                        .addComponent(btnTraeTodoProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)))
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(216, 216, 216))
            .addGroup(pnProductosLayout.createSequentialGroup()
                .addGap(323, 323, 323)
                .addComponent(btnFiltroProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnProductosLayout.setVerticalGroup(
            pnProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnProductosLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(14, 14, 14)
                        .addComponent(btnTraeTodoProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnFiltroProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        TabPane.addTab("Productos", pnProductos);

        panel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel4.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel4.setColorSecundario(new java.awt.Color(233, 255, 255));

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Ordenar por:");

        cbOrdenar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbOrdenarItemStateChanged(evt);
            }
        });

        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbPrincipal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbPrincipal.setEnabled(false);
        tbPrincipal.setGridColor(new java.awt.Color(0, 153, 204));
        tbPrincipal.setOpaque(false);
        tbPrincipal.setRowHeight(20);
        tbPrincipal.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPrincipal.getTableHeader().setReorderingAllowed(false);
        tbPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPrincipalMousePressed(evt);
            }
        });
        tbPrincipal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPrincipalKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbPrincipal);

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                .addComponent(lblBuscarCampo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(341, 341, 341)
                .addComponent(buttonSeven2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(TabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonSeven2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltroProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroProductosActionPerformed
        ConsultaProductos();
    }//GEN-LAST:event_btnFiltroProductosActionPerformed

    private void buttonSeven2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSeven2ActionPerformed
        if (tbPrincipal.getRowCount() <= 0) {
            JOptionPane.showMessageDialog(null, "No existe ningún registro en la tabla", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map parametros;
        String rutajasper;
        SimpleDateFormat formatosuda = new SimpleDateFormat("dd/MM/yyyy");
        switch (laTablaSelect) {
            case "Ventas":
                //Parametros
                parametros = new HashMap();
                parametros.put("ORDEN", cbOrdenar.getSelectedItem().toString());
                parametros.put("MONEDA", cbMonedaCompras.getSelectedItem().toString());
                parametros.put("DESDE_NUMVENTA", txtDesdeNumVenta.getText());
                parametros.put("HASTA_NUMVENTA", txtHastaNumVenta.getText());
                parametros.put("DESDE_VENDEDOR", txtDesdeVendedor.getText());
                parametros.put("HASTA_VENDEDOR", txtHastaVendedor.getText());
                parametros.put("DESDE_CLIENTE", txtDesdeCliente.getText());
                parametros.put("HASTA_CLIENTE", txtHastaCliente.getText());
                parametros.put("DESDE_FECHAVENTA", formatosuda.format(dcDesdeFechaVenta.getDate()));
                parametros.put("HASTA_FECHAVENTA", formatosuda.format(dcHastaFechaVenta.getDate()));
                parametros.put("COTIZACION", "Cotización:   Guaraníes: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdGsCompra)
                        + "   Reales: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdRsCompra)
                        + "   Pesos argentinos: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdPaCompra));
                rutajasper = "/reportes/reporte_ventas.jasper";
                metodos.GenerarReporteJTABLE(rutajasper, parametros, tbPrincipal.getModel());
                break;
            case "Compras":
                //Parametros
                parametros = new HashMap();
                parametros.put("ORDEN", cbOrdenar.getSelectedItem().toString());
                parametros.put("MONEDA", cbMonedaCompras.getSelectedItem().toString());
                parametros.put("DESDE_NUMCOMPRA", txtDesdeNumCompra.getText());
                parametros.put("HASTA_NUMCOMPRA", txtHastaNumCompra.getText());
                parametros.put("DESDE_PROVEEDOR", txtDesdeProveedor.getText());
                parametros.put("HASTA_PROVEEDOR", txtHastaProveedor.getText());
                parametros.put("DESDE_FECHAREGISTRO", formatosuda.format(dcDesdeFechaRegistro.getDate()));
                parametros.put("HASTA_FECHAREGISTRO", formatosuda.format(dcHastaFechaRegistro.getDate()));
                parametros.put("DESDE_FECHACOMPRA", formatosuda.format(dcDesdeFechaCompra.getDate()));
                parametros.put("HASTA_FECHACOMPRA", formatosuda.format(dcHastaFechaCompra.getDate()));
                parametros.put("COTIZACION", "Cotización:   Guaraníes: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdGsCompra)
                        + "   Reales: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdRsCompra)
                        + "   Pesos argentinos: " + metodostxt.DoubleAFormatoSudamerica(cotiUsdPaCompra));
                rutajasper = "/reportes/reporte_compras.jasper";
                metodos.GenerarReporteJTABLE(rutajasper, parametros, tbPrincipal.getModel());
                break;
            case "Productos":
                //Parametros
                parametros = new HashMap();
                parametros.put("ORDEN", cbOrdenar.getSelectedItem().toString());
                parametros.put("DESDE_IDENTIFICADOR", txtDesdeIdentificador.getText());
                parametros.put("HASTA_IDENTIFICADOR", txtHastaIdentificador.getText());
                parametros.put("DESDE_MARCA", txtDesdeMarca.getText());
                parametros.put("HASTA_MARCA", txtHastaMarca.getText());
                parametros.put("DESDE_EXISTENCIA", txtDesdeExistencia.getText());
                parametros.put("HASTA_EXISTENCIA", txtHastaExistencia.getText());
                rutajasper = "/reportes/reporte_productos.jasper";
                metodos.GenerarReporteJTABLE(rutajasper, parametros, tbPrincipal.getModel());
                break;

            default:
                JOptionPane.showMessageDialog(this, "No se encontro", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }//GEN-LAST:event_buttonSeven2ActionPerformed

    private void cbOrdenarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbOrdenarItemStateChanged
        if (tbPrincipal.getRowSorter() == null) {
            RowSorter<TableModel> sorter = new TableRowSorter<>(tbPrincipal.getModel());
            tbPrincipal.setRowSorter(sorter);
            tbPrincipal.getRowSorter().toggleSortOrder(cbOrdenar.getSelectedIndex());
            System.out.println("Tipo de columna " + tbPrincipal.getColumnClass(cbOrdenar.getSelectedIndex()));
            System.out.println("Orden " + tbPrincipal.getRowSorter());
        }
    }//GEN-LAST:event_cbOrdenarItemStateChanged

    private void btnTraeTodoProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraeTodoProductosActionPerformed
        txtDesdeIdentificador.setText("0");
        txtHastaIdentificador.setText("999999");
        txtDesdeMarca.setText("A");
        txtHastaMarca.setText("Z");
        txtDesdeExistencia.setText("0");
        txtHastaExistencia.setText("999999");

        ConsultaProductos();
    }//GEN-LAST:event_btnTraeTodoProductosActionPerformed

    private void txtDesdeMarcaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeMarcaKeyReleased
        metodostxt.TxtMayusKeyReleased(txtDesdeMarca, evt);
    }//GEN-LAST:event_txtDesdeMarcaKeyReleased

    private void txtDesdeIdentificadorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeIdentificadorKeyReleased

    }//GEN-LAST:event_txtDesdeIdentificadorKeyReleased

    private void txtDesdeIdentificadorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeIdentificadorKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtDesdeIdentificadorKeyTyped

    private void txtHastaIdentificadorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaIdentificadorKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtHastaIdentificadorKeyTyped

    private void txtDesdeExistenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeExistenciaKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtDesdeExistenciaKeyTyped

    private void txtHastaExistenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaExistenciaKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtHastaExistenciaKeyTyped

    private void txtHastaMarcaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaMarcaKeyReleased
        metodostxt.TxtMayusKeyReleased(txtHastaMarca, evt);
    }//GEN-LAST:event_txtHastaMarcaKeyReleased

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed

    }//GEN-LAST:event_tbPrincipalMousePressed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased

    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void txtDesdeNumCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeNumCompraKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeNumCompraKeyReleased

    private void txtDesdeNumCompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeNumCompraKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeNumCompraKeyTyped

    private void txtHastaNumCompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaNumCompraKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHastaNumCompraKeyTyped

    private void btnFiltroComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroComprasActionPerformed
        txtDesdeNumCompra.setText("0");
        txtHastaNumCompra.setText("999999");
        txtDesdeProveedor.setText("A");
        txtHastaProveedor.setText("Z");

        dcDesdeFechaRegistro.setDate(PrimerDiaMesActual());
        dcDesdeFechaCompra.setDate(PrimerDiaMesActual());
        dcHastaFechaRegistro.setDate(UltimoDiaMesActual());
        dcHastaFechaCompra.setDate(UltimoDiaMesActual());

        ConsultaCompras();
    }//GEN-LAST:event_btnFiltroComprasActionPerformed

    private Date PrimerDiaMesActual() {
        Date primerdiames = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            int mesActual = cal.get(Calendar.MONTH) + 1; //Enero es 0 y Diciembre 11, por eso se suma
            int anhoActual = cal.get(Calendar.YEAR);
            primerdiames = formato.parse("01/" + mesActual + "/" + anhoActual); //Primer dia del mes y año actual
        } catch (ParseException e) {
        }
        return primerdiames;
    }

    private Date UltimoDiaMesActual() {
        Date ultimodiames = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();
            int mesActual = cal.get(Calendar.MONTH) + 1; //Enero es 0 y Diciembre 11, por eso se suma
            int anhoActual = cal.get(Calendar.YEAR);
            int ultimodia = cal.getActualMaximum(Calendar.DAY_OF_MONTH); //Ultimo dia del mes actual
            ultimodiames = formato.parse(ultimodia + "/" + mesActual + "/" + anhoActual); //Ultimo dia del mes y año actual
        } catch (Exception e) {
        }

        return ultimodiames;
    }

    private void btnTraerTodoComprasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraerTodoComprasActionPerformed
        txtDesdeNumCompra.setText("0");
        txtHastaNumCompra.setText("999999");
        txtDesdeProveedor.setText("A");
        txtHastaProveedor.setText("Z");
        dcDesdeFechaRegistro.setDate(PrimerDiaDelAnho());
        dcDesdeFechaCompra.setDate(PrimerDiaDelAnho());
        dcHastaFechaRegistro.setDate(FechaActual());
        dcHastaFechaRegistro.setDate(FechaActual());

        ConsultaCompras();
    }//GEN-LAST:event_btnTraerTodoComprasActionPerformed

    private void txtDesdeProveedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeProveedorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeProveedorKeyReleased

    private void txtHastaProveedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaProveedorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHastaProveedorKeyReleased

    private void btnFiltroCompras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroCompras1ActionPerformed
        ConsultaCompras();
    }//GEN-LAST:event_btnFiltroCompras1ActionPerformed

    private void btnFiltroCompras2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroCompras2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFiltroCompras2ActionPerformed

    private void cbMonedaComprasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMonedaComprasItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cbMonedaComprasItemStateChanged

    private void txtDesdeNumVentaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeNumVentaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeNumVentaKeyReleased

    private void txtDesdeNumVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeNumVentaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeNumVentaKeyTyped

    private void txtHastaNumVentaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaNumVentaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHastaNumVentaKeyTyped

    private void btnFiltroCompras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroCompras3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFiltroCompras3ActionPerformed

    private void btnTraerTodoCompras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraerTodoCompras1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTraerTodoCompras1ActionPerformed

    private void txtDesdeVendedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeVendedorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeVendedorKeyReleased

    private void txtHastaVendedorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaVendedorKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHastaVendedorKeyReleased

    private void btnFiltroVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroVentasActionPerformed
        ConsultaVentas();
    }//GEN-LAST:event_btnFiltroVentasActionPerformed

    private void btnFiltroCompras5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroCompras5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFiltroCompras5ActionPerformed

    private void cbMonedaVentasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMonedaVentasItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cbMonedaVentasItemStateChanged

    private void txtDesdeClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDesdeClienteKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDesdeClienteKeyReleased

    private void txtHastaClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtHastaClienteKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHastaClienteKeyReleased

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
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Reporte.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Reporte dialog = new Reporte(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    private void ConsultaVentas() {
        SimpleDateFormat formatamericano = new SimpleDateFormat("yyyy/MM/dd");
        String sentencia = "CALL SP_VentasReporte('"
                + txtDesdeNumVenta.getText() + "','" + txtHastaNumVenta.getText()
                + "','" + txtDesdeVendedor.getText() + "','" + txtHastaVendedor.getText()
                + "','" + txtDesdeCliente.getText() + "','" + txtHastaCliente.getText()
                + "','" + formatamericano.format(dcDesdeFechaVenta.getDate()) + "','" + formatamericano.format(dcHastaFechaVenta.getDate()) + "')";
        String titlesJtabla[] = {"N° de venta", "Vendedor/a", "Cliente", "Tipo de documento", "Fecha de venta", "Total de la venta", "Ganancia"};

        tbPrincipal.setModel(con.ConsultaBD(sentencia, titlesJtabla, cbOrdenar));

        double totalventa;
        double ganancia;
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            totalventa = Double.parseDouble(tbPrincipal.getValueAt(i, 5).toString());
            ganancia = Double.parseDouble(tbPrincipal.getValueAt(i, 6).toString());
            switch (cbMonedaVentas.getSelectedItem().toString()) {
                case "Dolares":
                    break;
                case "Guaranies":
                    totalventa = totalventa * cotiUsdGsCompra;
                    ganancia = ganancia * cotiUsdGsCompra;
                    break;
                case "Reales":
                    totalventa = totalventa * cotiUsdRsCompra;
                    ganancia = ganancia * cotiUsdRsCompra;
                    break;
                case "Pesos argentinos":
                    totalventa = totalventa * cotiUsdPaCompra;
                    ganancia = ganancia * cotiUsdPaCompra;
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "No se encontró", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }

            totalventa = metodostxt.FormatearATresDecimales(totalventa);
            ganancia = metodostxt.FormatearATresDecimales(ganancia);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(totalventa), i, 5);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(ganancia), i, 6);
        }
        metodos.AnchuraColumna(tbPrincipal);
        lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
    }

    private void ConsultaCompras() {
        SimpleDateFormat formatamericano = new SimpleDateFormat("yyyy/MM/dd");
        String sentencia = "CALL SP_ComprasReporte('"
                + txtDesdeNumCompra.getText() + "','" + txtHastaNumCompra.getText()
                + "','" + txtDesdeProveedor.getText() + "','" + txtHastaProveedor.getText()
                + "','" + formatamericano.format(dcDesdeFechaRegistro.getDate()) + "','" + formatamericano.format(dcHastaFechaRegistro.getDate())
                + "','" + formatamericano.format(dcDesdeFechaCompra.getDate()) + "','" + formatamericano.format(dcHastaFechaCompra.getDate()) + "')";
        String titlesJtabla[] = {"N° de compra", "Proveedor", "Tipo de documento", "Fecha de registro", "Fecha de compra", "Total de la compra"};

        tbPrincipal.setModel(con.ConsultaBD(sentencia, titlesJtabla, cbOrdenar));

        double totalcompra;
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            totalcompra = Double.parseDouble(tbPrincipal.getValueAt(i, 5).toString());

            switch (cbMonedaCompras.getSelectedItem().toString()) {
                case "Dolares":
                    break;
                case "Guaranies":
                    totalcompra = totalcompra * cotiUsdGsCompra;
                    break;
                case "Reales":
                    totalcompra = totalcompra * cotiUsdRsCompra;
                    break;
                case "Pesos argentinos":
                    totalcompra = totalcompra * cotiUsdPaCompra;
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "No se encontró", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }

            totalcompra = metodostxt.FormatearATresDecimales(totalcompra);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(totalcompra), i, 5);
        }
        metodos.AnchuraColumna(tbPrincipal);
        lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
    }

    private void ConsultaProductos() {
        String sentencia = "CALL SP_ProductosReporte('"
                + txtDesdeIdentificador.getText() + "','" + txtHastaIdentificador.getText()
                + "','" + txtDesdeMarca.getText() + "','" + txtHastaMarca.getText()
                + "','" + txtDesdeExistencia.getText() + "','" + txtHastaExistencia.getText() + "')";
        String titlesJtabla[] = {"Codigo", "Descripcion", "Marca", "Stock/Existencia"};

        tbPrincipal.setModel(con.ConsultaBD(sentencia, titlesJtabla, cbOrdenar));
        metodos.AnchuraColumna(tbPrincipal);
        lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabPane;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroCompras;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroCompras1;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroCompras2;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroCompras3;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroCompras5;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroProductos;
    private org.edisoncor.gui.button.ButtonSeven btnFiltroVentas;
    private org.edisoncor.gui.button.ButtonSeven btnTraeTodoProductos;
    private org.edisoncor.gui.button.ButtonSeven btnTraerTodoCompras;
    private org.edisoncor.gui.button.ButtonSeven btnTraerTodoCompras1;
    private org.edisoncor.gui.button.ButtonSeven buttonSeven2;
    private javax.swing.JComboBox cbMonedaCompras;
    private javax.swing.JComboBox cbMonedaVentas;
    private javax.swing.JComboBox cbOrdenar;
    private com.toedter.calendar.JDateChooser dcDesdeFechaCompra;
    private com.toedter.calendar.JDateChooser dcDesdeFechaRegistro;
    private com.toedter.calendar.JDateChooser dcDesdeFechaVenta;
    private com.toedter.calendar.JDateChooser dcHastaFechaCompra;
    private com.toedter.calendar.JDateChooser dcHastaFechaRegistro;
    private com.toedter.calendar.JDateChooser dcHastaFechaVenta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblBuscarCampo1;
    private javax.swing.JLabel lblBuscarCampo2;
    private org.edisoncor.gui.label.LabelMetric lblTitulo;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel10;
    private org.edisoncor.gui.panel.Panel panel11;
    private org.edisoncor.gui.panel.Panel panel13;
    private org.edisoncor.gui.panel.Panel panel14;
    private org.edisoncor.gui.panel.Panel panel15;
    private org.edisoncor.gui.panel.Panel panel16;
    private org.edisoncor.gui.panel.Panel panel17;
    private org.edisoncor.gui.panel.Panel panel18;
    private org.edisoncor.gui.panel.Panel panel19;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.Panel panel3;
    private org.edisoncor.gui.panel.Panel panel4;
    private org.edisoncor.gui.panel.Panel panel5;
    private org.edisoncor.gui.panel.Panel panel6;
    private org.edisoncor.gui.panel.Panel panel7;
    private org.edisoncor.gui.panel.Panel panel8;
    private org.edisoncor.gui.panel.Panel panel9;
    private org.edisoncor.gui.panel.Panel pnCompras;
    private org.edisoncor.gui.panel.Panel pnProductos;
    private org.edisoncor.gui.panel.Panel pnVentas;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtDesdeCliente;
    private javax.swing.JTextField txtDesdeExistencia;
    private javax.swing.JTextField txtDesdeIdentificador;
    private javax.swing.JTextField txtDesdeMarca;
    private javax.swing.JTextField txtDesdeNumCompra;
    private javax.swing.JTextField txtDesdeNumVenta;
    private javax.swing.JTextField txtDesdeProveedor;
    private javax.swing.JTextField txtDesdeProveedor2;
    private javax.swing.JTextField txtDesdeProveedor3;
    private javax.swing.JTextField txtDesdeProveedor4;
    private javax.swing.JTextField txtDesdeProveedor5;
    private javax.swing.JTextField txtDesdeVendedor;
    private javax.swing.JTextField txtHastaCliente;
    private javax.swing.JTextField txtHastaExistencia;
    private javax.swing.JTextField txtHastaIdentificador;
    private javax.swing.JTextField txtHastaMarca;
    private javax.swing.JTextField txtHastaNumCompra;
    private javax.swing.JTextField txtHastaNumVenta;
    private javax.swing.JTextField txtHastaProveedor;
    private javax.swing.JTextField txtHastaProveedor2;
    private javax.swing.JTextField txtHastaProveedor3;
    private javax.swing.JTextField txtHastaProveedor4;
    private javax.swing.JTextField txtHastaProveedor5;
    private javax.swing.JTextField txtHastaVendedor;
    // End of variables declaration//GEN-END:variables
}
