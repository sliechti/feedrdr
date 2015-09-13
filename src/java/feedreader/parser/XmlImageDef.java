package feedreader.parser;


public class XmlImageDef 
{
    int w;
    int h;
    String src;

    public XmlImageDef(String src, int w, int h)
    {
        this.w = w;
        this.h = h;
        this.src = src;
    }

    public XmlImageDef(String src, String w, String h)
    {
        this.src = src;
        
        try {
            this.w = Integer.parseInt(w);
        } catch (NumberFormatException nfe) {
            this.w = 0;
        }
        
        try{
            this.h = Integer.parseInt(h);
        } catch (NumberFormatException nfe) {
            this.h = 0;
        }
    }
    
    XmlImageDef()
    {
        w = 0;
        h = 0;
        src = "";
    }

    public int getW()
    {
        return w;
    }

    public int getH()
    {
        return h;
    }

    public String getSrc()
    {
        return src;
    }

    public void setW(int w)
    {
        this.w = w;
    }

    public void setH(int h)
    {
        this.h = h;
    }

    public void setSrc(String src)
    {
        this.src = src;
    }

    
    @Override
    public String toString()
    {
        return "XmlImageDef{" + "w=" + w + ", h=" + h + ", src=" + src + '}';
    }
    
}
