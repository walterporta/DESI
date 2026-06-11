package tuti.desi.accesoDatos;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.Contrato;
import tuti.desi.entidades.EstadoContrato;

@Repository
public interface IContratoRepo extends JpaRepository<Contrato, Long> {

    // Validar si existe un contrato activo para una propiedad (excluyendo el
    // contrato actual para ediciones)
    boolean existsByPropiedadIdAndEstadoContratoAndEliminadaFalse(Long propiedadId, EstadoContrato estado);

    boolean existsByPropiedadIdAndEstadoContratoAndEliminadaFalseAndIdNot(Long propiedadId, EstadoContrato estado,
            Long id);

    // Consulta de filtrado para el listado de contratos
    @Query("""
            SELECT c FROM Contrato c
            WHERE c.eliminada = false
              AND (:propiedadId IS NULL OR c.propiedad.id = :propiedadId)
              AND (:inquilinoId IS NULL OR c.inquilino.id = :inquilinoId)
              AND (:estado IS NULL OR c.estadoContrato = :estado)
              AND (:fechaInicio IS NULL OR c.fechaInicio = :fechaInicio)
            """)
    List<Contrato> filter(
            @Param("propiedadId") Long propiedadId,
            @Param("inquilinoId") Long inquilinoId,
            @Param("estado") EstadoContrato estado,
            @Param("fechaInicio") LocalDate fechaInicio);
}
