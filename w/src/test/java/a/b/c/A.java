package a.b.c;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.io.IOException;

/**
 * Date: 13-10-27 - 下午1:18
 */
public class A {
    public static void main(String[] args) throws SOAPException, IOException {
        System.out.println("!");
//        SOAPFactory soapFactory=SOAPFactory.newInstance();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPHeader header = envelope.getHeader();
        SOAPBody body = envelope.getBody();

        header.addTextNode("header text");
        body.addBodyElement(new QName("http://www.baidu.com", "n", "pri")).addTextNode("12345,12345");
        body.addBodyElement(envelope.createName("http://www.example.com/3", "n3", "pre3")).addTextNode("333");

        message.getMimeHeaders().addHeader("h1", "1");
        message.setContentDescription("the description");
        message.setProperty("a", 2);
        message.writeTo(System.out);
    }
}