package request;

import java.util.Objects;

public class StatRequest extends Request {

    private final StatRequestType statRequestType;
    public static final String STAT_REQS_COMMAND = "STAT_REQS";
    public static final String STAT_AVG_TIME_COMMAND = "STAT_AVG_TIME";
    public static final String STAT_MAX_TIME_COMMAND = "STAT_MAX_TIME";

    public enum StatRequestType {
        STAT_REQS(STAT_REQS_COMMAND),
        STAT_AVG_TIME(STAT_AVG_TIME_COMMAND),
        STAT_MAX_TIME(STAT_MAX_TIME_COMMAND);

        private final String command;

        StatRequestType(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public StatRequest(StatRequestType statType) {
        super(RequestType.STAT_REQUEST);
        this.statRequestType = statType;
    }

    public StatRequestType getStatRequestType() {
        return statRequestType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatRequest req = (StatRequest) o;
        return req.getStatRequestType().equals(this.statRequestType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.statRequestType);
    }
}
