package tuti.desi.presentacion.propiedades;
/**
 *
 * @author Nico
 */
import jakarta.validation.Valid;
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
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/propiedadesEditar")
public class PropiedadesEditarController {

    @Autowired
    private PropiedadService service;

    @Autowired
    private PersonaService servicePersona;

    @RequestMapping(path = {"", "/{id}"}, method = RequestMethod.GET)
    public String preparaForm(Model modelo,
            @PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            Propiedad p = service.getPropiedadById(id.get());
            modelo.addAttribute("formBean", new PropiedadForm(p));
        } else {
            modelo.addAttribute("formBean", new PropiedadForm());
        }
        return "propiedadesEditar";
    }

    @ModelAttribute("allPropietarios")
    public List<Persona> getAllPropietarios() {
        return servicePersona.getAll();
    }

    @ModelAttribute("allTipos")
    public TipoPropiedad[] getAllTipos() {
        return TipoPropiedad.values();
    }

    @ModelAttribute("allEstados")
    public EstadoDisponibilidad[] getAllEstados() {
        return EstadoDisponibilidad.values();
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public String deleteById(Model modelo, @PathVariable("id") Long id) {
        try {
            service.deletePropiedadById(id);
        } catch (Excepcion e) {
            modelo.addAttribute("errorEliminar", e.getMessage());
            List<Propiedad> propiedades = service.getAll();
            modelo.addAttribute("propiedades", propiedades);
            return "propiedadesBuscar";
        }
        return "redirect:/propiedadesBuscar";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(
            @ModelAttribute("formBean") @Valid PropiedadForm formBean,
            BindingResult result, ModelMap modelo,
            @RequestParam String action) {

        if (action.equals("Aceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "propiedadesEditar";
            }
            try {
                Propiedad p = formBean.toPojo();
                if (formBean.getIdPropietario() != null) {
                    p.setPropietario(servicePersona.getPersonaById(
                            formBean.getIdPropietario()));
                }
                service.save(p);
                return "redirect:/propiedadesBuscar";
            } catch (Excepcion e) {
                if (e.getAtributo() == null) {
                    result.addError(new ObjectError("globalError", e.getMessage()));
                } else {
                    result.addError(new FieldError("formBean",
                            e.getAtributo(), e.getMessage()));
                }
                modelo.addAttribute("formBean", formBean);
                return "propiedadesEditar";
            }
        }

        if (action.equals("Cancelar")) {
            modelo.clear();
            return "redirect:/propiedadesBuscar";
        }

        return "redirect:/";
    }
}