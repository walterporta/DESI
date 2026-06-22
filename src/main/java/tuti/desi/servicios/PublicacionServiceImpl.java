package tuti.desi.servicios;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tuti.desi.accesoDatos.IPublicacionRepo;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.entidades.Publicacion;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.excepciones.Excepcion; // Vinculado a tu clase base
import tuti.desi.excepciones.EntidadNoEncontradaException; // Vinculado a tu excepción de búsqueda
import tuti.desi.presentacion.publicaciones.PublicacionForm;
import tuti.desi.presentacion.publicaciones.PublicacionesBuscarForm;

@Service
public class PublicacionServiceImpl implements PublicacionService {

	@Autowired
	private IPublicacionRepo publicacionRepo;

	@Autowired
	private PropiedadService propiedadService;

	@Override
	@Transactional
	public void guardar(PublicacionForm form) throws Exception {
		// 1. Necesito validar la existencia de la propiedad para publicarla (HU 2.1)
		Propiedad propiedad = propiedadService.getPropiedadById(form.getPropiedadId());
		if (propiedad == null) {
			// Usa tu constructor exacto (entidad, id)
			throw new EntidadNoEncontradaException("Propiedad", form.getPropiedadId());
		}

		// Criterio de Aceptación: "Solo podrán publicarse propiedades activas y no
		// eliminadas"
		if (propiedad.getEliminada() != null && propiedad.getEliminada()) {
		    throw new Excepcion("No se puede publicar una propiedad que ha sido eliminada.", "propiedadId");
		}

		// Criterio de Aceptación: "Solo se podrá crear una publicación para una
		// propiedad que se encuentre disponible"
		if (propiedad.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE) {
			throw new Excepcion("La propiedad seleccionada no se encuentra DISPONIBLE para alquiler.", "propiedadId");
		}

		// 2. Criterio de Aceptación: "No podrá existir más de una publicación activa
		// para la misma propiedad"
		boolean existeActiva = publicacionRepo.existsByPropiedadIdAndEstadoAndEliminada(form.getPropiedadId(),
				EstadoPublicacion.ACTIVA, false);
		if (existeActiva) {
			throw new Excepcion("Ya existe una publicación activa vigente para esta propiedad.", "propiedadId");
		}

		// 3. Mapear datos del Formulario a la Entidad JPA
		Publicacion publicacion = new Publicacion();
		publicacion.setPrecioMensual(form.getPrecioMensual());
		publicacion.setCondiciones(form.getCondiciones());
		publicacion.setDescripcion(form.getDescripcion());
		publicacion.setFechaPublicacion(LocalDate.now()); // Setea la fecha de hoy automáticamente
		publicacion.setEliminada(false);
		publicacion.setPropiedad(propiedad);

		// Criterio de Aceptación: "El estado por defecto de una nueva publicación será
		// ACTIVA" + Historial inicial
		publicacion.registrarCambioEstado(EstadoPublicacion.ACTIVA);

		publicacionRepo.save(publicacion);
	}

	@Override
	@Transactional
	public void modificar(PublicacionForm form) throws Exception {
		// Buscar la publicación existente o lanzar error parametrizado
		Publicacion publicacion = publicacionRepo.findById(form.getId())
				.orElseThrow(() -> new EntidadNoEncontradaException("Publicación", form.getId()));

		// Criterio de Aceptación (HU 2.3): "Las condiciones de alquiler podrán
		// modificarse mientras la publicación no esté finalizada"
		if (publicacion.getEstado() == EstadoPublicacion.FINALIZADA
				&& !publicacion.getCondiciones().equals(form.getCondiciones())) {
			throw new Excepcion(
					"No se permiten alterar las condiciones contractuales de una publicación en estado FINALIZADA.",
					"condiciones");
		}

		// Criterio de Aceptación (HU 2.3): "No se podrá modificar una publicación para
		// dejarla activa si ya existe otra activa"
		if (form.getEstado() == EstadoPublicacion.ACTIVA && publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
			boolean existeActiva = publicacionRepo.existsByPropiedadIdAndEstadoAndEliminada(
					publicacion.getPropiedad().getId(), EstadoPublicacion.ACTIVA, false);
			if (existeActiva) {
				throw new Excepcion("Acción bloqueada: Ya existe otra publicación activa asociada a este inmueble.",
						"estado");
			}
		}

		// Traspaso de campos editables autorizados
		publicacion.setPrecioMensual(form.getPrecioMensual());
		publicacion.setCondiciones(form.getCondiciones());
		publicacion.setDescripcion(form.getDescripcion());

		// Criterio de Aceptación: "Mantener el registro de cambios de estado en caso de
		// que el mismo se editara"
		if (publicacion.getEstado() != form.getEstado()) {
			publicacion.registrarCambioEstado(form.getEstado());
		}

		publicacionRepo.save(publicacion);
	}

	@Override
	@Transactional
	public void eliminarLogicamente(Long id) throws Exception {
		Publicacion publicacion = publicacionRepo.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException("Publicación", id));

		// Criterio de Aceptación (HU 2.2): "Solo pueden eliminarse publicaciones
		// activas"
		if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
			throw new Excepcion(
					"Operación denegada: Solo es posible remover publicaciones que estén en estado ACTIVA.");
		}

		// Criterio de Aceptación: "La eliminación de una publicación será lógica"
		publicacion.setEliminada(true);
		publicacionRepo.save(publicacion);
	}

	@Override
	@Transactional(readOnly = true)
	public PublicacionForm buscarPorIdParaForm(Long id) throws Exception {
		Publicacion p = publicacionRepo.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException("Publicación", id));

		// Traspaso de datos de la DB al formulario para cargar los inputs de la vista
		// web
		PublicacionForm form = new PublicacionForm();
		form.setId(p.getId());
		form.setPrecioMensual(p.getPrecioMensual());
		form.setCondiciones(p.getCondiciones());
		form.setDescripcion(p.getDescripcion());
		form.setEstado(p.getEstado());
		form.setPropiedadId(p.getPropiedad().getId());
		return form;
	}

	@Override
    @Transactional(readOnly = true)
	public List<Publicacion> buscarConFiltros(PublicacionesBuscarForm filtros) {
	    // Trae de manera directa todas las publicaciones vigentes (HU 2.4)
	    return publicacionRepo.findByEliminadaFalse();
	}
}