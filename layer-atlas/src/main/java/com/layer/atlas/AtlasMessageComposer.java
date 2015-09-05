package com.layer.atlas;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerTypingIndicatorListener;
import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.MessagePart;

import java.util.ArrayList;

/**
 * @author Oleg Orlov
 * @since 12 May 2015
 */
public class AtlasMessageComposer extends FrameLayout {
    private static final String TAG = AtlasMessageComposer.class.getSimpleName();

    private EditText messageText;
    private View btnSend;

    private Listener listener;
    private Conversation conv;
    private LayerClient layerClient;

    // styles
    private int textColor;
    private Typeface typeFace;
    private int textStyle;

    //
    public AtlasMessageComposer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs, defStyle);
    }

    public AtlasMessageComposer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AtlasMessageComposer(Context context) {
        super(context);
    }

    public void parseStyle(Context context, AttributeSet attrs, int defStyle) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AtlasMessageComposer, R.attr.AtlasMessageComposer, defStyle);
        this.textColor = ta.getColor(R.styleable.AtlasMessageComposer_composerTextColor, context.getResources().getColor(R.color.atlas_text_black));
        //this.textSize  = ta.getDimension(R.styleable.AtlasMessageComposer_composerTextSize, context.getResources().getDimension(R.dimen.atlas_text_size_general));
        this.textStyle = ta.getInt(R.styleable.AtlasMessageComposer_composerTextStyle, Typeface.NORMAL);
        String typeFaceName = ta.getString(R.styleable.AtlasMessageComposer_composerTextTypeface);
        this.typeFace = typeFaceName != null ? Typeface.create(typeFaceName, textStyle) : null;
        ta.recycle();
    }

    /**
     * Initialization is required to engage MessageComposer with LayerClient and Conversation
     * to send messages.
     * <p/>
     * If Conversation is not defined, "Send" action will not be able to send messages
     *
     * @param client       - must be not null
     * @param conversation - could be null. Conversation could be provided later using {@link #setConversation(Conversation)}
     */
    public void init(LayerClient client, Conversation conversation) {
        if (client == null) throw new IllegalArgumentException("LayerClient cannot be null");
        if (messageText != null)
            throw new IllegalStateException("AtlasMessageComposer is already initialized!");

        this.layerClient = client;
        this.conv = conversation;

        LayoutInflater.from(getContext()).inflate(R.layout.atlas_message_composer, this);

        messageText = (EditText) findViewById(R.id.atlas_message_composer_text);
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (conv == null) return;
                try {
                    if (s.length() > 0) {
                        conv.send(LayerTypingIndicatorListener.TypingIndicator.STARTED);
                    } else {
                        conv.send(LayerTypingIndicatorListener.TypingIndicator.FINISHED);
                    }
                } catch (LayerException e) {
                    // `e.getType() == LayerException.Type.CONVERSATION_DELETED`
                }
            }
        });

        btnSend = findViewById(R.id.atlas_message_composer_send);
        btnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                String text = messageText.getText().toString();

                if (text.trim().length() > 0) {

                    ArrayList<MessagePart> parts = new ArrayList<MessagePart>();
                    String[] lines = text.split("\n+");
                    for (String line : lines) {
                        parts.add(layerClient.newMessagePart(line));
                    }
                    Message msg = layerClient.newMessage(parts);

                    if (listener != null) {
                        boolean proceed = listener.beforeSend(msg);
                        if (!proceed) return;
                    } else if (conv == null) {
                        Log.e(TAG, "Cannot send message. Conversation is not set");
                    }
                    if (conv == null) return;

                    conv.send(msg);
                    messageText.setText("");
                }
            }
        });
        applyStyle();
    }

    private void applyStyle() {
        //messageText.setTextSize(textSize);
        messageText.setTypeface(typeFace, textStyle);
        messageText.setTextColor(textColor);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Conversation getConversation() {
        return conv;
    }

    public void setConversation(Conversation conv) {
        this.conv = conv;
    }

    public interface Listener {
        boolean beforeSend(Message message);
    }
}
