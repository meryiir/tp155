package ma.projet.graph.controllers;

import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.repositories.CompteRepository;
import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {
    private final CompteRepository compteRepository;
    private final TransactionRepository transactionRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte %s not found", id)));
    }

    @MutationMapping
    public Compte saveCompte(@Argument Map<String, Object> compteInput) throws ParseException {
        Compte compte = new Compte();

        if (compteInput.containsKey("solde")) {
            compte.setSolde(Double.parseDouble(compteInput.get("solde").toString()));
        }

        if (compteInput.containsKey("dateCreation")) {
            Date dateCreation = dateFormat.parse(compteInput.get("dateCreation").toString());
            compte.setDateCreation(dateCreation);
        } else {
            compte.setDateCreation(new Date());
        }

        if (compteInput.containsKey("type")) {
            compte.setType(ma.projet.graph.entities.TypeCompte.valueOf(compteInput.get("type").toString()));
        }

        return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        Double sum = compteRepository.sumSoldes();
        double sumValue = (sum != null) ? sum : 0.0;
        double average = (count > 0) ? sumValue / count : 0;

        return Map.of(
                "count", count,
                "sum", sumValue,
                "average", average
        );
    }

    // Méthodes pour les transactions (Étape 8)

    @MutationMapping
    public Transaction addTransaction(@Argument Map<String, Object> transactionInput) throws ParseException {
        Transaction transaction = new Transaction();

        if (transactionInput.containsKey("compteId")) {
            Long compteId = Long.parseLong(transactionInput.get("compteId").toString());
            Compte compte = compteRepository.findById(compteId)
                    .orElseThrow(() -> new RuntimeException("Compte not found"));
            transaction.setCompte(compte);

            // Mise à jour du solde du compte
            if (transactionInput.containsKey("montant") && transactionInput.containsKey("type")) {
                double montant = Double.parseDouble(transactionInput.get("montant").toString());
                String type = transactionInput.get("type").toString();

                if ("DEPOT".equals(type)) {
                    compte.setSolde(compte.getSolde() + montant);
                } else if ("RETRAIT".equals(type)) {
                    compte.setSolde(compte.getSolde() - montant);
                }
                compteRepository.save(compte);
            }
        }

        if (transactionInput.containsKey("montant")) {
            transaction.setMontant(Double.parseDouble(transactionInput.get("montant").toString()));
        }

        if (transactionInput.containsKey("date")) {
            Date date = dateFormat.parse(transactionInput.get("date").toString());
            transaction.setDate(date);
        } else {
            transaction.setDate(new Date());
        }

        if (transactionInput.containsKey("type")) {
            transaction.setType(ma.projet.graph.entities.TypeTransaction
                    .valueOf(transactionInput.get("type").toString()));
        }

        return transactionRepository.save(transaction);
    }

    @QueryMapping
    public List<Transaction> compteTransactions(@Argument Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte not found"));
        return transactionRepository.findByCompte(compte);
    }

    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count();
        Double sumDepots = transactionRepository.sumByType(ma.projet.graph.entities.TypeTransaction.DEPOT);
        Double sumRetraits = transactionRepository.sumByType(ma.projet.graph.entities.TypeTransaction.RETRAIT);

        return Map.of(
                "count", count,
                "sumDepots", (sumDepots != null) ? sumDepots : 0.0,
                "sumRetraits", (sumRetraits != null) ? sumRetraits : 0.0
        );
    }
}