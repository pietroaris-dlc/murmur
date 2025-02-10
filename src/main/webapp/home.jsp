<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Pages/home.css">
    <title>Home Page</title>
</head>
<body>

<%@include file="JSPF/header.jspf"%>



<div id="infoDiv">
    <div id="aboutUsDiv">
        <h1 id="aboutUsTitle">About us</h1>
        <p id="aboutUsParagraph">
            La parola murmur deriva dal latino murmur, che significa
            mormorio o brusio, ed è onomatopeica, imitando il suono sommesso che
            descrive. Storicamente, il termine è stato associato al parlare sommesso e
            discreto, un borbottio che si diffonde senza clamore ma con una presenza costante.
            Questo concetto si presta perfettamente a rappresentare il passaparola, un
            meccanismo naturale e spesso informale attraverso cui informazioni, opinioni
            o dicerie si diffondono nella società, in particolare riguardo alla reputazione
            di una persona. La scelta di Murmur come nome per la piattaforma è ispirata proprio
            a questa associazione con il passaparola. Nella realtà lavorativa, le dicerie o
            i commenti sulla bravura o meno di un lavoratore possono avere un impatto
            significativo sulle sue opportunità di impiego. In questo senso, il mormorio
            rappresenta il flusso discreto ma potente di informazioni che circolano, creando una
            rete invisibile di connessioni e opinioni che influenzano la visibilità e la
            percezione dei lavoratori. Allo stesso tempo, il termine evoca anche l’idea
            di una comunicazione fluida, continua e non invadente, che ben si sposa
            con il processo di scoperta di nuove opportunità lavorative. Il nome della
            piattaforma vuole trasmettere un’esperienza user-friendly e naturale, dove
            le opportunità emergono come un mormorio rassicurante, senza sovrastare
            l’utente. In conclusione, il nome Murmur sintetizza il concetto di
            passaparola e la forza delle reti sociali nel mondo del lavoro, riflettendo al
            contempo una comunicazione discreta e naturale che permette di scoprire
            opportunità in modo intuitivo e continuo, senza pressioni.
        </p>
    </div>
    <div id="contactsDiv">
        <h2 id="contactsTitle">Contacts</h2>
        <ul id="contactsList">
            <li id="phoneItem">Phone: 1234567890</li>
            <li id="mailItem">E-Mail: murmur@murmur.com</li>
        </ul>
    </div>
</div>
<%@include file="JSPF/footer.jspf"%>
</body>
</html>
