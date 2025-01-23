package is.murmur.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class PageRouter {
    public void routePage(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();
        String pageName = req.getParameter("pageName");

        try {
            switch (pageName) {
                case "home page":
                    forwardRequest(req, resp, "/loadHomePage", session, "Home");
                    break;
                case "search page":
                    forwardRequest(req, resp, "/loadSearchPage", session, "Search");
                    break;
                case "personal infos page":
                    forwardRequest(req, resp, "/loadPersonalInfosPage", session, "My Personal Infos");
                    break;
                case "planner page":
                    forwardRequest(req, resp, "/loadPlannerPage", session, "My Planner");
                    break;
                case "activity area page":
                    forwardRequest(req, resp, "/loadActivityAreaPage", session, "My Activity Area");
                    break;
                case "career page":
                    forwardRequest(req, resp, "/loadCareerPage", session, "My Career");
                    break;
                case "drafts page":
                    forwardRequest(req, resp, "/loadDraftsPage", session, "My Drafts");
                    break;
                case "offers page":
                    forwardRequest(req, resp, "/loadOffersPage", session, "My Offers");
                    break;
                case "contracts page":
                    forwardRequest(req, resp, "/loadContractsPage", session, "My Contracts");
                    break;
                case "applications page":
                    forwardRequest(req, resp, "/loadApplicationsPage", session, "My Applications");
                    break;
                case "admin page":
                    forwardRequest(req, resp, "/loadAdministrationPage", session, "My Administration");
                    break;
                case "login page":
                    forwardRequest(req, resp, "/loadLoginPage", session, "Login");
                    break;
                case "sign in page":
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

    private void forwardRequest(HttpServletRequest req, HttpServletResponse resp, String path, HttpSession session, String pageTitle) throws Exception {
        session.setAttribute("pageName", pageTitle);
        req.getRequestDispatcher(path).forward(req, resp);
    }

    private void handlePageError(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException, ServletException {
        session.setAttribute("pageError", "This page does not exist!");
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}