package tuti.desi.servicios;

import java.util.List;
import tuti.desi.entidades.Contrato;
import tuti.desi.excepciones.Excepcion;
import tuti.desi.presentacion.contratos.ContratosBuscarForm;

public interface ContratoService {
    List<Contrato> getAll();

    List<Contrato> filter(ContratosBuscarForm filter);

    void save(Contrato contrato) throws Excepcion;

    Contrato getContratoById(Long id);

    void deleteContratoById(Long id) throws Excepcion;
}
