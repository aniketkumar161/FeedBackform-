package com.feedback;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Processes feedback submitted from index.html.
 * Supports both GET (data in URL) and POST (data in request body).
 */
@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If opened directly without form data, send the user back to the form
        if (request.getParameter("name") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
        handle(request, response, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        handle(request, response, "POST");
    }

    private void handle(HttpServletRequest request, HttpServletResponse response, String method)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");

        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String feedback = trim(request.getParameter("feedback"));

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang=\"en\"><head><meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            out.println("<title>Feedback Received</title>");
            out.println("<style>"
                    + "body{font-family:Segoe UI,Arial,sans-serif;background:#f0f2f5;margin:0;padding:40px 16px;}"
                    + ".card{max-width:560px;margin:auto;background:#fff;padding:28px 32px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,.1);}"
                    + ".ok{background:#e6f4ea;color:#1e7b34;padding:12px 16px;border-radius:6px;margin-bottom:20px;font-weight:600;}"
                    + ".err{background:#fdecea;color:#b3261e;padding:12px 16px;border-radius:6px;margin-bottom:20px;font-weight:600;}"
                    + "table{width:100%;border-collapse:collapse;}"
                    + "td{padding:10px 8px;border-bottom:1px solid #eee;vertical-align:top;}"
                    + "td:first-child{font-weight:600;width:120px;color:#444;}"
                    + "a{color:#1a73e8;text-decoration:none;}"
                    + "</style></head><body><div class=\"card\">");

            if (name.isEmpty() || email.isEmpty() || feedback.isEmpty() || !email.contains("@")) {
                out.println("<div class=\"err\">Please fill in all fields with a valid email address.</div>");
                out.println("<p><a href=\"" + request.getContextPath() + "/index.html\">&larr; Back to form</a></p>");
            } else {
                out.println("<div class=\"ok\">&#10004; Thank you, " + escape(name)
                        + "! Your feedback has been submitted successfully.</div>");
                out.println("<h2>Submitted Details</h2>");
                out.println("<table>");
                out.println("<tr><td>Name</td><td>" + escape(name) + "</td></tr>");
                out.println("<tr><td>Email</td><td>" + escape(email) + "</td></tr>");
                out.println("<tr><td>Feedback</td><td>" + escape(feedback).replace("\n", "<br>") + "</td></tr>");
                out.println("<tr><td>HTTP Method</td><td>" + method + "</td></tr>");
                out.println("</table>");
                out.println("<p style=\"margin-top:24px\"><a href=\"" + request.getContextPath()
                        + "/index.html\">&larr; Submit another feedback</a></p>");
            }
            out.println("</div></body></html>");
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Escapes HTML special characters to prevent XSS. */
    private static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}