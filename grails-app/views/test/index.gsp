<%--
  Created by IntelliJ IDEA.
  User: dariussuciu
  Date: 08.07.13
  Time: 09:57
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Simple Search with inputted field</title>
</head>

<body>
<g:form action="simpleSearch">

    Search for reviews: <input type="text" name="searchValue" size="50" value="${searchValue}">

    <g:actionSubmit value="Submit" action="simpleSearch"/>
</g:form>
<g:form action="gateTest">
    <g:textArea name="inputGate" value="${inputGate}" rows="10" cols="40"/>

    <g:actionSubmit value="Submit" action="gateTest"/>
</g:form>

    ${outputGate}

    <g:if test="${results}">

        <ul>
            <g:each in="${results}" var="result">
                    <li>
                        <pre>
                        Source: ${result.getSource()}
                        Critic:  ${result.getCritic()}
                        Date:  ${result.getDate()}
                        Quote:  ${result.getQuote()}
                        Publication:  ${result.getPublication()}
                        Freshness:  ${result.getFreshness()}
                        Original Score:  ${result.getOriginal_score()}
                        Link:  ${result.getLinks()}
                        </pre>
                    </li>

            </g:each>
        </ul>
    </g:if>


</body>
</html>