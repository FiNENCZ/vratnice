package cz.dp.share.enums;

public enum WebSocketStatusEnum {
    IN_PROGRESS,
    COMPLETE,
    ERROR;

    public static WebSocketStatusEnum getWebSocketStatusEnum(StavOperaceStatusEnum stavOperaceStatusEnum) {
        switch (stavOperaceStatusEnum) {
            case COMPLETE:
                return COMPLETE;
            case ERROR:
                return ERROR;
            default:
                return IN_PROGRESS;
        }
    }
}