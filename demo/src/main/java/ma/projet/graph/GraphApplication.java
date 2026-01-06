package ma.projet.graph;

import lombok.extern.slf4j.Slf4j;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.TypeCompte;
import ma.projet.graph.repositories.CompteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
@Slf4j
public class GraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphApplication.class, args);
    }

    @Bean
    CommandLineRunner start(CompteRepository compteRepository) {
        return args -> {
            // Création de comptes initiaux pour les tests
            compteRepository.save(new Compte(null, 8271.796491312965, new Date(), TypeCompte.EPARGNE));
            compteRepository.save(new Compte(null, 3672.765137493308, new Date(), TypeCompte.COURANT));
            compteRepository.save(new Compte(null, 422.171034829543, new Date(), TypeCompte.EPARGNE));
        };
    }
}