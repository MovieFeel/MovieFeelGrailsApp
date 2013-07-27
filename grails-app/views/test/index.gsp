<%--
  Created by IntelliJ IDEA.
  User: alexcosma
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
<g:form action="search">

    Search for reviews: <input type="text" name="searchValue" size="50" value="${searchValue}">

    <g:actionSubmit value="Submit" action="simpleSearch"/>


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

</g:form>
</body>
</html>