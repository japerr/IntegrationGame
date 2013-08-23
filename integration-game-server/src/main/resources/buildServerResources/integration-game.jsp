<%@include file="/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h3>Current score for all users:</h3>
<table>
    <c:forEach var="userScore" items="${requestScope.scores}">
    <tr>
        <td>${userScore.getUserName()}</td>
        <td>${userScore.getScore()}</td>
    </tr>
    </c:forEach>
</table>