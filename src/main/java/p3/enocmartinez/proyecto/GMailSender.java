package p3.enocmartinez.proyecto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.Properties;



public class GMailSender {
    String host = "smtp.gmail.com";
    String puerto = "465";
    String usuario = "enoc.9714@gmail.com";
    String ctrsena = "kamata46!";
    Properties props = new Properties();
    Session session;
    Message message;
    Multipart multipart = new MimeMultipart();
    Context context;

    public GMailSender(Context context){
        this.context = context;
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", puerto);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", puerto);
        session = Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usuario,ctrsena);
                    }
                });
    }

    public synchronized void enviarEmail(String destinatario, String asunto, String mensaje){
        try {
            message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject(asunto);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mensaje, "text/html");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);
                    } catch (Exception e) {
                        //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Algún error sucedió",e.toString());
                    }
                }
            }).start();
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
