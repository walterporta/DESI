package tuti.desi.presentacion.facturas;

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
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;
import tuti.desi.entidades.MedioPago;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.servicios.ContratoService;
import tuti.desi.servicios.FacturaService;

@Controller
@RequestMapping("/facturasEditar")
public class FacturasEditarController {

    @Autowired
    private FacturaService service;

    @Autowired
    private ContratoService contratoService;

    @RequestMapping(path = { "", "/{id}" }, method = RequestMethod.GET)
    public String preparaForm(Model modelo, @PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            Factura f = service.getFacturaById(id.get());
            modelo.addAttribute("formBean", new FacturaForm(f));
        } else {
            modelo.addAttribute("formBean", new FacturaForm());
        }
        return "facturasEditar";
    }

    @ModelAttribute("contratosActivos")
    public List<Contrato> getContratosActivos() {
        return service.getContratosActivos();
    }

    @ModelAttribute("allEstados")
    public EstadoFactura[] getAllEstados() {
        return EstadoFactura.values();
    }

    @ModelAttribute("allMediosPago")
    public MedioPago[] getAllMediosPago() {
        return MedioPago.values();
    }

    @RequestMapping(path = "/delete/{id}", method = RequestMethod.POST)
    public String deleteById(Model modelo, @PathVariable("id") Long id) {
        try {
            service.deleteFacturaById(id);
        } catch (Excepcion e) {
            modelo.addAttribute("errorEliminar", e.getMessage());
            modelo.addAttribute("facturas", service.filter(new FacturasBuscarForm()));
            modelo.addAttribute("filterBean", new FacturasBuscarForm());
            return "facturasBuscar";
        }
        return "redirect:/facturasBuscar";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(
            @ModelAttribute("formBean") @Valid FacturaForm formBean,
            BindingResult result,
            ModelMap modelo,
            @RequestParam String action) {

        if (action.equals("Aceptar")) {
            if (result.hasErrors()) {
                modelo.addAttribute("formBean", formBean);
                return "facturasEditar";
            }
            try {
                Factura f = formBean.toPojo();
                if (formBean.getIdContrato() != null) {
                    f.setContrato(contratoService.getContratoById(formBean.getIdContrato()));
                }
                service.save(f);
                return "redirect:/facturasBuscar";
            } catch (Excepcion e) {
                if (e.getAtributo() == null) {
                    result.addError(new ObjectError("globalError", e.getMessage()));
                } else {
                    result.addError(new FieldError("formBean", e.getAtributo(), e.getMessage()));
                }
                modelo.addAttribute("formBean", formBean);
                return "facturasEditar";
            }
        }

        if (action.equals("Cancelar")) {
            modelo.clear();
            return "redirect:/facturasBuscar";
        }

        return "redirect:/";
    }
}
