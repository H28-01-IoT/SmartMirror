
package iot.com.smartmirror.network.json.cloudvision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * this class has any responses from google cloud vision API.
 */
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
public class CloudVisionResponce implements JsonResponce {

    public List<Response> responses = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public CloudVisionResponce withResponses(List<Response> responses) {
        this.responses = responses;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public CloudVisionResponce withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
