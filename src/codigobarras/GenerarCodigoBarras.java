/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigobarras;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.Cell;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import metodos.Metodos;
import metodos.MetodosTXT;

/**
 *
 * @author Arnaldo_Cantero
 */
public class GenerarCodigoBarras extends javax.swing.JDialog {

    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();

    public GenerarCodigoBarras(java.awt.Frame parent, boolean modal, String elCodigo, String descripcion) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        txtCodigo.setText(elCodigo);
        txtDescripcion.setText(descripcion);
    }

    private void GenerarCodigoDeBarras() {
        try {
            String codigo = txtCodigo.getText();
            String descripcion = txtDescripcion.getText();
            int cantidad = Integer.parseInt(txtCantidad.getText());
            String nombrepdf = "CodigosBarras";
            int escala = 100;
            BaseColor colorBorde = new BaseColor(224, 224, 224); //Gris claro

            //Configuracion pagina
            float anchurapagina = Float.parseFloat("1");//1mm equivale a 2.83, 210mm
            float altopagina = Float.parseFloat("1");
            Rectangle tamPagina;
            int numColumnas = 1;
            PdfPTable tabla = null;
            Image laImagen = null;
            if (cbTamanoPagina.getSelectedItem().equals("Oficio")) {
                anchurapagina = Float.parseFloat((216 * 2.83) + "f");//1mm equivale a 2.83, 210mm
                altopagina = Float.parseFloat((330 * 2.83) + "f");
            }
            if (cbTamanoPagina.getSelectedItem().equals("A4")) {
                anchurapagina = Float.parseFloat((210 * 2.83) + "f");//1mm equivale a 2.83, 210mm
                altopagina = Float.parseFloat((297 * 2.83) + "f");
            }
            tamPagina = new RectangleReadOnly(anchurapagina, altopagina);
            Document doc = new Document(tamPagina);
            doc.setMargins(10, 10, 10, 10);
            String directorio = System.getProperty("user.home") + "/Desktop/" + nombrepdf + ".pdf";
            PdfWriter pdf;
            pdf = PdfWriter.getInstance(doc, new FileOutputStream(directorio));

            doc.open(); // Abrir el documento

            //Titulos
            Font fontBold = new Font(FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK); //Negrita
            Font fontRegular = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK); //Normal
            Font fonttitulo = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);

            Paragraph titulo = new Paragraph("Codigo de barras (" + cbTipoCodigo.getSelectedItem() + ")\n", fonttitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER); //Centrar titulo
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Descripción del producto: ", fontBold);
            subtitulo.add(new Chunk(descripcion, fontRegular));
            doc.add(subtitulo);

            Paragraph cantidadParrafo = new Paragraph("Cantidad: ", fontBold);
            cantidadParrafo.add(new Chunk(cantidad + "\n\n", fontRegular));
            doc.add(cantidadParrafo);

            if (cbTipoCodigo.getSelectedItem().equals("CODE 39")) {
                numColumnas = 3;
                tabla = new PdfPTable(numColumnas);
                Barcode39 barcode39 = new Barcode39();
                barcode39.setCode(codigo);
                laImagen = barcode39.createImageWithBarcode(pdf.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
                laImagen.scalePercent(escala);
            }

            if (cbTipoCodigo.getSelectedItem().equals("CODE 128")) {
                numColumnas = 4;
                tabla = new PdfPTable(numColumnas);
                Barcode128 barcode128 = new Barcode128();
                barcode128.setCode(codigo);
                laImagen = barcode128.createImageWithBarcode(pdf.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
                laImagen.scalePercent(escala);
            }
            PdfPCell celda = new PdfPCell(laImagen);
            celda.setPaddingLeft(5f); //Espaciado izquierda celda
            celda.setPaddingRight(5f); //Espaciado derecha celda
            celda.setPaddingTop(5f); //Espaciado arriba celda
            celda.setPaddingBottom(5f); //Espaciado abajo celda
            celda.setBorderWidth(0.2f); //Grosor del borde de la celda
            celda.setBorderColor(colorBorde); //Color de la celda
            celda.setHorizontalAlignment(Cell.ALIGN_CENTER);
            celda.setVerticalAlignment(Cell.ALIGN_CENTER);

            int cantidadextra = 0;
            float resultado = (float) cantidad / numColumnas;
            System.out.println("cantidad " + cantidad + "  numColumnas " + numColumnas + "  numero " + resultado);
            if (resultado % 2 != 0) { //si columnas es 4, y cantidad es 10, no imprimira todo ya que no completa todas las filas
                System.out.println("resultado es impar");

                for (int i = cantidad; resultado % 2 != 0; i++) {
                    resultado = (float) i / numColumnas;
                    cantidadextra++;
                }
            }

            if (cantidad < numColumnas) { //Si la cantidad a imprimir no alcanza a llenar una fila entera
                for (int i = 0; i < cantidad; i++) {
                    tabla.addCell(celda);
                }
                PdfPCell celdavacia = new PdfPCell(new Paragraph(""));
                celdavacia.setBorderColor(colorBorde); //Color de la celda
                for (int i = cantidad; i < numColumnas; i++) {
                    tabla.addCell(celdavacia);
                }
                doc.add(tabla);
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", directorio}); //Abre el archivo pdf generado
                doc.close();
                return;
            }

            for (int i = 0; i < cantidad; i++) {
                tabla.addCell(celda); // addCell() agrega una celda a la tabla, el cambio de fila ocurre automaticamente al llenar la fila     
            }
            PdfPCell celdavacia = new PdfPCell(new Paragraph(""));
            celdavacia.setBorderColor(colorBorde); //Color de la celda
            for (int i = 0; i < cantidadextra; i++) {
                tabla.addCell(celdavacia);
            }
            doc.add(tabla);

            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", directorio}); //Abre el archivo pdf generado
            doc.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Cierre el pdf creado, y vuelva a intentarlo", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(GenerarCodigoBarras.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(GenerarCodigoBarras.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean ComprobarCampos() {
        if (metodostxt.ValidarCampoVacioTXT(txtCantidad, lblCantidad) == false) {
            return false;
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTask1 = new org.edisoncor.gui.label.LabelTask();
        panel1 = new org.edisoncor.gui.panel.Panel();
        buttonAction1 = new org.edisoncor.gui.button.ButtonAction();
        txtCantidad = new org.edisoncor.gui.textField.TextFieldRoundBackground();
        lblCantidad = new javax.swing.JLabel();
        cbTipoCodigo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCodigo = new org.edisoncor.gui.textField.TextFieldRoundBackground();
        cbTamanoPagina = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtDescripcion = new org.edisoncor.gui.textField.TextFieldRoundBackground();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generar código de barras");
        setResizable(false);

        panel1.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(194, 228, 255));

        buttonAction1.setText("Generar");
        buttonAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAction1ActionPerformed(evt);
            }
        });

        txtCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCantidad.setText("1");
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadKeyReleased(evt);
            }
        });

        lblCantidad.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblCantidad.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCantidad.setText("Cantidad");

        cbTipoCodigo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CODE 39", "CODE 128" }));
        cbTipoCodigo.setSelectedIndex(1);

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Formato de código");

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Código");

        txtCodigo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCodigo.setEnabled(false);
        txtCodigo.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        cbTamanoPagina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Oficio", "A4" }));

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Tamaño de pagina");

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Descripción");

        txtDescripcion.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDescripcion.setEnabled(false);
        txtDescripcion.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(cbTamanoPagina, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(33, 33, 33)
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbTipoCodigo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2)))
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(235, 235, 235)
                        .addComponent(buttonAction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)
                                .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(cbTipoCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(cbTamanoPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addComponent(buttonAction1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAction1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAction1ActionPerformed
        if (ComprobarCampos() == true) {
            GenerarCodigoDeBarras();
        }
    }//GEN-LAST:event_buttonAction1ActionPerformed

    private void txtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCantidad, lblCantidad);
    }//GEN-LAST:event_txtCantidadKeyReleased

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
            java.util.logging.Logger.getLogger(GenerarCodigoBarras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GenerarCodigoBarras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GenerarCodigoBarras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GenerarCodigoBarras.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GenerarCodigoBarras dialog = new GenerarCodigoBarras(new javax.swing.JFrame(), true, "", "");
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
    private org.edisoncor.gui.button.ButtonAction buttonAction1;
    private javax.swing.JComboBox<String> cbTamanoPagina;
    private javax.swing.JComboBox<String> cbTipoCodigo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private org.edisoncor.gui.label.LabelTask labelTask1;
    private javax.swing.JLabel lblCantidad;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.textField.TextFieldRoundBackground txtCantidad;
    private org.edisoncor.gui.textField.TextFieldRoundBackground txtCodigo;
    private org.edisoncor.gui.textField.TextFieldRoundBackground txtDescripcion;
    // End of variables declaration//GEN-END:variables
}
