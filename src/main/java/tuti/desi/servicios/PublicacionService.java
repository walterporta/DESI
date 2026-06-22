package tuti.desi.servicios;

import java.util.List;
import tuti.desi.entidades.Publicacion;
import tuti.desi.presentacion.publicaciones.PublicacionForm;
import tuti.desi.presentacion.publicaciones.PublicacionesBuscarForm;

public interface PublicacionService {

    /**
     * Guarda una publicación nueva en base a los datos del formulario web (HU 2.1).
     */
    void guardar(PublicacionForm form) throws Exception;

    /**
     * Modifica los datos de una publicación existente en base al formulario (HU 2.3).
     */
    void modificar(PublicacionForm form) throws Exception;

    /**
     * Realiza la baja lógica de la publicación (HU 2.2).
     */
    void eliminarLogicamente(Long id) throws Exception;

    /**
     * Busca una publicación por su ID y la mapea a un formulario para editarla (HU 2.3).
     */
    PublicacionForm buscarPorIdParaForm(Long id) throws Exception;

    /**
     * Obtiene la lista de publicaciones aplicando los filtros de la grilla (HU 2.4).
     */
    List<Publicacion> buscarConFiltros(PublicacionesBuscarForm filtros);
}