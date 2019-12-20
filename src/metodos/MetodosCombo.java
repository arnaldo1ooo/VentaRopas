/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.text.StyledEditorKit;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class MetodosCombo {

    private int codigo;
    private String descripcion;

    public MetodosCombo() { //No borrar
    }

    public MetodosCombo(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int id) {
        this.codigo = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return this.descripcion;
    }

    public void setSelectedNombreItem(JComboBox ElCombo, String nombreitem) {
        MetodosCombo item;
        for (int i = 0; i < ElCombo.getItemCount(); i++) {
            item = (MetodosCombo) ElCombo.getItemAt(i);
            if (item.getDescripcion().equalsIgnoreCase(nombreitem)) {
                ElCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public void CargarComboBox(JComboBox ElCombo, String sentencia) {
        ElCombo.removeAllItems(); //Vaciamos el combo

        ElCombo.setRenderer(new DefaultListCellRenderer() {//Cambiar color de texto del combo cuando esta disabled
            @Override
            public void paint(Graphics g) {
                setForeground(Color.BLACK);
                super.paint(g);
            }
        });

        try {
            AutoCompleteDecorator.decorate(ElCombo);
            System.out.println("Cargar combo (" + ElCombo.getName() + "): " + sentencia);
            Connection con;
            con = (Connection) Conexion.ConectarBasedeDatos();
            Statement st;
            st = con.createStatement();
            ResultSet rs;
            rs = st.executeQuery(sentencia);
            while (rs.next()) {
                ElCombo.addItem(new MetodosCombo(
                        rs.getInt(1),
                        rs.getString(2)));
            }
            if (ElCombo.getItemCount() > 0) {
                ElCombo.setSelectedIndex(-1);
            }

            ElCombo.setMaximumRowCount(ElCombo.getModel().getSize()); //Hace que se despliegue en toda la pantalla vertical el combo
            rs.close();
            st.close();
            con.close();

            AnadirScrollHorizontal(ElCombo);
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Error al cargar combo: " + sentencia + ",   ERROR: " + e
            );
        }
    }

    public int ObtenerIdComboBox(JComboBox<MetodosCombo> ElCombo) {
        int codigoitemselect = -1;
        try {
            codigoitemselect = ElCombo.getItemAt(ElCombo.getSelectedIndex()).getCodigo();
        } catch (Exception e) {
            System.out.println("ObtenerIdCombo: No se selecciono ningun item en el combo: " + e);
        }
        return codigoitemselect;
    }

    private void AnadirScrollHorizontal(JComboBox ElCombo) {
        if (ElCombo.getItemCount() == 0) {
            return;
        }
        Object comp = ElCombo.getUI().getAccessibleChild(ElCombo, 0);
        if (!(comp instanceof JPopupMenu)) {
            return;
        }
        JPopupMenu popup = (JPopupMenu) comp;
        JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
        scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
