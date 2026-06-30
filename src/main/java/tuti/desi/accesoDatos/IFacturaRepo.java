package tuti.desi.accesoDatos;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.EstadoFactura;
import tuti.desi.entidades.Factura;

@Repository
public interface IFacturaRepo extends JpaRepository<Factura, Long> {

    @Query("""
            SELECT f FROM Factura f
            WHERE f.eliminada = false
              AND (:contratoId IS NULL OR f.contrato.id = :contratoId)
              AND (:propiedadId IS NULL OR f.contrato.propiedad.id = :propiedadId)
              AND (:inquilinoId IS NULL OR f.contrato.inquilino.id = :inquilinoId)
              AND (:estado IS NULL OR f.estadoFactura = :estado)
              AND (:vencimientoDesde IS NULL OR f.fechaVencimiento >= :vencimientoDesde)
              AND (:vencimientoHasta IS NULL OR f.fechaVencimiento <= :vencimientoHasta)
            """)
    List<Factura> filter(
            @Param("contratoId") Long contratoId,
            @Param("propiedadId") Long propiedadId,
            @Param("inquilinoId") Long inquilinoId,
            @Param("estado") EstadoFactura estado,
            @Param("vencimientoDesde") LocalDate vencimientoDesde,
            @Param("vencimientoHasta") LocalDate vencimientoHasta);
}
