package conexion;

import forms.ABMFuncionario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import metodos.Metodos;

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
        String tipoHost = "remoto";

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
                    + ex, "Error1 en la Conexión a la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Verifique que el nombre de la bd, el usuario y la contraseña esten correctas: "
                    + ex, "Error2 en la Conexión a la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        } catch (Exception ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error3, Verifique los datos de la conexion a la BD: "
                    + ex, "Error3 en la Conexión a la BD" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            conexion = null;
        }
        return conexion;
    }

    public void DesconectarBasedeDatos() {
        try {
            if (connection != null) {
                connection.close();
                if (st != null) {
                    st.close();
                    if (rs != null) {
                        rs.close();
                    }
                }
                System.out.println("DESCONEXIÓN DEL CONNECTION(" + nombreBD + "), RESULTSET y del STATEMENT, EXITOSA..");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR AL INTENTAR DESCONECTAR "
                    + "CONNECTION(" + nombreBD + "), RESULTSET y del STATEMENT", JOptionPane.ERROR_MESSAGE);
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

    public DefaultTableModel ConsultAllBD(String elSP, String titlesJtabla[], JComboBox ElComboCampos) {
        DefaultTableModel modelotabla = new DefaultTableModel(null, titlesJtabla);
        Conexion con = ObtenerRSSentencia("CALL " + elSP);
        try {
            ResultSetMetaData mdrs = con.rs.getMetaData();
            int numColumns = mdrs.getColumnCount();
            Object[] registro = new Object[numColumns]; //el numero es la cantidad de columnas del rs
            while (con.rs.next()) {
                for (int j = 0; j < numColumns; j++) {
                    registro[j] = (con.rs.getString(j + 1));
                }
                modelotabla.addRow(registro);//agrega el registro a la tabla
            }

            //Carga el combobox con los titulos de la tabla, solo si esta vacio
            if (ElComboCampos != null && ElComboCampos.getItemCount() == 0) {
                javax.swing.DefaultComboBoxModel modelCombo = new javax.swing.DefaultComboBoxModel(titlesJtabla);
                ElComboCampos.setModel(modelCombo);
            }

            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            Logger.getLogger(ABMFuncionario.class.getName()).log(Level.SEVERE, null, ex);
        }
        return modelotabla;
    }

    public Conexion ObtenerRSSentencia(String sentencia) { //con.Desconectar luego de usar el metodo
        Conexion conexion = new Conexion();
        try {
            System.out.println("Ejecutar sentencia ObtenerRSSentencia " + sentencia);
            conexion.connection = (Connection) Conexion.ConectarBasedeDatos();
            conexion.st = conexion.connection.createStatement();
            conexion.rs = conexion.st.executeQuery(sentencia);
            int cantreg = 0;
            while (conexion.rs.next() && cantreg < 2) { //Revisamos cuantos registro trajo la consulta
                cantreg++;
            }

            switch (cantreg) {
                case 0:
                    System.out.println("ObtenerRSSentencia no trajo ningun resultado");
                    //con.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                case 1:
                    System.out.println("ObtenerRSSentencia trajo un resultado");
                    conexion.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                case 2:
                    System.out.println("ObtenerRSSentencia trajo mas de un resultado");
                    conexion.rs.beforeFirst(); //Ponemos antes del primer registro en el puntero
                    break;
                default:
                //aca se escribe lo que si o si se ejecuta
            }

        } catch (SQLException e) {
            System.out.println("Error al EjecutarSentencia ObtenerRSSentencia,  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        } catch (NullPointerException e) {
            System.out.println("ObtenerRSSentencia no trajo ningun resultado (null),  sentencia: " + sentencia + ",  ERROR " + e);
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
        }
        return conexion;
    }

    public void EjecutarABM(String sentencia) {
        //Ejecuta consultas de Altas, Bajas y Modificaciones
        try {
            Connection con;
            con = (Connection) Conexion.ConectarBasedeDatos();

            System.out.println("Insertar o Modificar registro: " + sentencia);

            Statement st;
            st = (Statement) con.createStatement();
            st.executeUpdate(sentencia);
            con.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(Metodos.class.getName() + " Sentencia: " + sentencia).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al intentar crear o modificar registro" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
