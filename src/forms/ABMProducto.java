/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import codigobarras.GenerarCodigoBarras;
import conexion.Conexion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import metodos.Metodos;
import metodos.MetodosCombo;
import metodos.MetodosImagen;
import metodos.MetodosTXT;
import metodos.VistaCompletaImagen;

/**
 *
 * @author Arnaldo Cantero
 */
public final class ABMProducto extends javax.swing.JDialog {

    MetodosTXT metodostxt = new MetodosTXT();
    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosCombo metodoscombo = new MetodosCombo();
    MetodosImagen metodosimagen = new MetodosImagen();
    String rutaFotoProducto = "C:\\VentaRopas\\fotoproductos\\imageproducto_";
    String TitlePorDefault = "PRODUCTO SIN FOTO";
    String nombreTablaBD = "Producto";

    public ABMProducto(java.awt.Frame parent, Boolean modal) {

        super(parent, modal);
        initComponents();

        TablaConsultaBDAll(); //Trae todos los registros
        cbCampoBuscar.setSelectedIndex(1);
        txtBuscar.requestFocus();

        CargarComboBoxes();

        OrdenTabulador();
    }

    private long GenerarNumAlAzar() {
        Random r = new Random();
        long numMin = 000001L;
        long numMax = 999999L;
        long resultado = numMin + ((long) (r.nextDouble() * (numMax - numMin)));
        return resultado;
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbMarca, "SELECT mar_codigo, mar_descripcion FROM marca ORDER BY mar_descripcion", 1);
        metodoscombo.setSelectedNombreItem(cbMarca, "SIN ESPECIFICAR");

        metodoscombo.CargarComboBox(cbCategoria, "SELECT cat_codigo, cat_descripcion FROM categoria ORDER BY cat_descripcion", 1);
        metodoscombo.setSelectedNombreItem(cbCategoria, "SIN ESPECIFICAR");

        if (metodoscombo.ObtenerIDSelectComboBox(cbCategoria) > 0) {
            metodoscombo.CargarComboBox(cbSubcategoria, "SELECT subcat_codigo, subcat_descripcion FROM subcategoria "
                    + "WHERE subcat_categoria = " + metodoscombo.ObtenerIDSelectComboBox(cbCategoria) + " ORDER BY subcat_descripcion", 1);
        }

