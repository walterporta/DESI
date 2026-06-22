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
@RequestMapping("/publicacionEditar")
public class PublicacionRegistrarEditarController {

    @Autowired
    private PublicacionService servicioPublicacion;

    @Autowired
    private PropiedadService servicioPropiedad;
     
    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.GET)
    public String preparaForm(Model modelo, @PathVariable("id") Optional<Long> id) throws Exception {
        if (id.isPresent()) {
            // Caso Editar: Recuperamos la publicación y la convertimos al FormBean
            PublicacionForm form = servicioPublicacion.buscarPorIdParaForm(id.get());
            modelo.addAttribute("formBean", form);
        } else {
            // Caso Registrar: Enviamos un formulario vacío para completar los datos
            modelo.addAttribute("formBean", new PublicacionForm());
        }
        return "publicacionEditar";
    }
     
    /**
     * Mapea todas las propiedades activas de la base de datos para cargar 
     * el combo/select de selección de propiedades en la vista HTML.
     */
    @ModelAttribute("allPropiedades")
    public List<Propiedad> getAllPropiedades() {
        return this.servicioPropiedad.getAll(); // Ajustá al método que liste propiedades de tu servicio
    }

    /**
     * Mapea los valores del Enum para poder elegir el estado (ACTIVA, FINALIZADA, etc.)
     */
    @ModelAttribute("allEstados")
    public EstadoPublicacion[] getAllEstados() {
        return EstadoPublicacion.values();
    }
    
    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public String deleteById(Model model, @PathVariable("id") Long id) {
        try {
            servicioPublicacion.eliminarLogicamente(id);
        } catch (Exception e) {
            // Si la eliminación lógica falla por regla de negocio, se podría enviar el error a la grilla
            return "redirect:/publicacionesBuscar?error=" + e.getMessage();
        }
        return "redirect:/publicacionesBuscar";
    }
 
    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute("formBean") @Valid PublicacionForm formBean, 
                         BindingResult result, ModelMap modelo, @RequestParam String action) {
        
        if (action.equals("actionAceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "publicacionEditar";
            } else {
                try {
                    // Si el form tiene ID, corresponde a una modificación. Si no, es un alta nueva.
                    if (formBean.getId() != null) {
                        servicioPublicacion.modificar(formBean);
                    } else {
                        servicioPublicacion.guardar(formBean);
                    }
                    
                    return "redirect:/publicacionesBuscar";
                    
                } catch (EntidadNoEncontradaException e) {
                    // Manejo específico para cuando el ID de propiedad o publicación no existe
                    ObjectError error = new ObjectError("globalError", e.getMessage());
                    result.addError(error);
                    modelo.addAttribute("formBean", formBean);
                    return "publicacionEditar";

                } catch (Excepcion e) {
                    // Manejo de reglas de negocio usando el atributo de pantalla involucrado (ej: "propiedadId")
                    if (e.getAtributo() == null) {
                        ObjectError error = new ObjectError("globalError", e.getMessage());
                        result.addError(error);
                    } else {
                        FieldError errorField = new FieldError("formBean", e.getAtributo(), e.getMessage());
                        result.addError(errorField);
                    }
                    modelo.addAttribute("formBean", formBean);
                    return "publicacionEditar"; 
                } catch (Exception e) {
                    // Captura genérica de cualquier otro error inesperado
                    ObjectError error = new ObjectError("globalError", "Ocurrió un error inesperado: " + e.getMessage());
                    result.addError(error);
                    modelo.addAttribute("formBean", formBean);
                    return "publicacionEditar";
                }
            }
        } else if (action.equals("actionCancelar")) {
            modelo.clear();
            return "redirect:/publicacionesBuscar";
        }
            
        return "redirect:/";
    }
}