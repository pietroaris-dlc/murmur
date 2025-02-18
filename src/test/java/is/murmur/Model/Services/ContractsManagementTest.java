package is.murmur.Model.Services;

import is.murmur.Model.Beans.Contract;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ContractsManagementTest {

//    @Test
//    void sendOffer() {
//
//        }

    //    @Test
    //public void testNullInput(){
    //    // 1. Creiamo l'oggetto da testare
    //    ContractsManagement contractManagement = new ContractsManagement();

    //    // 2. Eseguiamo il metodo con un parametro nullo
    //    //    e verifichiamo che lanci IllegalArgumentException
    //    assertThrows(IllegalArgumentException.class, () -> {
    //        contractManagement.sendOffer(null, null);
    //    });
    //}

    @Test
    void testSpecialRequestsTooLong() {
        // 1. Istanza della classe da testare
        ContractsManagement cm = new ContractsManagement();

        // 2. Creiamo un input "specialRequests" con lunghezza > 250
        String specialRequests = "a".repeat(260 ); // stringa di 251 'a'
//        String specialRequests = "Si richiede che il servizio venga svolto nei giorni feriali dalle ore 8:00  alle ore 18:00, garantendo l’uso esclusivo di materiali certificati. Il personale impiegato dovrà essere qualificato e rispettare le norme di sicurezza vigenti. Al termine di ogni giornata, dovrà essere fornito un report dettagliato con l’elenco delle attività svolte, eventuali problemi riscontrati e le soluzioni adottate. Il pagamento sarà effettuato solo previa verifica e approvazione scritta del lavoro da parte del committente, con eventuali penali in caso di ritardi o difformità rispetto alle specifiche pattuite.";
//        String specialRequests = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";



        Contract aco = new Contract();
        aco.setProfession("Engineer");
        aco.setHourlyRate(new BigDecimal("10.00"));
        aco.setTotalFee(new BigDecimal("100.00"));

        aco.setScheduleType("WEEKLY");
        aco.setServiceMode("HOMEDELIVERY");
        // 3. Eseguiamo il metodo e verifichiamo che lanci IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            cm.sendOffer(aco, specialRequests);
        });
    }

    @Test
    void testInvalidTotalFeeFormat() {
        // 1. Creiamo un’istanza della classe da testare
        ContractsManagement cm = new ContractsManagement();

        // 2. Creiamo un Contract con dati coerenti, tranne totalFee errato
        Contract aco = new Contract();
        aco.setProfession("Idraulico");
        aco.setHourlyRate(new BigDecimal("10.00")); // OK

        // Impostiamo un totalFee che non rispetta la formattazione, per esempio con 3 decimali
        aco.setTotalFee(new BigDecimal("s6.1"));

        aco.setScheduleType("WEEKLY");
        aco.setServiceMode("HOMEDELIVERY");

        // 3. Definiamo la stringa specialRequests
//        String specialRequests = "Il personale impiegato dovrà essere qualificato e rispettare le norme di  sicurezza vigenti. Al termine di ogni giornata, dovrà essere fornito  un report dettagliato con l'elenco delle attività svolte, eventuali problemi riscontrati e le soluzioni adottate.";
        String specialRequests = "a".repeat(260 ); // stringa di 251 'a'

        // 4. Eseguiamo il metodo e verifichiamo che lanci un’eccezione
        //    (IllegalArgumentException o una custom exception, a seconda della tua logica)
        assertThrows(IllegalArgumentException.class, () -> {
            cm.sendOffer(aco, specialRequests);
        });
    }

    @Test
    void testInvalidHourlyRateFormat() {
        // 1. Creiamo un’istanza della classe da testare
        ContractsManagement cm = new ContractsManagement();

        // 2. Creiamo un Contract con dati coerenti, tranne totalFee errato
        Contract aco = new Contract();
        aco.setProfession("Idraulico");
        aco.setHourlyRate(new BigDecimal("10.00")); // OK

        // Impostiamo un totalFee che non rispetta la formattazione, per esempio con 3 decimali
        aco.setTotalFee(new BigDecimal("64343406.125"));

        aco.setScheduleType("WEEKLY");
        aco.setServiceMode("HOMEDELIVERY");

        // 3. Definiamo la stringa specialRequests
        String specialRequests = "Il personale impiegato dovrà essere qualificato e rispettare le norme di  sicurezza vigenti. Al termine di ogni giornata, dovrà essere fornito  un report dettagliato con l'elenco delle attività svolte, eventuali problemi riscontrati e le soluzioni adottate.";

        // 4. Eseguiamo il metodo e verifichiamo che lanci un’eccezione
        //    (IllegalArgumentException o una custom exception, a seconda della tua logica)
        assertThrows(IllegalArgumentException.class, () -> {
            cm.sendOffer(aco, specialRequests);
        });
    }




}