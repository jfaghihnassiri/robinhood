package Model;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public abstract class AbstractModel {
    JSONObject j;

    public JSONObject getJson() {
        return j;
    }

    public AbstractModel(JSONObject json) {
        this.j = json;
    }

    public AbstractModel() { }

    private boolean canParseString(String s) {
        return j.has(s) && !j.isNull(s);
    }

    public String parseString(String s) {
        if (canParseString(s)) {
            return j.getString(s);
        }
        else {
            return null;
        }
    }

    public double parseStringAsDouble(String s) {
        String asString = parseString(s);
        if (asString != null) {
            return Double.parseDouble(asString);
        }
        else {
            return 0;
        }
    }

    public Date parseStringAsDate(String s) {
        String asString = parseString(s);
        if (asString != null) {
            // ex: 2018-06-29T16:58:12.982033Z
            Calendar c = javax.xml.bind.DatatypeConverter.parseDateTime(asString);
            return c.getTime();
        }
        else {
            return null;
        }
    }
}
