<%--
  Created by IntelliJ IDEA.
  User: Alex
  Date: 7/26/13
  Time: 8:13 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
</head>

<body>
<g:form controller="Test" action="SimpleSearch">
    <input type="text" name="name"  id="name" />
    <button value="submit();">Get Reviews</button>
</g:form>

<g:each var="foo" in="${fooList}" status="counter">
    ${counter.count}, ${foo}
</g:each>

</body>
</html>