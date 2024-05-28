package com.cryptic.model;

import com.cryptic.model.cs2.MessageType;

public class BroadcastMessage {
    private final String message;
    private final int enumID;
    private final String url;
    private final boolean aboveChatBox;

    private BroadcastMessage(Builder builder) {
        this.message = builder.message;
        this.enumID = builder.enumID;
        this.url = builder.url;
        this.aboveChatBox = builder.aboveChatBox;
    }

    // Getters for the properties (optional, if you need them)
    public String getMessage() {
        return message;
    }

    public int getEnumID() {
        return enumID;
    }

    public String getUrl() {
        return url;
    }

    public boolean isAboveChatBox() {
        return aboveChatBox;
    }

    public static class Builder {
        private String message;
        private int enumID;
        private String url;
        private boolean aboveChatBox;

        public Builder() {
            this.aboveChatBox = true;
            this.enumID = -1;
            this.url = "";
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder enumID(int enumID) {
            this.enumID = enumID;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder aboveChatBox(boolean aboveChatBox) {
            this.aboveChatBox = aboveChatBox;
            return this;
        }

        public void send() {
            String appendToEnd = "";
            if (enumID != -1) {
                appendToEnd = "|" + enumID;
            }
            if (!url.isEmpty()) {
                appendToEnd = "|" + url;
            }
            final MessageType messageType = aboveChatBox ? MessageType.GLOBAL_BROADCAST : MessageType.UNFILTERABLE;
            String finalAppendToEnd = appendToEnd;
            World.getWorld().getPlayers().forEach(p -> p.getPacketSender().sendMessage(message + finalAppendToEnd, messageType));
        }
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "message='" + message + '\'' +
            ", enumID=" + enumID +
            ", url='" + url + '\'' +
            ", aboveChatBox=" + aboveChatBox +
            '}';
    }
}
