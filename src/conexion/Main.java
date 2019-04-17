/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexion;

import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 *
 * @author Ing.Ivan
 */
public class Main {

    public static void main(String[] args) {
        Connection miConexion;
        miConexion = Conexion.GetConnection();

        if (miConexion != null) {
            JOptionPane.showMessageDialog(null, "Conexión Realizada Correctamente");
        }
    }
}
