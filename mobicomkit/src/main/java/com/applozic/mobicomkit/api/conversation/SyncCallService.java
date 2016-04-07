package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.Date;
import java.util.List;
/**
 * Created by applozic on 12/2/15.
 */
public class SyncCallService {

    private static final String TAG = "SyncCall";

    public static boolean refreshView = false;
    public static boolean blockedBy = false;
    public static boolean unBlockedBy = false;
    private Context context;
    private static SyncCallService syncCallService;
    private MobiComMessageService mobiComMessageService;
    private MobiComConversationService mobiComConversationService;
    private BaseContactService contactService;
    private ChannelService channelService;

    private SyncCallService(Context context) {
        this.context = context;
        this.mobiComMessageService = new MobiComMessageService(context, MessageIntentService.class);
        this.mobiComConversationService = new MobiComConversationService(context);
        this.contactService = new AppContactService(context);
        this.channelService = ChannelService.getInstance(context);
    }

    public synchronized static SyncCallService getInstance(Context context) {
        if (syncCallService == null) {
            syncCallService = new SyncCallService(context);
        }
        return syncCallService;
    }
    
    public synchronized void updateDeliveryStatus(String key) {
        mobiComMessageService.updateDeliveryStatus(key,false);
        refreshView= true;
    }

    public synchronized void updateReadStatus(String key) {
        mobiComMessageService.updateDeliveryStatus(key,true);
        refreshView= true;

    }

    public synchronized List<Message> getLatestMessagesGroupByPeople() {
        return mobiComConversationService.getLatestMessagesGroupByPeople(null);
    }

    public synchronized List<Message> getLatestMessagesGroupByPeople(Long createdAt) {
        return mobiComConversationService.getLatestMessagesGroupByPeople(createdAt);
    }

    public synchronized void syncMessages(String key) {
        if (!TextUtils.isEmpty(key) && mobiComMessageService.isMessagePresent(key)) {
            Log.d(TAG, "Message is already present, MQTT reached before GCM.");
        } else {
            mobiComMessageService.syncMessages();
        }
    }

    public synchronized void updateDeliveryStatusForContact(String contactId,boolean markRead) {
        mobiComMessageService.updateDeliveryStatusForContact(contactId, markRead);
    }

    public synchronized void updateConnectedStatus(String contactId, Date date, boolean connected) {
        contactService.updateConnectedStatus(contactId, date, connected);
    }

    public synchronized void deleteConversationThread(String userId) {
        mobiComConversationService.deleteConversationFromDevice(userId);
        refreshView = true;
    }

    public synchronized void deleteMessage(String messageKey) {
        mobiComConversationService.deleteMessageFromDevice(messageKey, null);
        refreshView = true;
    }

    public synchronized void updateUserBlocked(String userId, boolean userBlocked) {
        contactService.updateUserBlocked(userId, userBlocked);
    }

    public synchronized void updateUserBlockedBy(String userId, boolean userBlockedBy) {
        contactService.updateUserBlockedBy(userId, userBlockedBy);
    }

    public synchronized  void updateUnreadCount(final Contact contact ,final Channel channel){
        mobiComConversationService.updateUnreadCount(contact,channel);
    }

}