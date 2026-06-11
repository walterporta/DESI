package tuti.desi.presentacion.contrato;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;

public class ContratoForm {

    private Long id;

    @NotNull(message = "La propiedad es obligatoria")
    private Long idPropiedad;

    @NotNull(message = "El inquilino es obligatorio")
    private Long idInquilino;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "La duración en meses es obligatoria")
    @Positive(message = "La duración debe ser un número positivo")
    private Integer duracionMeses;

    @NotNull(message = "El importe mensual es obligatorio")
    @Positive(message = "El importe mensual debe ser un número positivo")
    private Double importeMensual;

    @NotNull(message = "El día de vencimiento es obligatorio")
    @Min(value = 1, message = "El día de vencimiento debe ser al menos 1")
    @Max(value = 31, message = "El día de vencimiento no puede ser mayor a 31")
    private Integer diaVencimiento;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El estado del contrato es obligatorio")
    private EstadoContrato estadoContrato = EstadoContrato.BORRADOR;

    // Constructor vacío (para alta)
    public ContratoForm() {
    }

    // Constructor desde entidad (para edición)
    public ContratoForm(Contrato c) {
        this.id = c.getId();
        this.idPropiedad = c.getPropiedad() != null ? c.getPropiedad().getId() : null;
        this.idInquilino = c.getInquilino() != null ? c.getInquilino().getId() : null;
        this.fechaInicio = c.getFechaInicio();
        this.duracionMeses = c.getDuracionMeses();
        this.importeMensual = c.getImporteMensual();
        this.diaVencimiento = c.getDiaVencimiento();
        this.descripcion = c.getDescripcion();
        this.estadoContrato = c.getEstadoContrato();
    }

    // Convierte el form a entidad
    public Contrato toPojo() {
        Contrato c = new Contrato();
        c.setId(this.id);
        c.setFechaInicio(this.fechaInicio);
        c.setDuracionMeses(this.duracionMeses);
        c.setImporteMensual(this.importeMensual);
        c.setDiaVencimiento(this.diaVencimiento);
        c.setDescripcion(this.descripcion);
        c.setEstadoContrato(this.estadoContrato);
        c.setEliminada(false);
        return c;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPropiedad() {
        return idPropiedad;
    }

    public void setIdPropiedad(Long idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public Long getIdInquilino() {
        return idInquilino;
    }

    public void setIdInquilino(Long idInquilino) {
        this.idInquilino = idInquilino;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    public Double getImporteMensual() {
        return importeMensual;
    }

    public void setImporteMensual(Double importeMensual) {
        this.importeMensual = importeMensual;
    }

    public Integer getDiaVencimiento() {
        return diaVencimiento;
    }

    public void setDiaVencimiento(Integer diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoContrato getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(EstadoContrato estadoContrato) {
        this.estadoContrato = estadoContrato;
    }
}