package ma.projet.graph.repositories;

import ma.projet.graph.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    @Query("SELECT SUM(c.solde) FROM Compte c")
    Double sumSoldes();
}