package feedreader.parser;

import java.util.ArrayList;

import org.xml.sax.Attributes;

public class XmlQnode {
    String name;
    String val = "";

    ArrayList<AttrInfo> attr = new ArrayList<>();

    public XmlQnode(String name) {
        this.name = name;
    }

    public XmlQnode(String name, Attributes attrs) {
        this.name = name;
        setAttrs(attrs);
    }

    public final void setAttrs(Attributes attrs) {
        for (int x = 0; x < attrs.getLength(); x++) {
            AttrInfo a = new AttrInfo(attrs.getQName(x), attrs.getType(x), attrs.getValue(x));
            if (!attr.contains(a)) {
                attr.add(a);
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "QNode{" + "name=" + name + ", val=" + val + ", attr=" + attr + '}';
    }

    public ArrayList<AttrInfo> getAttr() {
        return attr;
    }

    public class AttrInfo {

        public String attrname;
        public String type;
        public String attrval;

        public AttrInfo(String name, String type, String val) {
            this.attrname = name;
            this.type = type;
            this.attrval = val;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AttrInfo == false && !((AttrInfo) obj).attrname.equals(attrname);
        }

        @Override
        public String toString() {
            return "AttrInfo{" + "name=" + attrname + ", type=" + type + ", val=" + attrval + '}';
        }
    }
}
