package server;

public class ServerStat {

    private int numberOfOkResp = 0;
    private float averageRespTime = 0;
    private float maximumRespTime = 0;

    public ServerStat() {

    }

    public synchronized void updateStat(float responseTime) {
        averageRespTime = (averageRespTime * numberOfOkResp);
        numberOfOkResp++;
        averageRespTime = (averageRespTime + responseTime) / numberOfOkResp;
        maximumRespTime = responseTime > maximumRespTime ? responseTime : maximumRespTime;
    }

    public int getNumberOfOkResponse() {
        return numberOfOkResp;
    }

    public float getAverageResponseTime() {
        return averageRespTime;
    }

    public float getMaximumResponseTime() {
        return maximumRespTime;
    }
}
