package is.murmur.Model.Services.SearchStrategy;

import is.murmur.Model.Helpers.Criteria;

public interface SearchStrategy {
    /**
     * Esegue la ricerca utilizzando i criteri specificati.
     * @param criteria un oggetto SearchCriteria contenente tutti i parametri necessari.
     * @return La stringa JSON con i risultati.
     */
    String search(Criteria criteria);
}