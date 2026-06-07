package tuti.desi.presentacion.propiedades;
/**
 *
 * @author Nico
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;

public class PropiedadForm {

    private Long id;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotNull(message = "El tipo de propiedad es obligatorio")
    private TipoPropiedad tipo;

    @NotNull(message = "La cantidad de ambientes es obligatoria")
    @Positive(message = "La cantidad de ambientes debe ser un número positivo")
    private Integer cantidadAmbientes;

    @NotNull(message = "Los metros cuadrados son obligatorios")
    @Positive(message = "Los metros cuadrados deben ser un número positivo")
    private Double metrosCuadrados;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El estado es obligatorio")
    private EstadoDisponibilidad estadoDisponibilidad = EstadoDisponibilidad.DISPONIBLE;

    @NotNull(message = "El propietario es obligatorio")
    private Long idPropietario;

    // Constructor vacío (para alta)
    public PropiedadForm() {}

    // Constructor desde entidad (para edición)
    public PropiedadForm(Propiedad p) {
        this.id = p.getId();
        this.direccion = p.getDireccion();
        this.ciudad = p.getCiudad();
        this.tipo = p.getTipo();
        this.cantidadAmbientes = p.getCantidadAmbientes();
        this.metrosCuadrados = p.getMetrosCuadrados();
        this.descripcion = p.getDescripcion();
        this.estadoDisponibilidad = p.getEstadoDisponibilidad();
        this.idPropietario = p.getPropietario() != null ? p.getPropietario().getId() : null;
    }

    // Convierte el form a entidad
    public Propiedad toPojo() {
        Propiedad p = new Propiedad();
        p.setId(this.id);
        p.setDireccion(this.direccion);
        p.setCiudad(this.ciudad);
        p.setTipo(this.tipo);
        p.setCantidadAmbientes(this.cantidadAmbientes);
        p.setMetrosCuadrados(this.metrosCuadrados);
        p.setDescripcion(this.descripcion);
        p.setEstadoDisponibilidad(this.estadoDisponibilidad);
        p.setEliminada(false);
        return p;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public TipoPropiedad getTipo() { return tipo; }
    public void setTipo(TipoPropiedad tipo) { this.tipo = tipo; }

    public Integer getCantidadAmbientes() { return cantidadAmbientes; }
    public void setCantidadAmbientes(Integer cantidadAmbientes) { this.cantidadAmbientes = cantidadAmbientes; }

    public Double getMetrosCuadrados() { return metrosCuadrados; }
    public void setMetrosCuadrados(Double metrosCuadrados) { this.metrosCuadrados = metrosCuadrados; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoDisponibilidad getEstadoDisponibilidad() { return estadoDisponibilidad; }
    public void setEstadoDisponibilidad(EstadoDisponibilidad estadoDisponibilidad) { this.estadoDisponibilidad = estadoDisponibilidad; }

    public Long getIdPropietario() { return idPropietario; }
    public void setIdPropietario(Long idPropietario) { this.idPropietario = idPropietario; }
}