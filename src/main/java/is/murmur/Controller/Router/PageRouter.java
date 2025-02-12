package is.murmur.Controller.Router;

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
            switch (pageName.toLowerCase().replace(" ","")) {
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

    private void forwardRequest(HttpServletRequest req, HttpServletResponse resp, String path, HttpSession session, String pageTitle) throws Exception {
        session.setAttribute("pageName", pageTitle);
        req.getRequestDispatcher(path).forward(req, resp);
    }

    private void handlePageError(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException, ServletException {
        session.setAttribute("pageError", "This page does not exist!");
        req.getRequestDispatcher("home.jsp").forward(req, resp);
    }
}