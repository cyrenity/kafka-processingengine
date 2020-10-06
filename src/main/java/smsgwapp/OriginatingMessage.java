package smsgwapp;

import java.util.Date;

public final class OriginatingMessage {
    private String ShortCode;
    private String MessageTo;
    private String MessageContent;
    private Date MessageTimestamp;
    private String SmscId;
    private String EsmeIp;

    public OriginatingMessage() {
    }

    public OriginatingMessage(String ShortCode,
                              String MessageTo,
                              String MessageContent,
                              Date MessageTimestamp,
                              String SmscId,
                              String EsmeIp) {
        this.ShortCode = ShortCode;
        this.MessageTo = MessageTo;
        this.MessageContent = MessageContent;
        this.MessageTimestamp = MessageTimestamp;
        this.SmscId = SmscId;
        this.EsmeIp = EsmeIp;
    }

    public String getShortCode() {
        return ShortCode;
    }

    public void setShortCode(String ShortCode) {
        this.ShortCode = ShortCode;
    }

    public String getMessageTo() {
        return MessageTo;
    }

    public void setMessageTo(String MessageTo) {
        this.MessageTo = MessageTo;
    }

    public String getMessageContent() {
        return MessageContent;
    }

    public void setMessageContent(String MessageContent) {
        this.MessageContent = MessageContent;
    }

    public Date getMessageTimestamp() {
        return MessageTimestamp;
    }

    public void setMessageTimestamp(Date MessageTimestamp) {
        this.MessageTimestamp = MessageTimestamp;
    }

    public String getSmscId() {
        return SmscId;
    }

    public void setSmscId(String SmscId) {
        this.SmscId = SmscId;
    }

    public String getEsmeIp() {
        return EsmeIp;
    }

    public void setEsmeIp(String EsmeIp) {
        this.EsmeIp = EsmeIp;
    }
}