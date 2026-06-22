package tuti.desi.presentacion.publicaciones;

import java.math.BigDecimal;
import tuti.desi.entidades.EstadoPublicacion;

public class PublicacionesBuscarForm {
    
    private Long propiedadId;
    private String ciudad;
    private EstadoPublicacion estado;
    private BigDecimal precioMinimo;
    private BigDecimal precioMaximo;
    private String propiedad;

    // --- Getters y Setters ---

    public Long getPropiedadId() { return propiedadId; }
    public void setPropiedadId(Long propiedadId) { this.propiedadId = propiedadId; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }

    public BigDecimal getPrecioMinimo() { return precioMinimo; }
    public void setPrecioMinimo(BigDecimal precioMinimo) { this.precioMinimo = precioMinimo; }

    public BigDecimal getPrecioMaximo() { return precioMaximo; }
    public void setPrecioMaximo(BigDecimal precioMaximo) { this.precioMaximo = precioMaximo; }
	
    public String getPropiedad() {return propiedad;	}
	public void setPropiedad(String propiedad) {this.propiedad = propiedad; }
    
    
}