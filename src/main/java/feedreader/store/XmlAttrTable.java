package feedreader.store;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import feedreader.entities.FeedSourceEntry;
import feedreader.log.Logger;
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

    static Class<?> clz = XmlAttrTable.class;

    static Connection conn;
    static Statement stmt;

    public static boolean init() {
        conn = Database.getConnection();
        stmt = Database.getStatement();
        Logger.info(clz).log("initialized.").end();
        return true;
    }

    public static void close() {
        Logger.info(clz).log("close()").end();

        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.error(clz).log("closing sql objects ").log(ex.getMessage()).end();
        }
    }

    public static int getNodeId(String owner, String nodeName) {
        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = '%s'", NODE_ID, TABLE_NODE, NODE_NAME,
                    SQLUtils.asSafeString(nodeName));
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(NODE_ID);
            }

            query = String.format("INSERT INTO %s (%s, %s) VALUES (DEFAULT, '%s') RETURNING %s", TABLE_NODE, NODE_ID,
                    NODE_NAME, SQLUtils.asSafeString(nodeName), NODE_ID);
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(NODE_ID);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("err ").log(ex.getMessage()).end();
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
            Logger.error(clz).log("no feed source id found for ").log(sourceOwner).end();
            return;
        }

        long nodeId = 0;

        try {
            String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = '%s'", NODE_ID, TABLE_NODE,
                    DBFields.LONG_XML_ID, entry.getId(), NODE_NAME, node.getName());
            ResultSet rs = stmt.executeQuery(query);
            Logger.debug(clz).log(query).end();
            nodeId = 0;

            if (rs.next()) {
                nodeId = rs.getLong(NODE_ID);
            } else {
                query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, DEFAULT, '%s', '%s') RETURNING %s",
                        TABLE_NODE, DBFields.LONG_XML_ID, NODE_ID, NODE_NAME, NODE_VAL, entry.getId(),
                        SQLUtils.asSafeString(node.getName()), SQLUtils.asSafeString(node.getVal()), NODE_ID);
                Logger.debug(clz).log(query).end();
                rs = stmt.executeQuery(query);

                if (!rs.next()) {
                    Logger.error(clz).log("no id returned. ").log(query).end();
                    return;
                }

                nodeId = rs.getLong(NODE_ID);
            }
        } catch (SQLException ex) {
            Logger.error(clz).log("save error, ").log(ex.getMessage()).end();
            return;
        }

        saveAttr(nodeId, node.getAttr());
    }

    public static void saveAttr(long nodeId, ArrayList<XmlQnode.AttrInfo> value) {
        for (XmlQnode.AttrInfo attr : value) {
            try {
                String query = String.format("SELECT %s FROM %s WHERE %s = %d AND %s = '%s'", NODE_ID, TABLE_ATTRS,
                        NODE_ID, nodeId, ATTR_NAME, SQLUtils.asSafeString(attr.name));
                Logger.debug(clz).log(query).end();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {
                    return;
                }
            } catch (SQLException ex) {
                Logger.error(clz).log("saveAttr ").log(ex.getMessage()).end();
            }

            try {
                String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (%d, '%s', '%s', '%s')",
                        TABLE_ATTRS, NODE_ID, ATTR_NAME, ATTR_TYPE, ATTR_VALUE, nodeId, attr.name, attr.type, attr.val);
                Logger.debug(clz).log(query).end();
                stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.error(clz).log("saveAttr #2").log(ex.getMessage()).end();
            }
        }
    }

}
