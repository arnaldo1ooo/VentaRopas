package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import principal.SplashScreen;

public class Conexion {

    public Connection connection;
    public Statement st;
    public ResultSet rs;
    private static String controlador;
    private static String usuarioDB;
    private static String passDB; //Contrasena de la BD
    private static String nombreBD;
    private static String host;
    private static String puerto;
    private static String servidor;

    public static Connection ConectarBasedeDatos() {
        String tipoHost = "local";

        if (tipoHost.equals("local")) {
            //Modo host local
            controlador = "com.mysql.cj.jdbc.Driver";
            usuarioDB = "root";
            passDB = "toor5127"; //Contrasena de la BD
            nombreBD = "ventaropas";
            host = "localhost";
            puerto = "3306";
            servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                    + "?useUnicode=true"
                    + "&useJDBCCompliantTimezoneShift=true"
                    + "&useLegacyDatetimeCode=false"
                    + "&serverTimezone=America/Mexico_City"
                    //+ "&serverTimezone=UTC"
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true";
        } else {
            if (tipoHost.equals("remoto")) {
                //Modo host remoto
                controlador = "com.mysql.cj.jdbc.Driver";
                usuarioDB = "invitado";
                passDB = "toor5127"; //Contrasena de la BD
                nombreBD = "ventaropas";
                host = "192.168.88.240";
                puerto = "3306";
                servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                        + "?useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=UTC"
                        + "&useSSL=false";
            } else {
                if (tipoHost.equals("online")) {
                    //Modo host online
                    controlador = "com.mysql.cj.jdbc.Driver";
                    usuarioDB = "invitado";
                    passDB = "toor5127"; //Contrasena de la BD
                    nombreBD = "ventaropas";
                    host = "143.255.143.230";
                    puerto = "3306";
                    servidor = "jdbc:mysql://" + host + ":" + puerto + "/" + nombreBD
                            + "?useUnicode=true"
                            + "&useJDBCCompliantTimezoneShift=true"
                            + "&useLegacyDatetimeCode=false"
                            + "&serverTimezone=UTC"
                            + "&useSSL=false";
                }
            }
        }

        Connection conexion;
        try {
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuarioDB, passDB);
            if (conexion != null) {
                System.out.println("CONEXIÓN A " + nombreBD + ", EXITOSA..");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error1, Verifique los datos de la conexion a la BD: "
                    + ex, "Error1 en la Conexión con la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Verifique que el nombre de la bd, el usuario y la contraseña esten correctas: "
                    + ex, "Error2 en la Conexión con la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error3, Verifique los datos de la conexion a la BD: "
                    + ex, "Error3 en la Conexión con la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
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
            Logger.getLogger(Conexion.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return NumColumnsRS;
    }
}
