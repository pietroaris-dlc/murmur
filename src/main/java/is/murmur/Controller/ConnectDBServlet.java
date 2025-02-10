package is.murmur.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "connectDB", value = "/connectDB")
public class ConnectDBServlet extends HttpServlet {
    private static final Map<String, String> DB_PASSWORDS = new HashMap<>();

    static {
        DB_PASSWORDS.put("pietro", "110st!Lrnz");
        DB_PASSWORDS.put("gerardo", "Bdlab3!");
        DB_PASSWORDS.put("mattia", "123456");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        String developer = req.getParameter("developer");

        String dbPassword = getDatabasePassword(developer.toLowerCase());
        if (dbPassword == null) {
            session.setAttribute("developerError", "This developer does not exist!");
            req.getRequestDispatcher("dbAccess.jsp").forward(req, resp);
        }else{
            session.setAttribute("dbPassword", dbPassword);
            session.setAttribute("loggedIn", false);
            session.setAttribute("userType", -1);
            session.setAttribute("isAdmin", false);
            resp.sendRedirect("home.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String getDatabasePassword(String developer) {
        return DB_PASSWORDS.get(developer);
    }
}