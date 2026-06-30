package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IFacturaRepo;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.HistorialEstadoFactura;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.facturas.FacturasBuscarForm;

@Service
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private IFacturaRepo repo;

    @Autowired
    private ContratoService contratoService;

    @Override
    public List<Factura> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Factura> filter(FacturasBuscarForm filter) {
        if (filter.getIdContrato() == null && filter.getIdPropiedad() == null
                && filter.getIdInquilino() == null && filter.getEstado() == null
                && filter.getVencimientoDesde() == null && filter.getVencimientoHasta() == null) {
            return repo.filter(null, null, null, null, null, null);
        }
        return repo.filter(
                filter.getIdContrato(),
                filter.getIdPropiedad(),
                filter.getIdInquilino(),
                filter.getEstado(),
                filter.getVencimientoDesde(),
                filter.getVencimientoHasta());
    }

    @Override
    public List<Contrato> getContratosActivos() {
        return contratoService.getAll().stream()
                .filter(c -> c.getEliminada() == null || !c.getEliminada())
                .filter(c -> c.getEstadoContrato() == EstadoContrato.ACTIVO)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Factura factura) throws Excepcion {
        boolean esNueva = factura.getId() == null;
        Factura destino = esNueva ? factura : getFacturaById(factura.getId());
        EstadoFactura estadoAnterior = esNueva ? null : destino.getEstadoFactura();

        if (!esNueva) {
            if (estadoAnterior == EstadoFactura.ANULADA) {
                throw new Excepcion("No se puede modificar una factura anulada", null);
            }
            if (estadoAnterior == EstadoFactura.PAGADA) {
                throw new Excepcion("No se puede modificar una factura pagada", null);
            }
            if (!destino.getContrato().getId().equals(factura.getContrato().getId())) {
                throw new Excepcion("No se puede modificar el contrato asociado a una factura creada", "idContrato");
            }
        }

        validarDatosObligatorios(factura);
        Contrato contrato = contratoService.getContratoById(factura.getContrato().getId());
        validarContratoActivo(contrato);
        validarCambioEstado(estadoAnterior, factura.getEstadoFactura(), esNueva);
        validarDatosPago(factura);

        destino.setContrato(contrato);
        destino.setConcepto(factura.getConcepto());
        destino.setFechaEmision(factura.getFechaEmision());
        destino.setFechaVencimiento(factura.getFechaVencimiento());
        destino.setImporte(factura.getImporte());
        destino.setEstadoFactura(factura.getEstadoFactura());
        destino.setFechaPago(factura.getFechaPago());
        destino.setMedioPago(factura.getMedioPago());
        destino.setImportePagado(factura.getImportePagado());
        destino.setInteresPagado(factura.getInteresPagado());
        destino.setEliminada(false);

        if (esNueva || estadoAnterior != destino.getEstadoFactura()) {
            HistorialEstadoFactura historial = new HistorialEstadoFactura();
            historial.setFactura(destino);
            historial.setEstado(destino.getEstadoFactura());
            historial.setFechaHora(LocalDateTime.now());
            destino.getHistorialEstados().add(historial);
        }

        repo.save(destino);
    }

    private void validarContratoActivo(Contrato contrato) throws Excepcion {
        if (contrato.getEliminada() != null && contrato.getEliminada()) {
            throw new Excepcion("No se puede facturar un contrato eliminado", "idContrato");
        }
        if (contrato.getEstadoContrato() != EstadoContrato.ACTIVO) {
            throw new Excepcion("Solo se pueden crear facturas para contratos activos", "idContrato");
        }
    }

    private void validarDatosObligatorios(Factura factura) throws Excepcion {
        if (factura.getContrato() == null || factura.getContrato().getId() == null) {
            throw new Excepcion("El contrato es obligatorio", "idContrato");
        }
        if (factura.getConcepto() == null || factura.getConcepto().trim().isEmpty()) {
            throw new Excepcion("El concepto es obligatorio", "concepto");
        }
        if (factura.getFechaEmision() == null) {
            throw new Excepcion("La fecha de emision es obligatoria", "fechaEmision");
        }
        if (factura.getFechaVencimiento() == null) {
            throw new Excepcion("La fecha de vencimiento es obligatoria", "fechaVencimiento");
        }
        if (factura.getFechaVencimiento().isBefore(factura.getFechaEmision())) {
            throw new Excepcion("La fecha de vencimiento debe ser igual o posterior a la de emision",
                    "fechaVencimiento");
        }
        if (factura.getImporte() == null || factura.getImporte() <= 0) {
            throw new Excepcion("El importe debe ser un numero positivo", "importe");
        }
        if (factura.getEstadoFactura() == null) {
            factura.setEstadoFactura(EstadoFactura.PENDIENTE);
        }
    }

    private void validarCambioEstado(EstadoFactura anterior, EstadoFactura nuevo, boolean esNueva) throws Excepcion {
        if (esNueva) {
            return;
        }
        if (anterior == nuevo) {
            return;
        }
        boolean valido = false;
        if (anterior == EstadoFactura.PENDIENTE) {
            valido = nuevo == EstadoFactura.PAGADA || nuevo == EstadoFactura.VENCIDA || nuevo == EstadoFactura.ANULADA;
        } else if (anterior == EstadoFactura.VENCIDA) {
            valido = nuevo == EstadoFactura.PAGADA;
        }
        if (!valido) {
            throw new Excepcion("El cambio de estado de la factura no es valido", "estadoFactura");
        }
    }

    private void validarDatosPago(Factura factura) throws Excepcion {
        if (factura.getEstadoFactura() == EstadoFactura.PAGADA) {
            if (factura.getFechaPago() == null) {
                throw new Excepcion("La fecha de pago es obligatoria para una factura pagada", "fechaPago");
            }
            if (factura.getMedioPago() == null) {
                throw new Excepcion("El medio de pago es obligatorio para una factura pagada", "medioPago");
            }
            if (factura.getImportePagado() == null || factura.getImportePagado() <= 0) {
                throw new Excepcion("El importe pagado debe ser un numero positivo", "importePagado");
            }
            if (factura.getInteresPagado() != null && factura.getInteresPagado() < 0) {
                throw new Excepcion("El interes pagado no puede ser negativo", "interesPagado");
            }
        } else {
            if (factura.getFechaPago() != null || factura.getMedioPago() != null
                    || factura.getImportePagado() != null || factura.getInteresPagado() != null) {
                throw new Excepcion("Los datos de pago solo se cargan cuando la factura esta pagada", null);
            }
        }
    }

    @Override
    public Factura getFacturaById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("la factura", id));
    }

    @Override
    public void deleteFacturaById(Long id) throws Excepcion {
        Factura factura = getFacturaById(id);
        if (factura.getEstadoFactura() == EstadoFactura.PAGADA) {
            throw new Excepcion("No se puede eliminar una factura pagada", null);
        }
        factura.setEliminada(true);
        repo.save(factura);
    }
}
