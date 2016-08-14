<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%!
public class Profile {
        private String name;
        private String color;
        private long id;

        public Profile(long id, String name, String color) {
            this.id = id;
            this.name = name;
            this.color = color;
        }

        public long getId() {
            return id;
        }

        public String getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return id + "," + name + "," + color;
        }
    }
%>
<%
	List<Profile> profiles = new ArrayList<Profile>(5);
	profiles.add(new Profile(0, "FeedRdr", "#99FF33"));
	profiles.add(new Profile(0, "Learning", "#8317FF"));
	profiles.add(new Profile(0, "WebDevelopment", "#B6FF38"));
	profiles.add(new Profile(0, "ShareCollections", "#FF7818"));
	profiles.add(new Profile(0, "Home", "#FF7818"));
	request.setAttribute("profiles", profiles);
%>

<c:forEach items="${profiles}" var="p">
<div>
	<a href="#" onclick="selectProfile(${p.id}, true); return false;">${p.name}</a>
	<a class="pr20p right" href="${baseUrl}/pages/settings.jsp#/v/pro">
		<i class="fa fa-cog fade-color"></i>
	</a>
</div>
</c:forEach>
