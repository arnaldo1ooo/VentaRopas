/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportes_modelos;

public class ModeloProductos {
    private String pro_identificador; //los nombres deben ser iguales al del ireport
    private String pro_descripcion;
    private String mar_descripcion;
    private String pro_existencia;

    public ModeloProductos(String pro_identificador, String pro_descripcion, String mar_descripcion, String pro_existencia) {
        this.pro_identificador = pro_identificador;
        this.pro_descripcion = pro_descripcion;
        this.mar_descripcion = mar_descripcion;
        this.pro_existencia = pro_existencia;
    }

    public String getPro_identificador() {
        return pro_identificador;
    }

    public void setPro_identificador(String pro_identificador) {
        this.pro_identificador = pro_identificador;
    }

    public String getPro_descripcion() {
        return pro_descripcion;
    }

    public void setPro_descripcion(String pro_descripcion) {
        this.pro_descripcion = pro_descripcion;
    }

    public String getMar_descripcion() {
        return mar_descripcion;
    }

    public void setMar_descripcion(String mar_descripcion) {
        this.mar_descripcion = mar_descripcion;
    }

    public String getPro_existencia() {
        return pro_existencia;
    }

    public void setPro_existencia(String pro_existencia) {
        this.pro_existencia = pro_existencia;
    }
}
