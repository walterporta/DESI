package tuti.desi.presentacion.contrato;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.ContratoService;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/contratosEditar")
public class ContratosEditarController {

    @Autowired
    private ContratoService service;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.GET)
    public String preparaForm(Model modelo,
            @PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            Contrato c = service.getContratoById(id.get());
            modelo.addAttribute("formBean", new ContratoForm(c));
        } else {
            modelo.addAttribute("formBean", new ContratoForm());
        }
        return "contratosEditar";
    }

    @ModelAttribute("allPropiedades")
    public List<Propiedad> getAllPropiedades() {
        return propiedadService.getAll().stream()
                .filter(p -> p.getEliminada() == null || !p.getEliminada())
                .collect(Collectors.toList());
    }

    @ModelAttribute("allInquilinos")
    public List<Persona> getAllInquilinos() {
        return personaService.getAll();
    }

    @ModelAttribute("allEstados")
    public EstadoContrato[] getAllEstados() {
        return EstadoContrato.values();
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public String deleteById(Model modelo, @PathVariable("id") Long id) {
        try {
            service.deleteContratoById(id);
        } catch (Excepcion e) {
            modelo.addAttribute("errorEliminar", e.getMessage());
            List<Contrato> contratos = service.filter(new ContratosBuscarForm());
            modelo.addAttribute("contratos", contratos);
            modelo.addAttribute("filterBean", new ContratosBuscarForm());
            return "contratosBuscar";
        }
        return "redirect:/contratosBuscar";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(
            @ModelAttribute("formBean") @Valid ContratoForm formBean,
            BindingResult result, ModelMap modelo,
            @RequestParam String action) {

        if (action.equals("Aceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "contratosEditar";
            }
            try {
                Contrato c = formBean.toPojo();
                if (formBean.getIdPropiedad() != null) {
                    c.setPropiedad(propiedadService.getPropiedadById(formBean.getIdPropiedad()));
                }
                if (formBean.getIdInquilino() != null) {
                    c.setInquilino(personaService.getPersonaById(formBean.getIdInquilino()));
                }
                service.save(c);
                return "redirect:/contratosBuscar";
            } catch (Excepcion e) {
                if (e.getAtributo() == null) {
                    result.addError(new ObjectError("globalError", e.getMessage()));
                } else {
                    result.addError(new FieldError("formBean",
                            e.getAtributo(), e.getMessage()));
                }
                modelo.addAttribute("formBean", formBean);
                return "contratosEditar";
            }
        }

        if (action.equals("Cancelar")) {
            modelo.clear();
            return "redirect:/contratosBuscar";
        }

        return "redirect:/";
    }
}