package com.campeggio.config;

import com.campeggio.accommodations.entity.*;
import com.campeggio.accommodations.repository.AccommodationRepository;
import com.campeggio.rentals.entity.ArticoloNoleggio;
import com.campeggio.rentals.repository.ArticoloNoleggioRepository;
import com.campeggio.users.entity.*;
import com.campeggio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final AccommodationRepository accommodationRepo;
    private final ArticoloNoleggioRepository articoloRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUtenti();
        seedAlloggi();
        seedArticoliNoleggio();
    }

    // ─── UTENTI ───────────────────────────────────────────────────────────────

    private void seedUtenti() {
        if (userRepo.existsByEmail("admin@campeggio.it")) {
            log.info("DataInitializer: utenti già presenti — skip");
            return;
        }

        // Admin
        Admin admin = new Admin();
        admin.setEmail("admin@campeggio.it");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setName("Marco");
        admin.setSurname("Direttore");
        userRepo.save(admin);

        // Staff reception
        Staff staff = new Staff();
        staff.setEmail("reception@campeggio.it");
        staff.setPassword(passwordEncoder.encode("Staff123!"));
        staff.setName("Laura");
        staff.setSurname("Receptionist");
        staff.setDepartment(Staff.Department.RECEPTION);
        userRepo.save(staff);

        // Staff bar
        Staff barista = new Staff();
        barista.setEmail("bar@campeggio.it");
        barista.setPassword(passwordEncoder.encode("Staff123!"));
        barista.setName("Giulio");
        barista.setSurname("Barman");
        barista.setDepartment(Staff.Department.BAR);
        userRepo.save(barista);

        // Ospite di esempio
        Ospite ospite = new Ospite();
        ospite.setEmail("ospite@example.com");
        ospite.setPassword(passwordEncoder.encode("Ospite123!"));
        ospite.setName("Mario");
        ospite.setSurname("Rossi");
        ospite.setPhone("+39 333 1234567");
        ospite.setNationality("Italiana");
        ospite.setBirthDate(LocalDate.of(1985, 6, 15));
        ospite.setDocumentNumber("AB1234567");
        userRepo.save(ospite);

        log.info("DataInitializer: creati 4 utenti (admin, 2 staff, 1 ospite)");
    }

    // ─── ALLOGGI ──────────────────────────────────────────────────────────────

    private void seedAlloggi() {
        if (accommodationRepo.count() > 0) {
            log.info("DataInitializer: alloggi già presenti — skip");
            return;
        }

        // ── 10 CHALET ─────────────────────────────────────────────────────────
        String[] chaletNomi = {
            "Chalet 1", "Chalet 2", "Chalet 3", "Chalet 4",
            "Chalet 5", "Chalet 6", "Chalet 7", "Chalet 8",
            "Chalet 9", "Chalet 10"
        };
        int[] chaletPosti = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4};
        double[] chaletPrezzi = {80, 80, 80, 80, 80, 80, 80, 80, 80, 80};

        for (int i = 0; i < 10; i++) {
            Bungalow chalet = new Bungalow();
            chalet.setName(chaletNomi[i]);
            chalet.setDescription("Chalet dotato di veranda privata, aria condizionata e tutti i comfort");
            chalet.setPricePerNight(BigDecimal.valueOf(chaletPrezzi[i]));
            chalet.setMaxCapacity(chaletPosti[i]);
            chalet.setRooms(chaletPosti[i] > 4 ? 3 : 2);
            chalet.setBeds(chaletPosti[i]);
            chalet.setHasBathroom(true);
            chalet.setHasKitchen(true);
            accommodationRepo.save(chalet);
        }

        // ── 67 PIAZZOLE CLIENTI FISSI ─────────────────────────────────────────
        for (int i = 1; i <= 67; i++) {
            PiazzolaFissa pf = new PiazzolaFissa();
            pf.setName(String.format("PF-%03d", i));
            pf.setDescription("Piazzola fissa per cliente abituale con contratto annuale");
            pf.setPricePerNight(BigDecimal.ZERO);   // gestita con quota annuale
            pf.setMaxCapacity(4);
            pf.setAnnualFee(BigDecimal.valueOf(1_200.00 + (i % 5) * 100)); // 2200 €
            pf.setHasPrivateEntrance(i % 3 == 0);  // ogni 3 piazzole ha ingresso privato
            pf.setContractStart(LocalDate.of(2025, 1, 1));
            pf.setContractEnd(LocalDate.of(2025, 12, 31));
            accommodationRepo.save(pf);
        }

        // ── 7 PIAZZOLE CAMPER / TENDA (transito stagionale) ───────────────────
        for (int i = 1; i <= 7; i++) {
            Piazzola p = new Piazzola();
            boolean isCamper = i <= 4;
            p.setName(isCamper ? "CT-" + i : "TN-" + (i - 4));
            p.setDescription(isCamper
                    ? "Piazzola per camper con allaccio elettrico e presa d'acqua"
                    : "Piazzola ombreggiata per tende, vicina ai servizi");
            p.setPricePerNight(BigDecimal.valueOf(isCamper ? 32.00 : 22.00));
            p.setMaxCapacity(isCamper ? 4 : 3);
            p.setTipoPiazzola(isCamper ? Piazzola.TipoPiazzola.CAMPER : Piazzola.TipoPiazzola.TENDA);
            p.setSurfaceM2(isCamper ? 50.0 : 28.0);
            p.setHasElectricity(isCamper);
            p.setHasWater(isCamper);
            accommodationRepo.save(p);
        }

        // ── 5 PIAZZOLE CAMPER STOP (sosta breve) ─────────────────────────────
        for (int i = 1; i <= 5; i++) {
            Piazzola p = new Piazzola();
            p.setName("CS-" + i);
            p.setDescription("Piazzola camper stop per soste brevi — max 48h, colonnina elettrica 220V e presa d'acqua");
            p.setPricePerNight(BigDecimal.valueOf(18.00));
            p.setMaxCapacity(2);
            p.setTipoPiazzola(Piazzola.TipoPiazzola.CAMPER);
            p.setSurfaceM2(35.0);
            p.setHasElectricity(true);
            p.setHasWater(false);
            accommodationRepo.save(p);
        }

        log.info("DataInitializer: creati 89 alloggi — 10 chalet, 67 piazzole fisse, 7 camper/tenda, 5 camper stop");
    }

    // ─── ARTICOLI NOLEGGIO ────────────────────────────────────────────────────

    private void seedArticoliNoleggio() {
        if (articoloRepo.count() > 0) {
            log.info("DataInitializer: articoli noleggio già presenti — skip");
            return;
        }

        Object[][] articoli = {
            {"Bicicletta adulto",      "City bike 26\" con casco incluso",              8.00,  15},
            {"Bicicletta bambino",     "Bici 20\" con rotelle e casco",                 5.00,   8},
            {"E-Bike",                 "Bicicletta elettrica 36V, autonomia 60km",     15.00,   4},
            {"Ombrellone mare",        "Ombrellone Ø 200cm con palo e base",            5.00,  25},
            {"Sdraio",                 "Sdraio reclinabile con poggiatesta",            3.00,  40},
            {"Lettino da spiaggia",    "Lettino pieghevole con materassino",            4.00,  20},
            {"Tavolo da campeggio",    "Tavolo alluminio pieghevole 80×120cm",          4.00,  10},
            {"Sedie da campeggio (×4)","Set 4 sedie pieghevoli con borsa",              4.00,  12},
            {"Tenda da sole",          "Tenda parasole 3×3m con picchetti",             6.00,   8},
            {"Frigorifero portatile",  "Frigo elettrico 25L 12/220V",                  10.00,   6},
        };

        for (Object[] row : articoli) {
            ArticoloNoleggio a = new ArticoloNoleggio();
            a.setNome((String) row[0]);
            a.setDescrizione((String) row[1]);
            a.setPrezzoGiornaliero(BigDecimal.valueOf((Double) row[2]));
            a.setQuantitaDisponibile((Integer) row[3]);
            articoloRepo.save(a);
        }

        log.info("DataInitializer: creati {} articoli noleggio", articoli.length);
    }
}
