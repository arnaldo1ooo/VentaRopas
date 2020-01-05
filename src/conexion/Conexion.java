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

    public Connection connection;
    public Statement st;
    public ResultSet rs;

    String TipoHost = "local";
    //Modo host local
    /*private static final String controlador = "com.mysql.cj.jdbc.Driver";
    private static final String usuarioDB = "root";
    private static final String passDB = "toor5127"; //Contrasena de la BD
    private static final String nombreBD = "ventaropas";
    private static final String host = "localhost";
    private static final String puerto = "3306";
    private static final String servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
            + "?useUnicode=true"
            + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=UTC"
            + "&useSSL=false"
            + "&allowPublicKeyRetrieval=true";*/

    //Modo host remoto
    private static final String controlador = "com.mysql.cj.jdbc.Driver";
    private static final String usuarioDB = "invitado";
    private static final String passDB = "toor5127"; //Contrasena de la BD
    private static final String nombreBD = "ventaropas";
    private static final String host = "192.168.88.240";
    private static final String puerto = "3306";
    private static final String servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
            + "?useUnicode=true"
            + "&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimeCode=false"
            + "&serverTimezone=UTC"
            + "&useSSL=false";
    public static Connection ConectarBasedeDatos() {
        Connection conexion;
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            if (conexion != null) {
                System.out.println("CONEXIÓN A " + nombreBD + ", EXITOSA..");
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("Error1 al intentar conectar con la bd: " + ex);
            JOptionPane.showMessageDialog(null, "Verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex, "Error1 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (SQLException ex) {
            System.out.println("Error2 al intentar conectar con la bd, verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex);
            JOptionPane.showMessageDialog(null, "Verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex, "Error2 en la Conexión con la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            System.out.println("Error3 al intentar conectar con la bd: " + ex);
            JOptionPane.showMessageDialog(null, "Verifique que el nombre de la bd, el usuario y la contraseña esten correctas: " + ex, "Error3 en la Conexión con la BD " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        }
        return conexion;
    }

    public void DesconectarBasedeDatos() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("DESCONEXIÓN DE " + nombreBD + ", EXITOSO..");
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
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error al intentar desconectar", JOptionPane.ERROR_MESSAGE);
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
