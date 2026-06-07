package tuti.desi.servicios;
/**
 *
 * @author Nico
 */
import java.util.List;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.propiedades.PropiedadesBuscarForm;

public interface PropiedadService {
    List<Propiedad> getAll();
    List<Propiedad> filter(PropiedadesBuscarForm filter);
    void save(Propiedad propiedad) throws Excepcion;
    Propiedad getPropiedadById(Long id);
    void deletePropiedadById(Long id) throws Excepcion;
}