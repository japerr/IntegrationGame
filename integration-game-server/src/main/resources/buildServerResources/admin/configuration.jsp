<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<h3>Select projects that should count for scoring:</h3>
<form method="POST" action="/admin/configureCIGame.html">
    <table>
        <c:forEach items="${configs}" var="config">
            <tr>
                <td>${config.getBuildName()}</td>
                <td><input type="checkbox" name="${config.getBuildId()}" value="true"
                           ${config.isEnabled() ? "checked=\"checked\"" : ""} /> </td>
            </tr>
        </c:forEach>
    </table>
    <input type="submit" value="Save"/>
</form>
