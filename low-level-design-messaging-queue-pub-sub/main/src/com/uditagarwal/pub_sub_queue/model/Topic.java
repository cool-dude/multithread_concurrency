package com.uditagarwal.pub_sub_queue.model;

import java.util.ArrayList;
import java.util.List;
public class Topic {
    private final String topicName;
    private final String topicId;
    private final List<Message> messages; // TODO: Change getter this to send only immutable list outside.
    private final List<TopicSubscriber> subscribers; // TODO: Change getter this to send only immutable list outside.

    public String getTopicName() {
        return topicName;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public List<TopicSubscriber> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    public String getTopicId() {
        return topicId;
    }

    public Topic(final String topicName, final String topicId) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.messages = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    public synchronized void addMessage(final Message message) {
        messages.add(message);
    }

    public void addSubscriber(final TopicSubscriber subscriber) {
        subscribers.add(subscriber);
    }
}
