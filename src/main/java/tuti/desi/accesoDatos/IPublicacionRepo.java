package tuti.desi.accesoDatos; // Acuérdate de mantener tu package exacto de persistencia

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tuti.desi.entidades.EstadoPublicacion;
import tuti.desi.entidades.Publicacion;

@Repository
public interface IPublicacionRepo extends JpaRepository<Publicacion, Long> {
    
    // 1. REQUERIDO PARA VALIDACIÓN: Arregla el error de compilación actual en el ServiceImpl
    boolean existsByPropiedadIdAndEstadoAndEliminada(Long propiedadId, EstadoPublicacion estado, boolean eliminada);
    
    // 2. REQUERIDO PARA EL LISTADO (Opción A): Trae todas las publicaciones que no fueron borradas lógicamente
    List<Publicacion> findByEliminadaFalse();
}