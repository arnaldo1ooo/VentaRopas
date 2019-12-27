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
        char caracter = evt.getKeyChar();
        if ((((caracter < '0') || (caracter > '9'))
                && (caracter != KeyEvent.VK_BACK_SPACE)
                && (caracter != ',' || ElTXT.getText().contains(",")))) {
            evt.consume(); // ignorar el evento de teclado
        }
    }

    //Metodo en cual si el texto esta vacio, su label queda en gris, si esta cargado queda en verde
    public void TxtColorLabelKeyReleased(JTextField ElTXT, JLabel ElLabel, String ElTexto) {
        if (ElTXT.getText().equals("")) {
            ElLabel.setForeground(new Color(102, 102, 102));
        } else {
            ElLabel.setText(ElTexto);
            ElLabel.setForeground(new Color(0, 153, 51));
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

    //Poner puntos miles cada 3 numeros
    public void PonerPuntosMilesKeyReleased(JTextField ElTXT) {
        try {
            String elNumero = ElTXT.getText();
            if (elNumero.equals("") == false) {
                int siEsDecimal = elNumero.indexOf(",");
                if (siEsDecimal != -1) { //Si es decimal
                    int parteEntera = (int) Double.parseDouble(elNumero.replace(",", "."));
                    String parteDecimal = elNumero.substring(elNumero.indexOf(','));

                    elNumero = parteEntera + "";
                    int longitud = elNumero.length();
                    if (longitud == 4) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        ElTXT.setText(sub1 + "." + sub2 + parteDecimal);
                        return;
                    }
                    if (longitud == 5) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        ElTXT.setText(sub1 + "." + sub2 + parteDecimal);
                        return;
                    }

                    if (longitud == 6) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        ElTXT.setText(sub1 + "." + sub2 + parteDecimal);
                        return;
                    }

                    if (longitud == 7) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        String sub3 = elNumero.substring(4, 7);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3 + parteDecimal);
                        return;
                    }

                    if (longitud == 8) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        String sub3 = elNumero.substring(5, 8);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3 + parteDecimal);
                        return;
                    }

                    if (longitud == 9) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        String sub3 = elNumero.substring(6, 9);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3 + parteDecimal);
                        return;
                    }
                } else { //Si no es decimal
                    elNumero = elNumero.replace(".", "");
                    ElTXT.setText(elNumero);
                    int longitud = elNumero.length();

                    if (longitud == 4) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        ElTXT.setText(sub1 + "." + sub2);
                        return;
                    }
                    if (longitud == 5) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        ElTXT.setText(sub1 + "." + sub2);
                        return;
                    }

                    if (longitud == 6) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        ElTXT.setText(sub1 + "." + sub2);
                        return;
                    }

                    if (longitud == 7) {
                        String sub1 = elNumero.substring(0, 1);
                        String sub2 = elNumero.substring(1, 4);
                        String sub3 = elNumero.substring(4, 7);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3);
                        return;
                    }

                    if (longitud == 8) {
                        String sub1 = elNumero.substring(0, 2);
                        String sub2 = elNumero.substring(2, 5);
                        String sub3 = elNumero.substring(5, 8);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3);
                        return;
                    }

                    if (longitud == 9) {
                        String sub1 = elNumero.substring(0, 3);
                        String sub2 = elNumero.substring(3, 6);
                        String sub3 = elNumero.substring(6, 9);
                        ElTXT.setText(sub1 + "." + sub2 + "." + sub3);
                        return;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Numero no valido " + e);
        }
    }

    //Formatear double para que tenga solo dos numeros despues de la coma
    public String DosDecimalesDouble(String ElDouble) {
        DecimalFormat df = new DecimalFormat("#.##");
        Double elNumero = Double.parseDouble(ElDouble);
        String elNumeroFormateado = df.format(elNumero);
        return elNumeroFormateado.replace(",", ".");
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
