package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feedreader.entities.FeedSourceEntry;
import feedreader.parser.XmlQnode;
import feedreader.utils.SQLUtils;

public class XmlAttrTable {

    public static final String TABLE_NODE = "feedreader.feedsourcenodes";
    public static final String TABLE_ATTRS = "feedreader.feedsourcenodeattrs";

    public static final String ATTR_NODE_ID = "attr_node_id";
    public static final String ATTR_NAME = "attr_name";
    public static final String ATTR_TYPE = "attr_type";
    public static final String ATTR_VALUE = "attr_value";
    public static final String NODE_ID = "l_node_id";
    public static final String NODE_NAME = "node_name";
    public static final String NODE_VAL = "node_val";
    public static final String NODE_OWNER = "node_owner";
    private static final Logger logger = LoggerFactory.getLogger(XmlAttrTable.class);

    public static boolean init() {
        logger.info("init");
        return true;
    }

    public static void close() {
        logger.info("close");
    }

    public static int getNodeId(String nodeName) {
        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'", NODE_ID, TABLE_NODE, NODE_NAME,
                    SQLUtils.asSafeString(nodeName));
            ResultSet rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return rs.getInt(NODE_ID);
            }

            query = String.format("INSERT INTO %s (%s, %s) VALUES (DEFAULT, '%s') RETURNING %s", TABLE_NODE, NODE_ID,
                    NODE_NAME, SQLUtils.asSafeString(nodeName), NODE_ID);
            rs = conn.createStatement().executeQuery(query);

            if (rs.next()) {
                return rs.getInt(NODE_ID);
            }
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
        }

        return -1;
    }

    public static void save(String sourceOwner, HashMap<String, XmlQnode> nodeList) {
        for (Map.Entry<String, XmlQnode> entries : nodeList.entrySet()) {
            save(sourceOwner, entries.getValue());
        }
    }

    public static void save(String sourceOwner, XmlQnode node) {
        FeedSourceEntry entry = FeedSourcesTable.getByField(DBFields.STR_XML_URL, SQLUtils.asSafeString(sourceOwner));

        if (entry.getId() == 0) {
            logger.error("no feed source id found for: {}", sourceOwner);
            return;
        }

        long nodeId = 0;

        try (Connection conn = Database.getConnection()) {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = '%s'", NODE_ID, TABLE_NODE,
                    DBFields.LONG_XML_ID, entry.getId(), NODE_NAME, node.getName());
            ResultSet rs = conn.createStatement().executeQuery(query);
            nodeId = 0;

            if (rs.next()) {
                nodeId = rs.getLong(NODE_ID);
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, DEFAULT, '%s', '%s') RETURNING %s",
                        TABLE_NODE, DBFields.LONG_XML_ID, NODE_ID, NODE_NAME, NODE_VAL, entry.getId(),
                        SQLUtils.asSafeString(node.getName()), SQLUtils.asSafeString(node.getVal()), NODE_ID);
                rs = conn.createStatement().executeQuery(query);

                if (!rs.next()) {
                    logger.error("no id returned: {}", query);
                    return;
                }

                nodeId = rs.getLong(NODE_ID);
            }
        } catch (SQLException ex) {
            logger.error("failed: {}", ex, ex.getMessage());
            return;
        }

        saveAttr(nodeId, node.getAttr());
    }

    public static void saveAttr(long nodeId, ArrayList<XmlQnode.AttrInfo> value) {
        for (XmlQnode.AttrInfo attr : value) {
            try (Connection conn = Database.getConnection()) {
                String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = '%s'", NODE_ID, TABLE_ATTRS,
                        NODE_ID, nodeId, ATTR_NAME, SQLUtils.asSafeString(attr.attrname));
                ResultSet rs = conn.createStatement().executeQuery(query);

                if (rs.next()) {
                    return;
                }
            } catch (SQLException ex) {
                logger.error("failed: {}", ex, ex.getMessage());
            }

            try (Connection conn = Database.getConnection()) {
                String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s')",
                        TABLE_ATTRS, NODE_ID, ATTR_NAME, ATTR_TYPE, ATTR_VALUE, nodeId, attr.attrname, attr.type, attr.attrval);
                conn.createStatement().executeQuery(query);
            } catch (SQLException ex) {
                logger.error("failed: {}", ex, ex.getMessage());
            }
        }
    }

}
