package tuti.desi.presentacion.facturas;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.Persona;
import tuti.desi.entidades.Propiedad;
import tuti.desi.servicios.ContratoService;
import tuti.desi.servicios.FacturaService;
import tuti.desi.servicios.PersonaService;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/facturasBuscar")
public class FacturasBuscarController {

    @Autowired
    private FacturaService service;

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private PersonaService personaService;

    @RequestMapping(method = RequestMethod.GET)
    public String buscar(Model modelo, @ModelAttribute("filterBean") FacturasBuscarForm filter) {
        List<Factura> facturas = service.filter(filter);
        modelo.addAttribute("facturas", facturas);
        modelo.addAttribute("filterBean", filter);
        return "facturasBuscar";
    }

    @ModelAttribute("allContratos")
    public List<Contrato> getAllContratos() {
        return contratoService.getAll().stream()
                .filter(c -> c.getEliminada() == null || !c.getEliminada())
                .collect(Collectors.toList());
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
    public EstadoFactura[] getAllEstados() {
        return EstadoFactura.values();
    }
}
