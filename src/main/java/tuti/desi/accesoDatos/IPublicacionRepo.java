package tuti.desi.accesoDatos;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.entidades.Publicacion;

@Repository
public interface IPublicacionRepo extends JpaRepository<Publicacion, Long> {
    
    boolean existsByPropiedadIdAndEstadoAndEliminada(Long propiedadId, EstadoPublicacion estado, boolean eliminada);
    
    List<Publicacion> findByEliminadaFalse();

    @Query("SELECT p FROM Publicacion p JOIN p.propiedad prop WHERE p.eliminada = false " +
           "AND (:propiedad IS NULL OR :propiedad = '' OR LOWER(prop.direccion) LIKE LOWER(CONCAT('%', :propiedad, '%'))) " +
           "AND (:ciudad IS NULL OR :ciudad = '' OR LOWER(prop.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) " +
           "AND (:estado IS NULL OR p.estado = :estado) " +
           "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> buscarConFiltrosAvanzados(
            @Param("propiedad") String propiedad,
            @Param("ciudad") String ciudad,
            @Param("estado") EstadoPublicacion estado,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );
}