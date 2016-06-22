package ua.artcode.taxi.to;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by serhii on 05.06.16.
 */
public class MessageBody {

    private Map<String, Object> map;

    public MessageBody() {
        map = new HashMap<>();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
