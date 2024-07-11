package com.gri.agriconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "userstat")
public class UserStat {
    //bir user için satır açıp, <tag : rate> trie'si tutulabilirdi.
     @Id String statId;
     String userId;
     String tagId;
     Integer rate;
     //Trie<Tag, Rate> tagCountMap;
}
