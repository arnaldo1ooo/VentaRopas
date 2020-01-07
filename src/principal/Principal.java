/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;
import conexion.Conexion;
import forms.ABMCliente;
import forms.ABMEmpleado;
import forms.ABMProducto;
import forms.RegistrarCompra;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import metodos.ImagenFondo;
import metodos.Metodos;
import metodos.MetodosTXT;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//Variables globales
import static login.Login.Alias;
//
/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Principal extends javax.swing.JFrame implements Runnable {

    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    Thread hilo;
    public static double cotiUsdGsCompra; //USD: Dolar americano, Gs: Guaranies
    public static double cotiUsdRsCompra; //RS: Reales
    public static double cotiUsdPaCompra; //PA: Peso Argentino

    public Principal() {
        initComponents();
        dpEscritorio.setBorder(new ImagenFondo());//Imagen de fondo al jdesktopane
        this.setExtendedState(Principal.MAXIMIZED_BOTH);//Maximizar ventana
        ObtenerHorayFecha();
        lbAlias.setText(Alias);
        PerfilUsuario();

        setVisible(true);
        ObtenerCotizacionScrapingWeb();
        AsignarCotizaciones();
    }

    private void AsignarCotizaciones() {
        try {
            Conexion con = metodos.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Guaranies'");
            con.rs.next();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");  //25/08/2015
            lblFechaCotizacion.setText("Fecha de cotización: " + formato.format(con.rs.getDate("coti_fecha")));

            cotiUsdGsCompra = Double.parseDouble(con.rs.getString("coti_valorcompra"));
            lblCotiUsdGsCompra.setText(cotiUsdGsCompra + "");
            lblCotiUsdGsVenta.setText(con.rs.getString("coti_valorventa"));

            con = metodos.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Reales'");
            con.rs.next();
            cotiUsdRsCompra = Double.parseDouble(con.rs.getString("coti_valorcompra"));
            lblCotiUsdRsCompra.setText(cotiUsdRsCompra + "");
            lblCotiUsdRsVenta.setText(con.rs.getString("coti_valorventa"));

            con = metodos.ObtenerRSSentencia("SELECT coti_valorcompra, coti_valorventa, coti_fecha "
                    + "FROM cotizacion WHERE coti_de='Dolares' AND coti_a='Pesos argentinos'");
            con.rs.next();
            cotiUsdPaCompra = Double.parseDouble(con.rs.getString("coti_valorcompra"));
            lblCotiUsdPaCompra.setText(cotiUsdPaCompra + "");
            lblCotiUsdPaVenta.setText(con.rs.getString("coti_valorventa"));

            con.DesconectarBasedeDatos();
        } catch (SQLException e) {
            System.out.println("Error al asignar cotizaciones desde bd" + e);
        }
    }

    private void ObtenerCotizacionScrapingWeb() {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect("http://www.cambioschaco.com.py/").validateTLSCertificates(false).get();

            //Obtiene el titulo de la pagina
            String title = doc.title();
            System.out.println("\nScraping Web (CAMBIOS CHACO)");
            System.out.println("Titulo de la pagina:  " + title + "\n");

            Elements losDiv;
            Elements losTr;
            Element fila1;
            Element fila2;

            losDiv = doc.select("div." + "col-sm-7"); //Las tablas, div.
            losTr = losDiv.select("tr"); //Las filas, tr.
            fila1 = losTr.get(1); //el get(0) seria los titulos
            String usdGsCompraString = fila1.getElementsByClass("purchase").text();
            double usdGsCompraDouble = Double.parseDouble(((usdGsCompraString).replace(".", "")).replace(",", "."));
            System.out.println("Compra Dolar x Guaranies: " + usdGsCompraString
                    + "     Compra Dolar x Guaranies Double: " + usdGsCompraDouble);

            String usdGsVentaString = fila1.getElementsByClass("sale").text();
            double usdGsVentaDouble = Double.parseDouble(((usdGsVentaString).replace(".", "")).replace(",", "."));
            System.out.println("Venta Dolar x Guaranies: " + usdGsVentaString
                    + "     Venta Dolar x Guaranies Double: " + usdGsVentaDouble + "\n");
            metodos.EjecutarUpdate("CALL SP_CotizacionModificar('Dolares','Guaranies','" + usdGsCompraDouble + "','"
                    + usdGsVentaDouble + "','" + FechaActual() + "')");

            losDiv = doc.select("div." + "col-sm-5"); //Las tablas, div.
            losTr = losDiv.select("tr"); //Las filas, tr.
            fila1 = losTr.get(1); //el get(0) seria los titulos
            fila2 = losTr.get(2); //el get(0) seria los titulos

            String usdRsCompraString = fila1.getElementsByClass("purchase").text();
            double usdRsCompraDouble = Double.parseDouble(((usdRsCompraString).replace(".", "")).replace(",", "."));
            System.out.println("Compra Dolar x Reales: " + usdRsCompraString
                    + "     Compra Dolar x Reales Double: " + usdRsCompraDouble);

            String usdRsVentaString = fila1.getElementsByClass("sale").text();
            double usdRsVentaDouble = Double.parseDouble(((usdRsVentaString).replace(".", "")).replace(",", "."));
            System.out.println("Venta Dolar x Reales: " + usdRsVentaString
                    + "     Venta Dolar x Reales Double: " + usdRsVentaDouble + "\n");
            metodos.EjecutarUpdate("CALL SP_CotizacionModificar('Dolares','Reales','" + usdRsCompraDouble + "','"
                    + usdRsVentaDouble + "','" + FechaActual() + "')");

            String usdPaCompraString = fila2.getElementsByClass("purchase").text();
            double usdPaCompraDouble = Double.parseDouble(((usdPaCompraString).replace(".", "")).replace(",", "."));
            System.out.println("Compra Dolar x PesoArg: " + usdPaCompraString
                    + "     Compra Dolar x PesoArg Double: " + usdPaCompraDouble);

            String usdPaVentaString = fila2.getElementsByClass("sale").text();
            double usdPaVentaDouble = Double.parseDouble(((usdPaVentaString).replace(".", "")).replace(",", "."));
            System.out.println("Venta Dolar x PesoArg: " + usdPaVentaString
                    + "     Venta Dolar x PesoArg Double: " + usdPaVentaDouble + "\n");
            metodos.EjecutarUpdate("CALL SP_CotizacionModificar('Dolares','Pesos argentinos','" + usdPaCompraDouble + "','"
                    + usdPaVentaDouble + "','" + FechaActual() + "')");
        } catch (IOException e) {
            System.out.println("Error al realizar el scraping web " + e);
        }
    }

    private String FechaActual() {
        Date fechaactual = new Date();
        DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        String fechaactualString = formatoFecha.format(fechaactual);
        return fechaactualString;
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
        jpBotones1 = new javax.swing.JPanel();
        lblCotiUsdGsCompra = new javax.swing.JLabel();
        lbldolares3 = new javax.swing.JLabel();
        lbldolares4 = new javax.swing.JLabel();
        lblCotiUsdRsCompra = new javax.swing.JLabel();
        lblCotiUsdPaCompra = new javax.swing.JLabel();
        lbldolares7 = new javax.swing.JLabel();
        lblFlagEeuu = new javax.swing.JLabel();
        lblFlagGuaranies = new javax.swing.JLabel();
        lblFlagBrasil = new javax.swing.JLabel();
        lblFlagArgentina = new javax.swing.JLabel();
        lblEeuu1 = new javax.swing.JLabel();
        lblEeuu2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblCotiUsdGsVenta = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblCotiUsdRsVenta = new javax.swing.JLabel();
        lblCotiUsdPaVenta = new javax.swing.JLabel();
        lblFechaCotizacion = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
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
        jMenu2 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
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

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoEmpleados.png"))); // NOI18N
        jButton3.setText("EMPLEADOS");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jpBotones1.setBackground(new java.awt.Color(255, 255, 255));
        jpBotones1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cotización"));
        jpBotones1.setPreferredSize(new java.awt.Dimension(100, 50));

        lblCotiUsdGsCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdGsCompra.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdGsCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdGsCompra.setText("0,000");

        lbldolares3.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares3.setForeground(new java.awt.Color(102, 102, 102));
        lbldolares3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares3.setText("Dólar americano x Guaraníes");

        lbldolares4.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares4.setForeground(new java.awt.Color(102, 102, 102));
        lbldolares4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares4.setText("Dólar americano x Reales");

        lblCotiUsdRsCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdRsCompra.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdRsCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdRsCompra.setText("0,000");

        lblCotiUsdPaCompra.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdPaCompra.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdPaCompra.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdPaCompra.setText("0,000");

        lbldolares7.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        lbldolares7.setForeground(new java.awt.Color(102, 102, 102));
        lbldolares7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbldolares7.setText("Dólar x Pesos argentinos");

        lblFlagEeuu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        lblFlagGuaranies.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paraguay.png"))); // NOI18N

        lblFlagBrasil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/brasil.png"))); // NOI18N

        lblFlagArgentina.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/argentina.png"))); // NOI18N

        lblEeuu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        lblEeuu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/eeuu.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("COMPRA");

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("VENTA");

        lblCotiUsdGsVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdGsVenta.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdGsVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdGsVenta.setText("0,000");

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("MONEDA");

        lblCotiUsdRsVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdRsVenta.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdRsVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdRsVenta.setText("0,000");

        lblCotiUsdPaVenta.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblCotiUsdPaVenta.setForeground(new java.awt.Color(102, 102, 102));
        lblCotiUsdPaVenta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCotiUsdPaVenta.setText("0,000");

        lblFechaCotizacion.setFont(new java.awt.Font("sansserif", 0, 10)); // NOI18N
        lblFechaCotizacion.setForeground(new java.awt.Color(51, 51, 51));
        lblFechaCotizacion.setText("Fecha de cotización: 00/00/0000");

        javax.swing.GroupLayout jpBotones1Layout = new javax.swing.GroupLayout(jpBotones1);
        jpBotones1.setLayout(jpBotones1Layout);
        jpBotones1Layout.setHorizontalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaCotizacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpBotones1Layout.createSequentialGroup()
                        .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpBotones1Layout.createSequentialGroup()
                                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEeuu1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEeuu2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFlagArgentina, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbldolares3)
                                    .addComponent(lbldolares4, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbldolares7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                .addComponent(lblCotiUsdGsCompra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblCotiUsdPaCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                .addComponent(lblCotiUsdRsCompra, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCotiUsdGsVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCotiUsdRsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCotiUsdPaVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jpBotones1Layout.setVerticalGroup(
            jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFlagEeuu, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFlagGuaranies, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldolares3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdGsCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdGsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblEeuu1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFlagBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbldolares4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdRsCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCotiUsdRsVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFlagArgentina, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEeuu2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpBotones1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbldolares7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCotiUsdPaCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCotiUsdPaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(lblFechaCotizacion))
        );

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoCompra.png"))); // NOI18N
        jButton4.setText("COMPRAS");
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        dpEscritorio.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jButton2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jpBotones1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dpEscritorio.setLayer(jButton4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dpEscritorioLayout = new javax.swing.GroupLayout(dpEscritorio);
        dpEscritorio.setLayout(dpEscritorioLayout);
        dpEscritorioLayout.setHorizontalGroup(
            dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dpEscritorioLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        dpEscritorioLayout.setVerticalGroup(
            dpEscritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dpEscritorioLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addContainerGap(91, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dpEscritorioLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jpBotones1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jMenu8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoCompra.png"))); // NOI18N
        jMenu8.setText("COMPRAS");
        jMenu8.setFont(new java.awt.Font("Sitka Text", 1, 14)); // NOI18N
        jMenu8.setPreferredSize(new java.awt.Dimension(270, 70));
        jMenu8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu8ActionPerformed(evt);
            }
        });

        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrarCompra.png"))); // NOI18N
        jMenuItem11.setText("Registrar compra");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem11);

        jMenuBar1.add(jMenu8);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoEmpleados.png"))); // NOI18N
        jMenu2.setText("VENTAS");
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

        jMenuItem6.setText("Cambio de moneda");
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

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        ABMProducto abmproducto = new ABMProducto(this, false);
        abmproducto.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

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

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        /*ABMParcela abmparcela = new ABMParcela(this, true);
        abmparcela.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        ABMCliente abmcliente = new ABMCliente(this, false);
        abmcliente.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        ABMEmpleado abmempleado = new ABMEmpleado(this, false);
        abmempleado.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jMenu8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu8ActionPerformed

    }//GEN-LAST:event_jMenu8ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        RegistrarCompra registrarcompra = new RegistrarCompra(this, false);
        registrarcompra.setVisible(true);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed

    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        /*  ABMDosis abmdosis = new ABMDosis(this, false);
        abmdosis.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        /*ABMTipoAgroquimico abmtipoagroquimico = new ABMTipoAgroquimico(null, null, false);
        abmtipoagroquimico.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        /* ABMClaseProducto abmpaisorigen = new ABMClaseProducto(null, this, false);
        abmpaisorigen.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        /*ABMIngredienteActivo abmingredienteactivo = new ABMIngredienteActivo(null, this, false);
        abmingredienteactivo.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        /*ABMFormulacion abmformulacion = new ABMFormulacion(null, this, false);
        abmformulacion.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        /*ABMEmpresaRegistrante abmempresaregistrante = new ABMEmpresaRegistrante(null, this, false);
        abmempresaregistrante.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem8ActionPerformed

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
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator10;
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
    private javax.swing.JPanel jpBotones1;
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
