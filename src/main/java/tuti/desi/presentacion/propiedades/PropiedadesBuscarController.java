package tuti.desi.presentacion.propiedades;
/**
 *
 * @author Nico
 */
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;
import tuti.desi.servicios.PropiedadService;

@Controller
@RequestMapping("/propiedadesBuscar")
public class PropiedadesBuscarController {

    @Autowired
    private PropiedadService service;

    @RequestMapping(method = RequestMethod.GET)
    public String buscar(Model modelo,
            @ModelAttribute("filterBean") PropiedadesBuscarForm filter) {
        List<Propiedad> propiedades = service.filter(filter);
        modelo.addAttribute("propiedades", propiedades);
        modelo.addAttribute("filterBean", filter);
        return "propiedadesBuscar";
    }

    @ModelAttribute("allTipos")
    public TipoPropiedad[] getAllTipos() {
        return TipoPropiedad.values();
    }

    @ModelAttribute("allEstados")
    public EstadoDisponibilidad[] getAllEstados() {
        return EstadoDisponibilidad.values();
    }
}