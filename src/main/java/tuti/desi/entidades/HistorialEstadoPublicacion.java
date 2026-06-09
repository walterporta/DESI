package tuti.desi.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiales_de_estado_de_las_publicaciones")

public class HistorialEstadoPublicacion {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotNull(message = "El estado es obligatorio")
	    @Enumerated(EnumType.STRING)
	    @Column(name = "estado", nullable = false, length = 15)
	    private EstadoPublicacion estado;

	    @NotNull(message = "La fecha y hora son obligatorias")
	    @Column(name = "fecha_hora", nullable = false)
	    private LocalDateTime fechaHora; // LocalDateTime mapea fecha y hora exacta

	    public HistorialEstadoPublicacion() {}

	    public HistorialEstadoPublicacion(EstadoPublicacion estado, LocalDateTime fechaHora) {
	        this.estado = estado;
	        this.fechaHora = fechaHora;
	    }

	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }

	    public EstadoPublicacion getEstado() { return estado; }
	    public void setEstado(EstadoPublicacion estado) { this.estado = estado; }

	    public LocalDateTime getFechaHora() { return fechaHora; }
	    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
	
}