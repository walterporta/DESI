package tuti.desi.presentacion.contrato;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.servicios.ContratoService;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/contratosBuscar")
public class ContratosBuscarController {

    @Autowired
    private ContratoService service;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @RequestMapping(method = RequestMethod.GET)
    public String buscar(Model modelo,
            @ModelAttribute("filterBean") ContratosBuscarForm filter) {
        List<Contrato> contratos = service.filter(filter);
        modelo.addAttribute("contratos", contratos);
        modelo.addAttribute("filterBean", filter);
        return "contratosBuscar";
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
}