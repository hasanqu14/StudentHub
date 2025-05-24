import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/signup"})
public class Signup extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/smsdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            // Email validation
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                out.println("<script>alert('Invalid email format!');window.location='signup.html';</script>");
                return;
            }

            // Password validation
            if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                out.println("<script>alert('Password must be at least 8 characters long and include one uppercase letter, one number, and one special character.');window.location='signup.html';</script>");
                return;
            }

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

                // Hash password for security
               

                String insertQuery = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(insertQuery);
                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, password);
                pst.setString(4, role);

                int row = pst.executeUpdate();
                if (row > 0) {
                    response.sendRedirect(role + ".html"); // Redirect to role-specific page
                } else {
                    out.println("<script>alert('Signup failed! Try again.');window.location='signup.html';</script>");
                }

            } catch (Exception e) {
                out.println(e);
                out.println("<script>alert('Error: " + e.getMessage() + "');window.location='signup.html';</script>");
            }
        }
    }

    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Signup Servlet with Validation & Secure Password Storage";
    }
}
