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

    private static final String controlador = "com.mysql.jdbc.Driver";
    private static final String usuarioDB = "root";
    private static final String passDB = "toor5127"; //Contrasena de la BD
    private static final String servidor = "jdbc:mysql://localhost:3306/kuarahyrese"
            + "?useUnicode=true"
            + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=UTC"
            + "&useSSL=false";

    public void ConectarBasedeDatos() {
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            st = conexion.createStatement();
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
    }

    public void DesconectarBasedeDatos() {
        try {
            if (conexion != null) {
                conexion.close();
                if (st != null) {
                    st.close();
                    if (rs != null) {
                        rs.close();
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

    @SuppressWarnings("FinallyDiscardsException")
    public static Connection GetConnection() {
        Connection conexion = null;
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } finally {
            return conexion;
        }
    }
}
