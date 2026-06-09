package tuti.desi.presentacion.propiedades;
/**
 *
 * @author Nico
 */
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.TipoPropiedad;

public class PropiedadesBuscarForm {

    private String direccion;
    private String ciudad;
    private TipoPropiedad tipo;
    private EstadoDisponibilidad estado;

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public TipoPropiedad getTipo() { return tipo; }
    public void setTipo(TipoPropiedad tipo) { this.tipo = tipo; }

    public EstadoDisponibilidad getEstado() { return estado; }
    public void setEstado(EstadoDisponibilidad estado) { this.estado = estado; }
}