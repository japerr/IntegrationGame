<%@include file="/include.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Configure Continuous Integration Game" scope="request"/>
<bs:page>
  <jsp:attribute name="head_include">
    <bs:linkCSS>
        /css/admin/adminMain.css
        /css/admin/projectConfig.css
        /css/admin/vcsRootsTable.css
    </bs:linkCSS>
    <bs:linkScript>
        /js/bs/blocks.js
        /js/bs/blocksWithHeader.js
        /js/bs/editProject.js
    </bs:linkScript>

    <script type="text/javascript">
        BS.Navigation.items = [
            {title:"Administration", url:'<c:url value="/admin/admin.html"/>'},
            {title:"${pageTitle}", selected:true}
        ];
    </script>
  </jsp:attribute>
<jsp:attribute name="body_include">
    <h3>Select projects that should count for scoring:</h3>
    <form method="POST">
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
</jsp:attribute>
</bs:page>