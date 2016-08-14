<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%!
public class StreamGroup {
	private String name;
	private int count;

	public StreamGroup(String name, int count) {
	    this.name = name;
	    this.count = count;
	}

	public String getName() {
	    return name;
	}

	public int getCount() {
	    return count;
	}

	public String toString() {
		return name + "," + count;
	}
}
%>
<%
	List<StreamGroup> groups = new ArrayList<StreamGroup>(40);
	groups.add(new StreamGroup("QA", 30));
	groups.add(new StreamGroup("Test", 50));
	groups.add(new StreamGroup("Java", 100));
	groups.add(new StreamGroup("Hardware", 200));

	request.setAttribute("groups", groups);
%>


<ul>
<c:forEach items="${groups}" var="g">
	<li id="e_121">
		<div class="left">
			<a href="#/f/121" onclick="closeLeftBar()">${g.name}</a>
		</div>
		<div id="e_c_121" class="w50px right text-right pr20p">
			${g.count}
		</div>
	</li>
</c:forEach>
</ul>
