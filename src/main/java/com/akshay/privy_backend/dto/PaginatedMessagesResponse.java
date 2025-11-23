package com.akshay.privy_backend.dto;

import java.time.Instant;
import java.util.List;

public class PaginatedMessagesResponse {
    private List<MessageHistoryItem> messages;
    private Instant nextCursorTimestamp;
    private String nextCursorId;
    private boolean hasMore;
	public List<MessageHistoryItem> getMessages() {
		return messages;
	}
	public void setMessages(List<MessageHistoryItem> messages) {
		this.messages = messages;
	}
	public Instant getNextCursorTimestamp() {
		return nextCursorTimestamp;
	}
	public void setNextCursorTimestamp(Instant nextCursorTimestamp) {
		this.nextCursorTimestamp = nextCursorTimestamp;
	}
	public String getNextCursorId() {
		return nextCursorId;
	}
	public void setNextCursorId(String nextCursorId) {
		this.nextCursorId = nextCursorId;
	}
	public boolean isHasMore() {
		return hasMore;
	}
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
}
