package tuti.desi.presentacion.publicaciones;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tuti.desi.entidades.EstadoPublicacion;

public class PublicacionForm {

    private Long id; // null para altas, con valor para modificaciones

    @NotNull(message = "El precio mensual es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser un número positivo mayor a cero")
    private BigDecimal precioMensual;

    @NotBlank(message = "Las condiciones contractuales son obligatorias")
    private String condiciones;

    @NotBlank(message = "La descripción de la publicación es obligatoria")
    private String descripcion;

    private EstadoPublicacion estado;

    @NotNull(message = "Debe seleccionar una propiedad para publicar")
    private Long propiedadId;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }

    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoPublicacion getEstado() { return estado; }
    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }

    public Long getPropiedadId() { return propiedadId; }
    public void setPropiedadId(Long propiedadId) { this.propiedadId = propiedadId; }
}