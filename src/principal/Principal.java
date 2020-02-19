package principal;

import conexion.Conexion;
import forms.ABMCliente;
import forms.ABMFuncionario;
import forms.ABMModulo;
import forms.ABMProducto;
import forms.ABMUsuario;
import forms.Compra;
import forms.Venta;
import forms.RegistrarCompra;
import forms.RegistrarVenta;
import forms.Reporte;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import utilidades.Metodos;
import utilidades.MetodosTXT;
//Variables globales
import static login.Login.Alias;
//

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Principal extends javax.swing.JFrame implements Runnable {
    
    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    Thread hilo;
    public static double cotiUsdGsCompra; //USD: Dolar americano, Gs: Guaranies
    public static double cotiUsdRsCompra; //RS: Reales
    public static double cotiUsdPaCompra; //PA: Peso Argentino

    public Principal() {
        initComponents();
        this.setExtendedState(Principal.MAXIMIZED_BOTH);//Maximizar ventana
        ObtenerHorayFecha();
        lbAlias.setText(Alias);
        PerfilesUsuario(Alias);
        PermisoModulos(Alias);
        AsignarCotizaciones();

        //Roles de usuario 
        meRegistrarCompra.setEnabled(metodos.PermisoRol(Alias, "COMPRA", "ALTA")); //Si el usuario Alias tiene rol de NUEVO COMPRA
        meRegistrarVenta.setEnabled(metodos.PermisoRol(Alias, "VENTA", "ALTA"));
        meAnularCompra.setEnabled(metodos.PermisoRol(Alias, "COMPRA", "BAJA"));
        meAnularVenta.setEnabled(metodos.PermisoRol(Alias, "VENTA", "BAJA"));

        //Redimensionar iconos menu
        meVenta.setIcon(metodos.AjustarIconoAButton(meVenta.getIcon(), meVenta.getHeight()));
        meCompra.setIcon(metodos.AjustarIconoAButton(meCompra.getIcon(), meCompra.getHeight()));
        meProducto.setIcon(metodos.AjustarIconoAButton(meProducto.getIcon(), meProducto.getHeight()));
        meUsuario.setIcon(metodos.AjustarIconoAButton(meUsuario.getIcon(), meUsuario.getHeight()));
        meReporte.setIcon(metodos.AjustarIconoAButton(meReporte.getIcon(), meReporte.getHeight()));
        meConfiguracion.setIcon(metodos.AjustarIconoAButton(meConfiguracion.getIcon(), meConfiguracion.getHeight()));
        meSalir.setIcon(metodos.AjustarIconoAButton(meSalir.getIcon(), meSalir.getHeight()));
    }
    
    private void PermisoModulos(String ElAlias) {
        con = con.ObtenerRSSentencia("CALL SP_UsuarioModuloConsulta('" + ElAlias + "')");
        String modulo;
        try {
            while (con.rs.next()) {
                modulo = con.rs.getString("mo_denominacion");
                switch (modulo) {
                    case "VENTA":
                        btnVenta.setEnabled(true);
                        meVenta.setEnabled(true);
                        break;
                    case "COMPRA":
                        btnCompra.setEnabled(true);
                        meCompra.setEnabled(true);
                        break;
                    case "PRODUCTO":
                        btnProducto.setEnabled(true);
                        meProducto.setEnabled(true);
                        break;
                    case "FUNCIONARIO":
                        btnFuncionario.setEnabled(true);
                        break;
                    case "CLIENTE":
                        btnCliente.setEnabled(true);
                        break;
                    case "USUARIO":
                        btnUsuario.setEnabled(true);
                        meUsuario.setEnabled(true);
                        break;
                    case "REPORTE":
                        meReporte.setEnabled(true);
                        break;
                    case "CONFIGURACION":
                        meConfiguracion.setEnabled(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "No se encontró", "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void AsignarCotizaciones() {
        try {
            con = con.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Guaranies'");
            con.rs.next();
            SimpleDateFormat formatoFechaAmericano = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat formatoFechaSudamerica = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date fechaFormatoAmericano = formatoFechaAmericano.parse(con.rs.getString("coti_fecha").replace("-", "/"));
            lblFechaCotizacion.setText("Fecha de cotización: " + formatoFechaSudamerica.format(fechaFormatoAmericano));
            
            cotiUsdGsCompra = con.rs.getDouble("coti_valorcompra"); //Variable global
            lblCotiUsdGsCompra.setText(metodostxt.DoubleAFormatoSudamerica(cotiUsdGsCompra));
            lblCotiUsdGsVenta.setText(metodostxt.DoubleAFormatoSudamerica(con.rs.getDouble("coti_valorventa")));
            
            con = con.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Reales'");
            con.rs.next();
            cotiUsdRsCompra = Double.parseDouble(con.rs.getString("coti_valorcompra"));
            lblCotiUsdRsCompra.setText(metodostxt.DoubleAFormatoSudamerica(cotiUsdRsCompra));
            lblCotiUsdRsVenta.setText(metodostxt.DoubleAFormatoSudamerica(con.rs.getDouble("coti_valorventa")));
            
            con = con.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Pesos argentinos'");
            con.rs.next();
            cotiUsdPaCompra = Double.parseDouble(con.rs.getString("coti_valorcompra"));
            lblCotiUsdPaCompra.setText(metodostxt.DoubleAFormatoSudamerica(cotiUsdPaCompra));
            lblCotiUsdPaVenta.setText(metodostxt.DoubleAFormatoSudamerica(con.rs.getDouble("coti_valorventa")));
            
            con.DesconectarBasedeDatos();
        } catch (SQLException e) {
            System.out.println("Error al asignar cotizaciones desde bd" + e);
        } catch (ParseException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void PerfilesUsuario(String alias) {
        
        String consulta = "SELECT per_codigo, per_denominacion FROM usuario,perfil,usuario_perfil "
                + "WHERE usu_alias = '" + alias + "' AND usuper_usuario = usu_codigo AND usuper_perfil = per_codigo ORDER BY per_denominacion";
        con = con.ObtenerRSSentencia(consulta);
        try {
            String perfil = "";
            int numfila = 1;
            while (con.rs.next()) {
                if (numfila == 1) {
                    perfil = con.rs.getString("per_denominacion");
                } else {
                    perfil = perfil + ", " + con.rs.getString("per_denominacion");
                }
                numfila = numfila + 1;
            }
            lblPerfil.setText(perfil);
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.DesconectarBasedeDatos();
    }
    
    private void ObtenerHorayFecha() {
        //Obtener fecha y hora
        hilo = new Thread(this);
        hilo.start();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        piPrincipal = new org.edisoncor.gui.panel.PanelImage();
        btnCliente = new javax.swing.JButton();
        btnFuncionario = new javax.swing.JButton();
        btnCompra = new javax.swing.JButton();
        btnVenta = new javax.swing.JButton();
        btnProducto = new javax.swing.JButton();
        btnUsuario = new javax.swing.JButton();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        lbAlias = new javax.swing.JLabel();
        lblPerfil = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbFechaTitulo = new javax.swing.JLabel();
        lbFecha = new javax.swing.JLabel();
        lbHoraTitulo = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        panel2 = new org.edisoncor.gui.panel.Panel();
        lblEeuu2 = new javax.swing.JLabel();
        lbldolares3 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbldolares4 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblCotiUsdRsCompra = new javax.swing.JLabel();
        lblCotiUsdGsVenta = new javax.swing.JLabel();
        lblCotiUsdPaCompra = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbldolares7 = new javax.swing.JLabel();
        lblCotiUsdRsVenta = new javax.swing.JLabel();
        lblFlagEeuu = new javax.swing.JLabel();
        lblCotiUsdPaVenta = new javax.swing.JLabel();
        lblFlagGuaranies = new javax.swing.JLabel();
        lblFechaCotizacion = new javax.swing.JLabel();
        lblFlagBrasil = new javax.swing.JLabel();
        lblFlagArgentina = new javax.swing.JLabel();
        lblEeuu1 = new javax.swing.JLabel();
        lblCotiUsdGsCompra = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        meVenta = new javax.swing.JMenu();
        meRegistrarVenta = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        meAnularVenta = new javax.swing.JMenuItem();
        meCompra = new javax.swing.JMenu();
        meRegistrarCompra = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        meAnularCompra = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        meProducto = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem16 = new javax.swing.JMenuItem();
        meReporte = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        jMenuItem24 = new javax.swing.JMenuItem();
        meUsuario = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        meConfiguracion = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        meSalir = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Principal");
        setName("Fm_Principal"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        piPrincipal.setFocusable(false);
        piPrincipal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/principal/iconos/fondo1.png"))); // NOI18N
        piPrincipal.setPreferredSize(new java.awt.Dimension(2000, 655));

        btnCliente.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoClientes70.png"))); // NOI18N
        btnCliente.setText("CLIENTES");
        btnCliente.setEnabled(false);
        btnCliente.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteActionPerformed(evt);
            }
        });

        btnFuncionario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoFuncionario70.png"))); // NOI18N
        btnFuncionario.setText("FUNCIONARIOS");
        btnFuncionario.setEnabled(false);
        btnFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFuncionarioActionPerformed(evt);
            }
        });

        btnCompra.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoCompra70.png"))); // NOI18N
        btnCompra.setText("COMPRAS");
        btnCompra.setEnabled(false);
        btnCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompraActionPerformed(evt);
            }
        });

        btnVenta.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoVenta70.png"))); // NOI18N
        btnVenta.setText("VENTAS");
        btnVenta.setEnabled(false);
        btnVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentaActionPerformed(evt);
            }
        });

        btnProducto.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoProducto70.png"))); // NOI18N
        btnProducto.setText("PRODUCTOS");
        btnProducto.setEnabled(false);
        btnProducto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductoActionPerformed(evt);
            }
        });

        btnUsuario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario70.png"))); // NOI18N
        btnUsuario.setText("USUARIOS");
        btnUsuario.setEnabled(false);
        btnUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuarioActionPerformed(evt);
            }
        });

        panel1.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(154, 255, 255));
        panel1.setGradiente(org.edisoncor.gui.panel.Panel.Gradiente.VERTICAL);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoUsuario.png"))); // NOI18N
        jLabel1.setText("Usuario:");

        lbAlias.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbAlias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbAlias.setText("Error de usuario");

        lblPerfil.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblPerfil.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPerfil.setText("Error de perfil");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Perfil:");

        lbFechaTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbFechaTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFechaTitulo.setText("Fecha de hoy:");
        lbFechaTitulo.setFocusable(false);
        lbFechaTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbFecha.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbFecha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbFecha.setText("00/00/0000");
        lbFecha.setFocusable(false);
        lbFecha.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbHoraTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbHoraTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbHoraTitulo.setText("Hora actual:");
        lbHoraTitulo.setFocusable(false);
        lbHoraTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbHora.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbHora.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbHora.setText("00:00:00");
        lbHora.setFocusable(false);
        lbHora.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(508, 508, 508)
                .addComponent(lbFechaTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFecha)
                .addGap(18, 18, 18)
                .addComponent(lbHoraTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFechaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbHoraTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lbFechaTitulo.getAccessibleContext().setAccessibleName("");

        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cotización"));
        panel2.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel2.setColorSecundario(new java.awt.Color(154, 255, 255));

        lblEeuu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        lbldolares3.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares3.setText("Dólar americano x Guaraníes");

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("COMPRA");

        lbldolares4.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares4.setText("Dólar americano x Reales");

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("VENTA");

        lblCotiUsdRsCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdRsCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdRsCompra.setText("0,000");

        lblCotiUsdGsVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdGsVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdGsVenta.setText("0,000");

        lblCotiUsdPaCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdPaCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdPaCompra.setText("0,000");

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("MONEDA");

        lbldolares7.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares7.setText("Dólar x Pesos argentinos");

        lblCotiUsdRsVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdRsVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdRsVenta.setText("0,000");

        lblFlagEeuu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        lblCotiUsdPaVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdPaVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdPaVenta.setText("0,000");

        lblFlagGuaranies.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paraguay.png"))); // NOI18N

        lblFechaCotizacion.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        lblFechaCotizacion.setText("Fecha de cotización: 00/00/0000");

        lblFlagBrasil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/brasil.png"))); // NOI18N

        lblFlagArgentina.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/argentina.png"))); // NOI18N

        lblEeuu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        lblCotiUsdGsCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdGsCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdGsCompra.setText("0,000");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaCotizacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEeuu1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEeuu2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFlagArgentina, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbldolares3)
                                    .addComponent(lbldolares4, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbldolares7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblCotiUsdGsCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblCotiUsdPaCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblCotiUsdRsCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCotiUsdGsVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCotiUsdRsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCotiUsdPaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldolares3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdGsCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdGsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEeuu1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldolares4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdRsCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdRsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFlagArgentina, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEeuu2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbldolares7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCotiUsdPaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCotiUsdPaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblFechaCotizacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout piPrincipalLayout = new javax.swing.GroupLayout(piPrincipal);
        piPrincipal.setLayout(piPrincipalLayout);
        piPrincipalLayout.setHorizontalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(piPrincipalLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFuncionario)
                    .addComponent(btnProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 724, Short.MAX_VALUE)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        piPrincipalLayout.setVerticalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(piPrincipalLayout.createSequentialGroup()
                .addGroup(piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(piPrincipalLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(piPrincipalLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(btnVenta)
                        .addGap(18, 18, 18)
                        .addComponent(btnCompra)
                        .addGap(18, 18, 18)
                        .addComponent(btnProducto)
                        .addGap(18, 18, 18)
                        .addComponent(btnFuncionario)
                        .addGap(18, 18, 18)
                        .addComponent(btnCliente)
                        .addGap(18, 18, 18)
                        .addComponent(btnUsuario)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)))
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jMenuBar1.setMinimumSize(new java.awt.Dimension(120, 70));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(120, 55));

        meVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoVenta70.png"))); // NOI18N
        meVenta.setText("VENTAS");
        meVenta.setEnabled(false);
        meVenta.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meVenta.setPreferredSize(new java.awt.Dimension(170, 70));
        meVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meVentaActionPerformed(evt);
            }
        });

        meRegistrarVenta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        meRegistrarVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrarCompra25.png"))); // NOI18N
        meRegistrarVenta.setText("Registrar venta");
        meRegistrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meRegistrarVentaActionPerformed(evt);
            }
        });
        meVenta.add(meRegistrarVenta);
        meVenta.add(jSeparator11);

        meAnularVenta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.SHIFT_MASK));
        meAnularVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meAnularVenta.setText("Anular venta");
        meAnularVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meAnularVentaActionPerformed(evt);
            }
        });
        meVenta.add(meAnularVenta);

        jMenuBar1.add(meVenta);

        meCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoCompra70.png"))); // NOI18N
        meCompra.setText("COMPRAS");
        meCompra.setEnabled(false);
        meCompra.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meCompra.setPreferredSize(new java.awt.Dimension(170, 70));
        meCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meCompraActionPerformed(evt);
            }
        });

        meRegistrarCompra.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        meRegistrarCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrarCompra25.png"))); // NOI18N
        meRegistrarCompra.setText("Registrar compra");
        meRegistrarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meRegistrarCompraActionPerformed(evt);
            }
        });
        meCompra.add(meRegistrarCompra);
        meCompra.add(jSeparator15);

        meAnularCompra.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.SHIFT_MASK));
        meAnularCompra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meAnularCompra.setText("Anular compra");
        meAnularCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meAnularCompraActionPerformed(evt);
            }
        });
        meCompra.add(meAnularCompra);
        meCompra.add(jSeparator16);

        jMenuItem10.setText("Proveedores");
        jMenuItem10.setToolTipText("");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        meCompra.add(jMenuItem10);

        jMenuBar1.add(meCompra);

        meProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoProducto70.png"))); // NOI18N
        meProducto.setText("PRODUCTOS");
        meProducto.setToolTipText("");
        meProducto.setEnabled(false);
        meProducto.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meProducto.setMinimumSize(new java.awt.Dimension(200, 70));
        meProducto.setPreferredSize(new java.awt.Dimension(170, 70));
        meProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meProductoActionPerformed(evt);
            }
        });

        jMenuItem8.setText("Categorias");
        jMenuItem8.setToolTipText("");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        meProducto.add(jMenuItem8);
        meProducto.add(jSeparator8);

        jMenuItem16.setText("Marcas");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        meProducto.add(jMenuItem16);

        jMenuBar1.add(meProducto);

        meReporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoReporte70.png"))); // NOI18N
        meReporte.setText("REPORTES");
        meReporte.setEnabled(false);
        meReporte.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meReporte.setPreferredSize(new java.awt.Dimension(170, 70));

        jMenuItem7.setText("Reporte de compras");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        meReporte.add(jMenuItem7);
        meReporte.add(jSeparator19);

        jMenuItem22.setText("Reporte de ventas");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        meReporte.add(jMenuItem22);
        meReporte.add(jSeparator20);

        jMenuItem24.setText("Reporte de productos");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        meReporte.add(jMenuItem24);

        jMenuBar1.add(meReporte);

        meUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario70.png"))); // NOI18N
        meUsuario.setText("USUARIOS");
        meUsuario.setEnabled(false);
        meUsuario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meUsuario.setMinimumSize(new java.awt.Dimension(210, 70));
        meUsuario.setPreferredSize(new java.awt.Dimension(170, 70));

        jMenuItem9.setText("Perfiles");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem9);
        meUsuario.add(jSeparator7);

        jMenuItem4.setText("Modulos");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem4);
        meUsuario.add(jSeparator6);

        jMenuItem17.setText("Roles");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem17);
        meUsuario.add(jSeparator18);

        jMenuItem18.setText("Cambiar contraseña");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem18);

        jMenuBar1.add(meUsuario);

        meConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoConfiguracion70.png"))); // NOI18N
        meConfiguracion.setText("CONFIGURACIÓN");
        meConfiguracion.setEnabled(false);
        meConfiguracion.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meConfiguracion.setPreferredSize(new java.awt.Dimension(220, 70));

        jMenuItem6.setText("Cotización");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        meConfiguracion.add(jMenuItem6);

        jMenuBar1.add(meConfiguracion);

        meSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoSalir70.png"))); // NOI18N
        meSalir.setText("SALIR");
        meSalir.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meSalir.setPreferredSize(new java.awt.Dimension(220, 70));

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem19.setText("OK");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        meSalir.add(jMenuItem19);

        jMenuBar1.add(meSalir);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(piPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1386, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(piPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Principal");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        ABMModulo abmmodulo = new ABMModulo(this, true);
        abmmodulo.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

    }//GEN-LAST:event_jMenuItem9ActionPerformed
    

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

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        /*TruncarTabla truncartabla = new TruncarTabla(this, true);
        truncartabla.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void btnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteActionPerformed
        ABMCliente abmcliente = new ABMCliente(this, true);
        abmcliente.setVisible(true);
    }//GEN-LAST:event_btnClienteActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        ABMFuncionario abmempleado = new ABMFuncionario(this, true);
        abmempleado.setVisible(true);
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void meVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meVentaActionPerformed

    }//GEN-LAST:event_meVentaActionPerformed

    private void meRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meRegistrarVentaActionPerformed
        RegistrarVenta registrarventa = new RegistrarVenta(this, true);
        registrarventa.setVisible(true);
    }//GEN-LAST:event_meRegistrarVentaActionPerformed

    private void btnCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompraActionPerformed
        Compra compra = new Compra(this, false);
        compra.setVisible(true);
    }//GEN-LAST:event_btnCompraActionPerformed

    private void btnVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentaActionPerformed
        Venta venta = new Venta(this, false);
        venta.setVisible(true);
    }//GEN-LAST:event_btnVentaActionPerformed

    private void meRegistrarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meRegistrarCompraActionPerformed
        RegistrarCompra registrarcompra = new RegistrarCompra(this, true);
        registrarcompra.setVisible(true);
    }//GEN-LAST:event_meRegistrarCompraActionPerformed

    private void meCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meCompraActionPerformed

    private void btnProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductoActionPerformed
        ABMProducto abmproducto = new ABMProducto(this, true);
        abmproducto.setVisible(true);
    }//GEN-LAST:event_btnProductoActionPerformed

    private void btnUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuarioActionPerformed
        ABMUsuario abmusuarios = new ABMUsuario(this, true);
        abmusuarios.setVisible(true);
    }//GEN-LAST:event_btnUsuarioActionPerformed

    private void meAnularVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meAnularVentaActionPerformed
        Venta venta = new Venta(this, true);
        venta.setVisible(true);
    }//GEN-LAST:event_meAnularVentaActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        Reporte reporte = new Reporte(this, true, "Compras");
        reporte.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        Reporte reporte = new Reporte(this, true, "Ventas");
        reporte.setVisible(true);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void meAnularCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meAnularCompraActionPerformed
        Compra anularcompra = new Compra(this, true);
        anularcompra.setVisible(true);
    }//GEN-LAST:event_meAnularCompraActionPerformed

    private void meProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meProductoActionPerformed

    }//GEN-LAST:event_meProductoActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        /*ABMFormulacion abmformulacion = new ABMFormulacion(null, this, false);
        abmformulacion.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        /*ABMEmpresaRegistrante abmempresaregistrante = new ABMEmpresaRegistrante(null, this, false);
        abmempresaregistrante.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        Reporte reporte = new Reporte(this, true, "Productos");
        reporte.setVisible(true);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

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
    private javax.swing.JButton btnCliente;
    private javax.swing.JButton btnCompra;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnProducto;
    private javax.swing.JButton btnUsuario;
    private javax.swing.JButton btnVenta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JLabel lbAlias;
    private javax.swing.JLabel lbFecha;
    private javax.swing.JLabel lbFechaTitulo;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbHoraTitulo;
    private javax.swing.JLabel lblCotiUsdGsCompra;
    private javax.swing.JLabel lblCotiUsdGsVenta;
    private javax.swing.JLabel lblCotiUsdPaCompra;
    private javax.swing.JLabel lblCotiUsdPaVenta;
    private javax.swing.JLabel lblCotiUsdRsCompra;
    private javax.swing.JLabel lblCotiUsdRsVenta;
    private javax.swing.JLabel lblEeuu1;
    private javax.swing.JLabel lblEeuu2;
    private javax.swing.JLabel lblFechaCotizacion;
    private javax.swing.JLabel lblFlagArgentina;
    private javax.swing.JLabel lblFlagBrasil;
    private javax.swing.JLabel lblFlagEeuu;
    private javax.swing.JLabel lblFlagGuaranies;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JLabel lbldolares3;
    private javax.swing.JLabel lbldolares4;
    private javax.swing.JLabel lbldolares7;
    private javax.swing.JMenuItem meAnularCompra;
    private javax.swing.JMenuItem meAnularVenta;
    private javax.swing.JMenu meCompra;
    private javax.swing.JMenu meConfiguracion;
    private javax.swing.JMenu meProducto;
    private javax.swing.JMenuItem meRegistrarCompra;
    private javax.swing.JMenuItem meRegistrarVenta;
    private javax.swing.JMenu meReporte;
    private javax.swing.JMenu meSalir;
    private javax.swing.JMenu meUsuario;
    private javax.swing.JMenu meVenta;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.PanelImage piPrincipal;
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
