package tuti.desi.servicios;

import java.util.List;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.Factura;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.facturas.FacturasBuscarForm;

public interface FacturaService {

    List<Factura> getAll();

    List<Factura> filter(FacturasBuscarForm filter);

    List<Contrato> getContratosActivos();

    void save(Factura factura) throws Excepcion;

    Factura getFacturaById(Long id);

    void deleteFacturaById(Long id) throws Excepcion;
}
