package is.murmur.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet che gestisce la pagina di benvenuto.
 * <p>
 * Questa servlet resetta gli attributi di sessione relativi all'autenticazione,
 * come "loggedIn", "userType" e "isAdmin", e reindirizza l'utente alla pagina home.jsp.
 * </p>
 */
@WebServlet(name = "welcome", value = "/welcome")
public class WelcomeServlet extends HttpServlet {

    /**
     * Gestisce le richieste GET.
     * <p>
     * Il metodo ottiene la sessione corrente, imposta gli attributi di autenticazione a valori di default,
     * e reindirizza l'utente alla pagina home.jsp.
     * </p>
     *
     * @param req  La richiesta HTTP.
     * @param resp La risposta HTTP.
     * @throws ServletException Se si verifica un errore nel servlet.
     * @throws IOException      Se si verifica un errore di I/O.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("loggedIn", false);
        session.setAttribute("userType", -1);
        session.setAttribute("isAdmin", false);
        resp.sendRedirect("home.jsp");
    }

    /**
     * Gestisce le richieste POST.
     * <p>
     * Questo metodo delega il trattamento delle richieste POST al metodo doGet.
     * </p>
     *
     * @param req  La richiesta HTTP.
     * @param resp La risposta HTTP.
     * @throws ServletException Se si verifica un errore nel servlet.
     * @throws IOException      Se si verifica un errore di I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}