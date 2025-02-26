package is.murmur.Storage.Services.SearchStrategy;

import is.murmur.Storage.Helpers.Criteria;

public interface SearchStrategy {
    /**
     * Esegue la ricerca utilizzando i criteri specificati.
     * @param criteria un oggetto SearchCriteria contenente tutti i parametri necessari.
     * @return La stringa JSON con i risultati.
     */
    String search(Criteria criteria);
}