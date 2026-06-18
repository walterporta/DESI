package tuti.desi.servicios;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IContratoRepo;
import tuti.desi.accesoDatos.IPropiedadRepo;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.HistorialEstadoPropiedad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.propiedades.PropiedadesBuscarForm;

@Service
public class PropiedadServiceImpl implements PropiedadService {

    @Autowired
    IPropiedadRepo repo;

    @Autowired
    IContratoRepo contratoRepo;

    @Override
    public List<Propiedad> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Propiedad> filter(PropiedadesBuscarForm filter) {
        return repo.filter(filter.getDireccion(), filter.getCiudad(),
                filter.getTipo(), filter.getEstado());
    }

    @Override
    public void save(Propiedad propiedad) throws Excepcion {

        // Validar duplicado por dirección + ciudad
        boolean duplicada = propiedad.getId() == null
                ? repo.existsByDireccionAndCiudadAndEliminadaFalse(
                        propiedad.getDireccion(), propiedad.getCiudad())
                : repo.existsByDireccionAndCiudadAndEliminadaFalseAndIdNot(
                        propiedad.getDireccion(), propiedad.getCiudad(), propiedad.getId());

        if (duplicada) {
            throw new Excepcion(
                "Ya existe una propiedad activa con esa dirección y ciudad", "direccion");
        }

        boolean esNueva = propiedad.getId() == null;
        boolean estadoCambio = false;
        EstadoDisponibilidad estadoAnterior = null;

        if (!esNueva) {
            Propiedad existente = repo.findById(propiedad.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", propiedad.getId()));
            estadoAnterior = existente.getEstadoDisponibilidad();
            estadoCambio = !estadoAnterior.equals(propiedad.getEstadoDisponibilidad());
        }

        // Validar que no cambie a DISPONIBLE o INACTIVA si tiene contrato activo
        if (!esNueva && estadoCambio) {
            EstadoDisponibilidad nuevoEstado = propiedad.getEstadoDisponibilidad();
            if (nuevoEstado == EstadoDisponibilidad.DISPONIBLE
                    || nuevoEstado == EstadoDisponibilidad.INACTIVA) {
                boolean tieneContratoActivo = contratoRepo
                        .existsByPropiedadIdAndEstadoContratoAndEliminadaFalse(
                                propiedad.getId(), EstadoContrato.ACTIVO);
                if (tieneContratoActivo) {
                    throw new Excepcion(
                        "No se puede cambiar el estado: la propiedad tiene un contrato activo vigente",
                        "estadoDisponibilidad");
                }
            }
        }

        // Registrar historial si el estado cambió o es nueva
        if (esNueva || estadoCambio) {
            HistorialEstadoPropiedad historial = new HistorialEstadoPropiedad();
            historial.setPropiedad(propiedad);
            historial.setEstado(propiedad.getEstadoDisponibilidad());
            historial.setFechaHora(LocalDateTime.now());
            propiedad.getHistorialEstados().add(historial);
        }

        repo.save(propiedad);
    }

    @Override
    public Propiedad getPropiedadById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", id));
    }

    @Override
    public void deletePropiedadById(Long id) throws Excepcion {
        Propiedad p = repo.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", id));

        // No se puede eliminar si tiene contrato activo vigente
        boolean tieneContratoActivo = contratoRepo
                .existsByPropiedadIdAndEstadoContratoAndEliminadaFalse(
                        p.getId(), EstadoContrato.ACTIVO);
        if (tieneContratoActivo) {
            throw new Excepcion(
                "No se puede eliminar una propiedad con un contrato activo vigente", null);
        }

        p.setEliminada(true);
        repo.save(p);
    }
}