package com.mediBuddy.medicos.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mediBuddy.medicos.model.CounslingSession;
import com.mediBuddy.medicos.model.Question;
import com.mediBuddy.medicos.model.SOS;
import com.mediBuddy.medicos.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class AdditionalFeatures {

    private static final String SENDER_EMAIL = "vishalaggar230@gmail.com";


    private  JavaMailSender mailSender;

    /**
     * Generate a PDF report for a counseling session.
     *
     * @param session the counseling session data
     * @param user    the user associated with the session
     * @return a ByteArrayOutputStream containing the PDF data
     */
    private ByteArrayOutputStream generatePDF(CounslingSession session, User user) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Paragraph title = new Paragraph("Counseling Session Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Add User Information
            document.add(new Paragraph("User Information:"));
            document.add(new Paragraph("Name: " + user.getUsername()));
            document.add(new Paragraph("Email: " + user.getEmail()));
            document.add(new Paragraph("\n"));

            // Add Session Information
            document.add(new Paragraph("Session Information:"));
            document.add(new Paragraph("Session Name: " + session.getSessionName()));
            document.add(new Paragraph("Doctor Name: " + session.getDoctorName()));
            document.add(new Paragraph("Summary: " + session.getSummary()));
            document.add(new Paragraph("Precautions: " + session.getPrecautions()));
            document.add(new Paragraph("\n"));

            // Add Questions Table
            if (session.getQuestionList() != null && !session.getQuestionList().isEmpty()) {
                document.add(new Paragraph("Questions and Answers:"));

                PdfPTable table = new PdfPTable(2); // Two columns for Question and Answer
                table.setWidthPercentage(100);
                table.addCell("Question");
                table.addCell("Answer");

                for (Question question : session.getQuestionList()) {
                    table.addCell(question.getQuestionTitle());
                    table.addCell(question.getAnswer());
                }
                document.add(table);
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
        return outputStream;
    }

    /**
     * Send an email with the generated PDF as an attachment.
     *
     * @param recipientEmail the recipient's email address
     * @param subject        the subject of the email
     * @param pdfOutputStream the ByteArrayOutputStream containing the PDF
     */
    private void sendEmailWithAttachment(String recipientEmail, String subject, ByteArrayOutputStream pdfOutputStream) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(SENDER_EMAIL);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(
                    "Dear Sir,\n\n" +
                            "We hope this email finds you well.\n\n" +
                            "Please find attached the detailed report for your recent counseling session. This report contains important insights, session details, and any recommendations provided by your doctor. We encourage you to review it carefully and follow the outlined precautions and guidance.\n\n" +
                            "If you have any questions or require further assistance, feel free to contact us at support@mediBuddy.com. Our team is here to support you.\n\n" +
                            "Thank you for choosing MediBuddy for your healthcare needs.\n\n" +
                            "Best regards,\n" +
                            "The MediBuddy Team\n\n" +
                            "--------------------------------------------\n"
            );


            // Attach the PDF
            DataSource dataSource = new ByteArrayDataSource(pdfOutputStream.toByteArray(), "application/pdf");
            helper.addAttachment("Counseling_Session_Report.pdf", dataSource);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a PDF for a counseling session and sends it via email.
     *
     * @param session the counseling session
     * @param user    the user associated with the session
     */
    public void generateAndSendReport(CounslingSession session, User user) {
        ByteArrayOutputStream pdfOutputStream = generatePDF(session, user);
        sendEmailWithAttachment(user.getEmail(), "Counseling Session Report", pdfOutputStream);
    }

    private void sendHtmlEmail(String recipientEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(SENDER_EMAIL);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);  // 'true' here indicates that the body is HTML content

            mailSender.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending HTML email: " + e.getMessage());
        }
    }


    public String sendEmail(List<SOS> sosList, String location) {
        try {
            // Construct the subject and the body of the email
            String subject = "SOS Alert - Immediate Attention Required";
            String body = "<html><body>"
                    + "<p>Dear Professional,</p>"
                    + "<p>We have received an SOS alert from a user. The location of the alert is: <b>" + location + "</b>.</p>"
                    + "<p>Please review the situation and take necessary action.</p>"
                    + "<p>If you need further details, please contact us at support@mediBuddy.com.</p>"
                    + "<p>Best regards,<br>The MediBuddy Team</p>"
                    + "<hr>"
                    + "<p><i>--------------------------------------------</i></p>"
                    + "</body></html>";

            // Loop through each SOS object in the list
            for (SOS sos : sosList) {
                // Send the email to the email address in the SOS object
                sendHtmlEmail(sos.getEmail(), subject, body);  // Send the HTML email without a PDF attachment
                System.out.println("Email sent to: " + sos.getEmail());
            }

            // Return a success message after processing all emails
            return "Emails sent successfully to all professionals.";
        } catch (Exception e) {
            // Log and return the error message in case of failure
            e.printStackTrace();
            return "Error sending emails: " + e.getMessage();
        }
    }


}
