package is.murmur.Services.SearchStrategy;

import is.murmur.Model.Helpers.*;

/**
 * Implementazione della strategia di ricerca per il servizio di consegna a domicilio giornaliero.
 * <p>
 * Questa classe implementa l'interfaccia {@link SearchStrategy} e definisce la logica
 * per eseguire una ricerca di lavoratori per il servizio di consegna a domicilio giornaliero.
 * La strategia delega l'operazione di ricerca alla strategia "DAILY" "ONSITE" ottenuta da
 * {@link SearchStrategyFactory}, sfruttando la logica già esistente per la modalità onsite.
 * </p>
 *
 */
public class DailyHomeDeliverySearchStrategy implements SearchStrategy {

    /**
     * Esegue la ricerca dei lavoratori per il servizio di consegna a domicilio giornaliero.
     * <p>
     * Il metodo delega la ricerca alla strategia ottenuta dal {@link SearchStrategyFactory}
     * passando i parametri "DAILY" e "ONSITE", poiché il servizio di consegna a domicilio
     * giornaliero utilizza la stessa logica di ricerca della modalità onsite.
     * </p>
     *
     * @param criteria I criteri di ricerca che includono le informazioni necessarie per filtrare i lavoratori.
     * @return Una stringa in formato JSON contenente i criteri di ricerca e i risultati, oppure
     *         un messaggio di errore in caso di eccezioni.
     */
    @Override
    public String search(Criteria criteria) {
        // Delegazione della ricerca alla strategia "DAILY" e "ONSITE" tramite il factory
        return SearchStrategyFactory.getStrategy("DAILY", "ONSITE").search(criteria);
    }
}