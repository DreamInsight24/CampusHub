
//abandoned

package com.campushub.entity;

import com.campushub.entity.demand.Demand;
import com.campushub.entity.demand.ExpressDemandDetail;
import com.campushub.entity.demand.SecondhandDemandDetail;
import com.campushub.entity.demand.TeamupDemandDetail;
import com.campushub.entity.demand.TutoringDemandDetail;
import com.campushub.entity.message.ChatMessage;
import com.campushub.entity.message.Conversation;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Demand.class, name = "dm"),
        @JsonSubTypes.Type(value = ExpressDemandDetail.class, name = "express_dmd"),
        @JsonSubTypes.Type(value = SecondhandDemandDetail.class, name = "secondhand_dmd"),
        @JsonSubTypes.Type(value = TutoringDemandDetail.class, name = "tutoring_dmd"),
        @JsonSubTypes.Type(value = TeamupDemandDetail.class, name = "teamup_dmd"),
        @JsonSubTypes.Type(value = User.class, name = "usr"),
        @JsonSubTypes.Type(value = UserDetail.class, name = "usd"),
        @JsonSubTypes.Type(value = Order.class, name = "od"),
        @JsonSubTypes.Type(value = Engagement.class, name = "egm"),
        @JsonSubTypes.Type(value = Conversation.class, name = "dl"),
        @JsonSubTypes.Type(value = ChatMessage.class, name = "cm"),
})

@Deprecated
public abstract class BaseDTO {
}
