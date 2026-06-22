package tuti.desi.presentacion.publicaciones; // Asegurate de usar tu package exacto de controladores

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tuti.desi.entidades.Publicacion;
import tuti.desi.servicios.PublicacionService; // Ajustá al package exacto de tu interfaz de servicio

@Controller
@RequestMapping("/publicacionesBuscar")
public class PublicacionesBuscarController {

    private final PublicacionService publicacionService;

    @Autowired
    public PublicacionesBuscarController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    /**
     * HU 2.4 - Prepara la pantalla de búsqueda y carga el listado inicial de publicaciones vigentes.
     * Se ejecuta cuando el usuario ingresa a la URL: http://localhost:8080/publicacionesBuscar
     */
    @GetMapping
    public String prepararBusqueda(Model model) {
        // 1. Enviamos un objeto vacío del formulario para que Thymeleaf mapee los inputs sin fallar (Error 500)
        model.addAttribute("publicacionesBuscarForm", new PublicacionesBuscarForm());
        
        // 2. Cargamos el listado inicial de las publicaciones vigentes (Opción A)
        List<Publicacion> publicacionesVigentes = publicacionService.buscarConFiltros(null);
        model.addAttribute("publicaciones", publicacionesVigentes);
        
        // 3. Retornamos el nombre exacto de tu archivo HTML
        return "publicacionesBuscar";
    }

    /**
     * HU 2.4 - Recibe la acción de filtrar desde la interfaz. 
     * Al usar la Opción A, mantiene el listado completo de registros no eliminados.
     */
    @PostMapping
    public String buscar(@ModelAttribute("publicacionesBuscarForm") PublicacionesBuscarForm form, Model model) {
        // Al pasarle el formulario, nos sigue devolviendo la lista segura sin romper la app por las dependencias eliminadas
        List<Publicacion> publicacionesVigentes = publicacionService.buscarConFiltros(form);
        model.addAttribute("publicaciones", publicacionesVigentes);
        
        return "publicacionesBuscar";
    }
}