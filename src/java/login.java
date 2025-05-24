import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class login extends HttpServlet {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/smsdb";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "root";

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            out.println("<script>alert('Invalid email format!');window.location='login.html';</script>");
            return;
        }

        String hashedPassword = hashPassword(password);

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            String query = "SELECT role FROM users WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, hashedPassword);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                HttpSession session = request.getSession();
                session.setAttribute("userEmail", email);
                session.setAttribute("userRole", role);

                switch (role.toLowerCase()) {
                    case "student":
                        response.sendRedirect("student.html");
                        break;
                    case "teacher":
                        response.sendRedirect("teacher.html");
                        break;
                    case "admin":
                        response.sendRedirect("admin.html");
                        break;
                    default:
                        response.sendRedirect("signup.html");
                        break;
                }
            } else {
                out.println("<script>alert('Invalid email or password!');window.location='login.html';</script>");
            }

            con.close();
        } catch (Exception e) {
            out.println("<script>alert('Database error!');window.location='error.html';</script>");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
