package tuti.desi.servicios;
/**
 *
 * @author Nico
 */
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuti.desi.accesoDatos.IPropiedadRepo;
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

    @Override
    public List<Propiedad> getAll() {
        return repo.findAll();
    }

    @Override
    public List<Propiedad> filter(PropiedadesBuscarForm filter) {
        if (filter.getDireccion() == null && filter.getCiudad() == null
                && filter.getTipo() == null && filter.getEstado() == null) {
            return repo.findAll();
        }
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

        // Registrar historial si el estado cambió o es nueva
        boolean esNueva = propiedad.getId() == null;
        boolean estadoCambio = false;

        if (!esNueva) {
            Propiedad existente = repo.findById(propiedad.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException("la propiedad", propiedad.getId()));
            estadoCambio = !existente.getEstadoDisponibilidad()
                    .equals(propiedad.getEstadoDisponibilidad());
        }

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
        // (esto se validará mejor cuando el Epic 3 esté implementado)
        if (p.getEstadoDisponibilidad() == EstadoDisponibilidad.ALQUILADA) {
            throw new Excepcion(
                "No se puede eliminar una propiedad con un contrato activo vigente", null);
        }

        // Eliminación lógica
        p.setEliminada(true);
        repo.save(p);
    }
}