        ModoEdicion(false);
    }

    public void RegistroNuevo() {
        try {
            if (ComprobarCampos() == true) {
                String codigoproducto = txtCodigoProducto.getText();
                String descripcion = txtDescripcion.getText();
                int marca = metodoscombo.ObtenerIDSelectComboBox(cbMarca);
                String tamano = cbTamano.getSelectedItem().toString();
                String existencia = txtExistencia.getText();
                int idsubcategoria = metodoscombo.ObtenerIDSelectComboBox(cbSubcategoria);
                String obs = taObs.getText();
                int estado = cbEstado.getSelectedIndex();

                int confirmado = JOptionPane.showConfirmDialog(this, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    //REGISTRAR NUEVO
                    String sentencia = "CALL SP_ProductoAlta ('" + codigoproducto + "','" + descripcion + "','"
                            + marca + "','" + existencia + "','" + tamano + "','" + idsubcategoria + "','"
                            + obs + "','" + estado + "')";
                    con.EjecutarABM(sentencia);
                    TablaConsultaBDAll(); //Actualiza la tabla
                    //Guardarimagen
                    String ultimoid = metodosimagen.ObtenerUltimoID();
                    metodosimagen.GuardarImagen(rutaFotoProducto + ultimoid);
                    JOptionPane.showMessageDialog(this, "El registro se agregó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    Limpiar();
                    ModoEdicion(false);
                }
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
            txtDescripcion.requestFocus();
        }
    }

    public void RegistroModificar() {
        if (ComprobarCampos() == true) {
            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este registro?", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                //guarda los datos que se han modificado en los campos
                String codigo = txtCodigo.getText();
                String codigoproducto = txtCodigoProducto.getText();
                String descripcion = txtDescripcion.getText();
                int marca = metodoscombo.ObtenerIDSelectComboBox(cbMarca);
                String existencia = txtExistencia.getText();
                String tamano = cbTamano.getSelectedItem().toString();
                int subcategoria = metodoscombo.ObtenerIDSelectComboBox(cbSubcategoria);
                String obs = taObs.getText();
                int estado = cbEstado.getSelectedIndex();

                String sentencia = "CALL SP_ProductoModificar(" + codigo + ",'" + codigoproducto + "','" + descripcion + "','"
                        + marca + "','" + existencia + "','" + tamano + "','" + subcategoria + "','"
                        + obs + "','" + estado + "')";

                con.EjecutarABM(sentencia);
                TablaConsultaBDAll(); //Actualiza la tabla

                //Guardarimagen
                if (lblImagen.getIcon() == null) {
                    metodosimagen.EliminarImagen(rutaFotoProducto + codigo);
                } else {
                    metodosimagen.GuardarImagen(rutaFotoProducto + codigo);
                }
                JOptionPane.showMessageDialog(this, "Registro modificado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                Limpiar();
                ModoEdicion(false);
            }
        }
    }

    private void RegistroEliminar() {
        int filasel = tbPrincipal.getSelectedRow();
        if (filasel == -1) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        } else {
            int confirmado = javax.swing.JOptionPane.showConfirmDialog(this, "¿Realmente desea eliminar este registro?", "Confirmación", JOptionPane.YES_OPTION);
            if (confirmado == JOptionPane.YES_OPTION) {
                String codigo = (String) tbPrincipal.getModel().getValueAt(filasel, 0);
                String sentencia = "CALL SP_ProductoEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia);
                metodosimagen.EliminarImagen(rutaFotoProducto + txtCodigo.getText()); //Eliminar la foto
                Limpiar();
                ModoEdicion(false);
                JOptionPane.showMessageDialog(this, "Registro eliminado correctamente", "Información", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void TablaConsultaBDAll() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "CALL SP_" + nombreTablaBD + "Consulta";
        String titlesJtabla[] = {"Código", "Código del producto", "Descripción",
            "Marca", "Stock", "Tamaño", "Categoria", "Subcategoria", "Observación", "Estado"};

        tbPrincipal.setModel(con.ConsultAllBD(sentencia, titlesJtabla, cbCampoBuscar));
        metodos.AnchuraColumna(tbPrincipal);
        lbCantRegistros.setText(metodos.CantRegistros + " Registros encontrados");
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString());
        txtCodigoProducto.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1).toString());
        txtDescripcion.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2).toString());
        metodoscombo.setSelectedNombreItem(cbMarca, tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3).toString());
        txtExistencia.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4).toString());
        cbTamano.setSelectedItem(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5).toString());
        metodoscombo.setSelectedNombreItem(cbCategoria, tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 6).toString());
        metodoscombo.setSelectedNombreItem(cbSubcategoria, tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 7).toString());
        taObs.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 8).toString());
        cbEstado.setSelectedItem(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 9).toString());

        metodosimagen.LeerImagenExterna(lblImagen, rutaFotoProducto + txtCodigo.getText(), TitlePorDefault);
    }

    private void ModoEdicion(boolean valor) {
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        txtCodigoProducto.setEnabled(valor);
        btnGenerarNumAzar.setEnabled(valor);
        txtDescripcion.setEnabled(valor);
        cbMarca.setEnabled(valor);
        cbTamano.setEnabled(valor);
        cbCategoria.setEnabled(valor);
        cbSubcategoria.setEnabled(valor);
        taObs.setEnabled(valor);
        cbEstado.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);
        btnCargarImagen.setEnabled(valor);
        btnEliminarImagen.setEnabled(lblImagen.getIcon() != null);
        btnPantallaCompleta.setEnabled(!valor);
        btnGenerarCodigo.setEnabled(!valor);

        txtCodigoProducto.requestFocus();
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtCodigoProducto.setText("");
        txtDescripcion.setText("");
        metodoscombo.setSelectedNombreItem(cbMarca, "SIN ESPECIFICAR");
        cbTamano.setSelectedIndex(0);
        txtExistencia.setText("0");
        metodoscombo.setSelectedNombreItem(cbCategoria, "SIN ESPECIFICAR");
        metodoscombo.setSelectedNombreItem(cbSubcategoria, "SIN ESPECIFICAR");
        taObs.setText("");
        cbEstado.setSelectedIndex(1);

        lblCodigoProducto.setForeground(new Color(102, 102, 102));
        lblDescripcion.setForeground(new Color(102, 102, 102));
        lblTamano.setForeground(new Color(102, 102, 102));

        lblImagen.setIcon(null);
        lblImagen.setText(TitlePorDefault);

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();
    }

    public boolean ComprobarCampos() {
        if (metodostxt.ValidarCampoVacioTXT(txtCodigoProducto, lblCodigoProducto) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtDescripcion, lblDescripcion) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtExistencia, lblExistencia) == false) {
            return false;
        }

        if (txtCodigo.getText().equals("")) {
            try {
                Conexion con = new Conexion();
                con = con.ObtenerRSSentencia("SELECT pro_identificador FROM producto "
                        + "WHERE pro_identificador = '" + txtCodigoProducto.getText() + "'");
                if (con.rs.next() == true) { //Si ya existe el numero de cedula en la bd de clientes
                    JOptionPane.showMessageDialog(this, "Este código de producto ya se encuentra registrado", "Error", JOptionPane.ERROR_MESSAGE);
                    txtCodigoProducto.setForeground(Color.RED);
                    txtCodigoProducto.requestFocus();
                    Toolkit.getDefaultToolkit().beep();
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error al comprobar si codigo de producto ya existe en la bd: " + e);
            } catch (NullPointerException e) {
                System.out.println("El codigo de producto ingresado no existe en la bd, aprobado: " + e);
            }
        }
        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpTabla = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lblBuscarCampo = new javax.swing.JLabel();
        cbCampoBuscar = new javax.swing.JComboBox();
        lbCantRegistros = new javax.swing.JLabel();
        btnGenerarCodigo = new org.edisoncor.gui.button.ButtonSeven();
        jpBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jtpEdicion = new javax.swing.JTabbedPane();
        jpEdicion = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblCodigoProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblTamano = new javax.swing.JLabel();
        lblCategoria = new javax.swing.JLabel();
        lblSubcategoria = new javax.swing.JLabel();
        lblObs = new javax.swing.JLabel();
        scpObs = new javax.swing.JScrollPane();
        taObs = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        cbCategoria = new javax.swing.JComboBox<>();
        cbSubcategoria = new javax.swing.JComboBox<>();
        btnCargarImagen = new javax.swing.JButton();
        btnEliminarImagen = new javax.swing.JButton();
        btnPantallaCompleta = new javax.swing.JButton();
        lblSubcategoria1 = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        lblExistencia = new javax.swing.JLabel();
        txtExistencia = new javax.swing.JTextField();
        cbTamano = new javax.swing.JComboBox<>();
        cbMarca = new javax.swing.JComboBox<>();
        lblMarca = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();
        btnGenerarNumAzar = new org.edisoncor.gui.button.ButtonSeven();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setTitle("Ventana Productos");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(255, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");
        jLabel10.setIconTextGap(1);

        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(0, 153, 153));
        txtBuscar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuscar.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscar.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        tbPrincipal.setAutoCreateRowSorter(true);
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
        scPrincipal.setViewportView(tbPrincipal);

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Buscar por:");

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        btnGenerarCodigo.setBackground(new java.awt.Color(14, 154, 153));
        btnGenerarCodigo.setText("Generar codigo de barras");
        btnGenerarCodigo.setColorBrillo(new java.awt.Color(255, 255, 255));
        btnGenerarCodigo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnGenerarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarCodigoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpTablaLayout = new javax.swing.GroupLayout(jpTabla);
        jpTabla.setLayout(jpTablaLayout);
        jpTablaLayout.setHorizontalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 819, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpTablaLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnGenerarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85))))
        );
        jpTablaLayout.setVerticalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jpBotones.setBackground(new java.awt.Color(255, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnNuevo.setBackground(new java.awt.Color(14, 154, 153));
        btnNuevo.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoNuevo.png"))); // NOI18N
        btnNuevo.setText("NUEVO");
        btnNuevo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnModificar.setBackground(new java.awt.Color(14, 154, 153));
        btnModificar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoModifcar.png"))); // NOI18N
        btnModificar.setText("MODIFICAR");
        btnModificar.setEnabled(false);
        btnModificar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(14, 154, 153));
        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("ELIMINAR");
        btnEliminar.setEnabled(false);
        btnEliminar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotonesLayout = new javax.swing.GroupLayout(jpBotones);
        jpBotones.setLayout(jpBotonesLayout);
        jpBotonesLayout.setHorizontalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpEdicion.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jtpEdicion.setName(""); // NOI18N

        jpEdicion.setBackground(new java.awt.Color(255, 255, 255));
        jpEdicion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpEdicion.setForeground(new java.awt.Color(214, 217, 223));

        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setForeground(new java.awt.Color(102, 102, 102));
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código:");

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblCodigoProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigoProducto.setForeground(new java.awt.Color(102, 102, 102));
        lblCodigoProducto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigoProducto.setText("Código del producto*:");
        lblCodigoProducto.setToolTipText("");

        txtCodigoProducto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigoProducto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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

        lblDescripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDescripcion.setForeground(new java.awt.Color(102, 102, 102));
        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción*:");

        txtDescripcion.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        txtDescripcion.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDescripcion.setEnabled(false);
        txtDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyTyped(evt);
            }
        });

        lblTamano.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTamano.setForeground(new java.awt.Color(102, 102, 102));
        lblTamano.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTamano.setText("Tamaño:");

        lblCategoria.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCategoria.setForeground(new java.awt.Color(102, 102, 102));
        lblCategoria.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCategoria.setText("Categoria:");

        lblSubcategoria.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblSubcategoria.setForeground(new java.awt.Color(102, 102, 102));
        lblSubcategoria.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubcategoria.setText("Subcategoría:");

        lblObs.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblObs.setForeground(new java.awt.Color(102, 102, 102));
        lblObs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObs.setText("Obs:");

        taObs.setColumns(20);
        taObs.setRows(5);
        taObs.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        taObs.setEnabled(false);
        scpObs.setViewportView(taObs);

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        cbCategoria.setEnabled(false);
        cbCategoria.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbCategoriaItemStateChanged(evt);
            }
        });
        cbCategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cbCategoriaKeyReleased(evt);
            }
        });

        cbSubcategoria.setEnabled(false);
        cbSubcategoria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cbSubcategoriaKeyReleased(evt);
            }
        });

        btnCargarImagen.setBackground(new java.awt.Color(0, 153, 153));
        btnCargarImagen.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnCargarImagen.setText("+");
        btnCargarImagen.setToolTipText("Cargar una imagen del producto");
        btnCargarImagen.setEnabled(false);
        btnCargarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarImagenActionPerformed(evt);
            }
        });

        btnEliminarImagen.setBackground(new java.awt.Color(255, 0, 51));
        btnEliminarImagen.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminarImagen.setText("-");
        btnEliminarImagen.setToolTipText("Eliminar imagen del producto");
        btnEliminarImagen.setEnabled(false);
        btnEliminarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarImagenActionPerformed(evt);
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

        lblSubcategoria1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblSubcategoria1.setForeground(new java.awt.Color(102, 102, 102));
        lblSubcategoria1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSubcategoria1.setText("Estado:");

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INACTIVO", "ACTIVO" }));
        cbEstado.setSelectedIndex(1);
        cbEstado.setEnabled(false);

        lblExistencia.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblExistencia.setForeground(new java.awt.Color(102, 102, 102));
        lblExistencia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExistencia.setText("Existencia/Stock:");

        txtExistencia.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtExistencia.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtExistencia.setText("0");
        txtExistencia.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtExistencia.setEnabled(false);
        txtExistencia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtExistenciaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtExistenciaKeyTyped(evt);
            }
        });

        cbTamano.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SIN ESPECIFICAR", "P", "M", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));
        cbTamano.setEnabled(false);

        cbMarca.setEnabled(false);

        lblMarca.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(102, 102, 102));
        lblMarca.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMarca.setText("Marca:");

        lblImagen.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        lblImagen.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagen.setText("PRODUCTO SIN FOTO");
        lblImagen.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnGenerarNumAzar.setBackground(new java.awt.Color(14, 154, 153));
        btnGenerarNumAzar.setText("Generar");
        btnGenerarNumAzar.setColorBrillo(new java.awt.Color(255, 255, 255));
        btnGenerarNumAzar.setEnabled(false);
        btnGenerarNumAzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarNumAzarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTamano, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMarca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDescripcion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigoProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                    .addComponent(lblExistencia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEdicionLayout.createSequentialGroup()
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbTamano, javax.swing.GroupLayout.Alignment.LEADING, 0, 260, Short.MAX_VALUE)
                    .addComponent(cbMarca, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEdicionLayout.createSequentialGroup()
                        .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGenerarNumAzar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtExistencia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCategoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubcategoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblObs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubcategoria1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSubcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scpObs, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEliminarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCargarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSubcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbSubcategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGenerarNumAzar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addComponent(scpObs, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObs, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblTamano, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbTamano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblSubcategoria1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtExistencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnCargarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPantallaCompleta, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jtpEdicion.addTab("Edición", jpEdicion);

        jpBotones2.setBackground(new java.awt.Color(255, 255, 255));
        jpBotones2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setToolTipText("Inserta el nuevo registro");
        btnGuardar.setEnabled(false);
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
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotones2Layout = new javax.swing.GroupLayout(jpBotones2);
        jpBotones2.setLayout(jpBotones2Layout);
        jpBotones2Layout.setHorizontalGroup(
            jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotones2Layout.setVerticalGroup(
            jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotones2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                                .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(345, 345, 345)
                        .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("ABMProducto");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

//--------------------------Eventos de componentes----------------------------//
    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        metodos.FiltroJTable(txtBuscar.getText(), cbCampoBuscar.getSelectedIndex(), tbPrincipal);

        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (txtCodigo.getText().equals("")) {//Si es nuevo
            RegistroNuevo();
        } else { //Si es modificar
            RegistroModificar();
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Limpiar();
        ModoEdicion(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        Limpiar();
        ModoEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        ModoEdicion(true);
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        RegistroEliminar();
        Limpiar();
        ModoEdicion(false);
        TablaConsultaBDAll();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnGuardar.doClick();
        }
    }
//GEN-LAST:event_btnGuardarKeyPressed

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        if (tbPrincipal.isEnabled() == true) {
            btnModificar.setEnabled(true);
            btnEliminar.setEnabled(true);

            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void txtCodigoProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtCodigoProducto, 30);
    }//GEN-LAST:event_txtCodigoProductoKeyTyped

    private void txtCodigoProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoProductoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCodigoProducto, lblCodigoProducto);
    }//GEN-LAST:event_txtCodigoProductoKeyReleased

    private void txtDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyTyped
        metodostxt.FiltroCaracteresProhibidos(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtDescripcion, 150);
    }//GEN-LAST:event_txtDescripcionKeyTyped

    private void txtDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtDescripcion, lblDescripcion);
    }//GEN-LAST:event_txtDescripcionKeyReleased

    private void cbCategoriaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbCategoriaItemStateChanged
        if (metodoscombo.ObtenerIDSelectComboBox(cbCategoria) > 0) {
            metodoscombo.CargarComboBox(cbSubcategoria, "SELECT subcat_codigo, subcat_descripcion FROM subcategoria "
                    + "WHERE subcat_categoria = " + metodoscombo.ObtenerIDSelectComboBox(cbCategoria) + " ORDER BY subcat_descripcion", 1);

            if (cbSubcategoria.getItemCount() > 0 && cbSubcategoria.isEnabled()) {
                cbSubcategoria.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_cbCategoriaItemStateChanged

    private void btnCargarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarImagenActionPerformed
        metodosimagen.CargarImagenDesdeFC(lblImagen);
        btnEliminarImagen.setEnabled(!(lblImagen.getIcon().toString().equals("javax.swing.ImageIcon@4356c900"))); //Revisa si el icono es default 
    }//GEN-LAST:event_btnCargarImagenActionPerformed

    private void btnEliminarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarImagenActionPerformed
        lblImagen.setIcon(null);
        lblImagen.setText(TitlePorDefault);
        btnEliminarImagen.setEnabled(false);
    }//GEN-LAST:event_btnEliminarImagenActionPerformed

    private void btnPantallaCompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPantallaCompletaActionPerformed
        VistaCompletaImagen vistacompleta = new VistaCompletaImagen(rutaFotoProducto + txtCodigo.getText());
        //metodos.CentrarVentanaJDialog(vistacompleta);
        vistacompleta.setVisible(true);
    }//GEN-LAST:event_btnPantallaCompletaActionPerformed

    private void cbCategoriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbCategoriaKeyReleased
        if (cbCategoria.getSelectedItem().equals("")) {
            lblCategoria.setForeground(new Color(102, 102, 102));
        } else {
            lblCategoria.setText("Categoria:"); //Vuelve a poner el texto original ya que cuando hay un error cambia
            lblCategoria.setForeground(new Color(0, 153, 51));
        }
    }//GEN-LAST:event_cbCategoriaKeyReleased

    private void cbSubcategoriaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbSubcategoriaKeyReleased
        if (cbSubcategoria.getSelectedItem().equals("")) {
            lblSubcategoria.setForeground(new Color(102, 102, 102));
        } else {
            lblSubcategoria.setText("Subcategoria*:"); //Vuelve a poner el texto original ya que cuando hay un error cambia
            lblSubcategoria.setForeground(new Color(0, 153, 51));
        }
    }//GEN-LAST:event_cbSubcategoriaKeyReleased

    private void txtExistenciaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtExistenciaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtExistenciaKeyReleased

    private void txtExistenciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtExistenciaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtExistenciaKeyTyped

    private void btnGenerarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarCodigoActionPerformed
        if (txtCodigoProducto.getText().equals("") == false) {
            GenerarCodigoBarras generarCodigoBarras = new GenerarCodigoBarras(null, true, txtCodigoProducto.getText(), txtDescripcion.getText());
            generarCodigoBarras.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerarCodigoActionPerformed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void btnGenerarNumAzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarNumAzarActionPerformed
        txtCodigoProducto.setText(GenerarNumAlAzar() + "");
        metodostxt.TxtColorLabelKeyReleased(txtCodigoProducto, lblCodigoProducto);
    }//GEN-LAST:event_btnGenerarNumAzarActionPerformed

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtCodigoProducto);
        ordenTabulador.add(txtDescripcion);
        ordenTabulador.add(txtExistencia);
        ordenTabulador.add(cbCategoria);
        ordenTabulador.add(cbSubcategoria);
        ordenTabulador.add(taObs);
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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCargarImagen;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarImagen;
    private org.edisoncor.gui.button.ButtonSeven btnGenerarCodigo;
    private org.edisoncor.gui.button.ButtonSeven btnGenerarNumAzar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnPantallaCompleta;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox<MetodosCombo> cbCategoria;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<MetodosCombo> cbMarca;
    private javax.swing.JComboBox<MetodosCombo> cbSubcategoria;
    private javax.swing.JComboBox<String> cbTamano;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones2;
    private javax.swing.JPanel jpEdicion;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JTabbedPane jtpEdicion;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblCategoria;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblCodigoProducto;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblExistencia;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblObs;
    private javax.swing.JLabel lblSubcategoria;
    private javax.swing.JLabel lblSubcategoria1;
    private javax.swing.JLabel lblTamano;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JScrollPane scpObs;
    private javax.swing.JTextArea taObs;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtExistencia;
    // End of variables declaration//GEN-END:variables
}
