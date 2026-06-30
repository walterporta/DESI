package tuti.desi.presentacion.facturas;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import tuti.desi.entidades.EstadoFactura;

public class FacturasBuscarForm {

    private Long idContrato;
    private Long idPropiedad;
    private Long idInquilino;
    private EstadoFactura estado;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vencimientoDesde;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vencimientoHasta;

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
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

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public LocalDate getVencimientoDesde() {
        return vencimientoDesde;
    }

    public void setVencimientoDesde(LocalDate vencimientoDesde) {
        this.vencimientoDesde = vencimientoDesde;
    }

    public LocalDate getVencimientoHasta() {
        return vencimientoHasta;
    }

    public void setVencimientoHasta(LocalDate vencimientoHasta) {
        this.vencimientoHasta = vencimientoHasta;
    }
}
