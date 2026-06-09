package tuti.desi.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.math.BigDecimal;


@Entity
public class Publicacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "El precio es obligatorio")
	@DecimalMin(value = "0.01", message = "El precio debe ser un número positivo mayor a cero")
	@Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioMensual;

    @NotBlank(message = "Las condiciones son obligatorias")
    @Column(name = "condiciones", nullable = false, columnDefinition = "TEXT")
    private String condiciones;
	
    @NotNull(message = "La fecha de publicación es obligatoria")
    @FutureOrPresent(message = "La fecha de publicación debe ser hoy o una fecha futura")
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING) // Guarda el nombre del estado en vez del número por si en el futuro agrega otro estado al DER.
    @Column(name = "estado", nullable = false, length =15)
    private EstadoPublicacion estado;

    @Column(name = "eliminada", nullable = false)
    private boolean eliminada = false; // Publicación activa por defecto al crearse
    
    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "publicacion_id") // Crea la clave foránea en la tabla del historial
    private List<HistorialEstadoPublicacion> historialEstados = new ArrayList<>();

	@ManyToOne
	private Propiedad propiedad;
	

	  public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }

	    public BigDecimal getPrecioMensual() { return precioMensual; }
	    public void setPrecioMensual(BigDecimal precioMensual) { this.precioMensual = precioMensual; }

	    public String getCondiciones() { return condiciones; }
	    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }

	    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
	    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

	    public EstadoPublicacion getEstado() { return estado; }
	    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }

	    public boolean isEliminada() { return eliminada; }
	    public void setEliminada(boolean eliminada) { this.eliminada = eliminada; }

	    public String getDescripcion() { return descripcion; }
	    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

	    public List<HistorialEstadoPublicacion> getHistorialEstados() { return historialEstados; }
	    public void setHistorialEstados(List<HistorialEstadoPublicacion> historialEstados) { this.historialEstados = historialEstados; }

}