/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import conexion.Conexion;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import principal.Principal;

/**
 *
 * @author Arnaldo_Cantero
 */
public class SplashScreen extends javax.swing.JFrame implements Runnable {

    private Thread tiempo = null;
    Conexion con = new Conexion();

    public SplashScreen(java.awt.Frame parent, boolean modal) {
        initComponents();

        tiempo = new Thread(this);
        tiempo.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnPrincipal = new org.edisoncor.gui.panel.Panel();
        panelCurves1 = new org.edisoncor.gui.panel.PanelCurves();
        panelImage1 = new org.edisoncor.gui.panel.PanelImage();
        lmCargando = new org.edisoncor.gui.label.LabelMetric();
        rSProgressMaterial1 = new rojerusan.componentes.RSProgressMaterial();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cargando sistema...");
        setUndecorated(true);
        setResizable(false);

        pnPrincipal.setColorPrimario(new java.awt.Color(1, 1, 11));
        pnPrincipal.setColorSecundario(new java.awt.Color(1, 1, 11));

        panelImage1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/LogoElegancia.png"))); // NOI18N

        javax.swing.GroupLayout panelImage1Layout = new javax.swing.GroupLayout(panelImage1);
        panelImage1.setLayout(panelImage1Layout);
        panelImage1Layout.setHorizontalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        panelImage1Layout.setVerticalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 212, Short.MAX_VALUE)
        );

        lmCargando.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lmCargando.setText("Cargando sistema...");
        lmCargando.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N

        rSProgressMaterial1.setForeground(new java.awt.Color(0, 153, 153));
        rSProgressMaterial1.setAnchoProgress(7);

        javax.swing.GroupLayout panelCurves1Layout = new javax.swing.GroupLayout(panelCurves1);
        panelCurves1.setLayout(panelCurves1Layout);
        panelCurves1Layout.setHorizontalGroup(
            panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCurves1Layout.createSequentialGroup()
                .addGroup(panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lmCargando, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelCurves1Layout.createSequentialGroup()
                        .addGroup(panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCurves1Layout.createSequentialGroup()
                                .addGap(118, 118, 118)
                                .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelCurves1Layout.createSequentialGroup()
                                .addGap(221, 221, 221)
                                .addComponent(rSProgressMaterial1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 124, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelCurves1Layout.setVerticalGroup(
            panelCurves1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCurves1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(lmCargando, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rSProgressMaterial1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        javax.swing.GroupLayout pnPrincipalLayout = new javax.swing.GroupLayout(pnPrincipal);
        pnPrincipal.setLayout(pnPrincipalLayout);
        pnPrincipalLayout.setHorizontalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCurves1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnPrincipalLayout.setVerticalGroup(
            pnPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelCurves1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SplashScreen dialog = new SplashScreen(new javax.swing.JFrame(), true);

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.label.LabelMetric lmCargando;
    private org.edisoncor.gui.panel.PanelCurves panelCurves1;
    private org.edisoncor.gui.panel.PanelImage panelImage1;
    private org.edisoncor.gui.panel.Panel pnPrincipal;
    private rojerusan.componentes.RSProgressMaterial rSProgressMaterial1;
    // End of variables declaration//GEN-END:variables

    public void run() {
        try {
            //while (tiempo != null) {
            Thread.sleep(2000);
            lmCargando.setText("Obteniendo cotizaciones del día...");
            ObtenerCotizacionScrapingWeb();
            this.dispose();

            Principal principal = new Principal();
            principal.setVisible(true);
            //}
        } catch (InterruptedException ex) {
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ObtenerCotizacionScrapingWeb() {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup
                    .connect("http://www.cambioschaco.com.py/")
                    .validateTLSCertificates(false).get();

            //Obtiene el titulo de la pagina
            String title = doc.title();
            System.out.println("\nScraping Web (CAMBIOS CHACO)");
            System.out.println("Titulo de la pagina:  " + title + "\n");

            Elements losDiv;
            Elements losTr;
            Element fila1;
            Element fila2;
            String sentencia;

            losDiv = doc.select("div." + "col-sm-7"); //Las tablas, div.
            losTr = losDiv.select("tr"); //Las filas, tr.
            fila1 = losTr.get(1); //el get(0) seria los titulos

            String usdGsCompraString = fila1.getElementsByClass("purchase").text();
            double usdGsCompraDouble = Double.parseDouble(((usdGsCompraString).replace(".", "")).replace(",", "."));

            String usdGsVentaString = fila1.getElementsByClass("sale").text();
            double usdGsVentaDouble = Double.parseDouble(((usdGsVentaString).replace(".", "")).replace(",", "."));
            sentencia = "CALL SP_CotizacionModificar('1','Dolares','Guaranies','" + usdGsCompraDouble + "','"
                    + usdGsVentaDouble + "','" + FechaActual() + "')";
            con.EjecutarABM(sentencia, false);

            losDiv = doc.select("div." + "col-sm-5"); //Las tablas, div.
            losTr = losDiv.select("tr"); //Las filas, tr.
            fila1 = losTr.get(1); //el get(0) seria los titulos
            fila2 = losTr.get(2); //el get(0) seria los titulos

            String usdRsCompraString = fila1.getElementsByClass("purchase").text();
            double usdRsCompraDouble = Double.parseDouble(((usdRsCompraString).replace(".", "")).replace(",", "."));

            String usdRsVentaString = fila1.getElementsByClass("sale").text();
            double usdRsVentaDouble = Double.parseDouble(((usdRsVentaString).replace(".", "")).replace(",", "."));
            sentencia = "CALL SP_CotizacionModificar('2','Dolares','Reales','" + usdRsCompraDouble + "','"
                    + usdRsVentaDouble + "','" + FechaActual() + "')";
            con.EjecutarABM(sentencia, false);

            String usdPaCompraString = fila2.getElementsByClass("purchase").text();
            double usdPaCompraDouble = Double.parseDouble(((usdPaCompraString).replace(".", "")).replace(",", "."));

            String usdPaVentaString = fila2.getElementsByClass("sale").text();
            double usdPaVentaDouble = Double.parseDouble(((usdPaVentaString).replace(".", "")).replace(",", "."));
            sentencia = "CALL SP_CotizacionModificar('3','Dolares','Pesos argentinos','" + usdPaCompraDouble + "','"
                    + usdPaVentaDouble + "','" + FechaActual() + "')";
            con.EjecutarABM(sentencia, false);
        } catch (IOException e) {
            System.out.println("Error al realizar el scraping web " + e);
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private String FechaActual() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date fechaactual = new Date();

        return dateFormat.format(fechaactual); // ingresa hora a la bd
    }
}
