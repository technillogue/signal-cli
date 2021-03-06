package org.asamk.signal.json;

import org.asamk.Signal;
import org.asamk.signal.manager.Manager;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.messages.SignalServiceGroup;
import org.whispersystems.signalservice.api.messages.SignalServiceGroupV2;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class JsonReaction {
    String emoji; // unicode?
    String targetAuthor;
    long targetTimestamp;
    boolean isRemove;
	JsonReaction (SignalServiceDataMessage.Reaction reaction) {
        this.emoji = reaction.getEmoji();
        // comment on this line from ReceiveMessageHandler: todo resolve
        this.targetAuthor = reaction.getTargetAuthor().getLegacyIdentifier();
    	this.targetTimestamp = reaction.getTargetSentTimestamp();
    	this.isRemove = reaction.isRemove();
    }
}


class JsonDataMessage {

    long timestamp;
    String message;
    int expiresInSeconds;

    JsonReaction reaction;
    JsonQuote quote;
    List<JsonMention> mentions;
    List<JsonAttachment> attachments;
    JsonGroupInfo groupInfo;
    JsonReaction reaction;
	SignalServiceDataMessage.Quote quote;

    JsonDataMessage(SignalServiceDataMessage dataMessage, Manager m) {
        this.timestamp = dataMessage.getTimestamp();
        if (dataMessage.getGroupContext().isPresent()) {
            if (dataMessage.getGroupContext().get().getGroupV1().isPresent()) {
                SignalServiceGroup groupInfo = dataMessage.getGroupContext().get().getGroupV1().get();
                this.groupInfo = new JsonGroupInfo(groupInfo);
            } else if (dataMessage.getGroupContext().get().getGroupV2().isPresent()) {
                SignalServiceGroupV2 groupInfo = dataMessage.getGroupContext().get().getGroupV2().get();
                this.groupInfo = new JsonGroupInfo(groupInfo);
            }
        }
        if (dataMessage.getBody().isPresent()) {
            this.message = dataMessage.getBody().get();
        }
        this.expiresInSeconds = dataMessage.getExpiresInSeconds();
        if (dataMessage.getReaction().isPresent()) {
            this.reaction = new JsonReaction(dataMessage.getReaction().get(), m);
        }
        if (dataMessage.getQuote().isPresent()) {
            this.quote = new JsonQuote(dataMessage.getQuote().get(), m);
        }
        if (dataMessage.getMentions().isPresent()) {
            this.mentions = dataMessage.getMentions()
                    .get()
                    .stream()
                    .map(mention -> new JsonMention(mention, m))
                    .collect(Collectors.toList());
        } else {
            this.mentions = List.of();
        }
        if (dataMessage.getAttachments().isPresent()) {
            this.attachments = dataMessage.getAttachments()
                    .get()
                    .stream()
                    .map(JsonAttachment::new)
                    .collect(Collectors.toList());
        } else {
            this.attachments = List.of();
        }
        if (dataMessage.getReaction().isPresent()) {
            final SignalServiceDataMessage.Reaction reaction = dataMessage.getReaction().get();
            this.reaction = new JsonReaction(reaction);
/*          this.emoji = reaction.getEmoji();
            this.targetAuthor = reaction.getTargetAuthor().getLegacyIdentifier();
			this.targetTimestamp = reaction.getTargetSentTimestamp();
*/        } /*else {
			this.reaction = null;
            this.emoji = "";
            this.targetAuthor = "";
            this.targetTimestamp = 0;

	}

        if (message.getQuote().isPresent()) {
            SignalServiceDataMessage.Quote quote = message.getQuote().get();
            System.out.println("Quote: (" + quote.getId() + ")");
            // there doesn't seem to be any way to find a message's id?
            System.out.println(" Author: " + quote.getAuthor().getLegacyIdentifier());
            System.out.println(" Text: " + quote.getText());
        }
        if (message.isExpirationUpdate()) {
            System.out.println("Is Expiration update: " + message.isExpirationUpdate());
        }
*/
    }
    public JsonDataMessage(Signal.MessageReceived messageReceived) {
        timestamp = messageReceived.getTimestamp();
        message = messageReceived.getMessage();
        groupInfo = new JsonGroupInfo(messageReceived.getGroupId());
        reaction = null;    // TODO Replace these 3 with the proper commands
        quote = null;
        mentions = null;
        attachments = messageReceived.getAttachments().stream().map(JsonAttachment::new).collect(Collectors.toList());
    }
	// i don't understand what SyncMessages are so i'm going to ignore them
	// i think it only matters if you have multiple devices on your end
    public JsonDataMessage(Signal.SyncMessageReceived messageReceived) {
        timestamp = messageReceived.getTimestamp();
        message = messageReceived.getMessage();
        groupInfo = new JsonGroupInfo(messageReceived.getGroupId());
        reaction = null;    // TODO Replace these 3 with the proper commands
        quote = null;
        mentions = null;
        attachments = messageReceived.getAttachments().stream().map(JsonAttachment::new).collect(Collectors.toList());
    }
}
