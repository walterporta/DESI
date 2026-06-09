package tuti.desi.entidades;
/**
 *
 * @author Nico
 */
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPropiedad tipo;

    @Column(nullable = false)
    private Integer cantidadAmbientes;

    @Column(nullable = false)
    private Double metrosCuadrados;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDisponibilidad estadoDisponibilidad = EstadoDisponibilidad.DISPONIBLE;

    @ManyToOne
    @JoinColumn(name = "id_propietario")
    private Persona propietario;

    @Column(nullable = false)
    private Boolean eliminada = false;

    @OneToMany(mappedBy = "propiedad", cascade = CascadeType.ALL)
    private List<HistorialEstadoPropiedad> historialEstados = new ArrayList<>();

    // --- Getters y Setters ---

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

    public Persona getPropietario() { return propietario; }
    public void setPropietario(Persona propietario) { this.propietario = propietario; }

    public Boolean getEliminada() { return eliminada; }
    public void setEliminada(Boolean eliminada) { this.eliminada = eliminada; }

    public List<HistorialEstadoPropiedad> getHistorialEstados() { return historialEstados; }
    public void setHistorialEstados(List<HistorialEstadoPropiedad> historialEstados) { this.historialEstados = historialEstados; }
}