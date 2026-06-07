package tuti.desi.accesoDatos;
/**
 *
 * @author Nico
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.EstadoDisponibilidad;
import tuti.desi.entidades.Propiedad;
import tuti.desi.entidades.TipoPropiedad;

@Repository
public interface IPropiedadRepo extends JpaRepository<Propiedad, Long> {

    // Para validar duplicados en alta (misma dirección y ciudad, no eliminada)
    boolean existsByDireccionAndCiudadAndEliminadaFalse(String direccion, String ciudad);

    // Para validar duplicados en modificación (excluyendo la misma propiedad)
    boolean existsByDireccionAndCiudadAndEliminadaFalseAndIdNot(String direccion, String ciudad, Long id);

    // Filtros para el listado
    @Query("""
            SELECT p FROM Propiedad p
            WHERE p.eliminada = false
              AND (:direccion IS NULL OR LOWER(p.direccion) LIKE LOWER(CONCAT('%', :direccion, '%')))
              AND (:ciudad IS NULL OR LOWER(p.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
              AND (:tipo IS NULL OR p.tipo = :tipo)
              AND (:estado IS NULL OR p.estadoDisponibilidad = :estado)
            """)
    List<Propiedad> filter(
            @Param("direccion") String direccion,
            @Param("ciudad") String ciudad,
            @Param("tipo") TipoPropiedad tipo,
            @Param("estado") EstadoDisponibilidad estado);
}