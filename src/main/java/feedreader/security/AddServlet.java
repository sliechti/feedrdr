package feedreader.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import feedreader.entities.StreamGroup;
import feedreader.entities.UserData;
import feedreader.opml.OPMLParser;
import feedreader.opml.UserOPMLImportHandler;
import feedreader.pages.PageHeader;
import feedreader.store.CollectionsTable;
import feedreader.store.Database;
import feedreader.store.UserStreamGroupsTable;
import feedreader.store.UsersTable;
import feedreader.store.CollectionsTable.SourceCollection;
import feedreader.utils.FormUploadHelper;
import feedreader.utils.PageUtils;
import feedreader.utils.ServletUtils;

@WebServlet(name = "add", urlPatterns = { "/add" })
public class AddServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        PageHeader.showSettingsMenuEntry(req);
        PageHeader.showBackButton(req);
        PageHeader.showReaderInMenuEntry(req);
        UserData user = UsersTable.get(userId);

        if (Parameter.isSet(req, "to")) {
            int streamId = Parameter.asInt(req, "to", 0);
            StreamGroup toGroup = UserStreamGroupsTable.getStream(userId, streamId);
            req.setAttribute("togroup", toGroup);
        }

        if (Parameter.isSet(req, "collection")) {
            int colId = Parameter.asInt(req, "collection", 0);
            try {
                SourceCollection collection = CollectionsTable.getCollection(colId);
                if (!UserStreamGroupsTable.hasStream(userId, user.getSelectedProfileId(), collection.getName())) {
                    try {
                        CollectionsTable.addCollection(userId, colId, Arrays.asList(user.getSelectedProfileId()));
                        req.setAttribute("collection", collection);
                    } catch (Exception e) {
                        req.setAttribute("error", "Collection error: " + e.getMessage());
                    }
                } else {
                    req.setAttribute("error", "Collection alreay added");
                }
            } catch (SQLException e1) {
                req.setAttribute("error", "Couldn't get collection: " + e1.getMessage());
                logger.error("collection e1: {}", e1, e1.getMessage());
            }
        }

        try (Connection conn = Database.getConnection()) {
            ResultSet rs = UserStreamGroupsTable.get(conn, user.getSelectedProfileId(), false);
            List<StreamGroup> groups = new ArrayList<>();
            while (rs.next()) {
                groups.add(StreamGroup.fromRs(0, rs));
            }
            req.setAttribute("groups", groups);
        } catch (SQLException ex) {
            logger.error("/list failed: {}", ex, ex.getMessage());
        }
        req.setAttribute("profile", user.getSelectedProfile());
        ServletUtils.redirect(req, resp, "/pages/add.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long userId = UserSession.getUserId(req);
        if (userId == 0) {
            PageUtils.gotoStart(req, resp);
            return;
        }
        if (FormUploadHelper.isMultiPartContent(req)) {
            UserData user = UsersTable.get(userId);
            try {
                FormUploadHelper helper = new FormUploadHelper(req);

                UserOPMLImportHandler handler = new UserOPMLImportHandler(user.getUserId());

                // if (addToProfile.equals(ONLYSELECTED)) {
                // ArrayList<String> selected = helper.asString(SELECTED_PROFILES);
                // handler.addOnlyToProfile(selected);
                // } else if (addToProfile.equals(CURRENT)) {
                handler.addOnlyToProfile(user.getSelectedProfileId());
                // } else {
                // Default is save to all profiles.
                // }

                OPMLParser parser = new OPMLParser(handler);
                parser.parse(new ByteArrayInputStream(helper.asStream("opml-file").toByteArray()));

                req.setAttribute("handler", handler);
                // req.setAttribute("<p class='lead'>Feeds imported " + handler.getSubsOk() + ", ");
                // req.setAttribute("failed " + handler.getSubsErrors() + "<br>");
                // req.setAttribute("Stream groups created: " + handler.getStreamsOk() + ", ");
                // req.setAttribute("failed " + handler.getStreamsErrors() + "<br>");
                // req.setAttribute("Feed source queued : " + handler.getSourceQueued() + ", ");
                // req.setAttribute("already known " + handler.getSourceKnown() + ", ");
                // req.setAttribute("failed " + handler.getSourceError() + "</p><hr></div></div></div>");
            } catch (SAXException e) {
                req.setAttribute("error", e.getMessage());
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        doGet(req, resp);
    }

}
