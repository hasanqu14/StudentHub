import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/SendOtpServlet")
public class SendOtpServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("student_id");

        if (studentId == null || studentId.isEmpty()) {
            response.getWriter().println("Invalid Student ID!");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smsdb", "root", "root");
                 PreparedStatement pst = con.prepareStatement("SELECT email FROM students WHERE students_id=?")) {

                pst.setInt(1, Integer.parseInt(studentId));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String email = rs.getString("email");
                        String generatedOtp = generateOTP();

                        // Store OTP in session with an expiration time
                        HttpSession session = request.getSession();
                        session.setAttribute("otp", generatedOtp);
                        session.setAttribute("otpTime", System.currentTimeMillis());

                        // Send OTP email
                        if (sendEmail(email, generatedOtp)) {
                            response.getWriter().println("OTP sent successfully to " + email);
                        } else {
                            response.getWriter().println("Failed to send OTP. Check mail settings.");
                        }
                    } else {
                        response.getWriter().println("Student not found!");
                    }
                }
            }
        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    private String generateOTP() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    private boolean sendEmail(String recipientEmail, String otp) {
       final String senderEmail = "hjqureshi14@gmail.com";
        final String senderPassword = "scnj ovmy rmtv vyqm";

        if (senderEmail == null || senderPassword == null) {
            System.err.println("Email credentials are missing!");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your OTP for Attendance");
            message.setText("Your OTP is: " + otp + "\nThis OTP is valid for 5 minutes.");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
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
        return "Sends OTP to student's registered email.";
    }
}
