<%@page import="feedreader.entities.UserData"%>
<%@page import="feedreader.entities.ProfileData"%>
<%@page import="feedreader.config.FeedAppConfig" %>

<%! static String baseUrl = FeedAppConfig.BASE_APP_URL; %>
<%! static String baseApiUrl = FeedAppConfig.BASE_APP_URL + "/api"; %>

<jsp:include page="../pages/header.jsp"></jsp:include>

<% 
UserData user = (UserData)request.getAttribute("user");
ProfileData profile =  (ProfileData)request.getAttribute("profile");
%>

    <div class="col-xs-12">
        
        <h2>API</h2>
        
		<h3>collections</h3>
		<ul>
			<li><a href="<%= baseApiUrl %>/v1/collections/list">list</a></li>
			<li><a href="<%= baseApiUrl %>/v1/collections/entries?ids=1,2">entries 1,2</a></li>
			<li><a href="<%= baseApiUrl %>/v1/collections/share">share (post, use cURL)</a></li>
		</ul>
            
        <h3>Profile / User</h3>
        <ul>
            <li><a href="<%= baseUrl %>/api/v1/user/profiles/list?fid=1337">
                <%= baseUrl %>/api/v1/user/profiles/list?fid=1337</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/profiles/new?n=NAME&c=CACACA">
                <%= baseUrl %>/api/v1/user/profiles/new?n=NAME&c=CACACA</a>
            </li>      
            <li><a href="<%= baseUrl %>/api/v1/user/profiles/save?pid=<%= profile.getProfileId() %>n=NAME&c=CACACA">
                <%= baseUrl %>/api/v1/user/profiles/save?pid=<%= profile.getProfileId() %>n=NAME&c=CACACA</a>
            </li>      
            <li><a href="<%= baseUrl %>/api/v1/user/profiles/delete?pid=<%= profile.getProfileId() %>">
                <%= baseUrl %>/api/v1/user/profiles/delete?pid=<%= profile.getProfileId() %></a>
            </li>      
        </ul>
                       
        <h3>Stream Groups</h3>
        
        <ul>
            <li><a href="<%= baseUrl %>/api/v1/user/streams/list?">
                <%= baseUrl %>/api/v1/user/streams/list?</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/streams/list?pid=<%= profile.getProfileId() %>">
                <%= baseUrl %>/api/v1/user/streams/list?pid=<%= profile.getProfileId() %></a>
            </li>
            <li>show views <a href="<%= baseUrl %>/api/v1/user/streams/list?pid=<%= profile.getProfileId() %>&&views=true">
                <%= baseUrl %>/api/v1/user/streams/list?pid=<%= profile.getProfileId() %>&views=true</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/streams/save_view?&pid=<%= profile.getProfileId() %>&sid=180&v=1">
                <%= baseUrl %>/api/v1/user/streams/save_view?&pid=<%= profile.getProfileId() %>&sid=180&v=1</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/streams/add?n=ABC">
                <%= baseUrl %>/api/v1/user/streams/add?n=ABC</a>
            </li>            
            <li><a href="<%= baseUrl %>/api/v1/user/streams/rename?fid=-493748100&fn=Global+Finance ABC">
                <%= baseUrl %>/api/v1/user/streams/rename?sid=-493748100&fn=Global+Finance ABC</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/streams/delete?fid=1337">
                <%= baseUrl %>/api/v1/user/streams/delete?sid=1337</a>
            </li>
        </ul>            
        
        <h3>Feed Subscriptions</h3>            
            
        <ul>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/list?">
                <%= baseUrl %>/api/v1/user/subscriptions/list?</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/list?sid=133">
                <%= baseUrl %>/api/v1/user/subscriptions/list?sid=133</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/get?id=959">
                <%= baseUrl %>/api/v1/user/subscriptions/get?&id=959</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/removefromstream?sid=1&sui=2">
                <%= baseUrl %>/api/v1/user/subscriptions/removefromstream?&sid=1&sui=2</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/addtostream?sid=1&sui=2">
                <%= baseUrl %>/api/v1/user/subscriptions/addtostream?&sid=1&sui=2</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/set?id=XXX&n=XXXX">
                <%= baseUrl %>/api/v1/user/subscriptions/get?&id=XXX&n=XXX</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/withprofile?id=1595">
                <%= baseUrl %>/api/v1/user/subscriptions/withprofile?id=1595</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/subscriptions/remove?sid=1337">
                <%= baseUrl %>/api/v1/user/subscriptions/remove?sid=1337</a>
            </li>
        </ul>
            
        <h3>Feed Sources</h3>
        <ul>
            <li><a href="<%= baseUrl %>/api/v1/sources/get?id=14854">
                <%= baseUrl %>/api/v1/sources/get?id=14854</a>
            </li>
        </ul>
            
        <h3>Feed Entries</h3>
        
        <ul>
            <li><a href="<%= baseUrl %>/api/v1/user/feeds/all?">
                <%= baseUrl %>/api/v1/user/feeds/all?</a>
            </li>            
            <li><a href="<%= baseUrl %>/api/v1/user/feeds/list?fid=-478052306">
                <%= baseUrl %>/api/v1/user/feeds/list?fid=-478052306</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/feeds/sourceid?id=14854">
                <%= baseUrl %>/api/v1/user/feeds/sourceid?id=14854</a>
            </li>
            <li><a href="<%= baseUrl %>/api/v1/user/feeds/data?entries=1849394%2C1849395%2C1849396%2C1849397%2C1849398%2C1849399%2C1849400%2C1849401&img=true&cnt=true&ml=20">
                <%= baseUrl %>/api/v1/user/feeds/data?entries=1849394%2C1849395%2C1849396%2C1849397%2C1849398%2C1849399%2C1849400%2C1849401&img=true&cnt=true&ml=20</a>
            </li>
        </ul>            

        <h3>Entries</h3>
        
        <ul>
            <li>save <a href="<%= baseUrl %>/api/v1/entries/entry?pid=<%= profile.getProfileId() %>&id=843839&act=s">
                <%= baseUrl %>/api/v1/entries/entry?pid=<%= profile.getProfileId() %>&id=843839&act=s</a>
            </li>            
            <li>remove <a href="<%= baseUrl %>/api/v1/entries/entry?pid=<%= profile.getProfileId() %>&id=843839&act=r">
                <%= baseUrl %>/api/v1/entries/entry?pid=<%= profile.getProfileId() %>&id=843839&act=r</a>
            </li>            
            <li><a href="<%= baseUrl %>/api/v1/entries/saved_entries?pid=<%= profile.getProfileId() %>">
                <%= baseUrl %>/api/v1/entries/saved_entries?pid=<%= profile.getProfileId() %></a>
            </li> 
            <li><a href="<%= baseUrl %>/api/v1/entries/recently_read?pid=<%= profile.getProfileId() %>">
                <%= baseUrl %>/api/v1/entries/recently_read?pid=<%= profile.getProfileId() %></a>
            </li>  
            <li><a href="<%= baseUrl %>/api/v1/entries/clear_recently_read?pid=<%= profile.getProfileId() %>">
                <%= baseUrl %>/api/v1/entries/clear_recently_read?pid=<%= profile.getProfileId() %></a>
            </li>  
        </ul>
    </div>

<script type="text/javascript">
</script>

<jsp:include page="../pages/footer.jsp"></jsp:include>
