package is.murmur.Model.Services;

import is.murmur.Model.Beans.Contract;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ContractsManagementTest {


    @Test
    void testSpecialRequestsTooLong() {
        // 1. Istanza della classe da testare
        ContractsManagement cm = new ContractsManagement();

        String specialRequests = "a".repeat(240 ); // stringa di 251 'a'
//        String specialRequests = "Si richiede che il servizio venga svolto nei giorni feriali dalle ore 8:00  alle ore 18:00, garantendo l’uso esclusivo di materiali certificati. Il personale impiegato dovrà essere qualificato e rispettare le norme di sicurezza vigenti. Al termine di ogni giornata, dovrà essere fornito un report dettagliato con l’elenco delle attività svolte, eventuali problemi riscontrati e le soluzioni adottate. Il pagamento sarà effettuato solo previa verifica e approvazione scritta del lavoro da parte del committente, con eventuali penali in caso di ritardi o difformità rispetto alle specifiche pattuite.";

        // Cosi' Lancia L'exception
//        String specialRequests = "a".repeat(270 ); // stringa di 251 'a'

        Contract aco = new Contract();
        aco.setProfession("Engineer");
        aco.setHourlyRate(new BigDecimal("10.00"));
        aco.setTotalFee(new BigDecimal("30.75"));

        aco.setScheduleType("WEEKLY");
        aco.setServiceMode("HOMEDELIVERY");
        // 3. Eseguiamo il metodo e verifichiamo che lanci IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            cm.sendOffer(aco, specialRequests);
        });
    }

   @Test
   void testTotalFeeNull(){
       ContractsManagement cm = new ContractsManagement();

       String specialRequests = "a".repeat(240 ); // stringa di 251 'a'


       Contract aco = new Contract();
       aco.setProfession("Engineer");

       aco.setHourlyRate(new BigDecimal("10.00"));
       aco.setTotalFee(new BigDecimal("30.75"));

        // Cosi' Lancia L'exception
//       aco.setTotalFee(null);

       aco.setScheduleType("WEEKLY");
       aco.setServiceMode("HOMEDELIVERY");

       assertThrows(IllegalArgumentException.class, () -> {
           cm.sendOffer(aco, specialRequests);
       });

   }
   @Test
   void testInvalidHourlyRateFormat() {
       ContractsManagement cm = new ContractsManagement();

       String specialRequests = "a".repeat(240 ); // stringa di 251 'a'


       Contract aco = new Contract();
       aco.setProfession("Engineer");

       // Cosi' Lancia l'exception
//       aco.setHourlyRate(null);
       aco.setHourlyRate(new BigDecimal("10.00"));
       aco.setTotalFee(new BigDecimal("30.75"));

       aco.setScheduleType("WEEKLY");
       aco.setServiceMode("HOMEDELIVERY");

       assertThrows(IllegalArgumentException.class, () -> {
           cm.sendOffer(aco, specialRequests);
       });



   }





}