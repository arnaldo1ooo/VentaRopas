package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion {

    public Connection conexion;
    public Statement st;
    public ResultSet rs;

    //Modo host local
    private static final String controlador = "com.mysql.jdbc.Driver";
    private static final String usuarioDB = "root";
    private static final String passDB = "toor5127"; //Contrasena de la BD
    private static final String nombreDB = "ventaropas";
    private static final String servidor = "jdbc:mysql://localhost:3306/" + nombreDB
            + "?useUnicode=true"
            + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=UTC"
            + "&useSSL=false";

    //Modo host online
    /*private static final String controlador = "com.mysql.jdbc.Driver";
    private static final String usuarioDB = "epiz_24934882";
    private static final String passDB = "OrA4MhSv0LnhD9f"; //Contrasena de la BD
    private static final String NombreBD = "epiz_24934882_ventaropas";
    private static final String host = "sql309.epizy.com";
    private static final String puerto = "3306";
    private static final String servidor = "jdbc:mysql://sql309.epizy.com:3306/" + NombreBD
            + "?useUnicode=true"
            + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=UTC"
            + "&useSSL=false";*/

 /*public void ConectarBasedeDatos() {
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            st = conexion.createStatement();
            if (conexion != null) {
                System.out.println("Conexión a " + NombreBD + ", exitosa..");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        }
    }*/
    public static Connection ConectarBasedeDatos() {
        Connection conexion;
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            if (conexion != null) {
                System.out.println("CONEXIÓN A " + nombreDB + ", EXITOSA..");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Error1 al intentar conectar con la bd: " + ex);
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (SQLException ex) {
            System.out.println("Error2 al intentar conectar con la bd, verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex);
            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD, verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            System.out.println("Error3 al intentar conectar con la bd: " + ex);
            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        }
        return conexion;
    }

    public void DesconectarBasedeDatos() {
        try {
            if (conexion != null) {
                conexion.close();
                System.out.println("DESCONEXIÓN DE " + nombreDB + ", EXITOSO..");
                if (st != null) {
                    st.close();
                    System.out.println("DESCONEXIÓN DE STATEMENT, EXITOSO..");
                    if (rs != null) {
                        rs.close();
                        System.out.println("DESCONEXIÓN DEL RESULTSET, EXITOSO..");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public int NumColumnsRS() {
        int NumColumnsRS = -1;
        try {
            NumColumnsRS = rs.getMetaData().getColumnCount();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return NumColumnsRS;
    }
}
