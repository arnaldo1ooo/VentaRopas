/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Owicron-CodesBlue
 */
public class MetodosTXT {

    public void FiltroCaracteresProhibidos(java.awt.event.KeyEvent evt) {
        // Verificar si la tecla pulsada no es '
        char caracter = evt.getKeyChar();

        if (caracter == "'".charAt(0) || caracter == "\\".charAt(0)) {
            evt.consume(); // ignorar el evento de teclado
        }
    }

    public void SoloTextoKeyTyped(KeyEvent evt) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo texto
        if (((car < 'a' || car > 'z') && (car < 'A' || car > 'z')) && (car != (char) KeyEvent.VK_BACK_SPACE) && (car != (char) KeyEvent.VK_SPACE)) {
            evt.consume();
        }
    }

    public void SoloNumeroEnteroKeyTyped(KeyEvent evt) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo numerico
        if (((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE))) {
            evt.consume();
        }
    }

    public void SoloNumeroDecimalKeyTyped(KeyEvent evt, JTextField ElTXT) {
// Que solo entre numeros y .
        char teclaoprimida = evt.getKeyChar();
        if ((teclaoprimida < '0' || teclaoprimida > '9')
                && teclaoprimida != KeyEvent.VK_BACK_SPACE //Para que no entre espacio
                && teclaoprimida != KeyEvent.VK_LEFT
                && (teclaoprimida != ',' && teclaoprimida != '.' || ElTXT.getText().contains(",") == true)) { //Para que solo tenga una coma
            evt.consume(); // ignorar el evento de teclado
        } else {
            if (teclaoprimida == '.') { //Si se oprime . lo reemplaza con ,
                evt.setKeyChar(',');
            }
        }
    }

    //Metodo en cual si el texto esta vacio, su label queda en gris, si esta cargado queda en verde
    public void TxtColorLabelKeyReleased(JTextField ElTXT, JLabel ElLabel) {
        if (ElTXT.getText().equals("")) {
            if (ElLabel.getForeground() == Color.RED) { //Si esta en rojo, y es vacio entonces no hace nada
                return;
            }
            ElLabel.setForeground(new Color(0, 0, 0)); //Negro
        } else { //Si es distinto a vacio
            ElLabel.setForeground(new Color(0, 153, 51)); //Verde
        }
    }

