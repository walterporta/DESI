package tuti.desi.presentacion.publicaciones;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.excepciones.EntidadNoEncontradaException;
import tuti.desi.servicios.PublicacionService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionRegistrarEditarController {

    @Autowired
    private PublicacionService servicioPublicacion;

    @Autowired
    private PropiedadService servicioPropiedad;
     
    /**
     * primero muestro el formulario vacío para registrar una nueva publicación.
     * URL: GET /publicaciones/registrar
     */
    @RequestMapping(path = "/registrar", method = RequestMethod.GET)
    public String prepararRegistroForm(Model modelo) {
        // Inicializo el formulario vacío. Coincide con th:object="${publicacionForm}"
        modelo.addAttribute("publicacionForm", new PublicacionForm());
        return "publicacionesRegistrar";
    }
     
    /**
     * Muestro el formulario cargado con los datos existentes para editar.
     * URL: GET /publicaciones/editar/{id}
     */
    @RequestMapping(path = "/editar/{id}", method = RequestMethod.GET)
    public String prepararEditarForm(Model modelo, @PathVariable("id") Long id) throws Exception {
        // Recupero los datos de la base de datos y los mapeo al Form Object
        PublicacionForm form = servicioPublicacion.buscarPorIdParaForm(id);
        modelo.addAttribute("publicacionForm", form);
        return "publicacionesRegistrar"; 
    }
     
    /**
     * Mapeo todas las propiedades activas de la base de datos para cargar 
     * el combo/select de selección de propiedades en la vista HTML.
     */
    @ModelAttribute("allPropiedades")
    public List<Propiedad> getAllPropiedades() {
        return this.servicioPropiedad.getAll(); 
    }

    /**
     * Mapeo los valores del Enum para poder elegir el estado (ACTIVA, FINALIZADA, etc.)
     */
    @ModelAttribute("allEstados")
    public EstadoPublicacion[] getAllEstados() {
        return EstadoPublicacion.values();
    }
    
    /**
     * Realizo la eliminación lógica desde el botón del listado.
     * URL: GET /publicaciones/eliminar/{id} (Alineado con el enlace <a> de la búsqueda)
     */
    @RequestMapping(path = "/eliminar/{id}", method = RequestMethod.GET)
    public String deleteById(Model model, @PathVariable("id") Long id) {
        try {
            servicioPublicacion.eliminarLogicamente(id);
        } catch (Exception e) {
            // Envío el error como parámetro a la URL de búsqueda si algo falla
            return "redirect:/publicacionesBuscar?error=" + e.getMessage();
        }
        return "redirect:/publicacionesBuscar";
    }
 
    /**
     * Proceso las acciones de "Aceptar" (Guardar/Modificar) o "Cancelar" enviadas desde el formulario.
     * URL: POST /publicaciones/registrar
     */
    @RequestMapping(path = "/registrar", method = RequestMethod.POST)
    public String submit(@ModelAttribute("publicacionForm") @Valid PublicacionForm formBean, 
                         BindingResult result, ModelMap modelo, @RequestParam String action) {
        
        if (action.equals("actionAceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("publicacionForm", formBean);
                return "publicacionesRegistrar";
            } else {
                try {
                    // Si posee ID es porque proviene del flujo "/editar/{id}" -> Modificación
                    if (formBean.getId() != null) {
                        servicioPublicacion.modificar(formBean);
                    } else {
                        // Si el ID es nulo, proviene del flujo "/registrar" -> Alta nueva
                        servicioPublicacion.guardar(formBean);
                    }
                    
                    return "redirect:/publicacionesBuscar";
                    
                } catch (EntidadNoEncontradaException e) {
                    ObjectError error = new ObjectError("globalError", e.getMessage());
                    result.addError(error);
                    modelo.addAttribute("publicacionForm", formBean);
                    return "publicacionesRegistrar";

                } catch (Excepcion e) {
                    if (e.getAtributo() == null) {
                        ObjectError error = new ObjectError("globalError", e.getMessage());
                        result.addError(error);
                    } else {
                        FieldError errorField = new FieldError("publicacionForm", e.getAtributo(), e.getMessage());
                        result.addError(errorField);
                    }
                    modelo.addAttribute("publicacionForm", formBean);
                    return "publicacionesRegistrar"; 
                } catch (Exception e) {
                    ObjectError error = new ObjectError("globalError", "Ocurrió un error inesperado: " + e.getMessage());
                    result.addError(error);
                    modelo.addAttribute("publicacionForm", formBean);
                    return "publicacionesRegistrar";
                }
            }
        } else if (action.equals("actionCancelar")) {
            modelo.clear();
            return "redirect:/publicacionesBuscar";
        }
            
        return "redirect:/";
    }
}