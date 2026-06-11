package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IContratoRepo;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.HistorialEstadoContrato;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.contratos.ContratosBuscarForm;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private IContratoRepo repo;

    @Autowired
    private PropiedadService propiedadService;

    @Override
    public List<Contrato> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Contrato> filter(ContratosBuscarForm filter) {
        if (filter.getIdPropiedad() == null && filter.getIdInquilino() == null
                && filter.getEstado() == null && filter.getFechaInicio() == null) {
            // Retorna todos los no eliminados
            return repo.filter(null, null, null, null);
        }
        return repo.filter(
                filter.getIdPropiedad(),
                filter.getIdInquilino(),
                filter.getEstado(),
                filter.getFechaInicio());
    }

    @Override
    public void save(Contrato contrato) throws Excepcion {
        // Validaciones de negocio
        if (contrato.getImporteMensual() == null || contrato.getImporteMensual() <= 0) {
            throw new Excepcion("El importe mensual debe ser un número positivo", "importeMensual");
        }
        if (contrato.getDuracionMeses() == null || contrato.getDuracionMeses() <= 0) {
            throw new Excepcion("La duración en meses debe ser un número positivo", "duracionMeses");
        }
        if (contrato.getDiaVencimiento() == null || contrato.getDiaVencimiento() < 1
                || contrato.getDiaVencimiento() > 31) {
            throw new Excepcion("El día de vencimiento mensual debe ser un número entre 1 y 31", "diaVencimiento");
        }
        if (contrato.getFechaInicio() == null) {
            throw new Excepcion("La fecha de inicio es requerida", "fechaInicio");
        }

        // Calcular fecha de fin
        contrato.setFechaFin(contrato.getFechaInicio().plusMonths(contrato.getDuracionMeses()));

        boolean esNuevo = contrato.getId() == null;
        EstadoContrato estadoAnterior = null;
        Contrato existente = null;

        if (!esNuevo) {
            existente = repo.findById(contrato.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("el contrato", contrato.getId()));
            estadoAnterior = existente.getEstadoContrato();

            // No permitir cambiar el estado de finalizado o rescindido
            if (estadoAnterior == EstadoContrato.FINALIZADO || estadoAnterior == EstadoContrato.RESCINDIDO) {
                if (estadoAnterior != contrato.getEstadoContrato()) {
                    throw new Excepcion("No se permite cambiar el estado de un contrato finalizado o rescindido",
                            "estadoContrato");
                }
            }
        }

        EstadoContrato estadoNuevo = contrato.getEstadoContrato();
        Propiedad propiedad = propiedadService.getPropiedadById(contrato.getPropiedad().getId());

        // Lógica de transiciones y disponibilidad de propiedad
        if (estadoNuevo == EstadoContrato.ACTIVO) {
            boolean transitionToActive = esNuevo || (estadoAnterior != EstadoContrato.ACTIVO);
            if (transitionToActive) {
                // Verificar disponibilidad de la propiedad
                if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
                    throw new Excepcion("La propiedad no está disponible para alquiler", "propiedad");
                }
                // Verificar que no tenga otro contrato activo
                boolean tieneOtroActivo = esNuevo
                        ? repo.existsByPropiedadIdAndEstadoContratoAndEliminadaFalse(propiedad.getId(),
                                EstadoContrato.ACTIVO)
                        : repo.existsByPropiedadIdAndEstadoContratoAndEliminadaFalseAndIdNot(propiedad.getId(),
                                EstadoContrato.ACTIVO, contrato.getId());

                if (tieneOtroActivo) {
                    throw new Excepcion("La propiedad ya tiene un contrato activo", "propiedad");
                }

                // Cambiar propiedad a ALQUILADA
                propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.ALQUILADA);
                propiedadService.save(propiedad);
            }
        } else if (estadoNuevo == EstadoContrato.FINALIZADO || estadoNuevo == EstadoContrato.RESCINDIDO) {
            boolean transitionFromActive = !esNuevo && (estadoAnterior == EstadoContrato.ACTIVO);
            if (transitionFromActive) {
                // Devolver propiedad a DISPONIBLE
                propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
                propiedadService.save(propiedad);
            }
        }

        // Historial de estados del contrato
        boolean registrarHistorial = esNuevo || (estadoAnterior != estadoNuevo);
        if (registrarHistorial) {
            HistorialEstadoContrato historial = new HistorialEstadoContrato();
            historial.setContrato(contrato);
            historial.setEstado(estadoNuevo);
            historial.setFechaHora(LocalDateTime.now());
            contrato.getHistorialEstados().add(historial);
        }

        repo.save(contrato);
    }

    @Override
    public Contrato getContratoById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("el contrato", id));
    }

    @Override
    public void deleteContratoById(Long id) throws Excepcion {
        Contrato contrato = repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("el contrato", id));

        // Solo se pueden eliminar contratos en estado borrador
        if (contrato.getEstadoContrato() != EstadoContrato.BORRADOR) {
            throw new Excepcion("Solo se pueden eliminar contratos en estado BORRADOR", null);
        }

        contrato.setEliminada(true);
        repo.save(contrato);
    }
}