//Convierte a mayusculas
    public void TxtMayusKeyReleased(JTextField ElTXT, KeyEvent evt) {
        Character s = evt.getKeyChar();
        if (Character.isLetter(s)) {
            ElTXT.setText(ElTXT.getText().toUpperCase());
        }
    }

    //Limitar cantidad caracteres
    public void TxtCantidadCaracteresKeyTyped(JTextField ElTXT, int Cantidad) {
        int longitud = ElTXT.getText().length();
        Cantidad = Cantidad - 1;
        if (longitud > Cantidad) {
            ElTXT.setText(ElTXT.getText().substring(0, Cantidad));
        }
    }

    public String DoubleFormatoSudamericaKeyReleased(String elNumero) {
        String elNumeroModi = "";
        try {
            if (elNumero.equals("") == false) {
                int siEsDecimal = elNumero.indexOf(",");
                if (siEsDecimal != -1) { //Si es decimal
                    int parteEntera = (int) Double.parseDouble(elNumero.replace(",", "."));
                    String parteDecimal = elNumero.substring(elNumero.indexOf(','));
                    String parteEnteraString = parteEntera + "";
                    int longitud = parteEnteraString.length();
                    if (longitud == 1 || longitud == 2 || longitud == 3) {
                        return elNumero;
                    }
                    if (longitud == 4) {
                        String sub1 = parteEnteraString.substring(0, 1);
                        String sub2 = parteEnteraString.substring(1, 4);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }
                    if (longitud == 5) {
                        String sub1 = parteEnteraString.substring(0, 2);
                        String sub2 = parteEnteraString.substring(2, 5);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }

                    if (longitud == 6) {
                        String sub1 = parteEnteraString.substring(0, 3);
                        String sub2 = parteEnteraString.substring(3, 6);
                        elNumeroModi = sub1 + "." + sub2 + parteDecimal;
                        return elNumeroModi;
                    }

                    if (longitud == 7) {
                        String sub1 = parteEnteraString.substring(0, 1);
                        String sub2 = parteEnteraString.substring(1, 4);
                        String sub3 = parteEnteraString.substring(4, 7);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }

                    if (longitud == 8) {
                        String sub1 = parteEnteraString.substring(0, 2);
                        String sub2 = parteEnteraString.substring(2, 5);
                        String sub3 = parteEnteraString.substring(5, 8);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }

                    if (longitud == 9) {
                        String sub1 = parteEnteraString.substring(0, 3);
                        String sub2 = parteEnteraString.substring(3, 6);
                        String sub3 = parteEnteraString.substring(6, 9);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                        return elNumeroModi;
                    }
                } else { //Si no es decimal
                    elNumero = elNumero.replace(".", "");
                    elNumeroModi = elNumero;
                    int longitud = elNumero.length();

                    if (longitud == 4) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }
                    if (longitud == 5) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }

                    if (longitud == 6) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        elNumeroModi = sub1 + "." + sub2;
                        return elNumeroModi;
                    }

                    if (longitud == 7) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        String sub3 = elNumero.substring(4, 7);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }

                    if (longitud == 8) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        String sub3 = elNumero.substring(5, 8);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }

                    if (longitud == 9) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        String sub3 = elNumero.substring(6, 9);
                        elNumeroModi = sub1 + "." + sub2 + "." + sub3;
                        return elNumeroModi;
                    }
                }
            }
        } catch (NumberFormatException e) {
            elNumeroModi = elNumero;
            System.out.println("Numero " + elNumero + " no valido, error al poner puntos decimales " + e);
            e.printStackTrace();
        }
        return elNumeroModi;
    }

    //Convertir de Double americano a Double Sudamericano
    public String DoubleAFormatoSudamerica(double elDoubleAmericano) {
        String elDoubleSudamerica = "";
        try {
            String parteEntera = ((int) elDoubleAmericano) + "";
            String parteDecimal = (elDoubleAmericano + "").substring((elDoubleAmericano + "").indexOf('.'));

            parteDecimal = parteDecimal.replace(".", ",");
            if (parteDecimal.equals(",0")) {
                parteDecimal = "";
            }
            int longitud = parteEntera.length();
            if (longitud == 1 || longitud == 2 || longitud == 3) {
                elDoubleSudamerica = parteEntera + parteDecimal;
                return elDoubleSudamerica;
            }
            if (longitud == 4) {
                String sub1 = parteEntera.substring(0, 1);
                String sub2 = parteEntera.substring(1, 4);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }
            if (longitud == 5) {
                String sub1 = parteEntera.substring(0, 2);
                String sub2 = parteEntera.substring(2, 5);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 6) {
                String sub1 = parteEntera.substring(0, 3);
                String sub2 = parteEntera.substring(3, 6);
                elDoubleSudamerica = sub1 + "." + sub2 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 7) {
                String sub1 = parteEntera.substring(0, 1);
                String sub2 = parteEntera.substring(1, 4);
                String sub3 = parteEntera.substring(4, 7);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 8) {
                String sub1 = parteEntera.substring(0, 2);
                String sub2 = parteEntera.substring(2, 5);
                String sub3 = parteEntera.substring(5, 8);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }

            if (longitud == 9) {
                String sub1 = parteEntera.substring(0, 3);
                String sub2 = parteEntera.substring(3, 6);
                String sub3 = parteEntera.substring(6, 9);
                elDoubleSudamerica = sub1 + "." + sub2 + "." + sub3 + parteDecimal;
                return elDoubleSudamerica;
            }
        } catch (NumberFormatException e) {
            System.out.println("El Numero " + elDoubleAmericano + "  no es valido, error al convertir a formato sudamericano " + e);
            e.printStackTrace();
        }
        return elDoubleSudamerica;
    }

    public Double DoubleAFormatoAmericano(String ElNumero) {
        ElNumero = ElNumero.replace(".", ""); //Saca los puntos de miles
        ElNumero = ElNumero.replace(",", "."); //Cambia la coma en punto
        double ElNumeroDouble = Double.parseDouble(ElNumero);
        return ElNumeroDouble;
    }

    //Formatear double para que tenga solo dos numeros despues de la coma, y la coma es punto
    public double FormatearADosDecimales(double ElDouble) {
        String elDoubleString = "";
        Double elNumeroDouble = 0.00;
        try {
            elDoubleString = String.format("%.2f", ElDouble);
            elDoubleString = elDoubleString.replace(",", ".");
            elNumeroDouble = Double.parseDouble(elDoubleString);
        } catch (NumberFormatException e) {
            System.out.println("Error al poner DosDecimales al numero " + ElDouble + "   " + e);
        }
        return elNumeroDouble;
    }

    public void BloquearTeclaKeyTyped(KeyEvent evt, int tecla) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Que no entre espacio
        if ((car == (char) tecla)) { //Ejemplo de tecla: KeyEvent.VK_SPACE
            evt.consume();
        }
    }
};
