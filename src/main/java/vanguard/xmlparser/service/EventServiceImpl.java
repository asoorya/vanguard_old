package vanguard.xmlparser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import vanguard.xmlparser.repository.EventRepository;
import vanguard.xmlparser.repository.modal.Event;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> readAndSaveEvents(String filePath) {
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList buyerPartyNodes = (NodeList) xPath.compile("//buyerPartyReference/@href").evaluate(doc, XPathConstants.NODESET);
            NodeList sellerPartyNodes = (NodeList) xPath.compile("//sellerPartyReference/@href").evaluate(doc, XPathConstants.NODESET);
            NodeList premiumAmountNodes = (NodeList) xPath.compile("//paymentAmount/amount").evaluate(doc, XPathConstants.NODESET);
            NodeList premiumCurrencyNodes = (NodeList) xPath.compile("//paymentAmount/currency").evaluate(doc, XPathConstants.NODESET);

            List<Event> events = new ArrayList<>();

            IntStream.range(0, buyerPartyNodes.getLength()).forEach(i -> {
                Event event = new Event();
                event.setBuyerParty(buyerPartyNodes.item(i).getTextContent());
                event.setSellerParty(sellerPartyNodes.item(i).getTextContent());
                event.setPremiumAmount(Double.parseDouble(premiumAmountNodes.item(i).getTextContent()));
                event.setPremiumCurrency(premiumCurrencyNodes.item(i).getTextContent());
                events.add(event);
            });

            return eventRepository.saveAll(events);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML file", e);
        }
    }

    @Override
    public List<Event> filterEvents() {
        List<Event> allEvents = eventRepository.findAll();
        return allEvents.stream()
                .filter(event -> (("EMU_BANK".equals(event.getSellerParty()) && "AUD".equals(event.getPremiumCurrency())) ||
                        ("BISON_BANK".equals(event.getSellerParty()) && "USD".equals(event.getPremiumCurrency()))) &&
                        !areAnagrams(event.getSellerParty(), event.getBuyerParty()))
                .toList();
    }

    private boolean areAnagrams(String str1, String str2) {
        if (str1 == null || str2 == null) return false;
        if (str1.length() != str2.length()) return false;
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();
        java.util.Arrays.sort(charArray1);
        java.util.Arrays.sort(charArray2);
        return java.util.Arrays.equals(charArray1, charArray2);
    }
}


