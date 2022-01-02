package request;

import java.util.Objects;

public abstract class Request {

    public enum RequestType {
        STAT_REQUEST,
        COMPUTATION_REQUEST;
    }
    private final RequestType request;

    public Request(RequestType request) {
        this.request = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Request req = (Request) o;
        return req.getType().equals(this.request);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.request);
    }

    public RequestType getType() {
        return request;
    }
}
