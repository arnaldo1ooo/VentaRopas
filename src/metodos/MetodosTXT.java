/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metodos;

import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Owicron-CodesBlue
 */
public class MetodosTXT {

    public void FiltroCaracteresInvalidos(java.awt.event.KeyEvent evt) {
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
        //Condicion que nos permite ingresar datos de tipo texto
        if (((car < '0' || car > '9') && (car != (char) KeyEvent.VK_BACK_SPACE))) {
            evt.consume();
        }
    }

    public void SoloNumeroDecimalKeyPress(KeyEvent evt, JTextField ElTXT) {
        //Declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        //Condicion que nos permite ingresar datos de tipo texto
        if (((car < '0' || car > '9') && ElTXT.getText().contains("."))
                && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        } else if ((car < '0' || car > '9') && (car != '.')
                && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        }
    }

    //Metodo en cual si el texto esta vacio, su label queda en gris, si esta cargado queda en verde
    public void txtColorLabelKeyReleased(JTextField ElTXT, JLabel ElLabel, String ElTexto) {
        if (ElTXT.getText().equals("")) {
            ElLabel.setForeground(new Color(102, 102, 102));
        } else {
            ElLabel.setText(ElTexto);
            ElLabel.setForeground(new Color(0, 153, 51));
        }
    }

//Convierte a mayusculas
    public void txtMayusKeyReleased(JTextField ElTXT, KeyEvent evt) {
        Character s = evt.getKeyChar();
        if (Character.isLetter(s)) {
            ElTXT.setText(ElTXT.getText().toUpperCase());
        }
    }

    //Limitar cantidad caracteres
    public void txtCantidadCaracteresKeyTyped(JTextField ElTXT, int Cantidad) {
        int longitud = ElTXT.getText().length();
        if (longitud > Cantidad - 1) {
            ElTXT.setText(ElTXT.getText().substring(0, Cantidad + 1));
        };
    }

}
