package is.murmur.Application.Router;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * La classe PageRouter gestisce il routing delle pagine in base al parametro "pageName" ricevuto dalla richiesta.
 * <p>
 * A seconda del valore del parametro, il metodo {@code routePage} inoltra la richiesta al path appropriato e imposta il titolo della pagina nella sessione.
 * Se il parametro non corrisponde a nessuna pagina gestita, viene impostato un messaggio di errore e la richiesta viene inoltrata alla pagina "home.jsp".
 * </p>
 */
public class PageRouter {

    /**
     * Inoltra la richiesta al path corrispondente in base al parametro "pageName".
     *
     * @param req  La richiesta HTTP.
     * @param resp La risposta HTTP.
     * @throws IOException      Se si verifica un errore di I/O.
     * @throws ServletException Se si verifica un errore nel servlet.
     */
    public void routePage(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();
        String pageName = req.getParameter("pageName");

        try {
            switch (pageName.toLowerCase().replace(" ", "")) {
                case "homepage":
                    forwardRequest(req, resp, "/loadHomePage", session, "Home");
                    break;
                case "searchpage":
                    forwardRequest(req, resp, "/loadSearchPage", session, "Search");
                    break;
                case "personalinfospage":
                    forwardRequest(req, resp, "/loadPersonalInfosPage", session, "My Personal Infos");
                    break;
                case "plannerpage":
                    forwardRequest(req, resp, "/loadPlannerPage", session, "My Planner");
                    break;
                case "activityareapage":
                    forwardRequest(req, resp, "/loadActivityAreaPage", session, "My Activity Area");
                    break;
                case "careerpage":
                    forwardRequest(req, resp, "/loadCareerPage", session, "My Career");
                    break;
                case "contractspage":
                    forwardRequest(req, resp, "/loadContractsPage", session, "My Contracts");
                    break;
                case "applicationspage":
                    forwardRequest(req, resp, "/loadApplicationsPage", session, "My Applications");
                    break;
                case "adminpage":
                    forwardRequest(req, resp, "/loadAdministrationPage", session, "My Administration");
                    break;
                case "lockspage":
                    forwardRequest(req, resp, "/loadLocksPage", session, "Locks");
                    break;
                case "loginpage":
                    forwardRequest(req, resp, "/loadLoginPage", session, "Login");
                    break;
                case "signinpage":
                    forwardRequest(req, resp, "/loadSigninPage", session, "Sign in");
                    break;
                default:
                    handlePageError(req, resp, session);
                    break;
            }
        } catch (Exception e) {
            handlePageError(req, resp, session);
        }
    }

    /**
     * Imposta il titolo della pagina nella sessione e inoltra la richiesta al path specificato.
     *
     * @param req       La richiesta HTTP.
     * @param resp      La risposta HTTP.
     * @param path      Il path a cui inoltrare la richiesta.
     * @param session   La sessione corrente.
     * @param pageTitle Il titolo della pagina da impostare.
     * @throws Exception Se si verifica un errore durante l'inoltro della richiesta.
     */
    private void forwardRequest(HttpServletRequest req, HttpServletResponse resp, String path, HttpSession session, String pageTitle) throws Exception {
        session.setAttribute("pageName", pageTitle);
        req.getRequestDispatcher(path).forward(req, resp);
    }

    /**
     * Gestisce il caso in cui la pagina richiesta non esista.
     * <p>
     * Imposta un messaggio di errore nella sessione e inoltra la richiesta alla pagina "home.jsp".
     * </p>
     *
     * @param req     La richiesta HTTP.
     * @param resp    La risposta HTTP.
     * @param session La sessione corrente.
     * @throws IOException      Se si verifica un errore di I/O.
     * @throws ServletException Se si verifica un errore nel servlet.
     */
    private void handlePageError(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException, ServletException {
        session.setAttribute("pageError", "This page does not exist!");
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}