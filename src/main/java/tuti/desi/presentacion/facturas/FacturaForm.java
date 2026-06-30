package tuti.desi.presentacion.facturas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.MedioPago;

public class FacturaForm {

    private Long id;

    @NotNull(message = "El contrato es obligatorio")
    private Long idContrato;

    @NotBlank(message = "El concepto es obligatorio")
    private String concepto;

    @NotNull(message = "La fecha de emision es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaEmision;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVencimiento;

    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser un numero positivo")
    private Double importe;

    @NotNull(message = "El estado es obligatorio")
    private EstadoFactura estadoFactura = EstadoFactura.PENDIENTE;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPago;

    private MedioPago medioPago;
    private Double importePagado;
    private Double interesPagado;

    public FacturaForm() {
    }

    public FacturaForm(Factura f) {
        this.id = f.getId();
        this.idContrato = f.getContrato() != null ? f.getContrato().getId() : null;
        this.concepto = f.getConcepto();
        this.fechaEmision = f.getFechaEmision();
        this.fechaVencimiento = f.getFechaVencimiento();
        this.importe = f.getImporte();
        this.estadoFactura = f.getEstadoFactura();
        this.fechaPago = f.getFechaPago();
        this.medioPago = f.getMedioPago();
        this.importePagado = f.getImportePagado();
        this.interesPagado = f.getInteresPagado();
    }

    public Factura toPojo() {
        Factura f = new Factura();
        f.setId(this.id);
        f.setConcepto(this.concepto);
        f.setFechaEmision(this.fechaEmision);
        f.setFechaVencimiento(this.fechaVencimiento);
        f.setImporte(this.importe);
        f.setEstadoFactura(this.estadoFactura);
        f.setFechaPago(this.fechaPago);
        f.setMedioPago(this.medioPago);
        f.setImportePagado(this.importePagado);
        f.setInteresPagado(this.interesPagado);
        f.setEliminada(false);
        return f;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public EstadoFactura getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(EstadoFactura estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public Double getImportePagado() {
        return importePagado;
    }

    public void setImportePagado(Double importePagado) {
        this.importePagado = importePagado;
    }

    public Double getInteresPagado() {
        return interesPagado;
    }

    public void setInteresPagado(Double interesPagado) {
        this.interesPagado = interesPagado;
    }
}
